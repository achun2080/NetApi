package de.coding_bereich.net.channel;

import java.net.SocketAddress;

/**
 * Definition einer Verbindung.
 * @author Thomas
 *
 */
public interface Channel
{
	public void setHandler(ChannelHandler handler);
	public ChannelHandler getHandler();

	/**
	 * Das endgültige Verschicken(verarbeitung) des Events.
	 * @param event Das Events.
	 */
	public void finalWrite(ChannelEvent event);
	
	/**
	 * Löst die Verarbeitung durch einen Handler aus.
	 * @param event Das Event.
	 */
	public void write(ChannelEvent event);
	
	/**
	 * Löst die Verarbeitung durch einen Handler aus.
	 * @param message Das Nachrichten-Objekt.
	 */
	public ChannelEventFuture write(Object message);
	
	/**
	 * @param event
	 */
	public void fireIncomingEvent(ChannelEvent event);

	public boolean isOpen();
	public ChannelEventFuture close();
	
	public boolean isBound();
	public ChannelEventFuture bind(SocketAddress addr);
	public ChannelEventFuture unbind();
	
	public boolean isConnected();
	public ChannelEventFuture connect(SocketAddress addr);
	public ChannelEventFuture disconnect();
	
	public boolean isWritable();
	public boolean isReadable();
	
	public SocketAddress getLocalAddress();
	public SocketAddress getRemoteAddress();
}
