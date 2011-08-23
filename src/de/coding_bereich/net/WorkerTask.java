package de.coding_bereich.net;

/**
 * Ein Arbeitspacket für einen Arbeiter.
 * @see Worker
 * @author Thomas
 *
 */
public interface WorkerTask
{
	/**
	 * Wird von demjedigen Arbeiter aufgerufen um Arbeit zuerledigen.
	 * @param params Parameter.
	 * @throws Exception 
	 */
	public void executeTask(Object[] params) throws Exception;
}
