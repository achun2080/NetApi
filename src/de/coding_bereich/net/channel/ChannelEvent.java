package de.coding_bereich.net.channel;

/**
 * Grund-Channelevent.
 * @author Thomas
 *
 */
public interface ChannelEvent
{
	public Channel getChannel();
	public ChannelEventFuture getFuture();
}
