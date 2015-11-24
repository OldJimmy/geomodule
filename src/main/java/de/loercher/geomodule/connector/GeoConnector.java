/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.connector;

import de.loercher.geomodule.commons.exception.ArticleNotFoundException;
import de.loercher.geomodule.commons.exception.RevisionPreconditionFailedException;
import de.loercher.geomodule.commons.Coordinate;
import de.loercher.geomodule.commons.exception.ArticleConflictException;
import de.loercher.geomodule.commons.exception.GeneralCommunicationException;
import de.loercher.geomodule.commons.exception.TooManyResultsException;
import java.util.List;

/**
 *
 * @author Jimmy
 */
public interface GeoConnector
{
    public Integer getProviderBasedLimit();
    
    public ArticleIdentifier addArticle(ArticleEntity article) throws ArticleConflictException, GeneralCommunicationException;
    public ArticleIdentifier updateArticle(ArticleEntity article, String id, String revision) throws RevisionPreconditionFailedException, GeneralCommunicationException;
    public ArticleIdentifier saveArticle(ArticleEntity article, String id) throws ArticleConflictException, GeneralCommunicationException;
    public IdentifiedArticleEntity getArticle(String id) throws ArticleNotFoundException, GeneralCommunicationException;
    public List<IdentifiedArticleEntity> getArticlesNear(Coordinate coordinates, Integer radiusInMeter, Integer maxArticleCount) throws GeneralCommunicationException, TooManyResultsException;
}
