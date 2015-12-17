/*
 * Copyright 2015 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.loercher.geomodule.commons.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.loercher.geomodule.commons.GeoModuleProperties;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author Jimmy
 */
@ControllerAdvice
public class GeoExceptionAdvice
{

    private static final Logger log = LoggerFactory.getLogger(GeoExceptionAdvice.class);

    private final ObjectMapper objectMapper;
    private final Properties properties;

    @Autowired
    public GeoExceptionAdvice(ObjectMapper pObjectMapper, GeoModuleProperties pProperties)
    {
	objectMapper = pObjectMapper;
	properties = pProperties.getProp();
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception
    {
	log.warn("There is an unexpected error: ", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	result.put("error", "Internal Server Error");
	result.put("message", "Please try again later.");
	result.put("path", getURLString(req.getRequestURI()));

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
    @ExceptionHandler(value = GeneralCommunicationException.class)
    public ResponseEntity<String> generalCommunicationErrorHandler(HttpServletRequest req, GeneralCommunicationException e) throws Exception
    {
	log.warn("There is an unexpected error: ", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
	result.put("error", "Internal Server Error");
	result.put("message", "Please try again later. If this error happens continuously plz give feedback with the specified uuid.");
	result.put("path", getURLString(req.getRequestURI()));
	result.put("uuid", e.getUuid());

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<String> unauthorizedErrorHandler(HttpServletRequest req, UnauthorizedException e) throws Exception
    {
	log.warn("There was an unauthorized access to an article.", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("status", HttpStatus.UNAUTHORIZED.value());
	result.put("error", "Unauthorized");
	result.put("message", "The right to change the resource is not allowed for that user.");
	result.put("path", getURLString(req.getRequestURI()));
	result.put("uuid", e.getUuid());

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentErrorHandler(HttpServletRequest req, IllegalArgumentException e) throws Exception
    {
	log.warn("The delivered entity from the client didn't conform the requirements: ", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
	result.put("error", "Unprocessable Entity");
	result.put("message", e.getMessage());
	result.put("path", getURLString(req.getRequestURI()));

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = ArticleNotFoundException.class)
    public ResponseEntity<String> resourceNotFoundErrorHandler(HttpServletRequest req, ArticleNotFoundException e) throws Exception
    {
	String articleID = e.getArticleID();
	log.warn("Resource belonging to the articleId " + articleID + " not existing!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);

	result.put("status", HttpStatus.NOT_FOUND.value());
	result.put("error", "Not Found");
	result.put("message", "Resource belonging to articleID " + articleID + " not available.");
	result.put("path", getURLString(req.getRequestURI()));

	result.put("uuid", e.getUuid());
	result.put("timestamp", new Timestamp(e.getTime().getTime()));

	Timestamp now = new Timestamp(new Date().getTime());
	result.put("timestamp", now);
	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ArticleConflictException.class)
    public ResponseEntity<String> articleConflictErrorHandler(HttpServletRequest req, ArticleConflictException e) throws Exception
    {
	String articleID = e.getArticleID();
	log.warn("There was a conflict accessing the article with article ID " + articleID + "!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", articleID);
	result.put("error", "Conflict");
	result.put("status", HttpStatus.CONFLICT.value());

	result.put("message", "There was a conflict accessing the article with article ID " + articleID + "!");
	result.put("path", getURLString(req.getRequestURI()));

	result.put("uuid", e.getUuid());
	result.put("timestamp", new Timestamp(e.getTime().getTime()));

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = RevisionPreconditionFailedException.class)
    public ResponseEntity<String> revisionPreconditionFailedErrorHandler(HttpServletRequest req, RevisionPreconditionFailedException e) throws Exception
    {
	log.warn("A precondition failed on changing the article " + e.getArticleID() + ". Most likely there is set an outdated ETag: " + e.getTriedRevision() + "!", e);

	Map<String, Object> result = new LinkedHashMap<>();
	result.put("articleID", e.getArticleID());
	result.put("error", "Precondition Failed");
	result.put("status", HttpStatus.PRECONDITION_FAILED.value());
	result.put("etag", e.getTriedRevision());

	result.put("message", "A precondition failed on changing the article " + e.getArticleID() + ". Most likely there is a outdated ETag set: " + e.getTriedRevision() + "!");
	result.put("path", getURLString(req.getRequestURI()));

	result.put("uuid", e.getUuid());
	result.put("timestamp", new Timestamp(e.getTime().getTime()));

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(value = TooManyResultsException.class)
    public ResponseEntity<String> tooManyResultsErrorHandler(HttpServletRequest req, TooManyResultsException e) throws Exception
    {
	log.warn("There was a general Rating exception.", e);

	Map<String, Object> result = new LinkedHashMap<>();

	result.put("error", "Internal Server Error");
	result.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

	result.put("message", "There has been an unexpected error. Please try again later. If this happens frequently please report with the uuid carried in this response.");
	result.put("path", getURLString(req.getRequestURI()));

	result.put("uuid", e.getUuid());
	result.put("timestamp", new Timestamp(e.getTime().getTime()));

	return new ResponseEntity<>(objectMapper.writeValueAsString(result), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getURLString(String uri)
    {
	return properties.getProperty("baseUrl") + uri;
    }

}
