/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core.api;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.ArticleConflictException;
import de.loercher.geomodule.commons.exception.ArticleNotFoundException;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.RevisionPreconditionFailedException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import de.loercher.geomodule.commons.exception.UnauthorizedException;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.ArticleIdentifier;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import de.loercher.geomodule.geosearch.GeoSearchEntityMapper;
import de.loercher.geomodule.geosearch.GeoSearchPolicy;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Jimmy
 */
@RestController
@RequestMapping("geo")
public class GeoController
{

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    
    private static final Integer MAX_FIND_AROUND_RESULTS = 100;

    // has to be thread-safe!
    private final GeoConnector connector;
    private final GeoSearchEntityMapper mapper;
    private final GeoSearchPolicy policy;

    @Autowired
    public GeoController(GeoConnector pConnector, GeoSearchEntityMapper pMapper, GeoSearchPolicy pPolicy)
    {
	connector = pConnector;
	mapper = pMapper;
	policy = pPolicy;
    }

    @RequestMapping(value = "/{articleID}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteArticle(@PathVariable String articleID, @RequestHeader HttpHeaders headers) throws ArticleNotFoundException, RevisionPreconditionFailedException, GeneralCommunicationException, UnauthorizedException
    {
	ArticleIdentifier id = new ArticleIdentifier(articleID, headers.getETag());
	//first we have to check whether the user is allowed to delete the article
	IdentifiedArticleEntity entity = connector.getArticle(articleID);

	String userID = headers.getFirst("UserID");
	if ((userID == null) || !(userID.equals(entity.getEntity().getUserID())))
	{
	    throw new UnauthorizedException("User with ID " + userID + " is not authorized to delete article with ID " + articleID + ".", articleID, userID);
	}

	connector.removeArticle(id);

	Map<String, Object> result = generateResultMap(id.getId(), null);
	result.put("message", "Article deleted.");

	return generateResponseEntity(result, "There was an unexpected parsing exception in getArticle().", HttpStatus.OK, null);
    }

    @RequestMapping(value = "/{articleID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getArticle(@PathVariable String articleID) throws ArticleNotFoundException, GeneralCommunicationException
    {
	IdentifiedArticleEntity entity = connector.getArticle(articleID);
	GeoBaseEntity result = mapper.mapFromArticleEntity(entity);

	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("ETag", entity.getRev());

	return generateResponseEntity(result, "There was an unexpected parsing exception in getArticle().", HttpStatus.OK, responseHeaders);
    }

    @RequestMapping(value = "/{articleID}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateArticle(@PathVariable String articleID, @RequestBody GeoBaseEntity entity, @RequestHeader HttpHeaders headers) throws ArticleConflictException, GeneralCommunicationException, RevisionPreconditionFailedException, UnauthorizedException
    {
	if (StringUtils.isEmpty(entity.getUser()))
	{
	    throw new IllegalArgumentException("The added article entity has to carry a userid.");
	}

	ArticleEntity article = mapper.mapToArticleEntity(entity);

	try
	{
	    IdentifiedArticleEntity storedEntity;
	    storedEntity = connector.getArticle(articleID);

	    String userID = headers.getFirst("UserID");
	    if ((userID == null) || !(userID.equals(storedEntity.getEntity().getUserID())))
	    {
		throw new UnauthorizedException("User with ID " + userID + " is not authorized to delete article with ID " + articleID + ".", articleID, userID);
	    }
	} catch (ArticleNotFoundException ex)
	{
	    // it's ok to not find the article since it could be a new one
	    log.info("No article found on update. It's likely to be a conscious decision since it's allowed to create a new article doing so.");
	}

	String etag = headers.getETag();

	ArticleIdentifier id;
	if (etag != null)
	{
	    id = connector.updateArticle(article, articleID, etag);
	} else
	// It's a new entity but the URL is already defined
	{
	    id = connector.saveArticle(article, articleID);
	}

	IdentifiedArticleEntity resultEntity = new IdentifiedArticleEntity(id.getId(), id.getRev());
	resultEntity.setEntity(article);

	GeoBaseEntity result = mapper.mapFromArticleEntity(resultEntity);
	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("ETag", resultEntity.getRev());

	return generateResponseEntity(result, "There was an unexpected parsing exception in updateArticle().", HttpStatus.OK, responseHeaders);

    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addArticle(@RequestBody GeoBaseEntity entity, @RequestHeader HttpHeaders headers) throws ArticleConflictException, GeneralCommunicationException
    {
	if (StringUtils.isEmpty(entity.getUser()))
	{
	    throw new IllegalArgumentException("The added article entity has to carry a userid.");
	}

	String userID = headers.getFirst("UserID");
	if (userID == null)
	{
	    throw new IllegalArgumentException("There has to be set a header carrying the userid.");
	}
	
	if(!userID.equals(entity.getUser()))
	{
	    throw new IllegalArgumentException("UserIDs of entity and header have to match.");
	}

	ArticleEntity article = mapper.mapToArticleEntity(entity);

	ArticleIdentifier id = connector.addArticle(article);
	Map<String, Object> result = generateResultMap(id.getId(), null);

	String selfURL = mapper.mapIDToURL(id.getId());

	result.put("self", selfURL);
	result.put("etag", id.getRev());
	result.put("user", userID);
	result.put("message", "Article created.");

	return generateResponseEntity(result, "There was an unexpected parsing exception in addArticle().", HttpStatus.CREATED, null);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fetchArticlesAround(@RequestParam Double lat, @RequestParam Double lon, @RequestHeader HttpHeaders headers) throws GeneralCommunicationException, TooManyResultsException
    {
	String userID = headers.getFirst("UserID");
	
	Coordinate coord = new Coordinate(lat, lon);
	List<GeoSearchEntity> results = policy.fetchLayeredArticles(userID, coord, MAX_FIND_AROUND_RESULTS);
	
	return generateResponseEntity(results, "There was an unexpected parsing exception in fetchArticlesAround().", HttpStatus.OK, null);
    }

    private ResponseEntity<String> generateResponseEntity(Object toSerializeObject, String errorMessage, HttpStatus status, HttpHeaders headers) throws GeneralCommunicationException
    {
	try
	{
	    Gson gson = new Gson();

	    // convert java object to JSON format,
	    // and returned as JSON formatted string
	    String json = gson.toJson(toSerializeObject);

	    ResponseEntity<String> response;
	    if (headers != null)
	    {
		response = new ResponseEntity<>(json, headers, status);
	    } else
	    {
		response = new ResponseEntity<>(json, status);
	    }

	    return response;
	} catch (JsonParseException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException(errorMessage, ex);
	    log.error(e.getLoggingString());
	    throw e;
	}
    }

    private Map<String, Object> generateResultMap(String articleID, String userID)
    {
	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);
	result.put("userID", userID);

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);
	result.put("status", 200);

	return result;
    }
}
