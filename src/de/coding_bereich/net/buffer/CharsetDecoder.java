package de.coding_bereich.net.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import de.coding_bereich.net.buffer.exception.BufferUnderflowException;

/**
 * Decodiert Bytes zu einem String.
 * Überarbeitet, da Flaschenhals bei Multithreading.
 * @author thomas
 *
 */
public class CharsetDecoder
{
	
	
	/**
	 * DARF NICHT!!! KLEINER SEIN ALS MAX_BYTE_PER_CHAR, DA SONST DAUERSCHLEIFE!
	 */
	static protected ByteBuffer	byteBuffer	= ByteBuffer.allocate(1024);
	static protected char[]			charArray = new char[1024];
	static protected CharBuffer	charBuffer	= CharBuffer.wrap(charArray);
	
	static protected Lock lock = new ReentrantLock();
	
	/**
	 * Decodiert ein {@link IOBuffer} zu einem String.
	 * @param in Das zulesende {@link IOBuffer}.
	 * @param charset Das Zeichensatz.
	 * @param byteLength Die Anzahl an Bytes zum Decodieren.
	 * @return Der fertige String.
	 */
	static public String decode(IOBuffer in, String charset, int byteLength)
	{
		if( in.getReadableBytes() < byteLength )
			throw new BufferUnderflowException();
		
		ByteBuffer bb;
		CharBuffer cb;
		char[] ca;
		
		Thread thread = Thread.currentThread();
		if( thread instanceof CharsetDecoderEncoderThread )
		{
			CharsetDecoderEncoderThread thread1 = ((CharsetDecoderEncoderThread) thread);
			bb = thread1.getTempArrayByteBuffer();
			cb = thread1.getTempArrayCharBuffer();
			ca = cb.array();
		}
		else
		{
			bb = byteBuffer;
			cb = charBuffer;
			ca = charArray;
		}
		
		java.nio.charset.CharsetDecoder decoder = Charset.forName(charset).newDecoder();
		StringBuilder sb = new StringBuilder();

		decoder.reset();
		decoder.replaceWith("?");
		decoder.onMalformedInput(CodingErrorAction.REPLACE);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);

		boolean lastLoop = false;

		if( !(thread instanceof CharsetDecoderEncoderThread) )
			lock.lock();
		
		try
		{
			bb.clear();
			cb.clear();
			
			while( true )
			{
				if( byteLength > 0 )
				{
					int readed = Math.min(bb.remaining(), byteLength);
					in.read(bb, readed);
					byteLength -= readed;
				}
				
				bb.flip();
				
				CoderResult result = decoder.decode(bb, cb, lastLoop);
	
				if( lastLoop )
					result = decoder.flush(cb);
	
				cb.flip();
				int cbLen = cb.remaining();
				if( cbLen > 0 )
					sb.append(ca, cb.position(), cbLen);
	
				bb.compact();
				cb.clear();
	
				if( result == CoderResult.OVERFLOW
						|| (result == CoderResult.UNDERFLOW && byteLength > 0) )
					continue;
	
				if( !lastLoop && byteLength <= 0 )
				{
					lastLoop = true;
					continue;
				}
				else
					break;
			}
		}
		finally
		{
			if( !(thread instanceof CharsetDecoderEncoderThread) )
				lock.unlock();
		}
		
		return sb.toString();
	}

	/**
	 * Decodiert ein {@link IOBuffer} zu einem String.
	 * @param in Das zulesende {@link IOBuffer}.
	 * @param charset Das Zeichensatz.
	 * @return Der fertige String.
	 */
	static public String decode(IOBuffer in, String charset)
	{
		return decode(in, charset, in.getReadableBytes());
	}
}
