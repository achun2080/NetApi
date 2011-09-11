package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.Channel;
import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.ChannelEventFuture;
import de.coding_bereich.net.channel.ChannelHandler;
import de.coding_bereich.net.channel.ChannelMessageEvent;

public class Pipeline implements ChannelHandler
{
	private Channel			channel;

	private HandlerContext	firstEntry;
	private HandlerContext	lastEntry;

	public Pipeline(Channel channel)
	{
		this.channel = channel;
	}

	@Override
	public void onIncomingMessage(ChannelEvent event)
	{
		try
		{
			if( firstEntry != null )
				if( firstEntry.isUpstreamHander() )
				{
					PipelineUpstreamHandler handler = (PipelineUpstreamHandler) firstEntry
							.getHandler();
					handler.onUpstreamEvent(event, firstEntry);
				}
				else
					firstEntry.sendUpstream(event);
		}
		catch(Exception e)
		{
			event.getFuture().onException(e);
		}
	}

	@Override
	public void onOutgoingMessage(ChannelEvent event)
	{
		try
		{
			if( lastEntry != null )
				if( lastEntry.isDownstreamHander() )
				{
					PipelineDownstreamHandler handler = (PipelineDownstreamHandler) lastEntry
							.getHandler();
					handler.onDownstreamEvent(event, lastEntry);
				}
				else
					lastEntry.sendDownstream(event);
			else
				channel.finalWrite(event);
		}
		catch(Exception e)
		{
			event.getFuture().onException(e);
		}
	}

	public void addFirst(String name, PipelineHandler handler)
	{
		HandlerContext old = firstEntry;

		if( old == null )
		{
			firstEntry = lastEntry = new HandlerContext(name, null, null, handler,
					this);
			return;
		}

		callBeforeAdd(handler);
		firstEntry = new HandlerContext(name, null, old, handler, this);
		callAfterAdd(handler);
	}

	public void addLast(String name, PipelineHandler handler)
	{
		HandlerContext old = lastEntry;

		if( old == null )
		{
			firstEntry = lastEntry = new HandlerContext(name, null, null, handler,
					this);
			return;
		}

		callBeforeAdd(handler);
		lastEntry = old.next = new HandlerContext(name, old, null, handler, this);
		callAfterAdd(handler);
	}

	public void replace(String name, String newName, PipelineHandler handler)
	{
		HandlerContext entry = firstEntry;

		while( entry != null )
		{
			if( entry.name.equals(name) )
			{
				PipelineHandler oldHandler = entry.handler;

				callBeforeRemove(oldHandler);
				callBeforeAdd(handler);

				entry.name = newName;
				entry.handler = handler;

				callAfterAdd(handler);
				callAfterRemove(oldHandler);

				return;
			}

			entry = entry.next;
		}

		// TODO: exception
	}

	public void addAfter(String name, String newName, PipelineHandler handler)
	{
		HandlerContext entry = firstEntry;

		while( entry != null )
		{
			if( entry.name.equals(name) )
			{
				callBeforeAdd(handler);

				HandlerContext newEntry = new HandlerContext(newName, entry,
						entry.next, handler, this);
				entry.next.prev = newEntry;
				entry.next = newEntry;

				callAfterAdd(handler);

				return;
			}

			entry = entry.next;
		}

		// TODO: exception
	}

	public void addBefore(String name, String newName, PipelineHandler handler)
	{
		HandlerContext entryBefore = null;
		HandlerContext entry = firstEntry;

		while( entry != null )
		{
			if( entry.name.equals(name) )
			{
				if( entryBefore == null )
					addFirst(name, handler);
				else
				{
					callBeforeAdd(handler);

					HandlerContext newEntry = new HandlerContext(newName,
							entryBefore, entry, handler, this);
					entryBefore.next.prev = newEntry;
					entryBefore.next = newEntry;

					callAfterAdd(handler);
				}

				return;
			}

			entryBefore = entry;
			entry = entry.next;
		}

		// TODO: exception
	}

	private void callBeforeAdd(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;

		((PipelineLifeCycleHandler) handler).beforeAdd(this);
	}

	private void callAfterAdd(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;

		((PipelineLifeCycleHandler) handler).afterAdd(this);
	}

	private void callBeforeRemove(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;

		((PipelineLifeCycleHandler) handler).beforeRemove(this);
	}

	private void callAfterRemove(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;

		((PipelineLifeCycleHandler) handler).afterRemove(this);
	}

	@Override
	public Channel getChannel()
	{
		return channel;
	}

	private class HandlerContext implements PipelineHandlerContext
	{
		public String				name;
		public HandlerContext	prev;
		public HandlerContext	next;

		private PipelineHandler	handler;
		private Pipeline			pipeline;

		public HandlerContext(String name, HandlerContext prev,
				HandlerContext next, PipelineHandler handler, Pipeline pipeline)
		{
			if( name == null || handler == null )
				throw new NullPointerException();

			this.name = name;
			this.prev = prev;
			this.next = next;
			this.handler = handler;
			this.pipeline = pipeline;
		}

		@Override
		public boolean isUpstreamHander()
		{
			return handler instanceof PipelineUpstreamHandler;
		}

		@Override
		public boolean isDownstreamHander()
		{
			return handler instanceof PipelineDownstreamHandler;
		}

		@Override
		public void sendDownstream(ChannelEvent event) throws Exception
		{
			HandlerContext entry = prev;

			while( entry != null )
			{
				if( entry.handler instanceof PipelineDownstreamHandler )
				{
					PipelineDownstreamHandler upstreamHandler = (PipelineDownstreamHandler) entry.handler;
					upstreamHandler.onDownstreamEvent(event, entry);
					return;
				}

				entry = entry.prev;
			}

			pipeline.getChannel().finalWrite(event);
		}

		@Override
		public void sendUpstream(ChannelEvent event) throws Exception
		{
			HandlerContext entry = next;

			while( entry != null )
			{
				if( entry.handler instanceof PipelineUpstreamHandler )
				{
					PipelineUpstreamHandler upstreamHandler = (PipelineUpstreamHandler) entry.handler;
					upstreamHandler.onUpstreamEvent(event, entry);
					return;
				}

				entry = entry.next;
			}
		}

		@Override
		public Pipeline getPipline()
		{
			return pipeline;
		}

		@Override
		public PipelineHandler getHandler()
		{
			return handler;
		}

		@Override
		public ChannelEventFuture sendDownstream(Object message) throws Exception
		{
			ChannelMessageEvent event = new ChannelMessageEvent(
					pipeline.getChannel(), message);
			sendDownstream(event);
			return event.getFuture();
		}

		@Override
		public ChannelEventFuture sendUpstream(Object message) throws Exception
		{
			ChannelMessageEvent event = new ChannelMessageEvent(
					pipeline.getChannel(), message);
			sendUpstream(event);
			return event.getFuture();
		}
	}
}