package de.coding_bereich.net.channel;

import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptableNIOChannel extends AbstractNIOChannel
{
	private ChannelEventHandlerFactory	handlerFactory;

	public AcceptableNIOChannel(SocketAddress address, NIODispatcher dispatcher)
			throws Exception
	{
		super();
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.socket().bind(address);

		channel.configureBlocking(false);

		this.nioChannel = channel;

		registerToDispatcher(dispatcher);

		fireOpen();

		fireBind(address);
	}

	public AcceptableNIOChannel(NIODispatcher dispatcher) throws Exception
	{
		super();
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);

		this.nioChannel = channel;

		registerToDispatcher(dispatcher);

		fireOpen();
	}

	public AcceptableNIOChannel() throws Exception
	{
		super();
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);

		this.nioChannel = channel;

		fireOpen();
	}

	@Override
	public void onNIOEvent(SelectionKey key)
	{
		try
		{
			if( key.isAcceptable() )
			{
				SocketChannel socketChannel = ((ServerSocketChannel) nioChannel)
						.accept();

				Channel channel = new ReadWritableNIOChannel(socketChannel,
						dispatcher);
				if( handlerFactory != null )
					channel.setHandler(handlerFactory.getHandler(channel));
			}
		}
		catch(Exception e)
		{
			// TODO ....
		}
	}

	@Override
	protected boolean handleOutgoingEvent(ChannelEvent event) throws Exception
	{
		super.handleOutgoingEvent(event);
		return true;
	}

	@Override
	public void fireIncomingEvent(ChannelEvent event)
	{
		ChannelHandlerWorker.getInstance().onIncomingMessage(event);
	}

	@Override
	public void registerToDispatcher(NIODispatcher dispatcher) throws Exception
	{
		this.dispatcher = dispatcher;
		dispatcher.addChannel(this, SelectionKey.OP_ACCEPT);
	}

	public void setHandlerFactory(ChannelEventHandlerFactory factory)
	{
		handlerFactory = factory;
	}

	@Override
	public boolean isBound()
	{
		return isOpen() && ((ServerSocketChannel) nioChannel).socket().isBound();
	}

	@Override
	public boolean isConnected()
	{
		return false;
	}

	@Override
	protected synchronized void close0() throws Exception
	{
		super.close0();
		fireClose();
	}

	@Override
	protected void connect0(SocketAddress addr) throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void disconnect0() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void bind0(SocketAddress addr) throws Exception
	{
		((ServerSocketChannel) nioChannel).socket().bind(addr);
		fireBind(addr);
	}

	@Override
	protected void unbind0() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWritable()
	{
		return false;
	}

	@Override
	public boolean isReadable()
	{
		return false;
	}

	@Override
	public SocketAddress getLocalAddress()
	{
		return ((SocketChannel) nioChannel).socket().getLocalSocketAddress();
	}

	@Override
	public SocketAddress getRemoteAddress()
	{
		return null;
	}
}
