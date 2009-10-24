package net.me2day.entity;

import java.io.Serializable;
import java.net.URL;

import lombok.Data;

public @Data class Icon implements Serializable, GWTFriendly {
	private static final long serialVersionUID = -767861645667681261L;
	
	private int index;
	private int type;
	private URL uRL;
	private boolean isDefault;
	private String description;
	
	public void setDefault( boolean isDefaultIcon ) { 
		this.isDefault = isDefaultIcon;
	}
	
	public Object toGWT() 
	{
		net.me2day.gwt.client.Icon ret = new net.me2day.gwt.client.Icon();
		ret.setDefault(this.isDefault);
		ret.setDescription(this.description);
		ret.setIndex(this.index);
		ret.setType(this.type);
		ret.setURL(this.uRL.toString());
		return ret;
	}
}
