package net.me2day.java;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public class Metoo implements Serializable
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
}
