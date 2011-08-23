package de.coding_bereich.net.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.locks.Lock;

public interface IOBuffer
{		
	public Lock getLock();
	
	public void setByteOrder(ByteOrder order);
	public ByteOrder getByteOrder();
	
	public boolean isReadable();
	public boolean isWritable();
	
	
	public boolean hasReadableBytes();
	public int getReadableBytes();
	public int getReadPosition();
	public void setReadPosition(int pos);
	
	public boolean hasWritableBytes();
	public int getWritableBytes();
	public int getWritePosition();
	public void setWritePosition(int pos);
	
	public void clear();
	
	public void compact();
	
	public int capacity();
	public void capacity(int newCapacity);
	
	public boolean isExtendable();
	
	
	public byte readByte();
	public short readUnsignedByte();
	
	public void writeByte(byte a);
	public void writeUnsignedByte(short a);

	public void read(byte[] array);
	public void read(byte[] array, int destOffset, int length);
	
	public void write(byte[] array);
	public void write(byte[] array, int offset, int length);

	public int read(ByteBuffer bb);
	public void read(ByteBuffer bb, int length);
	
   public int write(ByteBuffer bb);
   public void write(ByteBuffer bb, int length);
	
   /**
    * 
    * @param channel
    * @param buffer kann null sein
    * @return = true Verbindung geschlossen
    * @throws IOException
    */
   public boolean write(ReadableByteChannel channel, ByteBuffer buffer) throws IOException;
   /**
    * Bei Exception hält <em>buffer</em> die nicht gesicherten Bytes
    * @param channel
    * @param buffer kann null sein
    * @throws IOException
    */
   public int read(WritableByteChannel channel, ByteBuffer buffer) throws IOException;
   
   public int read(WritableByteChannel channel, int length, ByteBuffer buffer) throws IOException;
   
   /**
    * Ruft put(ReadableByteChannel channel, ByteBuffer buffer) mit buffer = null auf.
    * @param channel
    * @return = true Verbindung geschlossen
    * @throws IOException
    */
   public boolean write(ReadableByteChannel channel) throws IOException;
   /**
    * Ruft get(ReadableByteChannel channel, ByteBuffer buffer) mit buffer = null auf.
    * @param channel
    * @throws IOException
    * @see IOBuffer#read(WritableByteChannel, ByteBuffer)
    */
   public int read(WritableByteChannel channel) throws IOException;

   
   public void read(IOBuffer buffer);
   public void read(IOBuffer buffer, int length);
   
   public void write(IOBuffer buffer);
   public void write(IOBuffer buffer, int length);
   
   
	public boolean readBoolean();
	public void writeBoolean(boolean a);
	
	public short readShort();
	public void writeShort(short a);
	public int readUnsignedShort();
	public void writeUnsignedShort(int a);
	
	public int readInteger();
	public void writeInteger(int a);
	public long readUnsignedInteger();
	public void writeUnsignedInteger(long a);
	
	public long readLong();
	public void writeLong(long a);
	
	public float readFloat();
	public void writeFloat(float a);
	
	public double readDouble();
	public void writeDouble(double a);
	

	public String readString(int byteLen, String charset);
	public String readPrefixedString(int prefixLen, String charset);
	public String readPrefixedString(String charset);
	public String readDelimitedString(String[] delimiters, String charset);
	public String readDelimitedString(byte[][] delimiters, String charset);
	public String readDelimitedString(String[] delimiters, String charset, int maxByteLength);
	public String readDelimitedString(byte[][] delimiters, String charset, int maxByteLength);
	
	public void writeString(CharSequence str, String charset);
	public void writePrefixedString(CharSequence str, String charset);
	public void writePrefixedString(int prefixLen, CharSequence str, String charset);
	
	public void addObserver(IOBufferObserver observer);

	public void flush();
	
	public IOBuffer getCountedRef();
	public void free();
}
