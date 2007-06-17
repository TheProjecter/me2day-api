package net.me2day.java;

import java.io.Serializable;
import java.util.Date;
/**
 *
 * @author Jang-Ho Hwang, rath@ncsoft.net
 * @version 1.0, $Id$ since 2007/06/17
 */
public class Comment implements Serializable
{
	private String body;
	private Date pubDate;
	private Person author;

	public Comment()
	{

	}

	public void setBody( String body )
	{
		this.body = body;
	}

	public String getBody()
	{
		return this.body;
	}

	public void setPubDate( Date pubDate )
	{
		this.pubDate = pubDate;
	}

	public Date getPubDate()
	{
		return this.pubDate;
	}

	public void setAuthor( Person person )
	{
		this.author = person;
	}

	public Person getAuthor()
	{
		return this.author;
	}
}
