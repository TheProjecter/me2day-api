package net.me2day.gwt.client;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
	private static final long serialVersionUID = 5625787533339187992L;
	
	private String id;
	private String body;
	private Date pubDate;
	private Person author;

	public Comment()
	{

	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getId()
	{
		return this.id;
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
