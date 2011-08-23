package de.coding_bereich.net.channel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

import de.coding_bereich.net.buffer.DynamicIOBuffer;
import de.coding_bereich.net.buffer.IOBuffer;

public class ReadWritableNIOChannel extends AbstractNIOChannel
{
	public ReadWritableNIOChannel(SocketChannel channel, NIODispatcher dispatcher)
			throws IOException
	{
		channel.configureBlocking(false);
		this.nioChannel = channel;

		registerToDispatcher(dispatcher);
		
		fireOpen();
		
		if( isBound() )
			fireBind(getLocalAddress());
		
		if( isConnected() )
			fireConnect(getRemoteAddress());
	}

	public ReadWritableNIOChannel(SocketChannel channel) throws IOException
	{
		channel.configureBlocking(false);
		this.nioChannel = channel;
		
		fireOpen();
		
		if( isBound() )
			fireBind(getLocalAddress());
		
		if( isConnected() )
			fireConnect(getRemoteAddress());
	}

	public ReadWritableNIOChannel(SocketAddress addr) throws IOException
	{
		SocketChannel channel = SocketChannel.open(addr);
		channel.configureBlocking(false);
		this.nioChannel = channel;
		
		fireOpen();
		
		fireBind(getLocalAddress());
		
		fireConnect(addr);
	}

	public ReadWritableNIOChannel() throws IOException
	{
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.nioChannel = channel;
	}

	@Override
	public void onNIOEvent(SelectionKey key)
	{
		try
		{
			super.onNIOEvent(key);

			if( key.isReadable() )
			{
				boolean eof;
				IOBuffer inputBuffer = DynamicIOBuffer.create();
				ByteBuffer byteBuffer = dispatcher.pollByteBuffer();
				try
				{
					eof = inputBuffer.write((ReadableByteChannel) nioChannel,
							byteBuffer);
				}
				finally
				{
					dispatcher.offerByteBuffer(byteBuffer);
				}

				if( eof )
				{
					try
					{
						close0();
					}
					catch(Exception e)
					{
					}
				}

				ChannelMessageEvent event = new ChannelMessageEvent(this,
						inputBuffer);
				event.getFuture().addListener(new ChannelEventFutureListener()
				{
					@Override
					public void onAction(ChannelEventFuture future)
					{
						((IOBuffer) ((ChannelMessageEvent) future.getEvent())
								.getMessage()).free();
					}
				});

				fireIncomingEvent(event);
			}
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			try
			{
				close0();
			}
			catch(Exception e1)
			{
			}
		}
	}

	@Override
	protected boolean handleOutgoingEvent(ChannelEvent oEvent) throws Exception
	{
		if( super.handleOutgoingEvent(oEvent) )
			return true;

		if( !(oEvent instanceof ChannelMessageEvent) )
			return false;

		ChannelMessageEvent event = (ChannelMessageEvent) oEvent;

		if( !(event.getMessage() instanceof IOBuffer) )
			throw new IllegalStateException("get an unexpected message type");

		IOBuffer buffer = (IOBuffer) event.getMessage();

		ByteBuffer byteBuffer = dispatcher.pollByteBuffer();
		try
		{
			int writtenBytes = buffer.read((WritableByteChannel) nioChannel,
					byteBuffer);

			if( writtenBytes > 0 )
				fireIncomingEvent(new ChannelBytesWrittenEvent(this, writtenBytes));
		}
		finally
		{
			dispatcher.offerByteBuffer(byteBuffer);
		}

		if( !buffer.hasReadableBytes() )
			buffer.free();

		return !buffer.hasReadableBytes();
	}

	@Override
	public void registerToDispatcher(NIODispatcher dispatcher)
			throws ClosedChannelException
	{
		this.dispatcher = dispatcher;
		dispatcher.addChannel(this, SelectionKey.OP_READ);
	}

	@Override
	public boolean isBound()
	{
		return isOpen() && ((SocketChannel) nioChannel).socket().isBound();
	}

	@Override
	public boolean isConnected()
	{
		return isOpen() && ((SocketChannel) nioChannel).isConnected();
	}

	@Override
	protected void close0() throws Exception
	{
		super.close0();
		fireClose();
	}
	
	@Override
	protected void bind0(SocketAddress addr) throws Exception
	{
		((SocketChannel) nioChannel).socket().bind(addr);
		fireBind(addr);
	}

	@Override
	protected void unbind0() throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void connect0(SocketAddress addr) throws Exception
	{
		((SocketChannel) nioChannel).socket().connect(addr);
		fireConnect(addr);
	}

	@Override
	protected void disconnect0() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWritable()
	{
		return true;
	}

	@Override
	public boolean isReadable()
	{
		return true;
	}

	@Override
	public SocketAddress getLocalAddress()
	{
		return ((SocketChannel) nioChannel).socket().getLocalSocketAddress();
	}

	@Override
	public SocketAddress getRemoteAddress()
	{
		return ((SocketChannel) nioChannel).socket().getRemoteSocketAddress();
	}
}
