package de.coding_bereich.net;

/**
 * Worker-Klassse.
 * @author Thomas
 *
 */
public interface Worker
{
	/**
	 * Wird aufgerufen, wenn Arbeit erledigt werden soll.
	 * @param task
	 * @param params
	 */
	public void doWork(WorkerTask task, Object[] params);
}
