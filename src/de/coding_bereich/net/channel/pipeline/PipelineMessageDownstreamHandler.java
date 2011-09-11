package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;

public abstract class PipelineMessageDownstreamHandler implements
		PipelineDownstreamHandler
{

	@Override
	public void onDownstreamEvent(ChannelEvent event,
											PipelineHandlerContext context)
			throws Exception
	{
		if( event instanceof ChannelMessageEvent )
			onDownstreamMessageEvent((ChannelMessageEvent) event, context);
		else
			context.sendUpstream(event);
	}

	abstract public void
			onDownstreamMessageEvent(ChannelMessageEvent event,
												PipelineHandlerContext context)
					throws Exception;
}
