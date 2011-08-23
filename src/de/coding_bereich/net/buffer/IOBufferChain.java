package de.coding_bereich.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Pseudo {@link IOBuffer}. Besteht setzt sich aus mehrern anderen Buffern
 * zusammen. Diese Buffer werden wie ein Buffer bahandelt.
 * 
 * @author Thomas
 * 
 */
public class IOBufferChain extends AbstractIOBuffer
{
	protected BufferEntry	firstEntry		= null;
	protected BufferEntry	lastEntry		= null;

	protected BufferEntry	lastReadEntry	= null;
	protected IOBuffer		lastReadBuffer	= null;
	protected int				lastReadPos;

	static protected class BufferEntry
	{
		public int				startPos				= 0;
		public int				endPos				= 0;
		public int				bufferStartOffset	= 0;
		public BufferEntry	next;
		public BufferEntry	back;
		public IOBuffer		buffer;
	}

	@Override
	public void writeByte(byte a)
	{
	}

	@Override
	public byte readByte()
	{
		if( firstEntry == null )
			throw new IndexOutOfBoundsException();

		updateReadPosition();

		byte b = lastReadBuffer.readByte();

		rPos++;
		lastReadPos = rPos;

		return b;
	}

	@Override
	public void read(byte[] array, int offset, int length)
	{
		int len = 0;

		for(int i = 0; i < length; i += len)
		{
			updateReadPosition();

			int inLen = lastReadEntry.endPos - rPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			lastReadBuffer.read(array, i + offset, len);

			rPos += len;
			lastReadPos = rPos;
		}
	}
	

	@Override
	public int read(WritableByteChannel channel, int length, ByteBuffer buffer)
			throws IOException
	{
		int len = 0;
		int i = 0;

		for(; i < length; i += len)
		{
			updateReadPosition();

			int inLen = lastReadEntry.endPos - rPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			len = lastReadBuffer.read(channel, len, buffer);

			rPos += len;
			lastReadPos = rPos;
		}

		return i;
	}
	
	@Override
	public void read(ByteBuffer bb, int length)
	{
		int len = 0;

		for(int i = 0; i < length; i += len)
		{
			updateReadPosition();

			int inLen = lastReadEntry.endPos - rPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			lastReadBuffer.read(bb, len);

			rPos += len;
			lastReadPos = rPos;
		}
	}

	@Override
	public int capacity()
	{
		return wPos;
	}

	@Override
	public void capacity(int newCapacity)
	{
	}

	public void addBuffer(IOBuffer buffer)
	{
		if( buffer == null )
			return;

		buffer.getCountedRef();

		if( lastEntry == null )
		{
			lastEntry = new BufferEntry();
			lastEntry.buffer = buffer;
			firstEntry = lastEntry;
		}
		else
		{
			BufferEntry oldLastEntry = lastEntry;
			lastEntry = new BufferEntry();
			lastEntry.buffer = buffer;
			oldLastEntry.next = lastEntry;
			lastEntry.back = oldLastEntry;
		}

		lastEntry.bufferStartOffset = buffer.getReadPosition();

		lastEntry.startPos = wPos;
		wPos = lastEntry.endPos = buffer.getReadableBytes() + wPos;
	}

	protected void updateReadPosition()
	{
		boolean toLow = false;
		boolean toHeight = false;

		if( lastReadEntry != null )
		{
			toLow = lastReadEntry.startPos > rPos;
			toHeight = lastReadEntry.endPos <= rPos;

			if( !(rPos != lastReadPos || toLow || toHeight) )
				return;
		}

		if( firstEntry == null )
			throw new IndexOutOfBoundsException();

		BufferEntry entry;
		if( toLow )
		{
			entry = lastReadEntry.back;

			while( entry != null )
			{
				if( rPos >= entry.startPos && rPos < entry.endPos )
				{
					updateReadPositionFound(entry);
					return;
				}

				entry = entry.back;
			}

			throw new IndexOutOfBoundsException();
		}
		else if( toHeight )
		{
			entry = lastReadEntry.next;

			while( entry != null )
			{
				if( rPos >= entry.startPos && rPos < entry.endPos )
				{
					updateReadPositionFound(entry);
					return;
				}

				entry = entry.next;
			}

			throw new IndexOutOfBoundsException();
		}
		else
		{
			if( lastReadEntry == null )
			{
				entry = firstEntry;

				do
				{
					if( rPos >= entry.startPos && rPos < entry.endPos )
					{
						updateReadPositionFound(entry);
						return;
					}

					entry = entry.next;
				}
				while( entry != null );

				throw new IndexOutOfBoundsException();
			}
			else
				updateReadPositionFound(lastReadEntry);
		}

	}

	protected void updateReadPositionFound(BufferEntry entry)
	{
		lastReadBuffer = entry.buffer;
		int subBufferPos = rPos - entry.startPos + entry.bufferStartOffset;
		lastReadBuffer.setReadPosition(subBufferPos);
		lastReadEntry = entry;
	}

	@Override
	protected void free0()
	{
		BufferEntry entry = firstEntry;

		while( entry != null )
		{
			if( entry.buffer != null )
				entry.buffer.free();

			entry.buffer = null;
			entry = entry.next;
		}
	}

	@Override
	public void clear()
	{
		super.clear();
		free0();
		firstEntry = lastEntry = null;
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
