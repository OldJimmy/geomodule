/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.integration;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.loercher.geomodule.cloudant.CloudantArticleEntity;
import de.loercher.geomodule.cloudant.CloudantArticleEntityMapperImpl;
import de.loercher.geomodule.cloudant.CloudantGeoConnectorImpl;
import de.loercher.geomodule.connector.ArticleEntity;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.GeoModuleProperties;
import de.loercher.geomodule.commons.SecurityHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jimmy
 */
public class CloudantConnectionITest
{

    private GeoModuleProperties properties;
    private CloudantClient client;
    private Database db;

    public CloudantConnectionITest()
    {
    }

    @Before
    public void setUp()
    {
	properties = new GeoModuleProperties(new SecurityHelper());
	Properties props = properties.getProp();

	String password = props.getProperty(CloudantGeoConnectorImpl.SECRET_KEY_NAME);
	client = new CloudantClient(props.getProperty(CloudantGeoConnectorImpl.ACCOUNT_KEY_NAME), props.getProperty(CloudantGeoConnectorImpl.USER_KEY_NAME), password);
	
	db = client.database("localpress", false);
    }

    @Test
    public void connectToCloudant() throws IOException
    {

	System.out.println("Connected to Cloudant");
	System.out.println("Server Version: " + client.serverVersion());

	List<String> databases = client.getAllDbs();
	System.out.println("All my databases : ");
	for (String db : databases)
	{
	    System.out.println(db);
	}

	HttpRequestBase requestBase = new HttpGet("https://oldjimmy.cloudant.com/localpress/_design/geodd/_geo/geoidx?radius=107000&lon=8.412880&lat=48.996225&format=geojson&include_docs=true&relation=contains");

	HttpResponse response = client.executeRequest(requestBase);
	
	HttpEntity entity = response.getEntity();
	BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
	String line = reader.readLine();
	String alles = "";
	while (line != null)
	{
	    alles += line;
	    line = reader.readLine();
	}

	reader.close();
	
	Gson gson = new Gson();
	Type type = new TypeToken<Map<String, Object>>(){}.getType();
	Map<String, Object> map = new HashMap<>();
	
	map = gson.fromJson(alles, type);
	System.out.println("ABC: " + map.toString());
    }
    
    @Test
    public void insertDocument()
    {
	String id = UUID.randomUUID().toString();
	
	Timestamp now = new Timestamp(new Date().getTime());
	
	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.id(id)
		.author("alf")
		.content("eins")
		.picture("zwei")
		.shortTitle("drei")
		.timestamp(now.getTime())
		.title("title")
		.build();
						   
	Response resp = db.save(entity);
	db.remove(resp.getId(), resp.getRev());
    }

    @Test
    public void insertGeoDocument()
    {
	String id = UUID.randomUUID().toString();
	
	Timestamp now = new Timestamp(new Date().getTime());
	
	ArticleEntity entity = new ArticleEntity.ArticleEntityBuilder()
		.id(id)
		.coordinate(new Coordinate(new Double(9.84), new Double(49.18)))
		.author("ich")
		.content("geo")
		.picture("geo")
		.shortTitle("geo")
		.timestamp(now.getTime())
		.title("geo")
		.build();
	
//	9.856485,
//      49.178897
	CloudantArticleEntity cloudantEntity = new CloudantArticleEntityMapperImpl().mapFromArticleEntity(entity);
	
	Response resp = db.save(cloudantEntity);
    }
}
