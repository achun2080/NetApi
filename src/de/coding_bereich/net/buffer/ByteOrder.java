package de.coding_bereich.net.buffer;

/**
 * Reine static-Klasse. Gibt die Byt-Reihenfolge an.
 * @author Thomas
 *
 */
public class ByteOrder
{
  final static public ByteOrder BIG_ENDIAN = new ByteOrder();
  final static public ByteOrder LITTLE_ENDIAN = new ByteOrder();
  
  private ByteOrder()
  {}
}
