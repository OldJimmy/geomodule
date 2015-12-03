/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.commons;

import java.util.Objects;

/**
 *
 * @author Jimmy
 */
public class Coordinate
{
    private Double latitude;
    private Double longitude;
    
    public Coordinate(){}
    
    public Coordinate(Double pLatitude, Double pLongitude)
    {
	latitude = pLatitude;
	longitude = pLongitude;
    }

    public Double getLatitude()
    {
	return latitude;
    }

    public void setLatitude(Double latitude)
    {
	this.latitude = latitude;
    }

    public Double getLongitude()
    {
	return longitude;
    }

    public void setLongitude(Double longitude)
    {
	this.longitude = longitude;
    }
    
    @Override
    public boolean equals(Object obj)
    {
	if (!(obj instanceof Coordinate)) return false;
	
	Coordinate other = (Coordinate) obj;
	
	if (!(other.latitude.equals(this.latitude))) return false;
	return other.longitude.equals(this.longitude);
    }

    @Override
    public int hashCode()
    {
	int hash = 7;
	hash = 41 * hash + Objects.hashCode(this.latitude);
	hash = 41 * hash + Objects.hashCode(this.longitude);
	return hash;
    }
}
