package de.coding_bereich.net.buffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Kein vollständiger {@link OutputStream}, da nicht blockierend. Blockierende
 * Streams, würden das restliche System zu sehr verlangsamen.
 * 
 * @author Thomas
 * 
 */
public class IOBufferOutputStream extends OutputStream
{
	protected IOBuffer	buffer;

	public IOBufferOutputStream(IOBuffer buffer)
	{
		this.buffer = buffer;
	}

	@Override
	public void write(int b) throws IOException
	{
		buffer.getLock().lock();
		try
		{
			buffer.writeByte((byte) b);
		}
		finally
		{
			buffer.getLock().unlock();
		}
	}

	@Override
	public void write(byte[] b, int offset, int length) throws IOException
	{
		buffer.getLock().lock();
		try
		{
			buffer.write(b, offset, length);
		}
		finally
		{
			buffer.getLock().unlock();
		}
	}
}
