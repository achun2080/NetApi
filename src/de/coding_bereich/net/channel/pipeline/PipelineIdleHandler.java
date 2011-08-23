package de.coding_bereich.net.channel.pipeline;

import java.util.concurrent.TimeUnit;

import de.coding_bereich.net.channel.ChannelBytesWrittenEvent;
import de.coding_bereich.net.channel.ChannelIdleEvent;
import de.coding_bereich.net.channel.ChannelStateEvent;
import de.coding_bereich.net.channel.Channel;
import de.coding_bereich.net.channel.ChannelEvent;

public class PipelineIdleHandler implements PipelineUpstreamHandler,
		PipelineDownstreamHandler, PipelineLifeCycleHandler
{
	volatile private long lastDownstreamEvent;
	volatile private long lastUpstreamEvent;
	
	private long maxUpIdle;
	private long maxDownIdle;
	private long maxAllIdle;
	
	private Channel channel = null;
	
	public PipelineIdleHandler(long maxUpIdle, long maxDownIdle, long maxAllIdle, TimeUnit unit)
	{
		this(unit.toSeconds(maxUpIdle), unit.toSeconds(maxDownIdle), unit.toSeconds(maxAllIdle));
	}
	
	public PipelineIdleHandler(long maxUpIdle, long maxDownIdle, long maxAllIdle)
	{
		this.maxUpIdle = maxUpIdle * 1000;
		this.maxDownIdle = maxDownIdle * 1000;
		this.maxAllIdle = maxAllIdle * 1000;
	}
	
	@Override
	public boolean onDownstreamEvent(ChannelEvent oEvent) throws Exception
	{		
		if( oEvent instanceof ChannelIdleEvent && channel != null )
		{
			channel.close();
			oEvent.getFuture().onSuccess();
			return false;
		}
		
		if( oEvent instanceof ChannelStateEvent )
		{
			ChannelStateEvent event = (ChannelStateEvent) oEvent;
			
			if( event.getState() == ChannelStateEvent.State.CLOSE )
				deinit();
		}
		
		return true;
	}

	@Override
	public boolean onUpstreamEvent(ChannelEvent oEvent) throws Exception
	{
		if( oEvent instanceof ChannelBytesWrittenEvent )
		{
			if( ((ChannelBytesWrittenEvent)oEvent).getByteCount() > 0 )
				lastDownstreamEvent = System.currentTimeMillis();
		}
		else
			lastUpstreamEvent = System.currentTimeMillis();
		
		if( oEvent instanceof ChannelStateEvent )
		{
			ChannelStateEvent event = (ChannelStateEvent) oEvent;
			
			if( event.getState() == ChannelStateEvent.State.OPEN )
				init(event.getChannel());
		}
		
		return true;
	}

	public void checkIdle(long time)
	{
		if( channel == null )
			deinit();
		
		System.out.println("IdleHandler.checkIdle()");
		
		long upIdle = time - lastUpstreamEvent;
		long downIdle = time - lastDownstreamEvent;
		
		if( maxUpIdle > 0 && maxUpIdle < upIdle )
			onIdle();
		else if( maxDownIdle > 0 && maxDownIdle < downIdle )
			onIdle();
		else if( maxAllIdle > 0 && maxAllIdle < downIdle && maxAllIdle < downIdle )
			onIdle();
	}
	
	private void init(Channel channel)
	{
		if( channel == null || this.channel != null )
			return;
		
		System.out.println("IdleHandler.init()");
		
		lastDownstreamEvent = lastUpstreamEvent = System.currentTimeMillis();
		
		this.channel = channel;
		PipelineIdleHandlerManager.getInstance().addHandler(this);
	}
	
	private void deinit()
	{
		if( channel == null )
			return;
		
		System.out.println("IdleHandler.deinit()");
		
		channel = null;
		PipelineIdleHandlerManager.getInstance().removeHandler(this);
	}
	
	private void onIdle()
	{
		System.out.println("IdleHandler.onIdle()");
		channel.write(new ChannelIdleEvent(channel));
	}

	@Override
	public void beforeAdd(Pipeline pipeline)
	{
		init(pipeline.getChannel());
	}

	@Override
	public void afterAdd(Pipeline pipeline)
	{
	}

	@Override
	public void beforeRemove(Pipeline pipeline)
	{
	}

	@Override
	public void afterRemove(Pipeline pipeline)
	{
		deinit();
	}
}
