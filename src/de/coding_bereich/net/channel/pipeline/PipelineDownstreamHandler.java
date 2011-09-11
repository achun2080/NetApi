package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelEvent;

public interface PipelineDownstreamHandler extends PipelineHandler
{
	void onDownstreamEvent(ChannelEvent event, PipelineHandlerContext context)
			throws Exception;
}
