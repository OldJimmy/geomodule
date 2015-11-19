/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.connector.ArticleEntityMapper;
import de.loercher.geomodule.commons.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jimmy
 */
public class CloudantArticleEntityMapperImpl implements ArticleEntityMapper<CloudantArticleEntity>
{
    @Override
    public CloudantArticleEntity mapFromArticleEntity(ArticleEntity entity)
    {
	CloudantArticleEntity cloudantEntity = new CloudantArticleEntity();
	cloudantEntity.setId(entity.getId());
	cloudantEntity.setRev(entity.getRev());

	/*
	    GeoJSON format dictates that the coordinates have to be represented using (longitude, latitude)
	    --- against common human usage
	*/
	List<Double> coords = new ArrayList<>();
	coords.add(entity.getCoord().getLongitude());
	coords.add(entity.getCoord().getLatitude());
	
	assert(coords.get(1).equals(entity.getCoord().getLatitude()) );
	assert(coords.get(0).equals(entity.getCoord().getLongitude()) );
	
	Geometry geo = new Geometry();
	geo.setCoordinates(coords);
	geo.setType("Point");
	
	cloudantEntity.setGeometry(geo);
	cloudantEntity.setType("Feature");
	
	Map<String, Object> properties = new HashMap<>();
	properties.put(AUTHORTAG, entity.getAuthor());
	properties.put(CONTENTTAG, entity.getContentURL());
	properties.put(PICTURETAG, entity.getPictureURL());
	properties.put(SHORTTAG, entity.getShortTitle());
	properties.put(TIMESTAMPTAG, entity.getTimestampOfPressEntry());
	properties.put(TITLETAG, entity.getTitle());
	properties.put(REFERENCETAG, entity.getReference());
	
	cloudantEntity.setProperties(properties);
	
	return cloudantEntity;
    }

    @Override
    public ArticleEntity mapToArticleEntity(CloudantArticleEntity src)
    {
	Map<String, Object> props = src.getProperties();
	/*
	    GeoJSON format dictates that the coordinates have to be represented using (longitude, latitude)
	    --- against common human usage
	*/
	Coordinate targetCoord = new Coordinate(src.getGeometry().getCoordinates().get(1), src.getGeometry().getCoordinates().get(0));
	
	Long timestamp = ((Double) props.get(TIMESTAMPTAG)).longValue();
	
	ArticleEntity.ArticleEntityBuilder builder = new ArticleEntity.ArticleEntityBuilder();
	ArticleEntity result = builder.author( (String) props.get(AUTHORTAG))
		.content((String) props.get(CONTENTTAG))
		.picture((String) props.get(PICTURETAG))
		.reference( (String) props.get(REFERENCETAG))
		.shortTitle( (String) props.get(SHORTTAG))
		.title( (String) props.get(TITLETAG))
		.timestamp( timestamp )
		.rev(src.getRev())
		.id(src.getId())
		.coordinate(targetCoord)
		.build();

	return result;
    }

    @Override
    public List<ArticleEntity> mapToArticleEntityList(List<CloudantArticleEntity> srcList)
    {
	List<ArticleEntity> result = new ArrayList<>();
	for (CloudantArticleEntity entity : srcList)
	{
	    result.add(mapToArticleEntity(entity));
	}
	
	return result;
    }
}
