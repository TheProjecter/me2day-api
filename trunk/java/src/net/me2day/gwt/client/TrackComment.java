package net.me2day.gwt.client;

import java.io.Serializable;

public class TrackComment implements Serializable {
	private static final long serialVersionUID = 428241280596278911L;
	
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
