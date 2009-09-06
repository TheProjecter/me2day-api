package net.me2day.entity;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 미투데이 이용자를 나타내는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public class Person implements Serializable, GWTFriendly
{
	private static final long serialVersionUID = 8999736931097933667L;
	
	private String id;
	private URL openid;
	private String nickname;
	private URL face;
	private String description;
	private URL homepage;
	private URL rssDaily;
	private String parentId;
	private int friendCount;
	
	private List<Icon> postIcons;

	/**
	 * Person 객체를 생성한다.
	 */
	public Person()
	{

	}

	/**
	 * 이 사람의 미투데이 아이디를 지정한다.
	 */
	public void setId( String id )
	{
		this.id = id;
	}

	/**
	 * 이 사람의 미투데이 아이디를 가져온다.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * 이 사람의 OpenID를 지정한다.
	 */
	public void setOpenId( URL openId )
	{
		this.openid = openId;	
	}

	/**
	 * 이 사람의 OpenID를 가져온다.
	 */
	public URL getOpenId()
	{
		return this.openid;
	}

	/**
	 * 이 사람의 미투데이 닉네임을 지정한다.
	 */
	public void setNickname( String nickname )
	{
		this.nickname = nickname;
	}

	/**
	 * 이 사람의 미투데이 닉네임을 가져온다.
	 */
	public String getNickname()
	{
		return this.nickname;
	}

	/**
	 * 이 사람의 미투데이 공개사진 위치를 지정한다.
	 */
	public void setFace( URL faceUrl )
	{
		this.face = faceUrl;
	}

	/**
	 * 이 사람의 미투데이 공개사진 위치를 가져온다.
	 */
	public URL getFace()
	{
		return this.face;
	}

	/**
	 * 이 사람의 미투데이 소개글을 지정한다.
	 */
	public void setDescription( String desc )
	{
		this.description = desc;
	}

	/**
	 * 이 사람의 미투데이 소개글을 가져온다.
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * 이 사람의 홈페이지(혹은 블로그)를 지정한다.
	 */
	public void setHomepage( URL home )
	{
		this.homepage = home;
	}

	/**
	 * 이 사람의 홈페이지(혹은 블로그)를 가져온다.
	 */
	public URL getHomepage()
	{
		return this.homepage;
	}

	public String getMe2dayHome() 
	{
		return String.format("http://me2day.net/%s", this.id);
	}

	/**
	 * 이 사람의 미투데이 글을 구독할 수 있는 RSS 주소를 지정한다.
	 */
	public void setRSSDaily( URL rss )
	{
		this.rssDaily = rss;
	}

	/**
	 * 이 사람의 미투데이 글을 구독할 수 있는 RSS 주소를 가져온다.
	 */
	public URL getRSSDaily()
	{
		return this.rssDaily;
	}

	/**
	 * 이 사람을 미투데이로 초대한 사람의 아이디를 지정한다.
	 */
	public void setParentId( String id )
	{
		this.parentId = id;
	}

	/**
	 * 이 사람을 미투데이로 초대한 사람의 아이디를 가져온다.
	 */
	public String getParentId()
	{
		return this.parentId;
	}

	/**
	 * 이 사람의 미투데이 친구수를 지정한다.
	 */
	public void setFriendsCount( int count )
	{
		this.friendCount = count;
	}

	/**
	 * 이 사람의 미투데이 친구수를 가져온다.
	 */
	public int getFriendsCount()
	{
		return this.friendCount;
	}

	/**
	 * 이 사람의 포스팅 아이콘 세트를 지정한다. 
	 * 
	 * @param postIcons 아이콘 목록.
	 */
	public void setPostIcons(List<Icon> postIcons) {
		this.postIcons = postIcons;
	}

	/**
	 * 이 사람의 포스팅 아이콘 세트를 가져온다. 
	 * 
	 * @return 아이콘 목록.
	 */
	public List<Icon> getPostIcons() {
		return postIcons;
	}

	public Object toGWT() {
		net.me2day.gwt.client.Person ret = new net.me2day.gwt.client.Person();
		if( this.description!=null )
		ret.setDescription(this.description);
		if( this.face!=null )
			ret.setFace(this.face.toString());
		ret.setFriendsCount(this.friendCount);
		if( this.homepage!=null )
			ret.setHomepage(this.homepage.toString());
		ret.setId(this.id);
		ret.setNickname(this.nickname);
		if( this.openid!=null ) 
			ret.setOpenId(this.openid.toString());
		ret.setParentId(this.parentId);
		ret.setRSSDaily(this.rssDaily.toString());
		
		List<net.me2day.gwt.client.Icon> icons = new ArrayList<net.me2day.gwt.client.Icon>(postIcons.size());
		for(Icon i : postIcons) 
			icons.add((net.me2day.gwt.client.Icon)i.toGWT());
		ret.setPostIcons(icons);
		return ret;
	}
}
