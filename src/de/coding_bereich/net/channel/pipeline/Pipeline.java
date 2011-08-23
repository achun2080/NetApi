package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.Channel;
import de.coding_bereich.net.channel.ChannelHandler;


public class Pipeline
	implements ChannelHandler
{
	private Channel channel;
	
	private Entry firstEntry;
	private Entry lastEntry;
	
	@Override
	public void onIncomingMessage(ChannelEvent event)
	{
		for(Entry entry = firstEntry; entry != null; entry = entry.next)
		{
			if( !(entry.handler instanceof PipelineUpstreamHandler) ) 
				continue;
			
			PipelineUpstreamHandler handler = (PipelineUpstreamHandler) entry.handler;
			
			try
			{
				if( !handler.onUpstreamEvent(event) )
				{
					event.getFuture().onCancel();
					return;
				}
			}
			catch(Exception e)
			{
				event.getFuture().onException(e);
				return;
			}
		}
		
		event.getFuture().onCancel();
	}

	@Override
	public void onOutgoingMessage(ChannelEvent event)
	{
		for(Entry entry = lastEntry; entry != null; entry = entry.prev)
		{
			if( !(entry.handler instanceof PipelineDownstreamHandler) ) 
				continue;
			
			PipelineDownstreamHandler handler = (PipelineDownstreamHandler) entry.handler;
			
			try
			{
				if( !handler.onDownstreamEvent(event) )
				{
					event.getFuture().onCancel();
					return;
				}
			}
			catch(Exception e)
			{
				event.getFuture().onException(e);
				return;
			}
		}
		
		event.getChannel().finalWrite(event);
	}
	
	public void addFirst(String name, PipelineHandler handler)
	{
		Entry old = firstEntry;
		
		if( old == null )
		{
			firstEntry = lastEntry = new Entry(name, null, null, handler);
			return;
		}
		
		callBeforeAdd(handler);
		firstEntry = new Entry(name, null, old, handler);
		callAfterAdd(handler);
	}
	
	public void addLast(String name, PipelineHandler handler)
	{
		Entry old = lastEntry;
		
		if( old == null )
		{
			firstEntry = lastEntry = new Entry(name, null, null, handler);
			return;
		}
		
		callBeforeAdd(handler);
		lastEntry = old.next = new Entry(name, old, null, handler);
		callAfterAdd(handler);
	}
	
	public void replace(String name, String newName, PipelineHandler handler)
	{
		Entry entry = firstEntry;
		
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
		
		//TODO: exception
	}
	
	public void addAfter(String name, String newName, PipelineHandler handler)
	{
		Entry entry = firstEntry;
		
		while( entry != null )
		{
			if( entry.name.equals(name) )
			{
				callBeforeAdd(handler);
				
				Entry newEntry = new Entry(newName, entry, entry.next, handler);
				entry.next.prev = newEntry;
				entry.next = newEntry;
				
				callAfterAdd(handler);
				
				return;
			}
			
			entry = entry.next;
		}
		
		//TODO: exception
	}
	
	public void addBefore(String name, String newName, PipelineHandler handler)
	{
		Entry entryBefore = null;
		Entry entry = firstEntry;
		
		while( entry != null )
		{
			if( entry.name.equals(name) )
			{
				if( entryBefore == null )
					addFirst(name, handler);
				else
				{
					callBeforeAdd(handler);
					
					Entry newEntry = new Entry(newName, entryBefore, entry, handler);
					entryBefore.next.prev = newEntry;
					entryBefore.next = newEntry;
					
					callAfterAdd(handler);
				}
				
				return;
			}
			
			entryBefore = entry;
			entry = entry.next;
		}
		
		//TODO: exception
	}
	
	
	private void callBeforeAdd(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;
		
		((PipelineLifeCycleHandler)handler).beforeAdd(this);
	}
	
	private void callAfterAdd(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;
		
		((PipelineLifeCycleHandler)handler).afterAdd(this);
	}
	
	private void callBeforeRemove(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;
		
		((PipelineLifeCycleHandler)handler).beforeRemove(this);
	}
	
	private void callAfterRemove(PipelineHandler handler)
	{
		if( !(handler instanceof PipelineLifeCycleHandler) )
			return;
		
		((PipelineLifeCycleHandler)handler).afterRemove(this);
	}
	
	@Override
	public Channel getChannel()
	{
		return channel;
	}

	@Override
	public void setChannel(Channel channel)
	{
		this.channel = channel;
	}
	
	private class Entry
	{
		public String name;
		public Entry prev;
		public Entry next;
		public PipelineHandler handler;
		
		public Entry(String name, Entry prev, Entry next, PipelineHandler handler)
		{
			if( name == null || handler == null )
				throw new NullPointerException();
				
			this.name = name;
			this.prev = prev;
			this.next = next;
			this.handler = handler;
		}
	}
}
