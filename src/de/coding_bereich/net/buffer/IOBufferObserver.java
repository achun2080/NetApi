package de.coding_bereich.net.buffer;

/**
 * @author Thomas
 *
 */
public interface IOBufferObserver
{
	/**
	 * Wird aufgerufen, wenn der Buffer verschickt werden "möchte".
	 * @param buffer Der {@link IOBuffer}.
	 */
	public void onBufferFlush(IOBuffer buffer);
}