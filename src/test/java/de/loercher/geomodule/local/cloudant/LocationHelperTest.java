/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.local.cloudant;

import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.core.LocationHelper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class LocationHelperTest
{
    private LocationHelper helper;
    
    public LocationHelperTest()
    {
    }
    
    @Before
    public void setUp()
    {
	helper = new LocationHelper();
    }
    
    @Test
    public void testDistance()
    {
	Coordinate from = new Coordinate(49.17271, 9.79864);
	Coordinate to = new Coordinate(49.18169, 9.79778);
	
	Double fromTo = helper.calculateDistance(from, to);
	Double toFrom = helper.calculateDistance(to, from);
	
	assertTrue("Distances using self endpoints in different order has to return same value. ", isStronglySimilar(toFrom, fromTo));
	System.out.println(fromTo / 1000);
	
	assertTrue("Distance should be roundabout 1 km, but is: " + fromTo + " km", isSimilar(1000.0, (fromTo)));
	
	assertTrue("Distance should always be greater than 0!", helper.calculateDistance(from, to) > 0);
	assertTrue("Distance should always be greater than 0!", helper.calculateDistance(to, from) > 0);
	
	assertTrue("Distance from a point to itself should be roundabout 0", isStronglySimilar(helper.calculateDistance(from, from), 0.0));
	
	from = new Coordinate(49.37342 , 8.65332);
	to = new Coordinate(49.18169, 9.79778);
	
	System.out.println(helper.calculateDistance(from, to));
	assertTrue("Distance should be roundabout 85.7394km! ", isSimilar(helper.calculateDistance(from, to), 85739.4));
    }
    
    private boolean isSimilar(Double a, Double b)
    {
	Double difference = Math.abs(a - b);
	return (Math.abs(difference / (a+b)) < 0.001);
    }
    
    private boolean isStronglySimilar(Double a, Double b)
    {
	Double difference = a - b;
	return (Math.abs(difference) < 0.000001);
    }
}
