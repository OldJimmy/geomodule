/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core;

import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import de.loercher.geomodule.core.api.GeoBaseEntity;
import de.loercher.geomodule.core.api.GeoSearchEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class GeoSearchEntityMapper
{
    public static String BASE_URL = "http://localhost:8080/geo/";
    
    public GeoSearchEntity mapFromArticleEntity(IdentifiedArticleEntity article, Integer layer, Double distance, Double factor)
    {
	ArticleEntity core = article.getEntity();
	Coordinate coord = core.getCoord();
	
	List<Coordinate> coords = new ArrayList<>();
	coords.add(coord);
	
	GeoSearchEntity.EntityBuilder builder = new GeoSearchEntity.EntityBuilder();
	builder = builder.self(BASE_URL + article.getId())
		.title(core.getTitle())
		.author(core.getAuthor())
		.user(core.getUserURL())
		.shortTitle(core.getShortTitle())
		.content(core.getContentURL())
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
	builder = builder.self(BASE_URL + article.getId())
		.title(core.getTitle())
		.author(core.getAuthor())
		.user(core.getUserURL())
		.shortTitle(core.getShortTitle())
		.content(core.getContentURL())
		.picture(core.getPictureURL())
		.timestamp(core.getTimestampOfPressEntry())
		.coordinates(coords);
	
	return builder.build();
    }
    
}
