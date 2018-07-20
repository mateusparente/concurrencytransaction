package br.com.mateus.concurrencytransaction;

public enum ConcurrencyUnblock {

	/**
	 * Invoked before transaction commit.
	 */
	BEFORE_COMMIT,
	
	/**
	 * Invoked after transaction commit/rollback.
	 */
	AFTER_COMPLETION;
	
}