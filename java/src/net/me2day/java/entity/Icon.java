package net.me2day.java.entity;

import java.io.Serializable;
import java.net.URL;

public class Icon implements Serializable, GWTFriendly {
	private static final long serialVersionUID = -767861645667681261L;
	
	private int index;
	private int type;
	private URL url;
	private boolean isDefault;
	private String description;
	
	public Icon() {
		
	}
	
	public void setIndex( int index ) 
	{
		this.index = index;
	}
	
	public int getIndex() 
	{
		return index;
	}
	
	public void setType( int type ) 
	{
		this.type = type;
	}
	
	public int getType() 
	{
		return type;
	}
	
	public void setURL( URL url ) 
	{
		this.url = url;
	}
	
	public URL getURL() 
	{
		return url;
	}
	
	public void setDefault( boolean value ) 
	{
		this.isDefault = value;
	}
	
	public boolean isDefault() 
	{
		return isDefault;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public Object toGWT() 
	{
		net.me2day.java.gwt.client.Icon ret = new net.me2day.java.gwt.client.Icon();
		ret.setDefault(this.isDefault);
		ret.setDescription(this.description);
		ret.setIndex(this.index);
		ret.setType(this.type);
		ret.setURL(this.url.toString());
		return ret;
	}
}
