package net.me2day.java.gwt.client;

import java.io.Serializable;
import java.util.List;

public class Person implements Serializable {

	private static final long serialVersionUID = -2223057261374310922L;
	
	private String id;
	private String openid;
	private String nickname;
	private String face;
	private String description;
	private String homepage;
	private String rssDaily;
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
	public void setOpenId( String openId )
	{
		this.openid = openId;	
	}

	/**
	 * 이 사람의 OpenID를 가져온다.
	 */
	public String getOpenId()
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
	public void setFace( String faceString )
	{
		this.face = faceString;
	}

	/**
	 * 이 사람의 미투데이 공개사진 위치를 가져온다.
	 */
	public String getFace()
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
	public void setHomepage( String home )
	{
		this.homepage = home;
	}

	/**
	 * 이 사람의 홈페이지(혹은 블로그)를 가져온다.
	 */
	public String getHomepage()
	{
		return this.homepage;
	}

	public String getMe2dayHome() 
	{
		return "http://me2day.net/" + this.id;
	}

	/**
	 * 이 사람의 미투데이 글을 구독할 수 있는 RSS 주소를 지정한다.
	 */
	public void setRSSDaily( String rss )
	{
		this.rssDaily = rss;
	}

	/**
	 * 이 사람의 미투데이 글을 구독할 수 있는 RSS 주소를 가져온다.
	 */
	public String getRSSDaily()
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
}
