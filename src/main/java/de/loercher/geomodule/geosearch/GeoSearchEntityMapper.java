/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.geosearch;

import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.GeoModuleProperties;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import de.loercher.geomodule.core.api.GeoBaseEntity;
import de.loercher.geomodule.core.api.GeoSearchEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class GeoSearchEntityMapper
{

    public final String BASE_URL;
    
    @Autowired
    public GeoSearchEntityMapper(GeoModuleProperties pProp)
    {
	BASE_URL = pProp.getProp().getProperty("baseUrl");
    }
    
    public String mapIDToURL(String id)
    {
	return BASE_URL + "/geo/" + id;
    }
    
    public ArticleEntity mapToArticleEntity(GeoBaseEntity entity)
    {
	Coordinate coord;
	if (entity.getCoordinates() == null || entity.getCoordinates().isEmpty())
	{
	    coord = null;
	} else
	{
	    coord = entity.getCoordinates().get(0);
	}

	ArticleEntity.ArticleEntityBuilder builder = new ArticleEntity.ArticleEntityBuilder();
	builder = builder.title(entity.getTitle())
		.author(entity.getAuthor())
		.user(entity.getAuthor())
		.userModule(entity.getUserModule())
		.shortTitle(entity.getShortTitle())
		.content(entity.getContent())
		.rating(entity.getRating())
		.timestamp(entity.getTimestampOfPressEntry())
		.coordinate(coord);
	
	return builder.build();
    }

    public GeoSearchEntity mapFromArticleEntity(IdentifiedArticleEntity article, Integer layer, Double distance, Double factor)
    {
	ArticleEntity core = article.getEntity();
	Coordinate coord = core.getCoord();

	List<Coordinate> coords = new ArrayList<>();
	coords.add(coord);

	GeoSearchEntity.EntityBuilder builder = new GeoSearchEntity.EntityBuilder();
	builder = builder.self(mapIDToURL(article.getId()))
		.title(core.getTitle())
		.author(core.getAuthor())
		.user(core.getUserID())
		.userModule(core.getUserModuleURL())
		.shortTitle(core.getShortTitle())
		.content(core.getContentURL())
		.rating(core.getRatingURL())
		.picture(core.getPictureURL())
		.timestamp(core.getTimestampOfPressEntry())
		.coordinates(coords)
		.distance(distance)
		.layer(layer)
		.factor(factor);

	return builder.build();
    }

    public GeoBaseEntity mapFromArticleEntity(IdentifiedArticleEntity article)
    {
	ArticleEntity core = article.getEntity();
	Coordinate coord = core.getCoord();

	List<Coordinate> coords = new ArrayList<>();
	coords.add(coord);

	GeoBaseEntity.EntityBuilder builder = new GeoBaseEntity.EntityBuilder();
	builder = builder.self(mapIDToURL(article.getId()))
		.title(core.getTitle())
		.author(core.getAuthor())
		.user(core.getUserID())
		.userModule(core.getUserModuleURL())
		.shortTitle(core.getShortTitle())
		.content(core.getContentURL())
		.rating(core.getRatingURL())
		.picture(core.getPictureURL())
		.timestamp(core.getTimestampOfPressEntry())
		.coordinates(coords);

	return builder.build();
    }

}
