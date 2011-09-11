package de.coding_bereich.net.channel;

/**
 * Verarbeitet die {@link ChannelEvent}s.
 * 
 * @author Thomas
 * 
 */
public interface ChannelHandler
{
	/**
	 * Wird bei ankommendem Event aufgerufen.
	 * 
	 * @param event
	 *           Das Event.
	 */
	public void onIncomingMessage(ChannelEvent event);

	/**
	 * Wird bei ausgehendem Event aufgerufen.
	 * 
	 * @param event
	 *           Das Event.
	 */
	public void onOutgoingMessage(ChannelEvent event);

	public Channel getChannel();
}
