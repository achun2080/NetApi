package de.coding_bereich.net.channel;

public class ChannelStateEvent implements ChannelEvent
{
	static public enum State
	{
		OPEN, CLOSE, BIND, UNBIND, CONNECT, DISCONNECT
	}

	private ChannelEventFuture	future	= new ChannelEventFuture(this);
	private Channel				channel;
	private State					state;
	private Object					value;

	public ChannelStateEvent(Channel channel, State state, Object value)
	{
		this.channel = channel;
		this.state = state;
		this.value = value;
	}

	public State getState()
	{
		return state;
	}

	public Object getValue()
	{
		return value;
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
