package br.com.mateus.concurrencytransaction;

public enum ConcurrencyAction {

	/**
	 * Wait for the locked entity in a queue thread.
	 */
	WAIT,
	/**
	 * Throw exception if try access a locked entity.
	 */
	ERROR;
	
}