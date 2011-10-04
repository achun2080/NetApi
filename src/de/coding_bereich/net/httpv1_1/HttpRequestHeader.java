package de.coding_bereich.net.httpv1_1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpRequestHeader
{
	private String method;
	private String plainUri;
	private String version;
	
	private HashMap<String, LinkedList<String>> headers = new HashMap<String, LinkedList<String>>();
	
	public String getMethod()
	{
		return method;
	}
	public void setMethod(String method)
	{
		this.method = method;
	}
	public String getPlainUri()
	{
		return plainUri;
	}
	public void setPlainUri(String plainUri)
	{
		this.plainUri = plainUri;
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public void setHeader(String key, String value)
	{
		key = key.toLowerCase().trim();
		value = value.trim();

		LinkedList<String> list = headers.get(key);

		if( list == null )
		{
			list = new LinkedList<String>();
			headers.put(key, list);
		}
		else
			list.clear();
		
		if( value != null )
			list.add(value);
	}

	public void addHeader(String key, String value)
	{
		key = key.toLowerCase().trim();
		value = value.trim();

		LinkedList<String> list = headers.get(key);

		if( list == null )
		{
			list = new LinkedList<String>();
			headers.put(key, list);
		}

		list.add(value);
	}

	public String getHeader(String key)
	{
		key = key.toLowerCase().trim();

		LinkedList<String> list = headers.get(key);

		if( list == null )
			return null;

		return list.getFirst();
	}

	public Iterator<String> getHeaders(String key)
	{
		key = key.toLowerCase().trim();

		LinkedList<String> list = headers.get(key);

		if( list == null )
			return null;

		return list.iterator();
	}
}
