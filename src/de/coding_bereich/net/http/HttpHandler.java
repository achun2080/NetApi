package de.coding_bereich.net.http;

/**
 * 
 * @author Thomas
 * 
 */
public interface HttpHandler
{
	/**
	 * 
	 * @param request
	 * @param response
	 * @return true wenn die Anfrage beantwortet werden konnte, sonst false.
	 * @throws Exception
	 */
	public boolean onRequest(HttpRequest request, HttpResponse response)
			throws Exception;
}
