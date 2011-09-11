package de.coding_bereich.net.buffer.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class ImplWritableByteChannel implements WritableByteChannel
{
	public int		dataOffset		= 0;
	public int		dataPartLength	= 2;
	public int		freeSpace		= 10;
	public boolean	open				= true;
	public boolean	failure			= false;

	@Override
	public boolean isOpen()
	{
		return open;
	}

	@Override
	public void close() throws IOException
	{
		open = false;
	}

	@Override
	public int write(ByteBuffer src) throws IOException
	{
		int len = Math.min(src.remaining(), freeSpace);
		len = Math.min(len, dataPartLength);

		for(int i = 0; i < len; i++)
			if( (byte) (i + dataOffset) != src.get() )
				failure = true;

		freeSpace -= len;
		dataOffset += len;

		return len;
	}
}
