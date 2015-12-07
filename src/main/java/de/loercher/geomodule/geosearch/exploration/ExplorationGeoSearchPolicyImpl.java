/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.geosearch.exploration;

import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import de.loercher.geomodule.connector.GeoConnector;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import de.loercher.geomodule.core.LocationHelper;
import de.loercher.geomodule.core.api.GeoSearchEntity;
import de.loercher.geomodule.geosearch.GeoSearchEntityMapper;
import de.loercher.geomodule.geosearch.GeoSearchPolicy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class ExplorationGeoSearchPolicyImpl implements GeoSearchPolicy
{
     private final LocationHelper helper;
     private final GeoSearchEntityMapper mapper;
     private final GeoConnector connector;
    
    @Autowired
    public ExplorationGeoSearchPolicyImpl(LocationHelper pHelper, GeoSearchEntityMapper pMapper, GeoConnector pConnector)
    {
	helper = pHelper;
	mapper = pMapper;
	connector = pConnector;
    }
	    
    @Override
    public List<GeoSearchEntity> fetchLayeredArticles(String userID, Coordinate coord, Integer maxResults) throws TooManyResultsException, GeneralCommunicationException
    {
	DistanceSphere sphere = new DistanceSphere();

	List<IdentifiedArticleEntity> articles = getArticlesRecursively(sphere, coord, maxResults);

	List<GeoSearchEntity> results = new ArrayList<>();

	for (IdentifiedArticleEntity article : articles)
	{
	    Double distance = helper.calculateDistance(coord, article.getEntity().getCoord());
	    Double factor = sphere.getLayerFactor(distance);
	    Integer layer = sphere.getLayerNumber(distance);

	    GeoSearchEntity entity = mapper.mapFromArticleEntity(article, layer, distance, factor);
	    results.add(entity);
	}
	
	return results;
    }
    
    private List<IdentifiedArticleEntity> getArticlesRecursively(DistanceSphere sphere, Coordinate coord, Integer maxResults) throws TooManyResultsException, GeneralCommunicationException
    {
	try
	{
	    return connector.getArticlesNear(coord, sphere.nextRadius(), maxResults);
	} catch (TooManyResultsException e)
	{
	    if (sphere.isLastRadius())
	    {
		throw e;
	    } else
	    {
		return getArticlesRecursively(sphere, coord, maxResults);
	    }
	}
    }
    
}
