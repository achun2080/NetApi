package de.coding_bereich.net.channel;

public class ChannelEventAdapter implements ChannelEvent
{
	protected ChannelEventFuture	future	= new ChannelEventFuture(this);
	protected Channel					channel	= null;

	public ChannelEventAdapter(Channel channel)
	{
		this.channel = channel;
	}

	@Override
	public Channel getChannel()
	{
		return channel;
	}

	@Override
	public ChannelEventFuture getFuture()
	{
		return future;
	}
}
