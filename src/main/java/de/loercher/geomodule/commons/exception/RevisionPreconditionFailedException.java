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

    private final String articleID;
    private final String triedRevision;

    public RevisionPreconditionFailedException(String pError, String pArticleID, String pTriedRevision, Throwable e)
    {
	super(pError, e);
	articleID = pArticleID;
	triedRevision = pTriedRevision;
    }

    public String getTriedRevision()
    {
	return triedRevision;
    }

    public String getArticleID()
    {
	return articleID;
    }

}
