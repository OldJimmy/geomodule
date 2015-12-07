/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.local.cloudant;

import de.loercher.geomodule.geosearch.exploration.DistanceSphere;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class GeoSearchPolicyTest
{

    private DistanceSphere policy;

    public GeoSearchPolicyTest()
    {
    }

    @Before
    public void setUp()
    {
	policy = new DistanceSphere();
    }

    @Test
    public void testLayers()
    {
	assertNull("First radius has to be null since there was never fetched one.", policy.getCurrentRadius());

	Integer first = policy.nextRadius();
	Integer second = policy.nextRadius();
	Integer third = policy.nextRadius();

	assertTrue("Second radius has to be smaller than first radius.", second < first);
	assertTrue("Third radius has to be smaller than second radius.", third < second);
    }

    @Test
    public void testResetMethod()
    {
	Integer first = policy.nextRadius();
	assertEquals("The first radius should be the maximum radius!", (Integer) (DistanceSphere.MAX_RADIUS - 1000), first);

	policy.nextRadius();
	policy.nextRadius();
	policy.nextRadius();
	policy.resetMaxRadius();

	assertNull("After reset maxRadius has to be null!", policy.getCurrentRadius());

	assertEquals("The next radius after reset has to be equal to that fetched in first step!", first, policy.nextRadius());
	assertEquals("The getCurrentRadius() method MUST NOT change the used radius!", policy.getCurrentRadius(), policy.getCurrentRadius());
    }

    @Test
    public void testLayerAssignment()
    {
	Double distance = DistanceSphere.MAX_RADIUS - 1000 + 5.0;
	System.out.println("RADIUS: " + policy.nextRadius());

	try
	{
	    policy.getLayerNumber(distance);

	    fail("Distance outside of max radius. There has to be an illegal argument exception!");
	} catch (IllegalArgumentException e)
	{
	}

	try
	{
	    policy.getLayerFactor(distance);

	    fail("Distance outside of max radius. There has to be an illegal argument exception!");
	} catch (IllegalArgumentException e)
	{
	}

	try
	{
	    policy.getLayerFactor(-1.9);

	    fail("The distance is lower than 0. There has to be an illegal argument exception!");
	} catch (IllegalArgumentException e)
	{
	}

	Integer effectiveMax = DistanceSphere.MAX_RADIUS;
	
	distance = (effectiveMax / 2) - 1010.0;
	assertEquals("The layer of the distance has to be the forelast one!", (Integer) (DistanceSphere.LAYER_COUNT - 2), policy.getLayerNumber(distance));
	assertTrue("The factor has to be 2!", isStronglySimilar(2.0, policy.getLayerFactor(distance)));

	distance = (effectiveMax / 2) - 990.0;
	assertEquals("The layer of the distance has to be the last one!", (Integer) (DistanceSphere.LAYER_COUNT - 1), policy.getLayerNumber(distance));
	
	distance = (effectiveMax / 8) - 1010.0;
	assertEquals("The layer of the distance has to be another one!", (Integer) (DistanceSphere.LAYER_COUNT - 4), policy.getLayerNumber(distance));
	assertTrue("The factor has to be 4!", isStronglySimilar(4.0, policy.getLayerFactor(distance)));

	distance = 10.0;
	assertEquals("10.0 meter should be inside the first layer.", new Integer(0), policy.getLayerNumber(distance));
	assertTrue("The factor has to be 5!", isStronglySimilar(5.0, policy.getLayerFactor(distance)));
	
	System.out.println("Next RADIUS is: " + policy.nextRadius());
	distance = (effectiveMax / 2) - 990.0;
	try
	{
	    policy.getLayerFactor(distance);

	    fail("Distance outside of max radius. There has to be an illegal argument exception!");
	} catch (IllegalArgumentException e)
	{
	}
	
	distance = (effectiveMax / 2) - 1100.0;
	assertEquals("The layer of the distance has to be the last one!", (Integer) (DistanceSphere.LAYER_COUNT - 1), policy.getLayerNumber(distance));
	
	distance = (DistanceSphere.MAX_RADIUS / 8) - 1010.0;
	assertEquals("The layer of the distance has to be another one!", (Integer) (DistanceSphere.LAYER_COUNT - 3), policy.getLayerNumber(distance));
    }
    
    private boolean isStronglySimilar(Double a, Double b)
    {
	Double difference = a - b;
	return (Math.abs(difference) < 0.000001);
    }
}
