package de.coding_bereich.net.channel.pipeline;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.coding_bereich.net.WatchdogTask;
import de.coding_bereich.net.Watchdog;

public class PipelineIdleHandlerManager implements WatchdogTask
{
	static final private PipelineIdleHandlerManager instance = new PipelineIdleHandlerManager();
	
	final private List<PipelineIdleHandler> handlers = new LinkedList<PipelineIdleHandler>();
	
	private PipelineIdleHandlerManager()
	{
		Watchdog.getInstance().addTask(this, 1000);
	}

	@Override
	synchronized public void runWatchdogTask(long time)
	{
		Iterator<PipelineIdleHandler> it = handlers.iterator();
		while(it.hasNext())
			it.next().checkIdle(time);
	}

	public static PipelineIdleHandlerManager getInstance()
	{
		return instance;
	}
	
	synchronized public void addHandler(PipelineIdleHandler idleHandler)
	{
		handlers.add(idleHandler);
	}
	
	synchronized public void removeHandler(PipelineIdleHandler idleHandler)
	{
		handlers.remove(idleHandler);
		System.out.println(handlers.size());
	}
}
