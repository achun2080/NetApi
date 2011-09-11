package de.coding_bereich.net.channel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChannelEventFuture
{
	private List<ChannelEventFutureListener>	listeners	= new LinkedList<ChannelEventFutureListener>();
	private ChannelEvent								event;
	private Exception									exception	= null;
	private boolean									success		= false;
	private boolean									cancel		= false;

	private boolean									done			= false;

	public ChannelEventFuture(ChannelEvent event)
	{
		this.event = event;
	}

	public ChannelEvent getEvent()
	{
		return event;
	}

	synchronized public void waitFor() throws InterruptedException
	{
		wait();
	}

	synchronized public void addListener(ChannelEventFutureListener listener)
	{
		listeners.add(listener);
	}

	synchronized public void removeListener(ChannelEventFutureListener listener)
	{
		listeners.remove(listener);
	}

	public Exception getException()
	{
		return exception;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public boolean isCancel()
	{
		return cancel;
	}

	public boolean isDone()
	{
		return done;
	}

	synchronized public boolean onException(Exception exception)
	{
		if( done )
			return false;
		done = true;

		this.exception = exception;

		onAction();

		return true;
	}

	synchronized public boolean onSuccess()
	{
		if( done )
			return false;
		done = true;

		success = true;
		onAction();

		return true;
	}

	/**
	 * Bricht alle IO-Tasks ab.
	 * @return
	 */
	synchronized public boolean cancel()
	{
		if( done )
			return false;
		done = true;

		cancel = true;
		onAction();

		return true;
	}

	private void onAction()
	{
		notifyAll();

		Iterator<ChannelEventFutureListener> it = listeners.iterator();

		while( it.hasNext() )
			it.next().onAction(this);
	}
}
