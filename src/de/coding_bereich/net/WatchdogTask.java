package de.coding_bereich.net;

/**
 * Dient als Listener für die Watchdog-Klasse.
 * 
 * @author Thomas
 * 
 */
public interface WatchdogTask
{
	/**
	 * Wird -je nach Einstellung- von der Watchdog-Klasse aufgerufen.
	 * 
	 * @param time
	 *           Aktuelle Zeit.
	 */
	public void runWatchdogTask(long time);
}