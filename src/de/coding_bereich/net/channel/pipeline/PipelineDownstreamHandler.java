package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelEvent;

public interface PipelineDownstreamHandler
	extends PipelineHandler
{
	/**
	 * 
	 * @param oEvent
	 * @return false => Event nicht in der Pipeline weitergeben. 
	 * @throws Exception
	 */
	public boolean onDownstreamEvent(ChannelEvent oEvent) throws Exception;
}
