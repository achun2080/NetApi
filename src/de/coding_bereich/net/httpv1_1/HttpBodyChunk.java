package de.coding_bereich.net.httpv1_1;

import de.coding_bereich.net.buffer.IOBuffer;

public class HttpBodyChunk
{
	private int lenght = 0;
	private IOBuffer buffer;
	private boolean last;
	
	public HttpBodyChunk(IOBuffer buffer, boolean last)
	{
		this.buffer = buffer;
		this.last = last;
		lenght = buffer.getReadableBytes();
	}
	
	public int getLenght()
	{
		return lenght;
	}
	
	public IOBuffer getBuffer()
	{
		return buffer;
	}
	
	public boolean isLast()
	{
		return last;
	}
}
