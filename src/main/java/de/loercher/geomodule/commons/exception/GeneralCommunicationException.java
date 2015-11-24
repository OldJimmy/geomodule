/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.loercher.geomodule.commons.exception;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Jimmy
 */
public class GeneralCommunicationException extends Exception
{
    private String uuid;
    private Date time;
    private String error;
    
    public GeneralCommunicationException(String pError)
    {
	uuid = UUID.randomUUID().toString();
	time = new Date();
	error = pError;
    }

    public GeneralCommunicationException(String pError, Throwable e)
    {
	super(e);

	uuid = UUID.randomUUID().toString();
	time = new Date();
	error = pError;
    }

    public String getUuid()
    {
	return uuid;
    }

    public void setUuid(String uuid)
    {
	this.uuid = uuid;
    }

    public Date getTime()
    {
	return time;
    }

    public void setTime(Date time)
    {
	this.time = time;
    }

    public String getError()
    {
	return error;
    }

    public void setError(String error)
    {
	this.error = error;
    }
    
    public String getLoggingString()
    {
	Timestamp stamp = new Timestamp(time.getTime());
	return stamp + ": " + uuid + ": " + error;
    }
}
