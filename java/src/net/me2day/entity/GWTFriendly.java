package net.me2day.entity;

/**
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 * @version 1.0
 */
public interface GWTFriendly {
    /**
     * Create a new object that fully transportable within GWT environment.
     */
	public Object toGWT();
}
