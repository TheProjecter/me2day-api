package net.me2day.gwt.client;

import java.io.Serializable;
import java.util.Date;

public class Metoo implements Serializable {

	private static final long serialVersionUID = 3111788212500888521L;
	
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
