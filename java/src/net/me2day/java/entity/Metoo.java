package net.me2day.java.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public class Metoo implements Serializable, GWTFriendly
{
	private static final long serialVersionUID = 4495373535109985874L;
	
	private Date pubDate;
	private Person author;

	public Metoo()
	{

	}

	public void setPubDate( Date pubDate )
	{
		this.pubDate = pubDate;
	}

	public Date getPubDate()
	{
		return this.pubDate;
	}

	public void setAuthor( Person author )
	{
		this.author = author;
	}

	public Person getAuthor()
	{
		return this.author;
	}
	
	public net.me2day.java.gwt.client.Metoo toGWT() 
	{
		net.me2day.java.gwt.client.Metoo ret = new net.me2day.java.gwt.client.Metoo();
		ret.setAuthor((net.me2day.java.gwt.client.Person) this.author.toGWT());
		ret.setPubDate(this.pubDate);
		return ret;
	}
}
