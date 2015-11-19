/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import java.util.List;

/**
 *
 * @author Jimmy
 */
public class CloudantQueryResponse
{
    private String bookmark;
    private String type;
    private List<CloudantArticleEntity> features;

    public String getBookmark()
    {
	return bookmark;
    }

    public void setBookmark(String bookmark)
    {
	this.bookmark = bookmark;
    }

    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public List<CloudantArticleEntity> getFeatures()
    {
	return features;
    }

    public void setFeatures(List<CloudantArticleEntity> features)
    {
	this.features = features;
    }
}
