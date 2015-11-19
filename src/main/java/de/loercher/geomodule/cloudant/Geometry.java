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
 * 
 * example entity:
 * 
 * "geometry": {
    "coordinates": [
      9.856485,
      49.178897
    ],
    "type": "Point"
  }
 */
public class Geometry
{
    private List<Double> coordinates;
    private String type;

    public Geometry() {}
    
    public Geometry(List<Double> coordinates, String type)
    {
	this.coordinates = coordinates;
	this.type = type;
    }

    public List<Double> getCoordinates()
    {
	return coordinates;
    }

    public void setCoordinates(List<Double> coordinates)
    {
	this.coordinates = coordinates;
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
