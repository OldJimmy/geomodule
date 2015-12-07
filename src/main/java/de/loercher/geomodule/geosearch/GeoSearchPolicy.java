/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.geosearch;

import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import de.loercher.geomodule.core.api.GeoSearchEntity;
import java.util.List;

/**
 *
 * @author Jimmy
 */
public interface GeoSearchPolicy
{
    public List<GeoSearchEntity> fetchLayeredArticles(String userID, Coordinate coord, Integer maxResults) throws TooManyResultsException, GeneralCommunicationException;
}
