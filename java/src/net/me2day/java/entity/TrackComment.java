package net.me2day.java.entity;

import java.io.Serializable;


/**
 * 
 * @author Jang-Ho Hwang, rath@xrath.com
 */
public class TrackComment implements Serializable
{	
	private static final long serialVersionUID = -8876156197228804187L;
	
	private Post post;
	private Comment comment;

	public TrackComment()
	{

	}

	public void setPost( Post post )
	{
		this.post = post;
	}

	public Post getPost()
	{
		return this.post;
	}

	public void setComment( Comment comment )
	{
		this.comment = comment;
	}

	public Comment getComment()
	{
		return this.comment;
	}
}
