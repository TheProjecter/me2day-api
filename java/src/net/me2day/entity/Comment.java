package net.me2day.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0
 */
public @Data class Comment implements Serializable, GWTFriendly
{
	private static final long serialVersionUID = -1461352834498546885L;
	private String id;
	private String body;
	private Date pubDate;
	private Person author;
	
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
