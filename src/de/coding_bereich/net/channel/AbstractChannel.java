package de.coding_bereich.net.channel;

import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractChannel implements Channel
{
	protected boolean					open			= true;
	protected ChannelHandler		handler;
	protected Queue<ChannelEvent>	writeQueue	= new LinkedList<ChannelEvent>();

	@Override
	public void setHandler(ChannelHandler handler)
	{
		this.handler = handler;
	}

	@Override
	public ChannelHandler getHandler()
	{
		return handler;
	}

	@Override
	public void finalWrite(ChannelEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOpen()
	{
		return open;
	}

	@Override
	public ChannelEventFuture close()
	{
		ChannelStateEvent event = new ChannelStateEvent(this,
				ChannelStateEvent.State.CLOSE, false);
		write(event);
		return event.getFuture();
	}

	@Override
	public ChannelEventFuture bind(SocketAddress addr)
	{
		ChannelStateEvent event = new ChannelStateEvent(this,
				ChannelStateEvent.State.BIND, addr);
		write(event);
		return event.getFuture();
	}

	@Override
	public ChannelEventFuture unbind()
	{
		ChannelStateEvent event = new ChannelStateEvent(this,
				ChannelStateEvent.State.UNBIND, null);
		write(event);
		return event.getFuture();
	}

	@Override
	public ChannelEventFuture connect(SocketAddress addr)
	{
		ChannelStateEvent event = new ChannelStateEvent(this,
				ChannelStateEvent.State.CONNECT, addr);
		write(event);
		return event.getFuture();
	}

	@Override
	public ChannelEventFuture disconnect()
	{
		ChannelStateEvent event = new ChannelStateEvent(this,
				ChannelStateEvent.State.DISCONNECT, null);
		write(event);
		return event.getFuture();
	}

	@Override
	public void write(ChannelEvent event)
	{
		ChannelHandlerWorker.getInstance().onOutgoingMessage(event);
	}

	@Override
	public ChannelEventFuture write(Object message)
	{
		ChannelMessageEvent event = new ChannelMessageEvent(this, message);
		write(event);
		return event.getFuture();
	}

	@Override
	public void fireIncomingEvent(ChannelEvent event)
	{
		ChannelHandlerWorker.getInstance().onIncomingMessage(event);
	}

	/**
	 * @return =true, dann event verarbeitet, sonst nicht.
	 */
	protected boolean handleOutgoingEvent(ChannelEvent event) throws Exception
	{
		if( !(event instanceof ChannelStateEvent) )
			return false;

		ChannelStateEvent stateEvent = (ChannelStateEvent) event;

		switch(stateEvent.getState())
		{
			case CLOSE:
				close0();
				break;

			case CONNECT:
				if( stateEvent.getValue() instanceof SocketAddress )
					connect0((SocketAddress) stateEvent.getValue());
				break;

			case DISCONNECT:
				disconnect();
				break;

			case BIND:
				if( stateEvent.getValue() instanceof SocketAddress )
					bind0((SocketAddress) stateEvent.getValue());
				break;

			case UNBIND:
				unbind0();
				break;
		}

		event.getFuture().onSuccess();
		return true;
	}

	synchronized protected void close0() throws Exception
	{
		if( !isOpen() )
			throw new ClosedChannelException();

		open = false;
	}

	abstract protected void bind0(SocketAddress addr) throws Exception;

	abstract protected void unbind0() throws Exception;

	abstract protected void connect0(SocketAddress addr) throws Exception;

	abstract protected void disconnect0() throws Exception;

	protected void fireOpen()
	{
		fireIncomingEvent(new ChannelStateEvent(this,
				ChannelStateEvent.State.OPEN, null));
	}

	protected void fireClose()
	{
		fireIncomingEvent(new ChannelStateEvent(this,
				ChannelStateEvent.State.CLOSE, null));
	}

	protected void fireBind(SocketAddress addr)
	{
		fireIncomingEvent(new ChannelStateEvent(this,
				ChannelStateEvent.State.BIND, addr));
	}

	protected void fireUnbind()
	{
		fireIncomingEvent(new ChannelStateEvent(this,
				ChannelStateEvent.State.UNBIND, null));
	}

	protected void fireConnect(SocketAddress addr)
	{
		fireIncomingEvent(new ChannelStateEvent(this,
				ChannelStateEvent.State.CONNECT, addr));
	}

	protected void fireDisconnect()
	{
		fireIncomingEvent(new ChannelStateEvent(this,
				ChannelStateEvent.State.DISCONNECT, null));
	}
}