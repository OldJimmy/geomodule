/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.connector;

/**
 *
 * @author Jimmy
 */
public class ArticleIdentifier
{
    private String id;
    private String rev = null;

    public ArticleIdentifier(String pId)
    {
	id = pId;
    }
    
    public ArticleIdentifier(String pId, String pRev)
    {
	rev = pRev;
    }
    
    public String getId()
    {
	return id;
    }

    public String getRev()
    {
	return rev;
    }

}
