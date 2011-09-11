package de.coding_bereich.net.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import de.coding_bereich.net.buffer.DynamicIOBuffer;
import de.coding_bereich.net.buffer.IOBuffer;
import de.coding_bereich.net.channel.ChannelEventFuture;
import de.coding_bereich.net.channel.ChannelEventFutureListener;
import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.pipeline.PipelineHandlerContext;
import de.coding_bereich.net.channel.pipeline.PipelineMessageDownstreamHandler;

/**
 * Codiert eine HTTP-Antwort und gibt den Byte-Stream weiter.
 * 
 * @author Thomas
 * 
 */
public class HttpResponseEncoder extends PipelineMessageDownstreamHandler
{
	public static final String							HEADER_CHARSET	= "UTF-8";

	static final public HashMap<Integer, String>	codeList			= new HashMap<Integer, String>();

	static
	{
		codeList.put(200, "OK");
		codeList.put(201, "Created");
		codeList.put(202, "Accepted");
		codeList.put(204, "No Content");
		codeList.put(301, "Moved Permanently");
		codeList.put(302, "Moved Temporarily");
		codeList.put(304, "Not Modified");
		codeList.put(400, "Bad Request");
		codeList.put(401, "Unauthorized");
		codeList.put(403, "Forbidden");
		codeList.put(404, "Not Found");
		codeList.put(500, "Internal Server Error");
		codeList.put(501, "Not Implemented");
		codeList.put(502, "Bad Gateway");
		codeList.put(503, "Service Unavailable");
	}

	@Override
	public void onDownstreamMessageEvent(ChannelMessageEvent event,
														PipelineHandlerContext context)
			throws Exception
	{
		if( !(event.getMessage() instanceof HttpResponse) )
		{
			context.sendDownstream(event);
			return;
		}

		HttpResponse response = (HttpResponse) event.getMessage();

		if( response.getBodyBuffer() != null )
		{
			response.setHeader("content-length", Integer.toString(response
					.getBodyBuffer().getReadableBytes()));
		}

		StringBuilder headBuffer = new StringBuilder();

		headBuffer.append("HTTP/1.0 ").append(response.getCode()).append(" ")
				.append(codeList.get(response.getCode())).append("\r\n");

		Iterator<Entry<String, LinkedList<String>>> it = response.getHeader()
				.entrySet().iterator();
		while( it.hasNext() )
		{
			Entry<String, LinkedList<String>> entry = it.next();

			/*
			 * StringBuffer buffer = new StringBuffer(entry.getKey());
			 * 
			 * char lastChar = 0; int len = buffer.length(); for(int i = 0; i <
			 * len; i++) { char c = buffer.charAt(i); if( lastChar < 'a' ||
			 * lastChar > 'z' ) buffer.setCharAt(i, Character.toUpperCase(c));
			 * 
			 * lastChar = c; }
			 */

			Iterator<String> it2 = entry.getValue().iterator();

			while( it2.hasNext() )
			{
				String value = it2.next();
				if( value != null )
					headBuffer.append(entry.getKey()).append(": ").append(value)
							.append("\r\n");
			}
		}

		headBuffer.append("\r\n");

		IOBuffer headIoBuffer = DynamicIOBuffer.create();
		headIoBuffer.writeString(headBuffer, HEADER_CHARSET);

		if( response.getBodyBuffer() != null
				&& !"head".equals(response.getRequest().getMethod()) )
		{
			context.sendDownstream(headIoBuffer)
					.addListener(new ChannelEventFutureListener()
					{

						@Override
						public void onAction(ChannelEventFuture future)
						{
							System.out.println(future.getException());
						}
					});;
			context.sendDownstream(response.getBodyBuffer())
					.addListener(new ChannelEventFutureListener()
					{

						@Override
						public void onAction(ChannelEventFuture future)
						{
							System.out.println(future.getException());
						}
					});
		}
		else
			context.sendDownstream(headIoBuffer)
					.addListener(ChannelEventFutureListener.CLOSE);

		headIoBuffer.free();
		if( response.getBodyBuffer() != null )
			response.getBodyBuffer().free();
	}
}
