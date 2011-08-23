package de.coding_bereich.net;

/**
 * Servermanager.
 * Implementiert das Singletonpattern.
 * @author Thomas
 *
 */
public class Manager
{
	static private Manager	instance = new Manager();
	private Worker	worker;

	private Manager()
	{}
	
	public static Manager getInstance()
	{
		return instance;
	}
	
	public Worker getWorker()
	{
		return worker;
	}
	
	public void setWorker(Worker worker)
	{
		this.worker = worker;
	}
}
