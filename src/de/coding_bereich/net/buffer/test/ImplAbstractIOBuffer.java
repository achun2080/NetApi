package de.coding_bereich.net.buffer.test;

import de.coding_bereich.net.buffer.AbstractIOBuffer;

public class ImplAbstractIOBuffer extends AbstractIOBuffer
{
	private byte[]	buffer	= new byte[512];

	@Override
	public boolean isReadable()
	{
		return true;
	}

	@Override
	public boolean isWritable()
	{
		return true;
	}

	@Override
	public int capacity()
	{
		return buffer.length;
	}

	@Override
	public void capacity(int newCapacity)
	{
	}

	@Override
	public boolean isExtendable()
	{
		return false;
	}

	@Override
	public byte readByte()
	{
		return buffer[rPos++];
	}

	@Override
	public void writeByte(byte a)
	{
		buffer[wPos++] = a;
	}

	@Override
	protected void free0()
	{

	}

	@Override
	public void compact()
	{
	}
}
