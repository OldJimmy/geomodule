/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import java.util.Map;

/**
 *
 * @author Jimmy
 * 
 * example entity:
 * 
 * {
  "_id": "1-84e269e0838ebe01a5da0165c66af912",
  "_rev": "5-f57a460ef296b4663dc44d6b1a9ac41e",
  "geometry": {
    "coordinates": [
      9.856485,
      49.178897
    ],
    "type": "Point"
  },
  "properties": {
	"reference": "Stuttgart",
	"author": "michi",
	"userid": "silk14ifjis",
	"short": "Hopfach Center",
	"title": "testNearMethod",
	"picture": "dearth.png",
	"timestamp": 1447775932108
    },
  "type": "Feature"
}
 */
public class CloudantArticleEntity
{
    private String _id = null;
    private String _rev = null;
    
    private Geometry geometry;
    private Map<String, Object> properties;
    
    private String type;

    public String getId()
    {
	return _id;
    }

    public void setId(String _id)
    {
	this._id = _id;
    }

    public String getRev()
    {
	return _rev;
    }

    public void setRev(String _rev)
    {
	this._rev = _rev;
    }

    public Geometry getGeometry()
    {
	return geometry;
    }

    public void setGeometry(Geometry geometry)
    {
	this.geometry = geometry;
    }

    public Map<String, Object> getProperties()
    {
	return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
	this.properties = properties;
    }

    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }
    
    
}
