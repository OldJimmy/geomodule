/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.ArticleEntityMapper;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.JSONParseException;
import de.loercher.geomodule.connector.IdentifiedArticleEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author Jimmy
 */
@Component
public class CloudantArticleEntityMapperImpl implements ArticleEntityMapper<CloudantArticleEntity>
{

    @Override
    public CloudantArticleEntity mapFromArticleEntity(ArticleEntity entity)
    {
	CloudantArticleEntity cloudantEntity = new CloudantArticleEntity();

	/*
	 GeoJSON format dictates that the coordinates have to be represented using (longitude, latitude)
	 --- against common human usage
	 */
	if (entity.getCoord() != null)
	{
	    List<Double> coords = new ArrayList<>();
	    coords.add(entity.getCoord().getLongitude());
	    coords.add(entity.getCoord().getLatitude());

	    assert (coords.get(1).equals(entity.getCoord().getLatitude()));
	    assert (coords.get(0).equals(entity.getCoord().getLongitude()));

	    Geometry geo = new Geometry();
	    geo.setCoordinates(coords);
	    geo.setType("Point");
	    cloudantEntity.setGeometry(geo);
	    cloudantEntity.setType("Feature");
	}

	Map<String, Object> properties = new HashMap<>();
	properties.put(AUTHORTAG, entity.getAuthor());
	properties.put(USERTAG, entity.getUserID());
	properties.put(CONTENTTAG, entity.getContentURL());
	properties.put(ARTICLEIDTAG, entity.getArticleID());
	properties.put(RATINGTAG, entity.getRatingURL());
	properties.put(PICTURETAG, entity.getPictureURL());
	properties.put(USERMODULETAG, entity.getUserModuleURL());
	properties.put(SHORTTAG, entity.getShortTitle());
	properties.put(TIMESTAMPTAG, entity.getTimestampOfPressEntry());
	properties.put(TITLETAG, entity.getTitle());
	properties.put(REFERENCETAG, entity.getReference());

	cloudantEntity.setProperties(properties);

	return cloudantEntity;
    }

    @Override
    public IdentifiedArticleEntity mapToArticleEntity(CloudantArticleEntity src)
    {
	Map<String, Object> props = src.getProperties();
	/*
	 GeoJSON format dictates that the coordinates have to be represented using (longitude, latitude)
	 --- against common human usage
	 */
	Coordinate targetCoord = new Coordinate(src.getGeometry().getCoordinates().get(1), src.getGeometry().getCoordinates().get(0));

	Long timestamp;
	if (props.get(TIMESTAMPTAG) == null)
	{
	    timestamp = null;
	} else
	{
	    timestamp = ((Double) props.get(TIMESTAMPTAG)).longValue();
	}

	ArticleEntity.ArticleEntityBuilder builder = new ArticleEntity.ArticleEntityBuilder();
	ArticleEntity resultEntity = builder.author((String) props.get(AUTHORTAG))
		.user((String) props.get(USERTAG))
		.content((String) props.get(CONTENTTAG))
		.rating((String) props.get(RATINGTAG))
		.articleID((String) props.get(AUTHORTAG))
		.picture((String) props.get(PICTURETAG))
		.reference((String) props.get(REFERENCETAG))
		.shortTitle((String) props.get(SHORTTAG))
		.title((String) props.get(TITLETAG))
		.userModule((String) props.get(USERMODULETAG))
		.timestamp(timestamp)
		.coordinate(targetCoord)
		.build();

	IdentifiedArticleEntity result = new IdentifiedArticleEntity(src.getId(), src.getRev());
	result.setEntity(resultEntity);

	return result;
    }

    @Override
    public List<IdentifiedArticleEntity> mapToArticleEntityList(List<CloudantArticleEntity> srcList)
    {
	List<IdentifiedArticleEntity> result = new ArrayList<>();
	for (CloudantArticleEntity entity : srcList)
	{
	    result.add(mapToArticleEntity(entity));
	}

	return result;
    }
    
    public List<IdentifiedArticleEntity> mapToArticleEntityList(CloudantGeoSearchStream stream) throws IOException, GeneralCommunicationException, JSONParseException
    {
	List<IdentifiedArticleEntity> result = new ArrayList<>();
	while (stream.hasNext())
	{
	    result.add(mapToArticleEntity(stream.nextArticle()));
	}
	
	return result;
    }
}
