package de.coding_bereich.net.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TimeZone;

import de.coding_bereich.net.buffer.IOBuffer;

/**
 * HTTP-Antwort.
 * 
 * @author Thomas
 * 
 */
public class HttpResponse
{
	public final static DateFormat					RFC_1123_FORMAT	= new SimpleDateFormat(
																								"EEE, dd MMM yyyyy HH:mm:ss z");

	private HttpRequest									request;
	private int												code					= 200;

	private String											innerCharSet		= "UTF-8";

	private IOBuffer										bodyBuffer			= null;

	private boolean										flushResponse		= true;

	private HashMap<String, LinkedList<String>>	header				= new HashMap<String, LinkedList<String>>();

	static
	{
		RFC_1123_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public HttpResponse(HttpRequest request)
	{
		this.request = request;

		setHeader("content-type", "text/html; charset=UTF-8");
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

	public HttpRequest getRequest()
	{
		return request;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public IOBuffer getBodyBuffer()
	{
		return bodyBuffer;
	}

	public void setBodyBuffer(IOBuffer bodyBuffer)
	{
		if( this.bodyBuffer != null )
			this.bodyBuffer.free();

		this.bodyBuffer = bodyBuffer.getCountedRef();
	}

	public String getInnerCharSet()
	{
		return innerCharSet;
	}

	public void setInnerCharSet(String charSet)
	{
		this.innerCharSet = charSet;
	}

	public void setCookie(String name, String value, Date expires, String path,
									String domain, boolean secure, boolean httpOnly)
	{
		StringBuilder buf = new StringBuilder();
		try
		{
			buf.append(URLEncoder.encode(name, HttpResponseEncoder.HEADER_CHARSET));
			buf.append("=");
			buf.append(URLEncoder
					.encode(value, HttpResponseEncoder.HEADER_CHARSET));

			if( expires != null )
				buf.append("; expires=").append(RFC_1123_FORMAT.format(expires));

			if( path != null )
				buf.append("; path=").append(path);

			if( domain != null )
				buf.append("; domain=").append(domain);

			if( secure )
				buf.append("; secure");

			if( httpOnly )
				buf.append("; httponly");

			addHeader("set-cookie", buf.toString());
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public void setCookie(String name, String value, Date expire)
	{
		setCookie(name, value, expire, null, null, false, false);
	}

	public void setCookie(String name, String value)
	{
		setCookie(name, value, null, null, null, false, false);
	}

	public boolean isContinue()
	{
		return flushResponse;
	}

	public void setFlushResponse(boolean flushResponse)
	{
		this.flushResponse = flushResponse;
	}
}
