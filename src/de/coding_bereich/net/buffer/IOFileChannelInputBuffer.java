package de.coding_bereich.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * {@link IOBuffer} der eine Datei auf der Festplatte wiederspiegelt.
 * 
 * @author Thomas
 * 
 */
public class IOFileChannelInputBuffer extends AbstractIOBuffer
{
	private FileChannel	channel;

	public IOFileChannelInputBuffer(FileChannel channel)
	{
		this.channel = channel;

		try
		{
			wPos = (int) channel.size();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public byte readByte()
	{
		throw new IllegalArgumentException(
				"use the get(WritableByteChannel channel[, ByteBuffer buffer]) method");
	}

	@Override
	public void writeByte(byte a)
	{
	}

	@Override
	public int read(WritableByteChannel channel, int length, ByteBuffer buffer)
			throws IOException
	{
		checkReadableBytes(length);
		int read = (int) this.channel.transferTo(rPos, length, channel);
		rPos += read;
		return read;
	}

	@Override
	public int capacity()
	{
		return 0;
	}

	@Override
	public void capacity(int newCapacity)
	{
	}

	@Override
	protected void free0()
	{
		try
		{
			channel.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isReadable()
	{
		return true;
	}

	@Override
	public boolean isWritable()
	{
		return false;
	}

	@Override
	public boolean isExtendable()
	{
		return false;
	}

	@Override
	public void compact()
	{
	}
}
