/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.local.cloudant;

import de.loercher.geomodule.cloudant.CloudantArticleEntity;
import de.loercher.geomodule.cloudant.CloudantArticleEntityMapperImpl;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.ArticleEntityMapper;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.SecurityHelper;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jimmy
 */
public class CloudantArticleEntityMapperTest
{
    private CloudantArticleEntityMapperImpl mapper = new CloudantArticleEntityMapperImpl();
    
    public CloudantArticleEntityMapperTest()
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @Test
    public void testMapFromArticleEntity()
    {
	String id = UUID.randomUUID().toString();
	
	Timestamp now = new Timestamp(new Date().getTime());
	
	String author = "ICH";
	String content = "http://content.me";
	String picture = "www.picture.org/ich.jpg";
	String shortTitle = "short";
	String title = "title";
	Long stamp = now.getTime();
	String revision = "Revision";
	
	final Double latitude = 9.555;
	final Double longitude = 42.888;
	
	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.coordinate(new Coordinate(latitude, longitude))
		.author(author)
		.content(content)
		.picture(picture)
		.shortTitle(shortTitle)
		.timestamp(stamp)
		.title(title)
		.build();
	
	Coordinate coord = new Coordinate(latitude, longitude);
	
	CloudantArticleEntity mappedEntity = mapper.mapFromArticleEntity(entity);
	
	final List<Double> mappedCoords = mappedEntity.getGeometry().getCoordinates();
	assertTrue("First element has to match to latitude (9.555), but is " + mappedCoords.get(1), latitude.equals(mappedCoords.get(1))); 
	assertTrue("Second element has to match longitude (42.888), but is " + mappedCoords.get(0), longitude.equals(mappedCoords.get(0)));
	
	
	assertEquals("Revision number wasn't mapped properly! ", revision, mappedEntity.getRev());
	assertEquals("Author wasn't mapped properly! ", author, mappedEntity.getProperties().get(ArticleEntityMapper.AUTHORTAG));
	assertEquals("ContentURL wasn't mapped properly! ", content, mappedEntity.getProperties().get(ArticleEntityMapper.CONTENTTAG));
	assertEquals("PictureURL wasn't mapped properly! ", picture, mappedEntity.getProperties().get(ArticleEntityMapper.PICTURETAG));
	assertEquals("Short wasn't mapped properly! ", shortTitle, mappedEntity.getProperties().get(ArticleEntityMapper.SHORTTAG));
	assertEquals("Timestamp wasn't mapped properly! ", stamp, mappedEntity.getProperties().get(ArticleEntityMapper.TIMESTAMPTAG));
	assertEquals("Title wasn't mapped properly! ", title, mappedEntity.getProperties().get(ArticleEntityMapper.TITLETAG));
	
	System.out.println("Passwort encrypted: " + new SecurityHelper().obfuscateString("schneider()"));
    }
}
