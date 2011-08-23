package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelStateEvent;
import de.coding_bereich.net.channel.ChannelEvent;

public abstract class PipelineChannelStateDownstreamHandler
	implements PipelineDownstreamHandler
{

	@Override
	public boolean onDownstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( oEvent instanceof ChannelStateEvent )
			return false;
		
		return onDownsteamChannelStateEvent((ChannelStateEvent) oEvent);
	}

	abstract public boolean onDownsteamChannelStateEvent(ChannelStateEvent oEvent);
}
