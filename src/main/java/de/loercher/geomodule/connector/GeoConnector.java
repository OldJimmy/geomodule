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
import java.util.List;

/**
 *
 * @author Jimmy
 */
public interface GeoConnector
{

    public ArticleIdentifier addArticle(ArticleEntity article) throws ArticleConflictException, GeneralCommunicationException;

    public ArticleIdentifier updateArticle(ArticleEntity article, String id, String revision) throws RevisionPreconditionFailedException, GeneralCommunicationException;

    public ArticleIdentifier saveArticle(ArticleEntity article) throws ArticleConflictException, GeneralCommunicationException;

    public ArticleEntity getArticle(String id) throws ArticleNotFoundException, GeneralCommunicationException;

    public List<ArticleEntity> getArticlesNear(Coordinate coordinates, Integer radiusInMeter) throws GeneralCommunicationException;
}
