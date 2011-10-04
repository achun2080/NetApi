package de.coding_bereich.net.channel.pipeline;

public class PipelineDecoderErrorMessage
{
	private PipelineDecoder<?> decoder;
	private Exception exception;
	
	public PipelineDecoderErrorMessage(PipelineDecoder<?> decoder, Exception exception)
	{
		this.decoder = decoder;
		this.exception = exception;
	}

	public PipelineDecoder<?> getDecoder()
	{
		return decoder;
	}

	public Exception getException()
	{
		return exception;
	}	
}
