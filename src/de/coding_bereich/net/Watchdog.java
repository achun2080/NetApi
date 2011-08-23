package de.coding_bereich.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Cronjob-Aufgabenverwaltung.
 * Ruft, je nach Einstellungen, 
 * @author Thomas
 *
 */
public class Watchdog
	implements Runnable
{	
	static final private Watchdog instance = new Watchdog();
	
	private Map<WatchdogTask, WatchdogEntry> taskList = new HashMap<WatchdogTask, WatchdogEntry>();
	
	private WatchdogEntry nextTask = null;
	private long nextTime = -1;
	
	static
	{
		new Thread(instance).start();
	}
	
	private Watchdog()
	{
	}
	
	public static Watchdog getInstance()
	{
		return instance;
	}
	
	/**
	 * Fügt einen Listener hinzu.
	 * Dieser wird alle <em>intervalMillis</em> Millisekunden aufgerufen.
	 * @param task Der Listener.
	 * @param intervalMillis Millisekunden angabe.
	 */
	public void addTask(WatchdogTask task, long intervalMillis)
	{
		synchronized(taskList)
		{
			taskList.put(task, new WatchdogEntry(task, intervalMillis));
			updateNextTask();
		}
	}
	
	/**
	 * Ändert das Zeitinterval für bestimmten Listener.
	 * @param task
	 * @param intervalMillis
	 */
	public void setTaskInterval(WatchdogTask task, long intervalMillis)
	{
		synchronized(taskList)
		{
			WatchdogEntry entry = taskList.get(task);
			entry.intervalMillis = intervalMillis;
			updateNextTask();
		}
	}
	
	public void removeTask(WatchdogTask task)
	{
		synchronized(taskList)
		{
			taskList.remove(task);
			updateNextTask();
		}
	}
	
	private void updateNextTask()
	{
		synchronized(taskList)
		{
			Iterator<WatchdogEntry> it = taskList.values().iterator();
			
			long minTime = Long.MAX_VALUE;
			WatchdogEntry minTask = null;
			
			while( it.hasNext() )
			{
				WatchdogEntry task = (WatchdogEntry) it.next();
				
				long tempTime = task.getIntervallMillis() + task.getLastExecMillis();
				
				if( minTime > tempTime )
				{
					minTime = tempTime;
					minTask = task;
				}
			}
			
			if( nextTask != minTask )
			{
				nextTask = minTask;
				nextTime = minTime;
				
				taskList.notifyAll();
			}
		}
	}
	
	public void run()
	{
		synchronized(taskList)
		{
			while( true )
			{
				long time = System.currentTimeMillis();
				
				if( nextTime <= time )
				{
					nextTask.getTask().runWatchdogTask(time);
					nextTask.setLastExecMillis(time);
					updateNextTask();
				}
				
				if( nextTask != null && taskList.size() > 0 )
				{
					time = System.currentTimeMillis();
					
					long timeToSleep = nextTime - time;
					
					if( timeToSleep > 0 )
					{
						try
						{
							taskList.wait(timeToSleep);
						}
						catch(InterruptedException e)
						{}
						
						continue;
					}
					else if( timeToSleep == 0 || nextTime == 0 )
						continue;
				}
				
				try
				{
					taskList.wait();
				}
				catch(InterruptedException e)
				{}
			}
		}
	}
	
	private class WatchdogEntry
	{
		private WatchdogTask task;
		private long intervalMillis;
		private long lastExecMillis;
		
		public WatchdogEntry(WatchdogTask task, long intervalMillis)
		{
			this.task = task;
			this.intervalMillis = intervalMillis;
		}

		public long getIntervallMillis()
		{
			return intervalMillis;
		}
		
		public WatchdogTask getTask()
		{
			return task;
		}

		public long getLastExecMillis()
		{
			return lastExecMillis;
		}
		
		public void setLastExecMillis(long lastExecMillis)
		{
			this.lastExecMillis = lastExecMillis;
		}
	}
}
