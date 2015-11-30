/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.JSONParseException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
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
    private final ObjectMapper objectMapper;

    @Autowired
    public GeoController(GeoConnector pConnector, ObjectMapper pObjectMapper)
    {
	connector = pConnector;
	objectMapper = pObjectMapper;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> fetchArticlesAround(@RequestParam Double lat, @RequestParam Double lon) throws GeneralCommunicationException, TooManyResultsException
    {
	Coordinate coord = new Coordinate(lat, lon);
	GeoSearchPolicy policy = new GeoSearchPolicy();

	List<IdentifiedArticleEntity> articles = getArticlesRecursively(policy, coord);

	try
	{
	    return new ResponseEntity<>(objectMapper.writeValueAsString(articles), HttpStatus.OK);
	} catch (JsonProcessingException ex)
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
