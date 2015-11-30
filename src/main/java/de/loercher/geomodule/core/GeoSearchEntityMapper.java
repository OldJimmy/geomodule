/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core;

import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;

/**
 *
 * @author Jimmy
 */
public class GeoSearchEntityMapper
{
    public GeoSearchEntity mapFromArticleEntity(IdentifiedArticleEntity article, Coordinate coord, Integer layerRadius)
    {
	ArticleEntity core = article.getEntity();
	
	GeoSearchEntity.EntityBuilder builder = new GeoSearchEntity.EntityBuilder();
	builder = builder.author(core.getAuthor())
		.content(core.getContentURL())
		.coordinate(core.getCoord())
		.picture(core.getPictureURL())
		.shortTitle(core.getShortTitle())
		.title(core.getTitle())
		.timestamp(core.getTimestampOfPressEntry());
	
//	Double distance
		
	return null;
    }
}
