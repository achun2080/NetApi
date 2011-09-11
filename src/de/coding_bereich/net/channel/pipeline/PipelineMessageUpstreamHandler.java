package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;

public abstract class PipelineMessageUpstreamHandler implements PipelineUpstreamHandler
{

	@Override
	public void onUpstreamEvent(ChannelEvent event, PipelineHandlerContext context) throws Exception
	{
		if( event instanceof ChannelMessageEvent )
			onUpstreamMessageEvent((ChannelMessageEvent)event, context);
		else
			context.sendUpstream(event);
	}
	
	abstract public void onUpstreamMessageEvent(ChannelMessageEvent event, PipelineHandlerContext context)  throws Exception;
}
