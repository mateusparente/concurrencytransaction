package br.com.mateus.concurrencytransaction;

public class ConcurrencyTransactionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5617572524705788590L;

	public ConcurrencyTransactionException() {
		super();
	}
	
	public ConcurrencyTransactionException(String message) {
		super(message);
	}
	
}