/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.connector;

import java.util.List;

/**
 *
 * @author Jimmy
 * @param <T>
 */
public interface ArticleEntityMapper<T>
{
    public T mapFromArticleEntity(ArticleEntity entity);
    public IdentifiedArticleEntity mapToArticleEntity(T sourceInstance);
    public List<IdentifiedArticleEntity> mapToArticleEntityList(List<T> srcList);
    
    // author is the string to be displayed, userid the system-wide unique id for the user
    public static final String AUTHORTAG = "author";
    public static final String USERTAG = "user";
    public static final String CONTENTTAG = "content";
    public static final String PICTURETAG = "picture";
    public static final String SHORTTAG = "short";
    public static final String TIMESTAMPTAG = "timestamp";
    public static final String TITLETAG = "title";
    public static final String REFERENCETAG = "reference";
    public static final String USERMODULETAG = "usermodule";
}
