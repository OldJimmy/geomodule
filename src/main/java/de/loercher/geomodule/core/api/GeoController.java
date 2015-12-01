/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core.api;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.ArticleNotFoundException;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import de.loercher.geomodule.core.GeoSearchEntityMapper;
import de.loercher.geomodule.core.GeoSearchPolicy;
import de.loercher.geomodule.core.LocationHelper;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    
    @RequestMapping(value = "/{articleID}")
    public ResponseEntity<String> getArticle(@PathVariable String articleID) throws ArticleNotFoundException, GeneralCommunicationException
    {
	IdentifiedArticleEntity entity = connector.getArticle(articleID);
	GeoBaseEntity result = mapper.mapFromArticleEntity(entity);
	
	try
	{
	    Gson gson = new Gson();
	    
	    // convert java object to JSON format,
	    // and returned as JSON formatted string
	    String json = gson.toJson(result);

	    return new ResponseEntity<>(json, HttpStatus.OK);
	} catch (JsonParseException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("There was an unexpected parsing exception in fetchArticlesAround().", ex);
	    log.error(e.getLoggingString());
	    throw e;
	}
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

	try
	{
	    Gson gson = new Gson();
	    
	    // convert java object to JSON format,
	    // and returned as JSON formatted string
	    String json = gson.toJson(results);

	    return new ResponseEntity<>(json, HttpStatus.OK);
	} catch (JsonParseException ex)
	{
	    GeneralCommunicationException e = new GeneralCommunicationException("There was an unexpected parsing exception in fetchArticlesAround().", ex);
	    log.error(e.getLoggingString());
	    throw e;
	}
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
}
