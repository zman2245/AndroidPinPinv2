package com.zman2245.pinpin.fragment.event;

import java.util.HashMap;

/**
 * An event that a fragment may "bubble up" to a parent
 * fragment or activity
 *
 * @author Zack
 */
public class Event
{
	/**
	 * The event type
	 */
	public EventType type;

	/**
	 * Data passed with the event
	 *
	 * The specific data may depend on the type of event
	 */
	public HashMap<String, Object> data;

	public Event(EventType type)
	{
		this(type, new HashMap<String, Object>());
	}

	public Event(EventType type, HashMap<String, Object> data)
	{
		this.type = type;
		this.data = data;
	}
}
