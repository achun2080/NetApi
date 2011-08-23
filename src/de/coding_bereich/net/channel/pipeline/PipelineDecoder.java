package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.buffer.IOBuffer;
import de.coding_bereich.net.buffer.IOBufferChain;
import de.coding_bereich.net.buffer.exception.BufferUnderflowException;
import de.coding_bereich.net.channel.ChannelEvent;
import de.coding_bereich.net.channel.ChannelMessageEvent;

public abstract class PipelineDecoder<S extends Enum<?>> implements
		PipelineUpstreamHandler
{
	private S			stdState;
	private S			state;

	private int			readPos		= 0;

	private IOBufferChain	bufferChain	= new IOBufferChain();

	protected PipelineDecoder(S stdState)
	{
		this.stdState = state = stdState;
	}

	@Override
	public boolean onUpstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( !(oEvent instanceof ChannelMessageEvent) )
			return false;
		
		ChannelMessageEvent event = (ChannelMessageEvent) oEvent;
		
		if( !(event.getMessage() instanceof IOBuffer) )
			return true;
		
		IOBuffer buffer = (IOBuffer) event.getMessage();
		
		bufferChain.getLock().lock();

		try
		{
			bufferChain.addBuffer(buffer);
			
			event.getFuture().onSuccess();
			
			while( bufferChain.hasReadableBytes() )
			{
				Object result;
				
				try
				{
					result = decode(state, bufferChain, event);
				}
				catch(BufferUnderflowException e)
				{		
					bufferChain.setReadPosition(readPos);
					break;
				}
				
				if( result == null )
					continue;

				state = stdState;
				
				event.getChannel().fireIncomingEvent(
						new ChannelMessageEvent(event.getChannel(), result));
			}
			
			if( !bufferChain.hasReadableBytes() )
			{
				readPos = 0;
				bufferChain.clear();
			}
		}
		finally
		{
			bufferChain.getLock().unlock();
		}
		return false;
	}

	protected void checkpoint()
	{
		state = stdState;
	}

	protected void checkpoint(S state)
	{
		this.state = state;
		readPos = bufferChain.getReadPosition();
	}

	abstract protected Object decode(S state, IOBuffer buffer, ChannelMessageEvent event)
			throws Exception;
}
