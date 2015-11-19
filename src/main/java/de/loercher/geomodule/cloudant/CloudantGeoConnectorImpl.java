/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.commons.GeoModuleProperties;
import de.loercher.geomodule.commons.exception.ArticleConflictException;
import de.loercher.geomodule.commons.exception.ArticleNotFoundException;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.RevisionPreconditionFailedException;
import de.loercher.geomodule.connector.ArticleIdentifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;
import org.lightcouch.PreconditionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudantGeoConnectorImpl implements GeoConnector
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final static String USER_KEY_NAME = "cloudantUser";
    public final static String SECRET_KEY_NAME = "cloudantSecretKey";
    public final static String ACCOUNT_KEY_NAME = "cloudantAccount";

    public final static String BASE_URL_KEY_NAME = "geoCloudantUrl";
    public final static String DB_NAME = "localpress";

    private final CloudantClient client;
    private final Database db;
    private final GeoModuleProperties properties;
    private final String baseURL;

    private final CloudantArticleEntityMapperImpl mapper;

    public CloudantGeoConnectorImpl(GeoModuleProperties pProperties, CloudantArticleEntityMapperImpl pMapper)
    {
	properties = pProperties;
	Properties props = properties.getProp();

	baseURL = props.getProperty(BASE_URL_KEY_NAME);

	String password = props.getProperty(SECRET_KEY_NAME);
	client = new CloudantClient(props.getProperty(ACCOUNT_KEY_NAME), props.getProperty(USER_KEY_NAME), password);

	db = client.database(DB_NAME, false);

	mapper = pMapper;
    }

    @Override
    public ArticleIdentifier addArticle(ArticleEntity article) throws ArticleConflictException, GeneralCommunicationException
    {
	CloudantArticleEntity entity = mapper.mapFromArticleEntity(article);
	entity.setId(null);
	entity.setRev(null);

	Response resp;
	try
	{
	    resp = db.save(entity);
	} catch (DocumentConflictException ex)
	{
	    ArticleConflictException e = new ArticleConflictException("There failed the adding of an article because of a conflict exception. Probably the id was illegally set! ", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	} catch (CouchDbException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("Unexpected exception by invoking cloudant API on saveArticle.", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	}

	ArticleIdentifier result = new ArticleIdentifier(resp.getId(), resp.getRev());
	return result;
    }

    @Override
    public ArticleIdentifier saveArticle(ArticleEntity article) throws ArticleConflictException, GeneralCommunicationException
    {
	CloudantArticleEntity entity = mapper.mapFromArticleEntity(article);
	entity.setRev(null);

	Response resp;
	try
	{
	    resp = db.save(entity);
	} catch (DocumentConflictException ex)
	{
	    ArticleConflictException e = new ArticleConflictException("There failed the adding of an article because of a conflict exception. Probably the rev was illegally set! ", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	} catch (CouchDbException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("Unexpected exception by invoking cloudant API on saveArticle.", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	}

	ArticleIdentifier result = new ArticleIdentifier(resp.getId(), resp.getRev());
	return result;
    }

    @Override
    public ArticleIdentifier updateArticle(ArticleEntity article, String pId, String pRevision) throws RevisionPreconditionFailedException, GeneralCommunicationException
    {
	CloudantArticleEntity entity = mapper.mapFromArticleEntity(article);
	entity.setId(pId);
	entity.setRev(pRevision);

	try
	{
	    db.update(entity);
	} catch (PreconditionFailedException ex)
	{
	    RevisionPreconditionFailedException e = new RevisionPreconditionFailedException("Revision of article " + pId + " is: " + pRevision + " but should have another value! ", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	} catch (CouchDbException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("Unexpected exception by invoking cloudant API on updateArticle with id: " + pId + " and rev: " + pRevision, ex);
	    log.warn(e.getLoggingString());
	    throw e;
	}

	ArticleIdentifier result = new ArticleIdentifier(pId, pRevision);
	return result;
    }

    @Override
    public ArticleEntity getArticle(String id) throws ArticleNotFoundException, GeneralCommunicationException
    {
	CloudantArticleEntity entity;
	try
	{
	    entity = db.find(CloudantArticleEntity.class, id);
	} catch (NoDocumentException ex)
	{
	    ArticleNotFoundException e = new ArticleNotFoundException("Article with id " + id + " not found.", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	} catch (CouchDbException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("Unexpected exception by invoking cloudant API on getArticle.", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	}

	return mapper.mapToArticleEntity(entity);
    }

    @Override
    public List<ArticleEntity> getArticlesNear(Coordinate coordinates, Integer radiusInMeter) throws GeneralCommunicationException
    {
	Double latitude = coordinates.getLatitude();
	Double longitude = coordinates.getLongitude();

	StringBuilder builder = new StringBuilder();
	String url = builder.append(baseURL)
		.append("?lat=").append(latitude)
		.append("&lon=").append(longitude)
		.append("&radius=").append(radiusInMeter)
		.append("&format=geojson&include_docs=true&relation=contains").toString();

	HttpRequestBase requestBase = new HttpGet(url);

	HttpResponse response = client.executeRequest(requestBase);

	HttpEntity entity = response.getEntity();
	String alles;
	try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent())))
	{
	    String line = reader.readLine();
	    alles = "";
	    while (line != null)
	    {
		alles += line;
		line = reader.readLine();
	    }
	} catch (IOException e)
	{
	    GeneralCommunicationException ex = new GeneralCommunicationException("Problem occured by reading the response string of buffered reader inside getArticlesNear(" + coordinates + ", " + radiusInMeter + ").", e);
	    log.error(ex.getLoggingString());
	    throw ex;
	}

	CloudantQueryResponse articles;
	try
	{
	    Gson gson = new Gson();
	    Type type = new TypeToken<CloudantQueryResponse>()
	    {
	    }.getType();
	    articles = gson.fromJson(alles, type);
	} catch (JsonSyntaxException e)
	{
	    GeneralCommunicationException ex = new GeneralCommunicationException("Problem occured by parsing the response JSON inside getArticlesNear(" + coordinates + ", " + radiusInMeter + "): " + alles, e);
	    log.error(ex.getLoggingString());
	    throw ex;
	}

	return mapper.mapToArticleEntityList(articles.getFeatures());
    }

}
