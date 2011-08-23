package de.coding_bereich.net.buffer.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ImplReadableByteChannel implements ReadableByteChannel
{
	public int		dataOffset		= 0;
	public int		dataLength		= 10;
	public int		dataPartLength	= 2;
	public boolean	open				= true;

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
	public int read(ByteBuffer dst) throws IOException
	{
		int len = Math.min(dst.remaining(), dataLength);
		len = Math.min(len, dataPartLength);


		for(int i = 0; i < len; i++)
			dst.put((byte) (i + dataOffset));

		dataLength -= len;
		dataOffset += len;

		return len;
	}

}
