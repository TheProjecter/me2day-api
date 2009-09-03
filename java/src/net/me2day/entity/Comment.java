package net.me2day.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0
 */
public class Comment implements Serializable, GWTFriendly
{
	private static final long serialVersionUID = -1461352834498546885L;
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
	
	public Object toGWT() 
	{
		net.me2day.gwt.client.Comment ret = new net.me2day.gwt.client.Comment();
		ret.setAuthor((net.me2day.gwt.client.Person) author.toGWT());
		ret.setBody(body);
		ret.setId(id);
		ret.setPubDate(pubDate);
		return ret;
	}
}
