/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.connector;

import de.loercher.geomodule.commons.Coordinate;

/**
 *
 * @author Jimmy
 */
public class ArticleEntity
{
    /*
	Possible future enhancement:
	If there are multiple coordinates of one article they share the same referenceID
	By default the referenceID is the same as the _id.
    */ 
    private String reference;

    private String pictureURL;
    private String author;
    private String userURL;
    private String title;
    private String shortTitle;
    private String contentURL;
    private Long timestampOfPressEntry;
    private Coordinate coord;

    private ArticleEntity()
    {
    }

    public String getUserURL()
    {
	return userURL;
    }

    public void setUserURL(String userId)
    {
	this.userURL = userId;
    }

    public void setReferenceID(String referenceID)
    {
	this.reference = referenceID;
    }

    public void setPictureURL(String pictureURL)
    {
	this.pictureURL = pictureURL;
    }

    public void setAuthor(String author)
    {
	this.author = author;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public void setShortTitle(String shortTitle)
    {
	this.shortTitle = shortTitle;
    }

    public void setContentURL(String contentURL)
    {
	this.contentURL = contentURL;
    }

    public void setTimestampOfPressEntry(Long timestampOfPressEntry)
    {
	this.timestampOfPressEntry = timestampOfPressEntry;
    }

    public void setCoord(Coordinate coord)
    {
	this.coord = coord;
    }
    
    public String getReference()
    {
	return reference;
    }
    
    public Coordinate getCoord()
    {
	return coord;
    }

    public String getPictureURL()
    {
	return pictureURL;
    }

    public String getAuthor()
    {
	return author;
    }

    public String getTitle()
    {
	return title;
    }

    public String getShortTitle()
    {
	return shortTitle;
    }

    public String getContentURL()
    {
	return contentURL;
    }

    public Long getTimestampOfPressEntry()
    {
	return timestampOfPressEntry;
    }

    public static class ArticleEntityBuilder
    {

	private ArticleEntity article;

	public ArticleEntityBuilder()
	{
	    article = new ArticleEntity();
	}

	public ArticleEntityBuilder reference(String pReference)
	{
	    article.reference = pReference;
	    return this;
	}
	
	public ArticleEntityBuilder picture(String pPictureURL)
	{
	    article.pictureURL = pPictureURL;
	    return this;
	}

	public ArticleEntityBuilder author(String pAuthor)
	{
	    article.author = pAuthor;
	    return this;
	}
	
	public ArticleEntityBuilder user(String pUserId)
	{
	    article.userURL = pUserId;
	    return this;
	}

	public ArticleEntityBuilder title(String pTitle)
	{
	    article.title = pTitle;
	    return this;
	}

	public ArticleEntityBuilder shortTitle(String pShortTitle)
	{
	    article.shortTitle = pShortTitle;
	    return this;
	}

	public ArticleEntityBuilder content(String pContent)
	{
	    article.contentURL = pContent;
	    return this;
	}

	public ArticleEntityBuilder timestamp(Long pTimestamp)
	{
	    article.timestampOfPressEntry = pTimestamp;
	    return this;
	}
	
	public ArticleEntityBuilder coordinate(Coordinate pCoord)
	{
	    article.coord = pCoord;
	    return this;
	}

	public ArticleEntity build()
	{
	    return article;
	}
    }
}
