package de.coding_bereich.net.channel;

import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;

public abstract class AbstractNIOChannel extends AbstractChannel implements
		NIOChannel
{
	protected Channel			nioChannel;
	protected NIODispatcher	dispatcher;

	@Override
	public void finalWrite(ChannelEvent event)
	{
		if( !writeQueue.isEmpty() )
		{
			writeQueue.offer(event);
			return;
		}

		boolean result = false;
		try
		{
			result = handleOutgoingEvent(event);
		}
		catch(Exception e)
		{
			event.getFuture().onException(e);
			return;
		}

		if( !result && isWritable() )
		{
			writeQueue.offer(event);
			if( dispatcher != null )
				dispatcher.setInterestOps(this, dispatcher.getInterestOps(this)
						| SelectionKey.OP_WRITE);
		}

		if( result )
			event.getFuture().onSuccess();
	}

	@Override
	public void onNIOEvent(SelectionKey key)
	{
		if( key.isWritable() )
		{
			boolean removeEvent = false;

			ChannelEvent event = writeQueue.peek();
			try
			{
				if( removeEvent = (event != null && handleOutgoingEvent(event)) )
					event.getFuture().onSuccess();
			}
			catch(Exception e)
			{
				removeEvent = true;
				if( event != null )
					event.getFuture().onException(e);
			}

			if( removeEvent )
			{
				writeQueue.remove();

				if( writeQueue.isEmpty() )
					dispatcher.setInterestOps(this, dispatcher.getInterestOps(this)
							^ SelectionKey.OP_WRITE);
			}
		}
	}

	synchronized protected void close0() throws Exception
	{
		super.close0();
		open = false;

		dispatcher.removeChannel(this);
		nioChannel.close();

		return;
	}

	@Override
	public Channel getNIOChannel()
	{
		return nioChannel;
	}

	@Override
	public NIODispatcher getDispatcher()
	{
		return dispatcher;
	}

	@Override
	public void setDispatcher(NIODispatcher dispatcher)
	{
		this.dispatcher = dispatcher;
	}
}
