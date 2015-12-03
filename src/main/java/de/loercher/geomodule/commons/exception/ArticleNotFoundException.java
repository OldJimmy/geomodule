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
public class ArticleNotFoundException extends GeneralGeoModuleException
{
    private final String articleID;

    public ArticleNotFoundException(String pError, String pArticleID, Throwable e)    {
	super(pError, e);
	articleID = pArticleID;
    }
    
    public String getArticleID()
    {
	return articleID;
    }
}
