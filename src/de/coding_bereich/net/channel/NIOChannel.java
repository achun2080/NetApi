package de.coding_bereich.net.channel;

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;


public interface NIOChannel extends de.coding_bereich.net.channel.Channel
{
	public void registerToDispatcher(NIODispatcher dispatcher) throws Exception;
	public void onNIOEvent(SelectionKey key);
	public Channel getNIOChannel();
	public NIODispatcher getDispatcher();
	public void setDispatcher(NIODispatcher dispatcher);
}
