package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelMessageEvent;
import de.coding_bereich.net.channel.ChannelEvent;

public abstract class PipelineMessageUpstreamHandler implements PipelineUpstreamHandler
{

	@Override
	public boolean onUpstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( oEvent instanceof ChannelMessageEvent )
			return onUpstreamMessageEvent((ChannelMessageEvent)oEvent);
		
		return true;
	}

	
	abstract public boolean onUpstreamMessageEvent(ChannelMessageEvent event)  throws Exception;
}
