package net.me2day.entity;

import java.io.Serializable;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 미투데이 이용자를 나타내는 클래스이다.
 *
 * @author Jang-Ho Hwang, rath@xrath.com 
 */
public @Data class Person implements Serializable, GWTFriendly
{
	private static final long serialVersionUID = 8999736931097933667L;
	
	/**
	 * 미투데이 아이디 
	 */
	protected String id;
	/**
	 * 오픈아이디, 자체 로그인을 쓰거나 네이버 아이디를 사용할 경우 null. 
	 */
	protected URL openId;
	/**
	 * 미투데이 닉네임.
	 */
	protected String nickname;
	/**
	 * 프로필 사진 URL.
	 */
	protected URL face;
	/**
	 * 자기소개 문구.
	 */
	protected String description;
	/**
	 * 등록해둔 홈페이지, 등록하지 않았을 경우 null.
	 */
	protected URL homepage;
	/**
	 * 미투데이에서 제공하는 RSS Daily 페이지 URL.
	 */
	protected URL rssDaily;
	/**
	 * 이 사용자를 초대한 사람의 아이디, 없을 경우 null.
	 */
	protected String parentId;
	/**
	 * 친구들의 수.
	 */
	protected int friendCount;
	
	/**
	 * 포스트 아이콘 목록.
	 */
	protected List<Icon> postIcons;

	/**
	 * 이 사용자의 미투데이 페이지.
	 */
	public URL getMe2dayHome() {
		URL url = null;
		try {
			url = new URL("http://me2day.net/" + this.id);
		} catch( MalformedURLException e ) {}
		return url;
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
		if( this.openId!=null ) 
			ret.setOpenId(this.openId.toString());
		ret.setParentId(this.parentId);
		ret.setRSSDaily(this.rssDaily.toString());
		
		List<net.me2day.gwt.client.Icon> icons = new ArrayList<net.me2day.gwt.client.Icon>(postIcons.size());
		for(Icon i : postIcons) 
			icons.add((net.me2day.gwt.client.Icon)i.toGWT());
		ret.setPostIcons(icons);
		return ret;
	}
}
