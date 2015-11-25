/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.cloudant;

import com.cloudant.client.api.CloudantClient;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.JSONParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jimmy
 */
public class CloudantGeoSearchStream
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Coordinate coordinates;
    private final Integer radiusInMeter;
    private final String baseURL;
    private String bookmark;
    private boolean hasNext = true;

    private final CloudantClient client;
    private final List<CloudantArticleEntity> bufferedEntities = new ArrayList<>();
    private final List<String> alreadyAvailableIds = new ArrayList<>();

    public CloudantGeoSearchStream(String pBaseURL, Coordinate pCoordinates, Integer pRadiusInMeter, CloudantClient pClient)
    {
	coordinates = pCoordinates;
	radiusInMeter = pRadiusInMeter;
	baseURL = pBaseURL;

	client = pClient;
    }

    private BufferedReader fetchNextResponse() throws IOException
    {
	Double latitude = coordinates.getLatitude();
	Double longitude = coordinates.getLongitude();

	StringBuilder builder = new StringBuilder();
	StringBuilder urlBuilder = builder.append(baseURL)
		.append("?lat=").append(latitude)
		.append("&lon=").append(longitude)
		// workaround on bug 56377 from cloudant where radius doesn't work properly
		.append("&rangex=").append(radiusInMeter)
		.append("&rangey=").append(radiusInMeter)
		.append("&format=geojson&include_docs=true&relation=contains");

	if (bookmark != null)
	{
	    urlBuilder.append("&bookmark=").append(bookmark);
	}

	String url = urlBuilder.toString();
	log.info("fetchNextResponse URL: " + url);

	HttpRequestBase requestBase = new HttpGet(url);
	HttpResponse response = client.executeRequest(requestBase);

	HttpEntity entity = response.getEntity();
	return new BufferedReader(new InputStreamReader(entity.getContent()));
    }

    private void extractEntities(BufferedReader bufferedReader) throws JSONParseException
    {
	JsonReader reader = new JsonReader(bufferedReader);
	try
	{
	    reader.beginObject();

	    String arrayName = null;
	    while (reader.hasNext() && !("features".equals(arrayName)))
	    {
		arrayName = reader.nextName();
		if ("bookmark".equals(arrayName))
		{
		    bookmark = reader.nextString();
		} else if (!("features".equals(arrayName)))
		{
		    reader.skipValue();
		}
	    }

	    if ("features".equals(arrayName))
	    {
		reader.beginArray();

		while (reader.hasNext())
		{
		    Gson gson = new Gson();
		    CloudantArticleEntity entity = gson.fromJson(reader, CloudantArticleEntity.class);
		    
		    // Duplicates should not be returned
		    if (!(alreadyAvailableIds.contains(entity.getId())))
		    {
			bufferedEntities.add(entity);
		    }
		    
		    alreadyAvailableIds.add(entity.getId());
		}

		reader.endArray();
		reader.endObject();
		reader.close();
	    } else
	    {
		JSONParseException e = new JSONParseException("Parsing of cloudant response failed. Tag 'features' not found. ");
		log.error(e.getLoggingString());
		throw e;
	    }
	} catch (IOException ex)
	{
	    JSONParseException e = new JSONParseException("Parsing of cloudant response failed.", ex);
	    log.error(e.getLoggingString());
	    throw e;
	}
    }

    public CloudantArticleEntity nextArticle() throws JSONParseException, IOException
    {
	if (hasNext)
	{
	    // check if it's necessary to reload
	    if (bufferedEntities.isEmpty())
	    {
		BufferedReader buffer = fetchNextResponse();
		extractEntities(buffer);

		if (bufferedEntities.isEmpty())
		{
		    hasNext = false;
		    return null;
		}
	    }

	    return bufferedEntities.remove(0);
	}

	return null;
    }

    public boolean hasNext() throws JSONParseException, IOException
    {
	// if flag is already set negative return false
	if (!hasNext)
	{
	    return false;
	}

	if (bufferedEntities.isEmpty())
	{
	    BufferedReader buffer = fetchNextResponse();
	    extractEntities(buffer);

	    if (bufferedEntities.isEmpty())
	    {
		hasNext = false;
		return hasNext;
	    }
	}

	return true;
    }
}
