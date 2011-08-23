package de.coding_bereich.net.channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NIODispatcher extends Thread
{
	private Selector									selector;
	private Thread										thread			= Thread
																						.currentThread();

	private Lock										selectorLock	= new ReentrantLock();

	private ConcurrentLinkedQueue<ByteBuffer>	byteBuffers		= new ConcurrentLinkedQueue<ByteBuffer>();

	public NIODispatcher() throws IOException
	{
		selector = Selector.open();
	}

	public void setInterestOps(NIOChannel channel, int ops)
	{
		if( !isInDispatcherThread() )
		{
			selectorLock.lock();
			selector.wakeup();
		}

		try
		{
			SelectionKey key = ((SelectableChannel) channel.getNIOChannel())
					.keyFor(selector);
			if( key == null )
				return; // TODO: Exception werfen ?

			key.interestOps(ops);

			if( !isInDispatcherThread() )
				selector.wakeup();
		}
		finally
		{
			if( !isInDispatcherThread() )
				selectorLock.unlock();
		}
	}

	public int getInterestOps(NIOChannel channel)
	{
		SelectionKey key = ((SelectableChannel) channel.getNIOChannel())
				.keyFor(selector);
		if( key == null )
			return 0; // TODO: Exception werfen ?

		return key.interestOps();
	}

	public void addChannel(NIOChannel channel, int ops)
			throws ClosedChannelException
	{
		if( !isInDispatcherThread() )
		{
			selectorLock.lock();
			selector.wakeup();
		}

		try
		{
			SelectableChannel selChannel = (SelectableChannel) channel
					.getNIOChannel();
			selChannel.register(selector, ops, channel);
		}
		finally
		{
			if( !isInDispatcherThread() )
				selectorLock.unlock();
		}
	}

	public void removeChannel(NIOChannel channel)
	{
		if( !isInDispatcherThread() )
		{
			selectorLock.lock();
			selector.wakeup();
		}

		try
		{
			SelectionKey key = ((SelectableChannel) channel.getNIOChannel())
					.keyFor(selector);
			if( key == null )
				return; // TODO: Exception werfen ?

			key.cancel();
		}
		finally
		{
			if( !isInDispatcherThread() )
				selectorLock.unlock();
		}
	}

	@Override
	public void run()
	{
		thread = Thread.currentThread();

		while( true )
		{
			try
			{
				selectorLock.lock();
				selectorLock.unlock();

				if( selector.select() == 0 )
					continue;

				Iterator<SelectionKey> it = selector.selectedKeys().iterator();

				while( it.hasNext() )
				{
					SelectionKey key = it.next();
					it.remove();

					NIOChannel channel = ((NIOChannel) key.attachment());

					if( !key.isValid() )
						channel.close();

					channel.onNIOEvent(key);
				}
			}
			catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isInDispatcherThread()
	{
		return thread == Thread.currentThread();
	}

	public ByteBuffer pollByteBuffer()
	{
		ByteBuffer bb;

		bb = byteBuffers.poll();

		if( bb == null )
			bb = ByteBuffer.allocateDirect(4096);

		return bb;
	}

	public void offerByteBuffer(ByteBuffer buffer)
	{
		if( byteBuffers.size() < 10 )
			byteBuffers.offer(buffer);
	}
}
