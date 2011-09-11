package de.coding_bereich.net.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import de.coding_bereich.net.channel.Channel;

/**
 * Eine HTTP-Anfrage.
 * 
 * @author Thomas
 * 
 */
public class HttpRequest
{
	static final protected String						SESSION_COOKIE_NAME	= "sessionId";

	private String											version;
	private String											plainUri;
	private String											method;

	private String											queryString;
	private String											path;

	private HttpResponse									response					= new HttpResponse(
																									this);											;

	private HashMap<String, LinkedList<String>>	header					= new HashMap<String, LinkedList<String>>();
	private HashMap<String, String>					postVars					= new HashMap<String, String>();
	private HashMap<String, String>					getVars					= new HashMap<String, String>();
	private HashMap<String, String>					cookies					= new HashMap<String, String>();

	private HttpSession									session					= null;

	private Channel										channel;

	public HttpRequest(Channel channel)
	{
		this.channel = channel;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public HttpResponse getResponse()
	{
		return response;
	}

	public String getVersion()
	{
		return version;
	}

	public String getMethod()
	{
		return method;
	}

	public void setHeader(String key, String value)
	{
		key = key.toLowerCase();

		LinkedList<String> list = header.get(key);

		if( list == null )
		{
			list = new LinkedList<String>();
			header.put(key, list);
		}

		list.clear();
		list.add(value);
	}

	public void addHeader(String key, String value)
	{
		key = key.toLowerCase();

		LinkedList<String> list = header.get(key);

		if( list == null )
		{
			list = new LinkedList<String>();
			header.put(key, list);
		}

		list.add(value);
	}

	public String getHeader(String key)
	{
		key = key.toLowerCase();

		LinkedList<String> list = header.get(key);

		if( list == null )
			return null;

		return list.getFirst();
	}

	public Iterator<String> getHeaders(String key)
	{
		key = key.toLowerCase();

		LinkedList<String> list = header.get(key);

		if( list == null )
			return null;

		return list.iterator();
	}

	public HashMap<String, LinkedList<String>> getHeader()
	{
		return header;
	}

	public void setVersion(String version)
	{
		this.version = version.toLowerCase();
	}

	public void setPlainUri(String plainUri)
	{
		this.plainUri = plainUri;
	}

	public String getPlainUri()
	{
		return plainUri;
	}

	public void setMethod(String method)
	{
		this.method = method.toLowerCase();
	}

	public HashMap<String, String> getPostVars()
	{
		return postVars;
	}

	public String getPostVar(String name)
	{
		return postVars.get(name);
	}

	public void setPostVar(String name, String value)
	{
		postVars.put(name, value);
	}

	public HashMap<String, String> getGetVars()
	{
		return getVars;
	}

	public String getGetVar(String name)
	{
		return getVars.get(name);
	}

	public void setGetVar(String name, String value)
	{
		getVars.put(name, value);
	}

	public String getQueryString()
	{
		return queryString;
	}

	public void setPlainQueryString(String queryString)
	{
		this.queryString = queryString;
	}

	public String getRequestedFilePath()
	{
		return path;
	}

	public void setRequestedFilePath(String path)
	{
		this.path = path;
	}

	public HashMap<String, String> getCookies()
	{
		return cookies;
	}

	public String getCookie(String name)
	{
		return cookies.get(name);
	}

	public void setCookie(String name, String value)
	{
		response.setCookie(name, value);
	}

	public HttpSession getSession()
	{
		if( session != null )
			return session;

		String sessId = getCookie(SESSION_COOKIE_NAME);

		session = HttpSessionManager.getInstance().getSession(sessId);

		if( sessId == null )
			setCookie(SESSION_COOKIE_NAME, session.getId());

		return session;
	}

	@Override
	public String toString()
	{
		return "HttpRequest [header=" + header + ", requestMethod=" + method
				+ ", plainUri=" + plainUri + ", version=" + version + "]";
	}
}
