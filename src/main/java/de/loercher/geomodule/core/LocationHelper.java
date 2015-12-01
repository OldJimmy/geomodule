/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core;

import de.loercher.geomodule.commons.Coordinate;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class LocationHelper
{

    public Double calculateDistance(Coordinate from, Coordinate to)
    {
	Double lon1 = from.getLongitude();
	Double lon2 = to.getLongitude();

	Double lat1 = from.getLatitude();
	Double lat2 = to.getLatitude();

	Double theta = lon1 - lon2;
	Double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	dist = Math.acos(dist);
	dist = rad2deg(dist);
	dist = dist * 60 * 1.1515;
	dist = dist * 1.609344;

	// the distance is messured in meter
	return (dist * 1000);
    }

    private double deg2rad(double deg)
    {
	return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad)
    {
	return (rad * 180 / Math.PI);
    }
}
