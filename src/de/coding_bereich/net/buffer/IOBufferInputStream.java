package de.coding_bereich.net.buffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Kein vollständiger {@link InputStream}, da nicht blockierend.
 * Blockierende Streams, würden das restliche System zu sehr verlangsamen.
 * @author Thomas
 *
 */
public class IOBufferInputStream extends InputStream
{
	protected IOBuffer	buffer;

	public IOBufferInputStream(IOBuffer buffer)
	{
		this.buffer = buffer;
	}

	@Override
	public int read() throws IOException
	{
		buffer.getLock().lock();
		try
		{
			if( !buffer.hasReadableBytes() )
				return -1;
			
			return buffer.readByte() & 0xFF;
		}
		finally
		{
			buffer.getLock().unlock();
		}
	}

	@Override
	public int read(byte[] b, int offset, int length) throws IOException
	{
		buffer.getLock().lock();
		try
		{
			if( !buffer.hasReadableBytes() )
				return -1;
			
			buffer.read(b, offset, length);
	
			return length;
		}
		finally
		{
			buffer.getLock().unlock();
		}
	}

	@Override
	public int available() throws IOException
	{
		buffer.getLock().lock();
		try
		{
			return buffer.getReadableBytes();
		}
		finally
		{
			buffer.getLock().unlock();
		}
	}
}
