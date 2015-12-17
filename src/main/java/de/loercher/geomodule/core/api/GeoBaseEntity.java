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
public class GeoBaseEntity
{
    protected String self;
    protected String content;
    protected String picture;

    protected String title;
    protected String shortTitle;

    protected String user;
    protected String author;
    
    protected String userModule;

    protected Long timestampOfPressEntry;
    // TODO there should also be a ZonedDateTime of the release!
    
    // there can be up to 3 coordinates per entry - future requirement, for the moment only one coordinate
    protected List<Coordinate> coordinates;
    
    protected GeoBaseEntity(){}

    public List<Coordinate> getCoordinates()
    {
	return coordinates;
    }

    public void setCoordinates(List<Coordinate> coordinates)
    {
	this.coordinates = coordinates;
    }

    public String getUserModule()
    {
	return userModule;
    }

    public void setUserModule(String userModule)
    {
	this.userModule = userModule;
    }
    
    public String getSelf()
    {
	return self;
    }

    public void setSelf(String entityURL)
    {
	this.self = entityURL;
    }

    public String getContent()
    {
	return content;
    }

    public void setContent(String contentURL)
    {
	this.content = contentURL;
    }

    public String getPicture()
    {
	return picture;
    }

    public void setPicture(String pictureURL)
    {
	this.picture = pictureURL;
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

    public String getUser()
    {
	return user;
    }

    public void setUser(String userID)
    {
	this.user = userID;
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

    public static class EntityBuilder
    {
	GeoBaseEntity entity = new GeoBaseEntity();

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

	public GeoBaseEntity build()
	{
	    return entity;
	}
    }

}
