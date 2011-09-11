package de.coding_bereich.net.buffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * Gibt einem Thread die Fähigkeit einen Byte- und Charbuffer zurückgeben
 * zukönnen. Dies soll die Umwandlung von Bytes nach Chars oder umgekehrt
 * beschleunigen.
 * 
 * @see CharsetEncoder
 * @see CharsetDecoder
 * @author Thomas
 * 
 */
public interface CharsetDecoderEncoderThread
{
	public ByteBuffer getTempArrayByteBuffer();

	public CharBuffer getTempArrayCharBuffer();
}
