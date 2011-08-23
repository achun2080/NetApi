package de.coding_bereich.net.channel;

public class ChannelBytesWrittenEvent extends ChannelEventAdapter
{
	private int	byteCount;

	public ChannelBytesWrittenEvent(Channel channel, int byteCount)
	{
		super(channel);
		this.byteCount = byteCount;
	}
	
	public int getByteCount()
	{
		return byteCount;
	}
}
