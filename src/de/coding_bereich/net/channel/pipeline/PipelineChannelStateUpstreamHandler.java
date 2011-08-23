package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelStateEvent;
import de.coding_bereich.net.channel.ChannelEvent;

public abstract class PipelineChannelStateUpstreamHandler
	implements PipelineUpstreamHandler
{

	@Override
	public boolean onUpstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( oEvent instanceof ChannelStateEvent )
			return false;
		
		return onUpsteamChannelStateEvent((ChannelStateEvent) oEvent);
	}

	abstract public boolean onUpsteamChannelStateEvent(ChannelStateEvent oEvent);
}
