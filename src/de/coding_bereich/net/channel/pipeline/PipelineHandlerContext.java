package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.ChannelEventFuture;

public interface PipelineHandlerContext
{
	public boolean isUpstreamHander();
	public boolean isDownstreamHander();

	public void sendDownstream(ChannelEvent event) throws Exception;
	public void sendUpstream(ChannelEvent event) throws Exception;
	
	public ChannelEventFuture sendDownstream(Object message) throws Exception;
	public ChannelEventFuture sendUpstream(Object message) throws Exception;

	public Pipeline getPipline();

	public PipelineHandler getHandler();
}
