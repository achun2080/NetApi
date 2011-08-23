package de.coding_bereich.net.channel;

import de.coding_bereich.net.WorkerTask;
import de.coding_bereich.net.Manager;

public class ChannelHandlerWorker implements WorkerTask
{
	private static final ChannelHandlerWorker	instance	= new ChannelHandlerWorker();

	private static enum TaskType
	{
		IN_MSG, OUT_MSG
	}

	private ChannelHandlerWorker()
	{
	}

	static public ChannelHandlerWorker getInstance()
	{
		return instance;
	}

	@Override
	public void executeTask(Object[] params) throws Exception
	{
		ChannelEvent event = (ChannelEvent) params[1];
		Channel channel = event.getChannel();

		try
		{
			switch((TaskType) params[0])
			{
				case IN_MSG:
					channel.getHandler().onIncomingMessage(event);
					break;

				case OUT_MSG:
					channel.getHandler().onOutgoingMessage(event);
					break;
			}
		}
		catch(Exception e)
		{
			event.getFuture().onException(e);
		}
	}

	public void onIncomingMessage(ChannelEvent event)
	{
		Object[] params = { TaskType.IN_MSG, event };
		Manager.getInstance().getWorker().doWork(this, params);
	}

	public void onOutgoingMessage(ChannelEvent event)
	{
		Object[] params = { TaskType.OUT_MSG, event };
		Manager.getInstance().getWorker().doWork(this, params);
	}
}
