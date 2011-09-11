package de.coding_bereich.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.coding_bereich.net.buffer.exception.BufferOverflowException;
import de.coding_bereich.net.buffer.exception.BufferUnderflowException;
import de.coding_bereich.net.buffer.exception.NotReadableBufferException;
import de.coding_bereich.net.buffer.exception.NotWritableBufferException;

/**
 * Grundimplemetierung des {@link IOBuffer}s.
 * 
 * @author Thomas
 * 
 */
public abstract class AbstractIOBuffer implements IOBuffer
{
	// read position
	protected int									rPos					= 0;

	// write position
	protected int									wPos					= 0;

	protected ByteOrder							order					= ByteOrder.BIG_ENDIAN;

	protected int									referenceCounter	= 1;

	protected Lock									lock					= new ReentrantLock();

	protected LinkedList<IOBufferObserver>	observerList		= null;

	protected void checkWritableBytes(int len)
	{
		if( !isWritable() )
			throw new NotWritableBufferException();

		if( getWritableBytes() >= len )
			return;

		if( !isExtendable() )
			throw new BufferOverflowException();

		capacity(len + capacity());
	}

	protected void checkReadableBytes(int bytesToRead)
	{
		if( !isReadable() )
			throw new NotReadableBufferException();

		if( getReadableBytes() < bytesToRead )
			throw new BufferUnderflowException();
	}

	@Override
	public boolean hasReadableBytes()
	{
		return rPos < wPos;
	}

	@Override
	public boolean hasWritableBytes()
	{
		return isExtendable() || wPos != capacity();
	}

	@Override
	public Lock getLock()
	{
		return lock;
	}

	@Override
	public void addObserver(IOBufferObserver observer)
	{
		if( observer == null )
			return;

		if( observerList == null )
			observerList = new LinkedList<IOBufferObserver>();

		observerList.add(observer);
	}

	@Override
	public double readDouble()
	{
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public float readFloat()
	{
		return Float.intBitsToFloat(readInteger());
	}

	@Override
	public int readInteger()
	{
		byte[] data = new byte[4];
		read(data);

		int value;

		if( order == ByteOrder.LITTLE_ENDIAN )
		{
			value = (0xFF & data[0]);
			value |= (0xFF & data[1]) << 8;
			value |= (0xFF & data[2]) << 16;
			value |= (0xFF & data[3]) << 24;
		}
		else
		{
			value = (0xFF & data[3]);
			value |= (0xFF & data[2]) << 8;
			value |= (0xFF & data[1]) << 16;
			value |= (0xFF & data[0]) << 24;
		}

		return value;
	}

	@Override
	public long readLong()
	{
		byte[] data = new byte[8];
		read(data);

		long value;

		if( order == ByteOrder.LITTLE_ENDIAN )
		{
			value = (0xFFL & (long) data[0]);
			value |= (0xFFL & (long) data[1]) << 8;
			value |= (0xFFL & (long) data[2]) << 16;
			value |= (0xFFL & (long) data[3]) << 24;
			value |= (0xFFL & (long) data[4]) << 32;
			value |= (0xFFL & (long) data[5]) << 40;
			value |= (0xFFL & (long) data[6]) << 48;
			value |= (0xFFL & (long) data[7]) << 56;
		}
		else
		{
			value = (0xFFL & (long) data[7]);
			value |= (0xFFL & (long) data[6]) << 8;
			value |= (0xFFL & (long) data[5]) << 16;
			value |= (0xFFL & (long) data[4]) << 24;
			value |= (0xFFL & (long) data[3]) << 32;
			value |= (0xFFL & (long) data[2]) << 40;
			value |= (0xFFL & (long) data[1]) << 48;
			value |= (0xFFL & (long) data[0]) << 56;
		}

		return value;
	}

	@Override
	public short readShort()
	{
		byte[] data = new byte[2];
		read(data);

		short value;

		if( order == ByteOrder.LITTLE_ENDIAN )
		{
			value = (short) ((0xFF & data[0]));
			value |= (short) ((0xFF & data[1]) << 8);
		}
		else
		{
			value = (short) ((0xFF & data[1]));
			value |= (short) ((0xFF & data[0]) << 8);
		}

		return value;
	}

	@Override
	public void writeInteger(final int value)
	{
		byte[] data = new byte[4];

		if( order == ByteOrder.LITTLE_ENDIAN )
		{
			data[0] = (byte) value;
			data[1] = (byte) (value >>> 8);
			data[2] = (byte) (value >>> 16);
			data[3] = (byte) (value >>> 24);
		}
		else
		{
			data[3] = (byte) (value >>> 32);
			data[2] = (byte) (value >>> 40);
			data[1] = (byte) (value >>> 48);
			data[0] = (byte) (value >>> 56);
		}

		write(data);
	}

	@Override
	public void writeLong(final long value)
	{
		byte[] data = new byte[8];

		if( order == ByteOrder.LITTLE_ENDIAN )
		{
			data[0] = (byte) value;
			data[1] = (byte) (value >>> 8);
			data[2] = (byte) (value >>> 16);
			data[3] = (byte) (value >>> 24);
			data[4] = (byte) (value >>> 32);
			data[5] = (byte) (value >>> 40);
			data[6] = (byte) (value >>> 48);
			data[7] = (byte) (value >>> 56);
		}
		else
		{
			data[7] = (byte) value;
			data[6] = (byte) (value >>> 8);
			data[5] = (byte) (value >>> 16);
			data[4] = (byte) (value >>> 24);
			data[3] = (byte) (value >>> 32);
			data[2] = (byte) (value >>> 40);
			data[1] = (byte) (value >>> 48);
			data[0] = (byte) (value >>> 56);
		}

		write(data);
	}

	@Override
	public void writeShort(final short value)
	{
		byte[] data = new byte[2];

		if( order == ByteOrder.LITTLE_ENDIAN )
		{
			data[0] = (byte) value;
			data[1] = (byte) (value >>> 8);
		}
		else
		{
			data[1] = (byte) value;
			data[0] = (byte) (value >>> 8);
		}

		write(data);
	}

	@Override
	public void writeDouble(final double value)
	{
		writeLong(Double.doubleToRawLongBits(value));
	}

	@Override
	public void writeFloat(final float value)
	{
		writeInteger(Float.floatToRawIntBits(value));
	}

	@Override
	public boolean readBoolean()
	{
		return readByte() != 0;
	}

	@Override
	public void writeBoolean(boolean value)
	{
		writeByte((byte) (value ? 1 : 0));
	}

	@Override
	public ByteOrder getByteOrder()
	{
		return order;
	}

	@Override
	public void setByteOrder(ByteOrder order)
	{
		this.order = order;
	}

	@Override
	public int getWritePosition()
	{
		return wPos;
	}

	@Override
	public int getReadPosition()
	{
		return rPos;
	}

	@Override
	public int getReadableBytes()
	{
		return wPos - rPos;
	}

	@Override
	public void setReadPosition(int pos)
	{
		rPos = pos;
	}

	@Override
	public void setWritePosition(int pos)
	{
		wPos = pos;
	}

	@Override
	public int getWritableBytes()
	{
		return capacity() - wPos;
	}

	@Override
	public void clear()
	{
		setReadPosition(0);
		setWritePosition(0);
	}

	@Override
	public void read(byte[] array)
	{
		read(array, 0, array.length);
	}

	@Override
	public void read(byte[] array, int offset, int length)
	{
		checkReadableBytes(length);

		for(int i = 0; i < length; i++)
			array[i + offset] = readByte();
	}

	@Override
	public int read(ByteBuffer bb)
	{
		int length = Math.min(getReadableBytes(), bb.remaining());
		read(bb, length);
		return length;
	}

	@Override
	public void read(ByteBuffer bb, int length)
	{
		checkReadableBytes(length);

		if( bb.hasArray() )
		{
			read(bb.array(), bb.position(), length);
			bb.position(bb.position() + length);
		}
		else
		{
			for(int i = 0; i < length; i++)
				bb.put(readByte());
		}
	}

	@Override
	public int read(WritableByteChannel channel) throws IOException
	{
		int read = read(channel, getReadableBytes(), null);
		return read;
	}

	@Override
	public int read(WritableByteChannel channel, ByteBuffer buffer)
			throws IOException
	{
		int read = read(channel, getReadableBytes(), buffer);
		return read;
	}

	@Override
	public int read(WritableByteChannel channel, int length, ByteBuffer buffer)
			throws IOException
	{
		checkReadableBytes(length);

		if( buffer == null )
			buffer = ByteBuffer.allocateDirect(Math.min(4096, getReadableBytes()));

		buffer.clear();

		int pos = 0;
		int oldRPos = getReadPosition();

		while( true )
		{
			int len = Math.min(length - pos, buffer.capacity());

			read(buffer, len);

			buffer.flip();

			len = channel.write(buffer);
			if( len == 0 )
				break;

			pos += len;
			setReadPosition(pos - oldRPos);

			buffer.clear();
		}

		buffer.clear();

		return pos;
	}

	@Override
	public boolean write(ReadableByteChannel channel) throws IOException
	{
		return write(channel, null);
	}

	@Override
	public boolean write(ReadableByteChannel channel, ByteBuffer buffer)
			throws IOException
	{
		if( buffer == null )
			buffer = ByteBuffer.allocateDirect(4096);

		int count;

		while( true )
		{
			buffer.clear();

			if( !isExtendable() && buffer.remaining() > getWritableBytes() )
				if( buffer.remaining() == 0 )
					throw new BufferOverflowException();
				else
					buffer.limit(getWritableBytes());

			try
			{
				count = channel.read(buffer);
			}
			catch(ClosedChannelException e)
			{
				count = 0;
			}

			if( count <= 0 )
				break;

			buffer.flip();

			write(buffer);
		}

		buffer.clear();

		return count < 0;
	}

	@Override
	public void read(IOBuffer buffer)
	{
		read(buffer, getReadableBytes());
	}

	@Override
	public void read(IOBuffer buffer, int length)
	{
		checkWritableBytes(length);

		for(int i = 0; i < length; i++)
			buffer.writeByte(readByte());
	}

	@Override
	public void write(IOBuffer buffer)
	{
		write(buffer, buffer.getReadableBytes());
	}

	@Override
	public void write(IOBuffer buffer, int length)
	{
		checkReadableBytes(length);

		for(int i = 0; i < length; i++)
			writeByte(buffer.readByte());
	}

	@Override
	public String readDelimitedString(String[] delimiters, String charset,
													int maxByteLength)
	{
		byte[][] byteArray = new byte[delimiters.length][];
		int len = delimiters.length;
		for(int i = 0; i < len; i++)
			byteArray[i] = delimiters[i].getBytes(Charset.forName(charset));

		return readDelimitedString(byteArray, charset, maxByteLength);
	}

	@Override
	public String readDelimitedString(String[] delimiters, String charset)
	{
		return readDelimitedString(delimiters, charset, getReadableBytes());
	}

	@Override
	public String readDelimitedString(byte[][] delimiters, String charset)
	{
		return readDelimitedString(delimiters, charset, getReadableBytes());
	}

	@Override
	public String readDelimitedString(byte[][] delimiters, String charset,
													int maxByteLength)
	{
		int startPos = getReadPosition();

		int delimitersLen = delimiters.length;

		int foundDelimiter = -1;
		int lastDelimiter = -1;
		int foundDelimiterPos = 0;

		int i = 0;
		for(; i < maxByteLength; i++)
		{
			byte b = readByte();

			if( foundDelimiter != -1 )
				if( b != delimiters[foundDelimiter][foundDelimiterPos] )
				{
					i -= foundDelimiterPos + 1;
					setReadPosition(getReadPosition() - foundDelimiterPos - 1);
					lastDelimiter = foundDelimiter;
					foundDelimiter = -1;
					foundDelimiterPos = 0;
				}

			// nochmal checken, ob der delimiter (derweil) wieder nicht gefunden
			// wurde
			if( foundDelimiter == -1 )
			{
				int j = lastDelimiter != -1 ? lastDelimiter + 1 : 0;
				for(; j < delimitersLen; j++)
				{
					if( b == delimiters[j][0] )
					{
						foundDelimiter = j;
						break;
					}
				}
			}

			// nochmal checken, ob der delimiter (derweil) gefunden wurde
			if( foundDelimiter != -1 )
				if( ++foundDelimiterPos == delimiters[foundDelimiter].length )
					break;
		}

		if( foundDelimiter == -1
				|| foundDelimiterPos != delimiters[foundDelimiter].length )
		{
			setReadPosition(startPos);
			throw new BufferUnderflowException();
		}

		setReadPosition(startPos);
		String temp = CharsetDecoder.decode(this, charset, i - foundDelimiterPos
				+ 1);
		setReadPosition(startPos + i + 1);

		return temp;
	}

	@Override
	public String readString(int byteLen, String charset)
	{
		return CharsetDecoder.decode(this, charset, byteLen);
	}

	@Override
	public void write(byte[] array)
	{
		write(array, 0, array.length);
	}

	@Override
	public void write(byte[] array, int offset, int length)
	{
		checkWritableBytes(length);

		for(int i = 0; i < length; i++)
			writeByte(array[i + offset]);
	}

	@Override
	public int write(ByteBuffer bb)
	{
		int length = bb.remaining();
		write(bb, length);
		return length;
	}

	@Override
	public void write(ByteBuffer bb, int length)
	{
		checkWritableBytes(length);

		if( bb.hasArray() )
		{
			write(bb.array(), bb.position(), length);
			bb.position(bb.position() + length);
		}
		else
		{
			for(int i = 0; i < length; i++)
				writeByte(bb.get());
		}
	}

	@Override
	public void writeString(CharSequence str, String charset)
	{
		CharsetEncoder.encode(str, this, charset);
	}

	@Override
	public String readPrefixedString(int prefixLen, String charset)
	{
		int length;

		switch(prefixLen)
		{
			case 1:
				length = readByte();
				break;
			case 2:
				length = readShort();
				break;
			case 4:
				length = readInteger();
				break;
			default:
				throw new IllegalArgumentException("prefixLen: " + prefixLen);
		}

		String temp = CharsetDecoder.decode(this, charset, length);

		return temp;
	}

	@Override
	public String readPrefixedString(String charset)
	{
		return readPrefixedString(4, charset);
	}

	@Override
	public void writePrefixedString(CharSequence str, String charset)
	{
		writePrefixedString(4, str, charset);
	}

	@Override
	public void writePrefixedString(int prefixLen, CharSequence str,
												String charset)
	{
		int maxLen;

		int prefixPos = getWritePosition();

		switch(prefixLen)
		{
			case 1:
				writeByte((byte) 0);
				maxLen = Byte.MAX_VALUE;
				break;
			case 2:
				writeShort((short) 0);
				maxLen = Short.MAX_VALUE;
				break;
			case 4:
				writeInteger(0);
				maxLen = Integer.MAX_VALUE;
				break;
			default:
				throw new IllegalArgumentException("prefixLen: " + prefixLen);
		}

		int startPos = getWritePosition();
		CharsetEncoder.encode(str, this, charset, maxLen);
		int endPos = getWritePosition();

		setWritePosition(prefixPos);

		int length = endPos - startPos;

		switch(prefixLen)
		{
			case 1:
				writeByte((byte) length);
				break;
			case 2:
				writeShort((short) length);
				break;
			case 4:
				writeInteger(length);
				break;
		}

		setWritePosition(endPos);
	}

	public short readUnsignedByte()
	{
		return (short) (0xFF & readByte());
	}

	public void writeUnsignedByte(short a)
	{
		writeByte((byte) a);
	}

	public int readUnsignedShort()
	{
		return (int) (0xFFFF & readShort());
	}

	public void writeUnsignedShort(int a)
	{
		writeShort((short) a);
	}

	public long readUnsignedInteger()
	{
		return (long) (0xFFFFFFFFL & readInteger());
	}

	public void writeUnsignedInteger(long a)
	{
		writeInteger((int) a);
	}

	@Override
	public void flush()
	{
		if( observerList == null )
			return;

		for(IOBufferObserver observer : observerList)
			observer.onBufferFlush(this);
	}

	@Override
	public IOBuffer getCountedRef()
	{
		++referenceCounter;
		return this;
	}

	@Override
	public void free()
	{

		if( --referenceCounter == 0 )
			free0();
	}

	@Override
	public String toString()
	{
		return "AbstractIOBuffer [rPos=" + rPos + ", wPos=" + wPos + ", order="
				+ order + "]";
	}

	abstract protected void free0();
}
