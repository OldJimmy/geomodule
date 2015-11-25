/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.commons.exception;

/**
 *
 * @author Jimmy
 */
public class TooManyResultsException extends GeneralGeoModuleException
{
    private Integer presentCount;
    private Integer allowedCount;
    
    public TooManyResultsException(Integer pPresentCount, Integer pAllowedCount)
    {
	super("There were too many results. Allowed: " + pAllowedCount + ", present: " + pPresentCount);
	
	presentCount = pPresentCount;
	allowedCount = pAllowedCount;
	
	
    }
    
    public TooManyResultsException(String pError, Integer pPresentCount, Integer pAllowedCount)
    {
	super(pError);

	presentCount = pPresentCount;
	allowedCount = pAllowedCount;
    }
    
    public TooManyResultsException(String pError, Throwable e)
    {
	super(pError, e);
    }

    public Integer getPresentCount()
    {
	return presentCount;
    }

    public Integer getAllowedCount()
    {
	return allowedCount;
    }
}