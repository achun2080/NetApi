package de.coding_bereich.net.http;

import java.util.Iterator;
import java.util.LinkedList;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.pipeline.PipelineHandlerContext;
import de.coding_bereich.net.channel.pipeline.PipelineMessageUpstreamHandler;

/**
 * Verwaltet die HTTP-Listener.
 * 
 * @author Thomas
 * 
 */
public class HttpPipelineHandler extends PipelineMessageUpstreamHandler
{
	private LinkedList<HttpHandler>	handlerList	= new LinkedList<HttpHandler>();

	@Override
	public void onUpstreamMessageEvent(ChannelMessageEvent event,
													PipelineHandlerContext context)
			throws Exception
	{
		if( !(event.getMessage() instanceof HttpRequest) )
		{
			context.sendUpstream(event);
			return;
		}

		HttpRequest req = (HttpRequest) event.getMessage();

		HttpResponse res = req.getResponse();

		Iterator<HttpHandler> it = handlerList.iterator();

		boolean found = false;

		while( it.hasNext() )
		{
			if( it.next().onRequest(req, res) )
			{
				found = true;
				break;
			}
		}

		if( !found )
		{
			res.setCode(404);
			res.setBodyBuffer(null);
		}

		event.getChannel().write(res);
	}

	/**
	 * Fügt einen Listener hinzu.
	 * 
	 * @param handler
	 */
	public void addHandler(HttpHandler handler)
	{
		handlerList.add(handler);
	}
}
