package de.coding_bereich.net.http;

import java.util.HashMap;
import java.util.UUID;

/**
 * Eine Session trägt die Daten, eines (eingeloggten) Benutzer. Der Nutzer wird
 * per Cookie wieder erkannt.
 * 
 * @author Thomas
 * 
 */
public class HttpSession
{
	private long							lastUseMillis;
	private HashMap<String, Object>	attributes	= new HashMap<String, Object>();
	private String							id;

	public HttpSession()
	{
		id = UUID.randomUUID().toString();
	}

	public HttpSession(String id)
	{
		this.id = id;
	}

	/**
	 * Gibt ein den Attributswert zurück.
	 * 
	 * @param name
	 *           Attributsname.
	 * @return Den Wert des Attributes.
	 */
	public Object getAttribute(String name)
	{
		return attributes.get(name);
	}

	/**
	 * Setzt ein Attributwert.
	 * 
	 * @param name
	 *           Attributsname.
	 * @param value
	 *           Den Wert des Attributes.
	 */
	public void setAttribute(String name, Object value)
	{
		if( value == null )
			attributes.remove(name);
		else
			attributes.put(name, value);
	}

	public String getId()
	{
		return id;
	}

	public long getLastUseMillis()
	{
		return lastUseMillis;
	}

	public void setLastUseMillis(long lastUseMillis)
	{
		this.lastUseMillis = lastUseMillis;
	}

	@Override
	public String toString()
	{
		return id;
	}
}
