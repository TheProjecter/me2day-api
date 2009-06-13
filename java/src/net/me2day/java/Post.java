package net.me2day.java;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
/**
 * 미투 포스트(글)을 나타내는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public class Post implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8074072040556134252L;
	private String id;
	private String username;
	private URL permalink;
	private String body;
	private Date pubDate;
	private String kind;
	private URL icon;
	private Person author;

	private int commentsCount;
	private int metooCount;

	private List<String> tags = new ArrayList<String>(2);

	/**
	 * 새로운 Post 객체를 만든다.
	 */
	public Post()
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

	public void setUsername( String id )
	{
		this.username = id;
	}

	public String getUsername()
	{
		return this.username;
	}

	/**
	 * 이 포스트의 퍼머링크를 지정한다.
	 */
	public void setPermalink( URL link )
	{
		this.permalink = link;
	}

	/**
	 * 이 포스트의 퍼머링크를 가져온다.
	 */
	public URL getPermalink()
	{
		return this.permalink;
	}

	/**
	 * 이 포스트의 종류를 지정한다. 
	 */
	public void setKind( String kind )
	{
		this.kind = kind;
	}

	public String getKind()
	{
		return this.kind;
	}

	public void setBody( String body )
	{
		this.body = body;
	}

	public String getBody()
	{
		return this.body;
	}

	public void setIcon( URL iconUrl )
	{
		this.icon = iconUrl;
	}

	public URL getIcon()
	{
		return this.icon;
	}

	public void setPubDate( Date pubDate )
	{
		this.pubDate = pubDate;
	}

	public Date getPubDate()
	{
		return this.pubDate;
	}

	public void setCommentsCount( int count )
	{
		this.commentsCount = count;
	}

	public int getCommentsCount()
	{
		return this.commentsCount;
	}

	public void setMetooCount( int count )
	{
		this.metooCount = count;
	}

	public int getMetooCount()
	{
		return this.metooCount;
	}

	public boolean equals( Object o )
	{
		if( o instanceof Post )
		{
			Post p = (Post)o;
			if( p.permalink.equals(this.permalink) &&
				p.commentsCount==this.commentsCount &&
				p.metooCount==this.metooCount )
				return true;
		}
		return false;
	}

	public void addTag( String tag )
	{
		tags.add(tag);
	}

	public void clearTags()
	{
		tags.clear();
	}

	public String getTags()
	{
		StringBuilder sb =  new StringBuilder();
		for(Iterator<String> i=tags.iterator(); i.hasNext(); )
		{
			sb.append( i.next() );
			if( i.hasNext() )
				sb.append(" ");
		}
		return sb.toString();
	}

	public List<String> getTagList()
	{
		return this.tags;
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
