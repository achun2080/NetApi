package de.coding_bereich.net.buffer;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sich selbst erweiternder {@link IOBuffer}.
 * 
 * @author Thomas
 * 
 */
public class DynamicIOBuffer extends AbstractIOBuffer
{
	static protected ConcurrentLinkedQueue<DynamicIOBuffer>	queue							= new ConcurrentLinkedQueue<DynamicIOBuffer>();
	static protected AtomicInteger									queueSize					= new AtomicInteger(
																														0);
	static protected int													maxQueueSize;

	static final protected int											byteIndexBits				= 12;
	static final protected int											blockSize					= 1 << byteIndexBits;
	static final protected int											byteIndexBitsWhildcard	= blockSize - 1;

	protected byte[][]													b								= null;

	private DynamicIOBuffer()
	{
		this(16);
	}

	private DynamicIOBuffer(int capacity)
	{
		capacity(capacity);
	}

	@Override
	public int capacity()
	{
		return b.length * blockSize;
	}

	@Override
	public void capacity(int newCapacity)
	{
		if( b != null && newCapacity == capacity() )
			return;

		if( b != null && newCapacity < capacity() )
			throw new IllegalArgumentException("newCapacity < oldcapacity");

		int newBNum = (int) Math.ceil((double) newCapacity / (double) blockSize);

		byte[][] newB = new byte[newBNum][];

		int len = b != null ? b.length : 0;
		for(int i = 0; i < len; i++)
			newB[i] = b[i];

		for(int i = len; i < newBNum; i++)
			newB[i] = new byte[blockSize];

		b = newB;
	}

	@Override
	public byte readByte()
	{
		/*
		 * if( index < 0 || index > capacity() ) throw new
		 * IndexOutOfBoundsException("index: " + index);
		 */
		byte ret = b[rPos >>> byteIndexBits][rPos & byteIndexBitsWhildcard];
		rPos++;
		return ret;
	}

	@Override
	public void writeByte(byte a)
	{
		/*
		 * if( index < 0 || index > capacity() ) throw new
		 * IndexOutOfBoundsException("index: " + index);
		 */

		b[wPos >>> byteIndexBits][wPos & byteIndexBitsWhildcard] = a;
		wPos++;
	}

	@Override
	public void read(byte[] array, int destOffset, int length)
	{
		checkReadableBytes(length);
		int len = 0;

		for(int i = 0; i < length; i += len)
		{
			byte[] src = b[(rPos + i) >>> byteIndexBits];
			int srcPos = (rPos + i) & byteIndexBitsWhildcard;
			int inLen = blockSize - srcPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			System.arraycopy(src, srcPos, array, i + destOffset, len);
		}

		rPos += length;
	}

	@Override
	public void write(byte[] array, int offset, int length)
	{
		checkWritableBytes(length);
		int len = 0;

		for(int i = 0; i < length; i += len)
		{
			byte[] dest = b[(wPos + i) >>> byteIndexBits];
			int destPos = (wPos + i) & byteIndexBitsWhildcard;
			int inLen = blockSize - destPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			System.arraycopy(array, i + offset, dest, destPos, len);
		}

		wPos += length;
	}

	@Override
	public void read(ByteBuffer bb, int length)
	{
		checkReadableBytes(length);
		int len = 0;

		for(int i = 0; i < length; i += len)
		{
			byte[] src = b[(rPos + i) >>> byteIndexBits];
			int srcPos = (rPos + i) & byteIndexBitsWhildcard;
			int inLen = blockSize - srcPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			bb.put(src, srcPos, len);
		}

		rPos += length;
	}

	@Override
	public void write(ByteBuffer bb, int length)
	{
		checkWritableBytes(length);
		int len = 0;

		for(int i = 0; i < length; i += len)
		{
			byte[] dest = b[(wPos + i) >>> byteIndexBits];
			int destPos = (wPos + i) & byteIndexBitsWhildcard;
			int inLen = blockSize - destPos;
			int outLen = length - i;

			len = inLen < outLen ? inLen : outLen;

			bb.get(dest, destPos, len);
		}

		wPos += length;
	}

	public static DynamicIOBuffer create()
	{
		// DynamicIOBuffer buffer = queue.poll();
		//
		// if( buffer != null )
		// {
		// buffer.referenceCounter = 1;
		// buffer.observerList = null;
		// buffer.clear();
		//
		// queueSize.addAndGet(-buffer.capacity());
		// }
		// else
		DynamicIOBuffer buffer = new DynamicIOBuffer();

		return buffer;
	}

	@Override
	protected void free0()
	{
		if( (queueSize.get() + capacity()) < maxQueueSize )
		{
			queueSize.addAndGet(capacity());
			queue.offer(this);
		}
	}

	static public int getMaxQueueSize()
	{
		return maxQueueSize;
	}

	static public void setMaxQueueSize(int maxQueueSize)
	{
		DynamicIOBuffer.maxQueueSize = maxQueueSize;
	}

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
	public boolean isExtendable()
	{
		return true;
	}

	@Override
	public void compact()
	{
		// TODO Auto-generated method stub

	}
}