package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.ChannelStateEvent;

public abstract class PipelineChannelStateUpstreamHandler implements
		PipelineUpstreamHandler
{

	@Override
	public void onUpstreamEvent(ChannelEvent event,
											PipelineHandlerContext context)
			throws Exception
	{
		if( event instanceof ChannelStateEvent )
			onUpstreamChannelState((ChannelMessageEvent) event, context);
		else
			context.sendUpstream(event);
	}

	abstract public void onUpstreamChannelState(ChannelMessageEvent event,
																PipelineHandlerContext context)
			throws Exception;
}