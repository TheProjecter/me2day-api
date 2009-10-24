package net.me2day.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public @Data class Metoo implements Serializable, GWTFriendly
{
	private static final long serialVersionUID = 4495373535109985874L;
	
	private Date pubDate;
	private Person author;

	public Metoo()
	{

	}

	public net.me2day.gwt.client.Metoo toGWT() 
	{
		net.me2day.gwt.client.Metoo ret = new net.me2day.gwt.client.Metoo();
		ret.setAuthor((net.me2day.gwt.client.Person) this.author.toGWT());
		ret.setPubDate(this.pubDate);
		return ret;
	}
}
