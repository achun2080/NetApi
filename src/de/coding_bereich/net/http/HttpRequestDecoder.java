package de.coding_bereich.net.http;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.coding_bereich.net.buffer.DynamicIOBuffer;
import de.coding_bereich.net.buffer.IOBuffer;
import de.coding_bereich.net.buffer.exception.BufferUnderflowException;
import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.pipeline.PipelineDecoder;
/**
 * Decodiert den Byte-Strom zu einem HttpRequest.
 * Es wird HTTP 1.0 unterstützt.
 * @author Thomas
 *
 */
public class HttpRequestDecoder extends PipelineDecoder<HttpRequestDecoder.State>
{
	private HttpRequest				request;

	static public final String		HEADER_CHARSET	= "UTF-8";
	static public final byte[][]	LINE_DELIMITER	= {{'\r','\n'}, {'\r'}, {'\n'}};

	static private Pattern			uriPattern		= Pattern
																		.compile("^(.*?://([^/?#]*))?(\\/[^?#]*)(\\?([^#]*))?(#(.*))?$");

	public HttpRequestDecoder()
	{
		super(State.HEADER_FIRST_LINE);
	}

	public static enum State
	{
		HEADER_FIRST_LINE, HEADER, BODY
	}

	@Override
	protected Object decode(State state, IOBuffer buffer, ChannelMessageEvent event)
			throws Exception
	{
		try
		{
			String line;

			switch(state)
			{
				case HEADER_FIRST_LINE:
					if( request == null )
						request = new HttpRequest(event.getChannel());

					line = buffer.readDelimitedString(LINE_DELIMITER, HEADER_CHARSET);

					
					String[] firstLine = line.split(" ");
					request.setMethod(firstLine[0].trim());
					request.setPlainUri(firstLine[1].trim());
					request.setVersion(firstLine[2].trim());

					checkpoint(State.HEADER);

				case HEADER:
					while( true )
					{					
						line = buffer.readDelimitedString(LINE_DELIMITER,
								HEADER_CHARSET);
						
						if( line.length() == 0 )
							break;

						String[] keyValue = line.split(":");
						request.addHeader(keyValue[0].trim(), keyValue[1].trim());
						checkpoint(State.HEADER);
					}

					Matcher matcher = uriPattern.matcher(request.getPlainUri());

					if( !matcher.find() )
					{
						onDecodeError(request, "URL-PARSE ERROR", event);
						return null;
					}

					String host = matcher.group(2);

					if( request.getHeader("host") == null && host != null )
						request.setHeader("host", host);

					if( request.getHeader("host") == null && host == null )
					{
						onDecodeError(request, "HOST not set", event);
						return null;
					}

					String file = URLDecoder
							.decode(matcher.group(3), HEADER_CHARSET);
					String queryString = matcher.group(5);

					request.setRequestedFilePath(file);
					request.setPlainQueryString(queryString);

					if( queryString != null )
						parseVars(queryString, request.getGetVars());

					String cookie = request.getHeader("cookie");

					if( cookie != null )
					{
						HashMap<String, String> cookies = request.getCookies();

						String[] tokens = cookie.split(";\\s*|=");

						for(int i = 0; i < tokens.length;)
						{
							String name = tokens[i++];
							String value = tokens[i++];

							name = URLDecoder.decode(name, HEADER_CHARSET);
							value = URLDecoder.decode(value, HEADER_CHARSET);

							cookies.put(name, value);
						}
					}

					checkpoint(State.BODY);

				case BODY:
					postDecode: if( request.getMethod().equals("post") )
					{
						String contentType = request.getHeader("content-type");
						if( contentType != null
								&& contentType.indexOf("application/x-www-form-urlencoded") < 0 )
							break postDecode;

						String contentLengthString = request.getHeader("content-length");

						if( contentLengthString == null )
						{
							onDecodeError(request, "POST-Request: Content-Length is not set", event);
							return null;
						}

						int contentLength = Integer.parseInt(contentLengthString);

						String body = buffer.readString(contentLength, HEADER_CHARSET);
						parseVars(body, request.getPostVars());
					}
					
					
					HttpRequest req = request;
					request = null;					
					return req;
			}
		}
		catch(BufferUnderflowException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			onDecodeError(request, "PASRSE ERROR", event);
		}

		return null;
	}

	private HashMap<String, String> parseVars(String in,
			HashMap<String, String> ret) throws Exception
	{
		String[] vars = in.split("&");

		for(int i = 0; i < vars.length; i++)
		{
			String[] var = vars[i].split("=", 2);
			String name = var[0];
			String value = var[1];

			name = URLDecoder.decode(name, HEADER_CHARSET);
			value = URLDecoder.decode(value, HEADER_CHARSET);

			ret.put(name, value);
		}

		return ret;
	}

	private void onDecodeError(HttpRequest request, String error, ChannelMessageEvent event)
	{
		if( request == null )
			return;
		
		HttpResponse response = request.getResponse();
		response.setCode(400);
		response.setBodyBuffer(DynamicIOBuffer.create());
		response.getBodyBuffer().writeString(error, response.getInnerCharSet());
		event.getChannel().write(response);
	}
}
