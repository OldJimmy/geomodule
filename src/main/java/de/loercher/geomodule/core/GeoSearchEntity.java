/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core;

import de.loercher.geomodule.commons.Coordinate;

/**
 *
 * @author Jimmy
 */
public class GeoSearchEntity
{

    private String entityURL;
    private String contentURL;
    private String pictureURL;

    private String title;
    private String shortTitle;

    private String userID;
    private String author;

    private Long timestampOfPressEntry;

    private Coordinate coord;
    private Double distance;

    private Integer layer;
    private Double layerMultiplier;
    
    private GeoSearchEntity(){}

    public String getEntityURL()
    {
	return entityURL;
    }

    public void setEntityURL(String entityURL)
    {
	this.entityURL = entityURL;
    }

    public String getContentURL()
    {
	return contentURL;
    }

    public void setContentURL(String contentURL)
    {
	this.contentURL = contentURL;
    }

    public String getPictureURL()
    {
	return pictureURL;
    }

    public void setPictureURL(String pictureURL)
    {
	this.pictureURL = pictureURL;
    }

    public String getTitle()
    {
	return title;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public String getShortTitle()
    {
	return shortTitle;
    }

    public void setShortTitle(String shortTitle)
    {
	this.shortTitle = shortTitle;
    }

    public String getUserID()
    {
	return userID;
    }

    public void setUserID(String userID)
    {
	this.userID = userID;
    }

    public String getAuthor()
    {
	return author;
    }

    public void setAuthor(String author)
    {
	this.author = author;
    }

    public Long getTimestampOfPressEntry()
    {
	return timestampOfPressEntry;
    }

    public void setTimestampOfPressEntry(Long timestampOfPressEntry)
    {
	this.timestampOfPressEntry = timestampOfPressEntry;
    }

    public Coordinate getCoord()
    {
	return coord;
    }

    public void setCoord(Coordinate coord)
    {
	this.coord = coord;
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

    public Double getLayerMultiplier()
    {
	return layerMultiplier;
    }

    public void setLayerMultiplier(Double layerMultiplier)
    {
	this.layerMultiplier = layerMultiplier;
    }

    public static class EntityBuilder
    {
	GeoSearchEntity entity = new GeoSearchEntity();

	public EntityBuilder()
	{
	}

	public EntityBuilder entity(String pEntityURL)
	{
	    entity.entityURL = pEntityURL;
	    return this;
	}

	public EntityBuilder content(String pContentURL)
	{
	    entity.contentURL = pContentURL;
	    return this;
	}

	public EntityBuilder picture(String pPictureURL)
	{
	    entity.pictureURL = pPictureURL;
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

	public EntityBuilder timestamp(Long pTimestamp)
	{
	    entity.timestampOfPressEntry = pTimestamp;
	    return this;
	}

	public EntityBuilder coordinate(Coordinate pCoord)
	{
	    entity.coord = pCoord;
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

	public EntityBuilder layerMultiplier(Double pMultiplier)
	{
	    entity.layerMultiplier = pMultiplier;
	    return this;
	}

	public GeoSearchEntity build()
	{
	    return entity;
	}
    }

}
