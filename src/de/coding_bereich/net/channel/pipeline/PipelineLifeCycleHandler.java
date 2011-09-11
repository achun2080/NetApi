/**
 * 
 */
package de.coding_bereich.net.channel.pipeline;

/**
 * @author Thomas
 * 
 */
public interface PipelineLifeCycleHandler extends PipelineHandler
{
	public void beforeAdd(Pipeline pipeline);

	public void afterAdd(Pipeline pipeline);

	public void beforeRemove(Pipeline pipeline);

	public void afterRemove(Pipeline pipeline);
}
