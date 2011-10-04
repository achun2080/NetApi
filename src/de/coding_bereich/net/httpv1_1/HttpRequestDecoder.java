package de.coding_bereich.net.httpv1_1;

import de.coding_bereich.net.buffer.IOBuffer;
import de.coding_bereich.net.channel.Channel;
import de.coding_bereich.net.channel.pipeline.PipelineDecoder;

public class HttpRequestDecoder extends
		PipelineDecoder<HttpRequestDecoder.State>
{
	static public final String		HEADER_CHARSET	= "UTF-8";
	static public final byte[][]	LINE_DELIMITER	= { { '\r', '\n' }, { '\r' },
			{ '\n' }										};

	private HttpRequestHeader		header;

	protected HttpRequestDecoder()
	{
		super(State.HEADER_FIRST_LINE);
	}

	public static enum State
	{
		HEADER_FIRST_LINE, HEADER, BODY
	}

	@Override
	protected Object decode(State state, IOBuffer buffer, Channel channel)
			throws Exception
	{
		String line;
		switch(state)
		{
			case HEADER_FIRST_LINE:
				header = new HttpRequestHeader();
				line = readLine(buffer);

				String[] firstLine = line.split(" ");
				header.setMethod(firstLine[0].trim());
				header.setPlainUri(firstLine[1].trim());
				header.setVersion(firstLine[2].trim());

				checkpoint(State.HEADER);

			case HEADER:
				while( true )
				{
					line = readLine(buffer);

					if( line.length() == 0 )
						break;

					String[] keyValue = line.split(":");
					header.addHeader(keyValue[0], keyValue[1]);
					checkpoint(State.HEADER);
				}

				checkpoint(State.BODY);

				return header;

			case BODY:
				if( header.getMethod().equals("post") )
					;
				String contentLengthString = header.getHeader("content-length");
				
				int contentLength = Integer.parseInt(contentLengthString);
				
				checkpoint(State.BODY);

				checkpoint();
		}

		return null;
	}

	private String readLine(IOBuffer buffer)
	{
		return buffer.readDelimitedString(LINE_DELIMITER, HEADER_CHARSET);
	}
}
