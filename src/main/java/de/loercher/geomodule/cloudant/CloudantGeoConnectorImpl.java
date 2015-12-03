/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.commons.GeoModuleProperties;
import de.loercher.geomodule.commons.exception.ArticleConflictException;
import de.loercher.geomodule.commons.exception.ArticleNotFoundException;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.JSONParseException;
import de.loercher.geomodule.commons.exception.RevisionPreconditionFailedException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import de.loercher.geomodule.connector.ArticleIdentifier;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;
import org.lightcouch.NoDocumentException;
import org.lightcouch.PreconditionFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloudantGeoConnectorImpl implements GeoConnector
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final static String USER_KEY_NAME = "cloudantUser";
    public final static String SECRET_KEY_NAME = "cloudantSecretKey";
    public final static String ACCOUNT_KEY_NAME = "cloudantAccount";

    public final static String BASE_URL_KEY_NAME = "geoCloudantUrl";
    public final static String DB_NAME = "localpress";

    private final Integer LIMIT = 200;

    private final CloudantClient client;
    private final Database db;
    private final GeoModuleProperties properties;
    private final String baseURL;

    private final CloudantArticleEntityMapperImpl mapper;

    @Autowired
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
	    ArticleConflictException e = new ArticleConflictException("There failed the adding of an article because of a conflict exception. Probably the id was illegally set! ", null, ex);
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
    public ArticleIdentifier saveArticle(ArticleEntity article, String pId) throws ArticleConflictException, GeneralCommunicationException
    {
	CloudantArticleEntity entity = mapper.mapFromArticleEntity(article);
	entity.setRev(null);
	entity.setId(pId);

	Response resp;
	try
	{
	    resp = db.save(entity);
	} catch (DocumentConflictException ex)
	{
	    ArticleConflictException e = new ArticleConflictException("There failed the adding of an article because of a conflict exception. Probably the rev was illegally set! ", pId, ex);
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
	} catch (DocumentConflictException | PreconditionFailedException ex)
	{
	    RevisionPreconditionFailedException e = new RevisionPreconditionFailedException("Revision of article " + pId + " is: " + pRevision + " but should have had another value! ", pId, pRevision, ex);
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
    public IdentifiedArticleEntity getArticle(String id) throws ArticleNotFoundException, GeneralCommunicationException
    {
	CloudantArticleEntity entity;
	try
	{
	    entity = db.find(CloudantArticleEntity.class, id);
	} catch (NoDocumentException ex)
	{
	    ArticleNotFoundException e = new ArticleNotFoundException("Article with id " + id + " not found.", id,  ex);
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
    public List<IdentifiedArticleEntity> getArticlesNear(Coordinate coordinates, Integer radiusInMeter, Integer maxArticleCount) throws GeneralCommunicationException, TooManyResultsException
    {
	if ((maxArticleCount == null) || (maxArticleCount < 1) || (maxArticleCount > LIMIT))
	{
	    maxArticleCount = LIMIT;
	}

	CloudantGeoSearchStream stream = new CloudantGeoSearchStream(baseURL, coordinates, radiusInMeter, client);
	try
	{
	    List<IdentifiedArticleEntity> result = mapper.mapToArticleEntityList(stream);
	    Integer size = result.size();

	    if (size > maxArticleCount)
	    {
		TooManyResultsException exception = new TooManyResultsException(size, maxArticleCount);
		exception.setCoord(coordinates);
		throw exception;
	    }

	    return result;
	} catch (IOException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("Unexpected IO-exception by trying to communicate with cloudant in method getArticlesNear.", ex);
	    log.error(e.getLoggingString());
	    throw e;
	} catch (JSONParseException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("There was an unexpected JSON parse exception by trying to parse response from cloudant.", ex);
	    log.error(e.getLoggingString());
	    throw e;
	}
    }

    @Override
    public void removeArticle(ArticleIdentifier articleId) throws ArticleNotFoundException, RevisionPreconditionFailedException, GeneralCommunicationException
    {
	String id = articleId.getId();
	String rev = articleId.getRev();

	try
	{
	    db.remove(id, rev);
	} catch (DocumentConflictException | PreconditionFailedException ex)
	{
	    RevisionPreconditionFailedException e = new RevisionPreconditionFailedException("Revision of article " + id + " is: " + rev + " but should have had another value! ", id, rev, ex);
	    log.warn(e.getLoggingString());
	    throw e;
	} catch (NoDocumentException ex)
	{
	    ArticleNotFoundException e = new ArticleNotFoundException("Article with id " + id + " not found.", id, ex);
	    log.warn(e.getLoggingString());
	    throw e;
	} catch (CouchDbException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("Unexpected exception by invoking cloudant API on removeArticle.", ex);
	    log.warn(e.getLoggingString());
	    throw e;
	}
    }

    @Override
    public Integer getProviderBasedLimit()
    {
	return LIMIT;
    }

}
