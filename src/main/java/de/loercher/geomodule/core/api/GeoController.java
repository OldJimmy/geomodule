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
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.ArticleIdentifier;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import de.loercher.geomodule.core.GeoSearchEntityMapper;
import de.loercher.geomodule.core.GeoSearchPolicy;
import de.loercher.geomodule.core.LocationHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // has to be thread-safe!
    private final GeoConnector connector;
    private final LocationHelper helper;
    private final GeoSearchEntityMapper mapper;

    @Autowired
    public GeoController(GeoConnector pConnector, LocationHelper pHelper, GeoSearchEntityMapper pMapper)
    {
	connector = pConnector;
	helper = pHelper;
	mapper = pMapper;
    }
    
    @RequestMapping(value = "/{articleID}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteArticle(@PathVariable String articleID, @RequestHeader HttpHeaders headers) throws ArticleNotFoundException, RevisionPreconditionFailedException, GeneralCommunicationException
    {
	ArticleIdentifier id = new ArticleIdentifier(articleID, headers.getETag());
	connector.removeArticle(id);
	
	return generateResponseEntity("", "There was an unexpected parsing exception in getArticle().", HttpStatus.OK, null);
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
    public ResponseEntity<String> updateArticle(@PathVariable String articleID, @RequestBody GeoBaseEntity entity, @RequestHeader HttpHeaders headers) throws ArticleConflictException, GeneralCommunicationException, RevisionPreconditionFailedException
    {
	ArticleEntity article = mapper.mapToArticleEntity(entity);
	String etag = headers.getETag();

	// It's a new entity but the URL is already defined
	ArticleIdentifier id;
	if (etag != null)
	{
	    id = connector.updateArticle(article, articleID, etag);
	} else
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
    public ResponseEntity<String> addArticle(@RequestBody GeoBaseEntity entity) throws ArticleConflictException, GeneralCommunicationException
    {
	ArticleEntity article = mapper.mapToArticleEntity(entity);

	ArticleIdentifier id = connector.addArticle(article);
	Map<String, String> result = new HashMap<>();

	String selfURL = mapper.mapIDToURL(id.getId());

	result.put("self", selfURL);
	result.put("etag", id.getRev());

	return generateResponseEntity(result, "There was an unexpected parsing exception in addArticle().", HttpStatus.CREATED, null);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fetchArticlesAround(@RequestParam Double lat, @RequestParam Double lon) throws GeneralCommunicationException, TooManyResultsException
    {
	Coordinate coord = new Coordinate(lat, lon);
	GeoSearchPolicy policy = new GeoSearchPolicy();

	List<IdentifiedArticleEntity> articles = getArticlesRecursively(policy, coord);

	List<GeoSearchEntity> results = new ArrayList<>();

	for (IdentifiedArticleEntity article : articles)
	{
	    Double distance = helper.calculateDistance(coord, article.getEntity().getCoord());
	    Double factor = policy.getLayerFactor(distance);
	    Integer layer = policy.getLayerNumber(distance);

	    GeoSearchEntity entity = mapper.mapFromArticleEntity(article, layer, distance, factor);
	    results.add(entity);
	}

	return generateResponseEntity(results, "There was an unexpected parsing exception in fetchArticlesAround().", HttpStatus.OK, null);

    }

    private List<IdentifiedArticleEntity> getArticlesRecursively(GeoSearchPolicy policy, Coordinate coord) throws TooManyResultsException, GeneralCommunicationException
    {
	try
	{
	    return connector.getArticlesNear(coord, policy.nextRadius(), policy.getMaxResultCount());
	} catch (TooManyResultsException e)
	{
	    if (policy.isLastRadius())
	    {
		throw e;
	    } else
	    {
		return getArticlesRecursively(policy, coord);
	    }
	}
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
}
