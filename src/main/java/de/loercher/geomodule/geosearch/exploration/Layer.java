/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.geosearch.exploration;

/**
 *
 * @author Jimmy
 */
public class Layer
{
    private Integer number;
    private Double factor;
    
    public Layer(Integer pNumber, Double pFactor)
    {
	number = pNumber;
	factor = pFactor;
    }

    public Integer getNumber()
    {
	return number;
    }

    public void setNumber(Integer number)
    {
	this.number = number;
    }

    public Double getFactor()
    {
	return factor;
    }

    public void setFactor(Double factor)
    {
	this.factor = factor;
    }

}
