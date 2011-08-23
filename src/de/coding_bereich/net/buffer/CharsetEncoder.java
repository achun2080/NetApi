package de.coding_bereich.net.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Encodiert ein String in ein {@link IOBuffer}.
 * @author thomas
 * 
 */
public class CharsetEncoder
{
	static protected ByteBuffer	byteBuffer	= ByteBuffer.allocate(1024);

	static protected Lock lock = new ReentrantLock();
	
	/**
	 * Encodiert ein String in ein {@link IOBuffer}.
	 * @param in Der String als Eingabe.
	 * @param out Der {@link IOBuffer} als Ausgabe.
	 * @param charset Der Zeichensatz.
	 * @param maxByteLength Maximale Anzahl an Bytes, die Codiert werden.
	 */
	static public void encode(CharSequence in, IOBuffer out, String charset,
			int maxByteLength)
	{
		ByteBuffer bb;
		CharBuffer cb;
		
		Thread thread = Thread.currentThread();
		boolean useTreadVars = thread instanceof CharsetDecoderEncoderThread;
		
		if( useTreadVars )
		{
			CharsetDecoderEncoderThread thread1 = ((CharsetDecoderEncoderThread) thread);
			bb = thread1.getTempArrayByteBuffer();
		}
		else
			bb = byteBuffer;
		
		java.nio.charset.CharsetEncoder e = Charset.forName(charset).newEncoder();

		cb = CharBuffer.wrap(in);
		
		e.reset();
		byte[] replaceWith = { 0 };
		e.replaceWith(replaceWith);
		e.onMalformedInput(CodingErrorAction.REPLACE);
		e.onUnmappableCharacter(CodingErrorAction.REPLACE);

		if( !useTreadVars )
			lock.lock();
		
		try
		{
			bb.clear();

			boolean lastLoop = false;

			while( true )
			{
				int bytesToEncode = Math.min(bb.capacity(), maxByteLength);

				if( bytesToEncode > 0 )
					bb.limit(bytesToEncode);

				CoderResult result = e.encode(cb, bb, lastLoop);

				if( lastLoop )
					result = e.flush(bb);

				bb.flip();
				bytesToEncode = bb.remaining();
				if( bytesToEncode > 0 )
				{
					out.write(bb, bytesToEncode);
					maxByteLength -= bytesToEncode;
				}

				bb.clear();

				if( result == CoderResult.OVERFLOW )
					continue;

				if( lastLoop )
					break;

				lastLoop = true;
				continue;
			}
		}
		finally
		{
			if( !useTreadVars )
				lock.unlock();
		}
	}

	/**
	 * Encodiert ein String in ein {@link IOBuffer}.
	 * @param str Der String als Eingabe.
	 * @param out Der {@link IOBuffer} als Ausgabe.
	 * @param charset Der Zeichensatz.
	 */
	static public void encode(CharSequence str, IOBuffer out, String charset)
	{
		encode(str, out, charset, Integer.MAX_VALUE);
	}
}
