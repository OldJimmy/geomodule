/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core.api;

import de.loercher.geomodule.commons.Coordinate;
import java.util.List;

/**
 *
 * @author Jimmy
 */
public class GeoSearchEntity extends GeoBaseEntity
{

    private Double distance;
    private Integer layer;
    private Double factor;

    private GeoSearchEntity()
    {
    }

    public Double getDistance()
    {
	return distance;
    }

    public void setDistance(Double distance)
    {
	this.distance = distance;
    }

    public Integer getLayer()
    {
	return layer;
    }

    public void setLayer(Integer layer)
    {
	this.layer = layer;
    }

    public Double getFactor()
    {
	return factor;
    }

    public void setFactor(Double layerMultiplier)
    {
	this.factor = layerMultiplier;
    }
    
    public static class EntityBuilder
    {

	GeoSearchEntity entity = new GeoSearchEntity();

	public EntityBuilder()
	{
	}

	public EntityBuilder self(String pEntityURL)
	{
	    entity.self = pEntityURL;
	    return this;
	}

	public EntityBuilder content(String pContentURL)
	{
	    entity.content = pContentURL;
	    return this;
	}
	
	public EntityBuilder articleID(String pArticleID)
	{
	    entity.articleID = pArticleID;
	    return this;
	}
	
	public EntityBuilder rating(String pRatingURL)
	{
	    entity.rating = pRatingURL;
	    return this;
	}

	public EntityBuilder picture(String pPictureURL)
	{
	    entity.picture = pPictureURL;
	    return this;
	}

	public EntityBuilder title(String pTitle)
	{
	    entity.title = pTitle;
	    return this;
	}

	public EntityBuilder shortTitle(String pShortTitle)
	{
	    entity.shortTitle = pShortTitle;
	    return this;
	}

	public EntityBuilder author(String pAuthor)
	{
	    entity.author = pAuthor;
	    return this;
	}
	
	public EntityBuilder user(String pUser)
	{
	    entity.user = pUser;
	    return this;
	}
	
	public EntityBuilder userModule(String pUserModule)
	{
	    entity.userModule = pUserModule;
	    return this;
	}

	public EntityBuilder timestamp(Long pTimestamp)
	{
	    entity.timestampOfPressEntry = pTimestamp;
	    return this;
	}

	public EntityBuilder coordinates(List<Coordinate> pCoord)
	{
	    entity.coordinates = pCoord;
	    return this;
	}

	public EntityBuilder distance(Double pDistance)
	{
	    entity.distance = pDistance;
	    return this;
	}

	public EntityBuilder layer(Integer pLayer)
	{
	    entity.layer = pLayer;
	    return this;
	}

	public EntityBuilder factor(Double pMultiplier)
	{
	    entity.factor = pMultiplier;
	    return this;
	}

	public GeoSearchEntity build()
	{
	    return entity;
	}
    }
}
