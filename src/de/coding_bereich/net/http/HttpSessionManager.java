package de.coding_bereich.net.http;

import java.util.HashMap;

//TODO: Löschen alter Session implementieren.
/**
 * Verwaltet alle Sessionen im System.
 * 
 * @author Thomas
 * 
 */
public class HttpSessionManager
{
	private long										lifeTime	= 3600000;

	static final private HttpSessionManager	instance	= new HttpSessionManager();

	private HashMap<String, HttpSession>		sessions	= new HashMap<String, HttpSession>();

	// private IWatchdogTask watchdogTask;

	static public HttpSessionManager getInstance()
	{
		return instance;
	}

	private HttpSessionManager()
	{
		/*
		 * IWatchdogTask task = new IWatchdogTask() {
		 * 
		 * @Override public void runWatchdogTask(long time) { long minTime = time
		 * - lifeTime;
		 * 
		 * synchronized(sessions) { Iterator<HttpSession> it =
		 * sessions.values().iterator(); while( it.hasNext() ) { HttpSession
		 * session = (HttpSession) it.next();
		 * 
		 * if( session.getLastUseMillis() < minTime ) remove(session.getId()); } }
		 * } };
		 * 
		 * watchdogTask = task;
		 * 
		 * //Watchdog.getInstance().addTask(task, lifeTime);
		 */
	}

	public HttpSession getSession(String sessionId)
	{
		synchronized(sessions)
		{
			HttpSession session = sessions.get(sessionId);

			if( session != null )
			{
				session.setLastUseMillis(System.currentTimeMillis());
				return session;
			}

			if( sessionId != null )
				session = new HttpSession(sessionId);
			else
				session = new HttpSession();

			session.setLastUseMillis(System.currentTimeMillis());
			sessions.put(session.getId(), session);
			return session;
		}
	}

	public HttpSession remove(String sessionId)
	{
		synchronized(sessions)
		{
			return sessions.remove(sessionId);
		}
	}

	public long getLifeTime()
	{
		return lifeTime;
	}

	public void setLifeTime(long lifeTime)
	{
		this.lifeTime = lifeTime;
		// Watchdog.getInstance().setTaskInterval(watchdogTask, lifeTime);
	}
}
