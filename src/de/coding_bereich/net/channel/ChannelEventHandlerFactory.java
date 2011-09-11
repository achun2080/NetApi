package de.coding_bereich.net.channel;

/**
 * Erzeugt, bei jeder neuen Channel, einen neuen Handler, zur Verarbeitung der
 * ChannelEvents.
 * 
 * @author Thomas
 * 
 */

public interface ChannelEventHandlerFactory
{
	/**
	 * Gibt den neuen Handler zurück.
	 * 
	 * @return Der Handler.
	 */
	public ChannelHandler getHandler(Channel channel);
}
