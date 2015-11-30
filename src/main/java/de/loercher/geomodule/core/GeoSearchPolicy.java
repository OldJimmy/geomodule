/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jimmy
 */
public class GeoSearchPolicy
{
    public static final Integer LAYER_COUNT = 5;
    public static final Integer MAX_RADIUS = 32000;

    private final List<Integer> maxRadiusTiers = new ArrayList<>();
    private final List<Layer> actualLayers = new ArrayList<>();
    
    private Integer currentRadius = null;

    public GeoSearchPolicy()
    {
	initializeRadiusTiers();

	initializeActualLayer();
    }

    private void initializeRadiusTiers()
    {
	maxRadiusTiers.clear();
	
	maxRadiusTiers.add(MAX_RADIUS - 1000);	   //31000 
	maxRadiusTiers.add(MAX_RADIUS / 2 - 1000); //15000
	maxRadiusTiers.add(MAX_RADIUS / 4 - 1000);  //7000
	maxRadiusTiers.add(MAX_RADIUS / 8 - 1000);  //3000
	maxRadiusTiers.add(MAX_RADIUS / 16 - 1000); //1000
	maxRadiusTiers.add(MAX_RADIUS / 32 - 1000); //0
    }
    
    private void initializeActualLayer()
    {
	for (int i = 0; i < LAYER_COUNT; i++)
	{
	    Layer item = new Layer(i, new Double(LAYER_COUNT - i));
	    actualLayers.add(item);
	}
    }

    private Layer getLayerByDistance(Double distance)
    {
	if (distance < 0) throw new IllegalArgumentException("The distance cannot be less than 0!");
	
	Integer indexOfLayer = new Double(Math.log( (distance / currentRadius * 31) + 1) / Math.log(2)).intValue();
	
	if (indexOfLayer >= actualLayers.size())
	{
	    throw new IllegalArgumentException("The distance of the point cannot be bigger than the max radius.");
	}
	return actualLayers.get(indexOfLayer);
    }

    public Double getLayerFactor(Double distance) 
    {
	return getLayerByDistance(distance).getFactor();
    }

    public Integer getLayerNumber(Double distance) 
    {
	if (actualLayers.isEmpty())
	{
	    initializeActualLayer();
	}
	
	return getLayerByDistance(distance).getNumber();
    }

    public Integer nextRadius()
    {
	if (maxRadiusTiers.size() > 1)
	{
	    currentRadius = maxRadiusTiers.remove(0);
	    actualLayers.clear();
	    return currentRadius;
	} else
	{
	    return maxRadiusTiers.get(0);
	}
    }

    public Integer getCurrentRadius()
    {
	return currentRadius;
    }

    public Integer getMaxResultCount()
    {
	return 100;
    }

    public boolean isLastRadius()
    {
	return (maxRadiusTiers.size() == 1);
    }

    public void resetMaxRadius()
    {
	currentRadius = null;
	actualLayers.clear();
	initializeRadiusTiers();
    }
}
