package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;

public abstract class PipelineMessageDownstreamHandler implements PipelineDownstreamHandler
{

	@Override
	public boolean onDownstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( oEvent instanceof ChannelMessageEvent )
			return onDownstreamMessageEvent((ChannelMessageEvent)oEvent);
		
		return true;
	}

	
	abstract public boolean onDownstreamMessageEvent(ChannelMessageEvent event)  throws Exception;
}
