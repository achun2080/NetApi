package de.coding_bereich.net.channel.pipeline;

import de.coding_bereich.net.buffer.IOBuffer;
import de.coding_bereich.net.buffer.IOBufferChain;
import de.coding_bereich.net.buffer.exception.BufferUnderflowException;
import de.coding_bereich.net.channel.Channel;
import de.coding_bereich.net.channel.ChannelMessageEvent;

public abstract class PipelineDecoder<S extends Enum<?>> extends
		PipelineMessageUpstreamHandler
{
	private S					stdState;
	private S					state;

	private int					readPos		= 0;
	private IOBufferChain	bufferChain	= new IOBufferChain();

	private boolean			errorState	= false;

	protected PipelineDecoder(S stdState)
	{
		this.stdState = state = stdState;
	}

	@Override
	public void onUpstreamMessageEvent(ChannelMessageEvent event,
													PipelineHandlerContext context)
			throws Exception
	{
		if( !(event.getMessage() instanceof IOBuffer) || errorState )
		{
			context.sendUpstream(event);
			return;
		}
		
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
					result = decode(state, bufferChain, event.getChannel());
				}
				catch(BufferUnderflowException e)
				{
					bufferChain.setReadPosition(readPos);
					break;
				}
				catch(Exception e)
				{
					errorState = true;
					context.sendUpstream(new ChannelMessageEvent(event.getChannel(),
							new PipelineDecoderErrorMessage(this, e)));

					return;
				}

				if( result == null )
					continue;

				context.sendUpstream(new ChannelMessageEvent(event.getChannel(),
						result));
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
	}

	protected void checkpoint()
	{
		state = stdState;
		readPos = bufferChain.getReadPosition();
	}

	protected void checkpoint(S state)
	{
		this.state = state;
		readPos = bufferChain.getReadPosition();
	}
	
	public boolean isInErrorState()
	{
		return errorState;
	}

	abstract protected Object decode(S state, IOBuffer buffer, Channel channel)
			throws Exception;
}
