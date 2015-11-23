/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.integration;

import de.loercher.geomodule.cloudant.CloudantArticleEntityMapperImpl;
import de.loercher.geomodule.cloudant.CloudantGeoConnectorImpl;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.GeoModuleProperties;
import de.loercher.geomodule.commons.SecurityHelper;
import de.loercher.geomodule.commons.exception.ArticleConflictException;
import de.loercher.geomodule.commons.exception.ArticleNotFoundException;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.RevisionPreconditionFailedException;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class CloudantGeoConnectorImplITest
{

    private CloudantGeoConnectorImpl connector;
    private GeoModuleProperties properties;
    public final static Coordinate FRANKFURT = new Coordinate(new Double(50.12221), new Double(8.712190));
    public final static Coordinate BERLIN = new Coordinate(new Double(52.50440), new Double(13.40335));
    public final static Coordinate STUTTGART = new Coordinate(new Double(48.78728), new Double(9.181856));
    public final static Coordinate LUDWIGSBURG = new Coordinate(new Double(48.89470), new Double(9.193227));

    public CloudantGeoConnectorImplITest()
    {
    }

    @Before
    public void setUp()
    {
	properties = new GeoModuleProperties(new SecurityHelper());

	connector = new CloudantGeoConnectorImpl(properties, new CloudantArticleEntityMapperImpl());
    }

    @Test
    public void testInsertion()
    {
	String id = "Frankfurt";
	System.out.println("UUID zu testInsertion: " + id);

	Timestamp now = new Timestamp(new Date().getTime());

	String author = "ich";
	String content = "http://www.content.de";
	String picture = "www.picture.de/hallo.jpg";
	String shortTitle = "kurz";
	String title = "testInsertion";
	Long stamp = now.getTime();

	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.coordinate(FRANKFURT)
		.author(author)
		.content(content)
		.picture(picture)
		.shortTitle(shortTitle)
		.timestamp(stamp)
		.title(title)
		.build();

	try
	{
	    connector.saveArticle(entity, id);
	} catch (ArticleConflictException ex)
	{
	    /*
	     Probably the point was already created in a test before
	     */
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}

	IdentifiedArticleEntity result = null;
	ArticleEntity resultEntity = null;
	try
	{
	    result = connector.getArticle(id);
	    resultEntity = result.getEntity();
	} catch (ArticleNotFoundException ex)
	{
	    fail("Article not found.");
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}

	assertNotNull("Entity either wasn't inserted correctly or couldn't be retrieved correctly. ID: " + id, result);

	assertEquals("Author has changed!", author, resultEntity.getAuthor());
	assertEquals("Content has changed!", content, resultEntity.getContentURL());
	assertEquals("Picture has changed!", picture, resultEntity.getPictureURL());
	assertEquals("Short text has changed!", shortTitle, resultEntity.getShortTitle());
	assertEquals("Title has changed!", title, resultEntity.getTitle());
	assertEquals("Coordinates have changed!", FRANKFURT, resultEntity.getCoord());
	assertEquals("Reference should match id!", id, resultEntity.getReference());
	
	/**
	 * Check if duplicate is detected using saveArticle
	 */
	System.out.println("UUID zu testUpdateNotExistingArticle: " + id);

	ArticleEntity newEntity = new ArticleEntity.ArticleEntityBuilder().build();

	try
	{
	    connector.saveArticle(newEntity, id);
	    fail("The article should not have been successfully saved in testUpdateNotExistingArticle.");
	} catch (ArticleConflictException ex)
	{
	} catch (GeneralCommunicationException ex)
	{
	    fail("There happend an GeneralCommunication exception!");
	}
	
	/**
	 * Check if wrong revision number is detected
	 */
	// well formed cloudant revision number
	String revision = "1-87a130b51cc32a84a6405c47c76c8713";
	
	/**
	 * Check if update with not available article id gets the expected exception
	 */
	ArticleEntity secondEntity = new ArticleEntity.ArticleEntityBuilder()
		.coordinate(BERLIN)
		.build();
	
	try
	{
	    connector.updateArticle(secondEntity, id, revision);
	    fail("There should have been an RevisionPreconditionFailedException!");
	} catch (RevisionPreconditionFailedException ex)
	{
	} catch (GeneralCommunicationException ex)
	{
	    System.out.println("INSERTEXC:" + ex);
	    fail("There should have been an RevisionPreconditionFailedException but was GeneralCommunicationException!");
	}
    }

    @Test
    public void testInsertionWithNullValues()
    {
	String id = "Berlin";
	System.out.println("UUID zu testInsertionWithNullValues: " + id);

	Timestamp now = new Timestamp(new Date().getTime());

	String picture = "www.picture.de/hallo.jpg";
	String shortTitle = null;
	String title = "testInsertionWithNullValues";
	String reference = "63401";
	Long stamp = now.getTime();

	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.coordinate(BERLIN)
		.picture(picture)
		.shortTitle(shortTitle)
		.timestamp(stamp)
		.title(title)
		.reference(reference)
		.build();

	try
	{
	    connector.saveArticle(entity, id);
	} catch (ArticleConflictException ex)
	{
	    /*
	     Probably the point was already created in a test before
	     */
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}

	ArticleEntity result = null;
	try
	{
	    result = connector.getArticle(id).getEntity();
	} catch (ArticleNotFoundException ex)
	{
	    fail("Article not found.");
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}

	assertNotNull("Entity either wasn't inserted correctly or couldn't be retrieved correctly. ID: " + id, result);

	assertEquals("Picture has changed!", picture, result.getPictureURL());
	assertNull("Short text should be null!", result.getShortTitle());
	assertEquals("Title has changed!", title, result.getTitle());
	assertEquals("Coordinates have changed!", BERLIN, result.getCoord());
	assertEquals("Reference has changed!", reference, result.getReference());
    }

    @Test
    public void testNearMethod()
    {

	String id = "Stuttgart";
	System.out.println("UUID zu testNearMethod: " + id);

	Timestamp now = new Timestamp(new Date().getTime());

	String picture = "dearth.png";
	String shortTitle = "Hopfach Center";
	String title = "testNearMethod";
	Long stamp = now.getTime();

	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.coordinate(STUTTGART)
		.picture(picture)
		.shortTitle(shortTitle)
		.timestamp(stamp)
		.title(title)
		.build();

	try
	{
	    connector.saveArticle(entity, id);
	} catch (ArticleConflictException ex)
	{
	    /*
	     Probably the point was already created in a test before
	     */
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}

	boolean found = false;

	/*
	 Distance should be 12km. Checked by using the tool distance tool on
	 http://www.kompf.de/trekka/distance.php

	 the tolerance in this scenario shouldn't be above 100 meter (nearly 1%).
	 */
	List<IdentifiedArticleEntity> result = null;
	try
	{
	    result = connector.getArticlesNear(LUDWIGSBURG, 12100);

	    for (IdentifiedArticleEntity article : result)
	    {
		if (article.getId().equals(id))
		{
		    found = true;
		}
	    }

	    assertFalse("Entry shouldn't have been found in testNearMethod!", found);
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}

	try
	{
	    result = connector.getArticlesNear(LUDWIGSBURG, 11900);
	    found = false;

	    for (IdentifiedArticleEntity article : result)
	    {
		if (article.getId().equals(id))
		{
		    found = true;
		}
	    }

	    assertTrue("Entry shouldn't have been found in testNearMethod!", found);
	} catch (GeneralCommunicationException ex)
	{
	    fail("There was a general exception");
	}
    }

    @Test
    public void testUpdateExceptions()
    {
	String id = "belasdfons";
	// well formed cloudant revision number
	String revision = "1-87a130b51cc32a84a6405c47c76c8713";
	
	/**
	 * Check if update with not available article id gets the expected exception
	 */
	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.coordinate(BERLIN)
		.build();
	
	try
	{
	    connector.updateArticle(entity, id, revision);
	    fail("There should have been an RevisionPreconditionFailedException because article not existing already!");
	} catch (RevisionPreconditionFailedException ex)
	{
	} catch (GeneralCommunicationException ex)
	{
	    fail("There should have been an RevisionPreconditionFailedException because article not existing already, but ther was a GeneralCommunicationException!");
	}
    }
}
