package net.me2day.gwt.client;

import java.io.Serializable;

public class Icon implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5416905108707501939L;
	
	private int index;
	private int type;
	private String url;
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
	
	public void setURL( String url ) 
	{
		this.url = url;
	}
	
	public String getURL() 
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
}
