package de.coding_bereich.net.http;

import java.io.File;
import java.io.FileInputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

import de.coding_bereich.net.buffer.IOFileChannelInputBuffer;

/**
 * Gibt einen Ordner im Web frei.
 * 
 * @author Thomas
 * 
 */
public class HttpFileHandler implements HttpHandler
{
	private String			urlToMapTo;
	private File			directory;
	private FileNameMap	mimeNameMap;

	/**
	 * Ist urlToMapTo "/data/" so muss die URL
	 * "http://{server-ip}/data/index.html", für die Datei "index.html" im
	 * gegebenen Ordner, übergeben werden.
	 * 
	 * @param directory
	 *           Der Ordner der freigegeben werden soll.
	 * @param urlToMapTo
	 *           Pfadteil in der URL, das für den Zugriff auf den Ordner benutzt
	 *           wird.
	 */

	public HttpFileHandler(File directory, String urlToMapTo)
	{
		if( !directory.isDirectory() )
			throw new IllegalArgumentException(directory.getAbsolutePath()
					+ " != directory");
		this.directory = directory;

		this.urlToMapTo = urlToMapTo;
		mimeNameMap = URLConnection.getFileNameMap();
	}

	/**
	 * Ist urlToMapTo "/data/" so muss die URL
	 * "http://{server-ip}/data/index.html", für die Datei "index.html" im
	 * gegebenen Ordner, übergeben werden.
	 * 
	 * @param directory
	 *           Der Ordner der freigegeben werden soll.
	 * @param urlToMapTo
	 *           Pfadteil in der URL, das für den Zugriff auf den Ordner benutzt
	 *           wird.
	 * @param mimeNameMap
	 *           Klasse die das MIME-Type zurück gibt.
	 */
	public HttpFileHandler(File directory, String urlToMapTo,
			FileNameMap mimeNameMap)
	{
		this(directory, urlToMapTo);

		this.mimeNameMap = mimeNameMap;
	}

	@Override
	public boolean onRequest(HttpRequest request, HttpResponse response)
			throws Exception
	{
		String filePath = request.getRequestedFilePath();

		if( urlToMapTo != null )
		{
			if( !filePath.startsWith(urlToMapTo) )
				return false;

			filePath = filePath.substring(urlToMapTo.length());
		}

		File file = new File(directory, filePath);

		if( !file.isFile() || !file.canRead()
				|| !file.getAbsolutePath().startsWith(directory.getAbsolutePath()) )
			return false;

		IOFileChannelInputBuffer buffer = new IOFileChannelInputBuffer(
				new FileInputStream(file).getChannel());

		response.setHeader("content-type", mimeNameMap.getContentTypeFor(file
				.getAbsolutePath()));

		response.setBodyBuffer(buffer);

		buffer.free();

		return true;
	}

}
