package de.coding_bereich.net;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import de.coding_bereich.net.buffer.CharsetDecoderEncoderThread;

/**
 * Arbeitsthread, wird vom {@link WorkerPool} erzeugt und von diesem Verwaltet.
 * Desweiteren bringt jeder Thread seine eigenen Byte- und Charbuffer mit. 
 * Dies soll die Umwandlung von Bytes nach Chars oder umgekehrt beschleunigen.
 * @author Thomas
 *
 */
public class WorkerPoolWorker
	extends Thread
	implements CharsetDecoderEncoderThread
{
	protected LinkedBlockingQueue<Object[]> queue;
	protected boolean run = true;	
	
 	protected ByteBuffer tempArrayByteBuffer = ByteBuffer.allocate(4096);
	protected CharBuffer tempArrayCharBuffer = CharBuffer.allocate(4096);
	
	public WorkerPoolWorker(LinkedBlockingQueue<Object[]> queue)
	{
		this.queue = queue;
	}
	
	public void stopRunning()
	{
		run = false;
	}
	
	public boolean isRunning()
	{
		return run;
	}
	
	public void run()
	{
		while(run)
		{
			try
			{
				Object[] t = queue.take();
				((WorkerTask)t[0]).executeTask((Object[])t[1]);
			}
			catch(Exception e)
			{}
		}
	}

	@Override
	public CharBuffer getTempArrayCharBuffer()
	{
		return tempArrayCharBuffer;
	}

	@Override
	public ByteBuffer getTempArrayByteBuffer()
	{
		return tempArrayByteBuffer;
	}
}
