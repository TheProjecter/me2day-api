package net.me2day.java;

import java.io.Serializable;

public class TrackComment implements Serializable
{
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
