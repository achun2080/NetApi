/**
 * 
 */
package de.coding_bereich.net.buffer.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.coding_bereich.net.buffer.DynamicIOBuffer;
import de.coding_bereich.net.buffer.IOBuffer;

/**
 * @author Thomas
 * 
 */
public class IOBufferTest
{
	Class<? extends IOBuffer>	clazz	= ImplAbstractIOBuffer.class;

	/**
	 * 
	 */
	
	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#hasReadableBytes()}.
	 */
	@Test
	public void testHasReadableBytes()
	{
		IOBuffer b = getNewBuffer();

		assertEquals(false, b.hasReadableBytes());

		b.writeInteger(7);
		assertEquals(true, b.hasReadableBytes());
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#hasWritableBytes()}.
	 */
	@Test
	public void testHasWritableBytes()
	{
		/*
		IOBuffer b = getNewBuffer();

		assertTrue(b.hasWritableBytes());

		for(int i = 0; i < 512; i++)
			b.writeByte((byte) 7);

		assertTrue(!b.hasWritableBytes() && !b.isExtendable());
		*/
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readDouble()}.
	 */
	@Test
	public void testReadDouble()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readFloat()}.
	 */
	@Test
	public void testReadFloat()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readInteger()}.
	 */
	@Test
	public void testReadInteger()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readLong()}.
	 */
	@Test
	public void testReadLong()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readShort()}.
	 */
	@Test
	public void testReadShort()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeInteger(int)}.
	 */
	@Test
	public void testWriteInteger()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeLong(long)}.
	 */
	@Test
	public void testWriteLong()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeShort(short)}.
	 */
	@Test
	public void testWriteShort()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeDouble(double)}.
	 */
	@Test
	public void testWriteDouble()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeFloat(float)}.
	 */
	@Test
	public void testWriteFloat()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readBoolean()}.
	 */
	@Test
	public void testReadBoolean()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeBoolean(boolean)}
	 * .
	 */
	@Test
	public void testWriteBoolean()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#getWritePosition()}.
	 */
	@Test
	public void testGetWritePosition()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#getReadPosition()}.
	 */
	@Test
	public void testGetReadPosition()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#getReadableBytes()}.
	 */
	@Test
	public void testGetReadableBytes()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#setReadPosition(int)}
	 * .
	 */
	@Test
	public void testSetReadPosition()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#setWritePosition(int)}
	 * .
	 */
	@Test
	public void testSetWritePosition()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#getWritableBytes()}.
	 */
	@Test
	public void testGetWritableBytes()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#clear()}.
	 */
	@Test
	public void testClear()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#compact()}.
	 */
	@Test
	public void testCompact()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(byte[])}.
	 */
	@Test
	public void testReadByteArray()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(byte[], int, int)}
	 * and
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(byte[], int, int)}
	 * .
	 */
	@Test
	public void testReadByteArrayIntInt()
	{
		IOBuffer b = getNewBuffer();

		byte[] bArray = new byte[128];

		for(int i = 0; i < 64; i++)
			bArray[i + 15] = (byte) i;

		b.write(bArray, 15, 64);

		byte[] bArray2 = new byte[128];

		b.read(bArray2, 15, 64);

		assertArrayEquals(bArray, bArray2);
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(java.nio.ByteBuffer)}
	 * .
	 */
	@Test
	public void testReadByteBuffer()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(java.nio.ByteBuffer, int)}
	 * .
	 */
	@Test
	public void testReadByteBufferInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(java.nio.ByteBuffer, int, int)}
	 * .
	 */
	@Test
	public void testReadByteBufferIntInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(java.nio.channels.WritableByteChannel, int, java.nio.ByteBuffer)}
	 * and  
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(java.nio.channels.ReadableByteChannel, int, java.nio.ByteBuffer)}
	 * .
	 */
	@Test
	public void testReadWriteByteChannelIntByteBuffer()
	{
		IOBuffer b = getNewBuffer();

		ImplReadableByteChannel rBC = new ImplReadableByteChannel();
		rBC.dataLength = 100;

		ImplWritableByteChannel wBC = new ImplWritableByteChannel();
		wBC.freeSpace = 100;

		try
		{
			b.write(rBC);
		}
		catch(IOException e)
		{
			fail(e.toString());
		}

		try
		{
			b.read(wBC);
		}
		catch(IOException e)
		{
			fail(e.toString());
		}

		assertFalse(wBC.failure);
		assertEquals(0, rBC.dataLength);
		assertEquals(0, wBC.freeSpace);
		
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#read(de.coding_bereich.net.buffer.IOBuffer, int)}
	 * .
	 */
	@Test
	public void testReadIOBufferInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(de.coding_bereich.net.buffer.IOBuffer, int)}
	 * .
	 */
	@Test
	public void testWriteIOBufferInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readDelimitedString(java.lang.String[], java.lang.String, int)}
	 * .
	 */
	@Test
	public void testReadDelimitedStringStringArrayStringInt()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readDelimitedString(java.lang.String[], java.lang.String)}
	 * .
	 */
	@Test
	public void testReadDelimitedStringStringArrayString()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readDelimitedString(byte[][], java.lang.String)}
	 * .
	 */
	@Test
	public void testReadDelimitedStringByteArrayArrayString()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readDelimitedString(byte[][], java.lang.String, int)}
	 * .
	 */
	@Test
	public void testReadDelimitedStringByteArrayArrayStringInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readString(int, java.lang.String)}
	 * .
	 */
	@Test
	public void testReadString()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(byte[])}.
	 */
	@Test
	public void testWriteByteArray()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(byte[], int, int)}
	 * .
	 */
	@Test
	public void testWriteByteArrayIntInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(java.nio.ByteBuffer)}
	 * .
	 */
	@Test
	public void testWriteByteBuffer()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#write(java.nio.ByteBuffer, int)}
	 * .
	 */
	@Test
	public void testWriteByteBufferInt()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeString(java.lang.CharSequence, java.lang.String)}
	 * .
	 */
	@Test
	public void testWriteString()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readPrefixedString(int, java.lang.String)}
	 * .
	 */
	@Test
	public void testReadPrefixedStringIntString()
	{
	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readPrefixedString(java.lang.String)}
	 * .
	 */
	@Test
	public void testReadPrefixedStringString()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writePrefixedString(java.lang.CharSequence, java.lang.String)}
	 * .
	 */
	@Test
	public void testWritePrefixedStringCharSequenceString()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writePrefixedString(int, java.lang.CharSequence, java.lang.String)}
	 * .
	 */
	@Test
	public void testWritePrefixedStringIntCharSequenceString()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readUnsignedByte()}.
	 */
	@Test
	public void testReadUnsignedByte()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeUnsignedByte(short)}
	 * .
	 */
	@Test
	public void testWriteUnsignedByte()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readUnsignedShort()}.
	 */
	@Test
	public void testReadUnsignedShort()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeUnsignedShort(int)}
	 * .
	 */
	@Test
	public void testWriteUnsignedShort()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#readUnsignedInteger()}
	 * .
	 */
	@Test
	public void testReadUnsignedInteger()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#writeUnsignedInteger(long)}
	 * .
	 */
	@Test
	public void testWriteUnsignedInteger()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#flush()}.
	 */
	@Test
	public void testFlush()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#getCountedRef()}.
	 */
	@Test
	public void testGetCountedRef()
	{

	}

	/**
	 * Test method for
	 * {@link de.coding_bereich.net.buffer.AbstractIOBuffer#free()}.
	 */
	@Test
	public void testFree()
	{

	}

	private IOBuffer getNewBuffer()
	{
		/*
		try
		{
			return clazz.newInstance();
		}
		catch(InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		*/
		return DynamicIOBuffer.create();
	}
}
