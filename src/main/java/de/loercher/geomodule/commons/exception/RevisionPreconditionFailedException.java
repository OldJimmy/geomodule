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
public class RevisionPreconditionFailedException extends GeneralGeoModuleException
{

    public RevisionPreconditionFailedException(String pError, Throwable e)
    {
	super(pError, e);
    }
    
}
