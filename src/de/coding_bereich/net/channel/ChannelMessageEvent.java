package de.coding_bereich.net.channel;

public class ChannelMessageEvent extends ChannelEventAdapter
{
	private Object message = null;
	
	public ChannelMessageEvent(Channel channel, Object message)
	{
		super(channel);
		this.message = message;
	}


	public Object getMessage()
	{
		return message;
	}
}
