package de.coding_bereich.net.channel;

/**
 * Lauscht auf Änderungen eines EventFuture.
 * @author Thomas
 *
 */
public interface ChannelEventFutureListener
{
	static public ChannelEventFutureListener	CLOSE	= 
		new ChannelEventFutureListener()
		{
			@Override
			public void onAction(ChannelEventFuture future)
			{
				future.getEvent().getChannel().close();
			}
		};
	
	static public ChannelEventFutureListener	CLOSE_ON_FAILURE	= 
		new ChannelEventFutureListener()
		{
			@Override
			public void onAction(ChannelEventFuture future)
			{
				if( !future.isSuccess() )
					future.getEvent().getChannel().close();
			}
		};

	/**
	 * Wird bei jeder Änderung des Futures aufgerufen.
	 * @param future
	 */
	public void onAction(ChannelEventFuture future);
}
