package net.me2day.java;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 미투 포스트(글)을 나타내는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public class Post implements Serializable
{
	private static final long serialVersionUID = 570026228180299315L;
	
	// pre-defined icons
	public static final int ICON_THINK = 1;
	public static final int ICON_FEELING = 2;
	public static final int ICON_ALARM = 3;
	
	// user-defined icons
	public static final int ICON_USER_1 = 4;
	public static final int ICON_USER_2 = 5;
	public static final int ICON_USER_3 = 6;
	public static final int ICON_USER_4 = 7;
	public static final int ICON_USER_5 = 8;
	public static final int ICON_USER_6 = 9;
	public static final int ICON_USER_7 = 10;
	public static final int ICON_USER_8 = 11;
	public static final int ICON_USER_9 = 12;
	
	private String id;
	private String username;
	private URL permalink;
	private String body;
	private Date pubDate;
	private String kind;
	private URL icon;
	private int iconIndex;
	private Person author;

	private int commentsCount;
	private int metooCount;
	
	private boolean receiveSMS = false;
	private boolean closeComment = false;
	
	private Float latitude;
	private Float longitude;
	
	private VirtualFile attachment;

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
	
	public void setIconIndex( int index )
	{
		this.iconIndex = index;
	}

	public int getIconIndex()
	{
		return this.iconIndex;
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
	
	public void setTags( String tags ) {
		String[] values = tags.split(" ");
		for (String tag : values)
			addTag(tag);
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
	
	/**
	 * 댓들을 SMS로 전달받는지 여부를 확인한다.
	 */
	public boolean isReceiveSMS() 
	{
		return receiveSMS;
	}

	/**
	 * 댓글을 SMS로 전달받을지 설정한다.
	 */
	public void setReceiveSMS( boolean receiveSms ) 
	{
		receiveSMS = receiveSms;
	}
	
	/**
	 * 댓글을 허용하지 않는지 확인한다.
	 */
	public boolean isCloseComment() 
	{
		return closeComment;
	}

	/**
	 * 댓글을 허용하지 않을지 설정한다. 기본값은 <code>false</code>로 댓글 허용함.
	 */
	public void setCloseComment( boolean closeComment ) 
	{
		this.closeComment = closeComment;
	}
	
	/**
	 * 위도/경도 값이 지정되어있는지 확인한다.
	 * 
	 * @return 지정되어있을 경우 true, 아닐 경우 false.
	 */
	public boolean hasLocation() 
	{
		return latitude!=null && longitude!=null;
	}
	
	/**
	 * 위도 값을 지정한다.
	 * 
	 * @param latitude 위도
	 */
	public void setLatutude( float latitude ) 
	{
		this.latitude = latitude;
	}
	
	/**
	 * 경도 값을 지정한다.
	 * 
	 * @param longitude 경도
	 */
	public void setLongitude( float longitude ) 
	{
		this.longitude = longitude;
	}
	
	/**
	 * 위도 값을 가져온다.
	 */
	public Float getLatitude() 
	{
		return latitude;
	}

	/**
	 * 경도 값을 가져온다.
	 */
	public Float getLongitude() 
	{
		return longitude;
	}

	/**
	 * 첨부파일을 가져온다.
	 */
	public VirtualFile getAttachment() 
	{
		return attachment;
	}
	
	/**
	 * 첨부파일을 설정한다.
	 */
	public void setAttachment( VirtualFile attachment ) 
	{
		this.attachment = attachment;
	}
	
	/**
	 * 미투데이 링크 형식을 제외한 본문을 반환한다.
	 * 
	 * @param content 미투데이 링크 형식을 포함하는 본문
	 * @return 링크를 제외한 본문
	 */
	public static String getPlainBodyOf( String content ) {
		String regex = "\"([^\"]*)\":(https?://[^\\s]*)";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(content);

		StringBuffer message = new StringBuffer();

		while (m.find()) {
			m.appendReplacement(message, m.group(1));
		}

		m.appendTail(message);
		
		return message.toString();
	}
	
	/**
	 * 링크를 제외한 실제 본문의 길이를 알려준다.
	 * 
	 * @param content
	 *            본문 (미투데이 링크 포함 가능)
	 * @return 링크를 제외한 본문의 길이
	 */
	public static int getLengthOf( String content ) 
	{
		return getPlainBodyOf(content).length();
	}
	
	/**
	 * 미투데이 링크 형식을 제외한 본문을 반환한다.
	 * 
	 * @return 링크를 제외한 본문
	 */
	public String getPlainBody() 
	{
		return getPlainBodyOf( getBody() );
	}
	
	/**
	 * 링크를 제외한 실제 본문의 길이를 알려준다.
	 * 
	 * @return 링크를 제외한 본문의 길이
	 */
	public int getLength()
	{
		return getLengthOf( getBody() );
	}
}
