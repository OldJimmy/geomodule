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
public class IdentifiedArticleEntity
{
    private String id;
    private String rev = null;
    
    private ArticleEntity entity;

    public IdentifiedArticleEntity(String pId)
    {
	id = pId;
    }
    
    public IdentifiedArticleEntity(String pId, String pRev)
    {
	id = pId;
	rev = pRev;
    }
    
    public String getId()
    {
	return id;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public String getRev()
    {
	return rev;
    }

    public void setRev(String rev)
    {
	this.rev = rev;
    }

    public ArticleEntity getEntity()
    {
	return entity;
    }

    public void setEntity(ArticleEntity entity)
    {
	if (entity.getReference() == null)
	{
	    entity.setReferenceID(id);
	}
	
	this.entity = entity;
    }
    
}
