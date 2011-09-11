package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.ChannelStateEvent;

public abstract class PipelineChannelStateDownstreamHandler implements PipelineDownstreamHandler
{

	@Override
	public void onDownstreamEvent(ChannelEvent event, PipelineHandlerContext context) throws Exception
	{
		if( event instanceof ChannelStateEvent )
			onDownstreamChannelState((ChannelMessageEvent)event, context);
		else
			context.sendUpstream(event);
	}

	
	abstract public void onDownstreamChannelState(ChannelMessageEvent event, PipelineHandlerContext context)  throws Exception;
}
