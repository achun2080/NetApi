package de.coding_bereich.net;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Verwaltet Arbeitsthreads und weist ihnen Arbeit zu.
 * 
 * @author Thomas
 * 
 */
public class WorkerPool implements Worker
{
	protected LinkedBlockingQueue<Object[]>	queue			= new LinkedBlockingQueue<Object[]>();
	protected LinkedList<WorkerPoolWorker>		workerList	= new LinkedList<WorkerPoolWorker>();

	public WorkerPool()
	{
		this(Runtime.getRuntime().availableProcessors() * 2);
	}

	public WorkerPool(int count)
	{
		for(int i = 0; i < count; i++)
		{
			WorkerPoolWorker wpw = new WorkerPoolWorker(queue);
			workerList.add(wpw);
			wpw.start();
		}
	}

	@Override
	public void doWork(WorkerTask task, Object[] params)
	{
		Object[] t = { task, params };
		queue.add(t);
	}

	public LinkedBlockingQueue<Object[]> getQueue()
	{
		return queue;
	}

	public LinkedList<WorkerPoolWorker> getWorkerList()
	{
		return workerList;
	}
}
