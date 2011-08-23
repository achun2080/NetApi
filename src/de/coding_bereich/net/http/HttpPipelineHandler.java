package de.coding_bereich.net.http;

import java.util.Iterator;
import java.util.LinkedList;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.pipeline.PipelineUpstreamHandler;
/**
 * Verwaltet die HTTP-Listener.
 * @author Thomas
 *
 */
public class HttpPipelineHandler implements PipelineUpstreamHandler
{
	private LinkedList<HttpHandler> handlerList = new LinkedList<HttpHandler>();
	
	
	@Override
	public boolean onUpstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( !(oEvent instanceof ChannelMessageEvent) )
			return true;

		ChannelMessageEvent event = (ChannelMessageEvent) oEvent;

		if( !(event.getMessage() instanceof HttpRequest) )
			return true;

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
		
		return true;
	}
	
	/**
	 * Fügt einen Listener hinzu.
	 * @param handler
	 */
	public void addHandler(HttpHandler handler)
	{
		handlerList.add(handler);
	}
}
