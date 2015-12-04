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
public class UnauthorizedException extends GeneralGeoModuleException
{

    private final String articleID;
    private final String userID;

    public UnauthorizedException(String pError, String pArticleID, String pUserID, Throwable e)
    {
	super(pError, e);
	articleID = pArticleID;
	userID = pUserID;
    }
    
    public UnauthorizedException(String pError, String pArticleID, String pUserID)
    {
	super(pError);
	articleID = pArticleID;
	userID = pUserID;
    }

    public String getUserID()
    {
	return userID;
    }

    public String getArticleID()
    {
	return articleID;
    }
}
