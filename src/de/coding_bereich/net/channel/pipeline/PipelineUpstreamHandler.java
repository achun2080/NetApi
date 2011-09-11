package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelEvent;

public interface PipelineUpstreamHandler extends PipelineHandler
{
	void onUpstreamEvent(ChannelEvent event, PipelineHandlerContext context)
			throws Exception;
}
