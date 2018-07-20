package br.com.mateus.concurrencytransaction;

import java.util.concurrent.Semaphore;

public class ConcurrencySemaphore {

	private static final Integer THREADS_ALLOWED = 1;
	
	private String threadName;
	private Semaphore semaphore;
	
	public ConcurrencySemaphore() {
		this.threadName = Thread.currentThread().getName();
		this.semaphore = new Semaphore(THREADS_ALLOWED);
	}
	
	public void acquire() throws InterruptedException{
		if(isSameThread()){
			this.semaphore.tryAcquire();
		} else {
			this.semaphore.acquire();
		}
	}
	
	public void bruteAcquire() throws InterruptedException{
		if(isSameThread()){
			this.semaphore.tryAcquire();
		} else {
			if(this.semaphore.availablePermits() > 0){
				this.semaphore.acquire();
			} else {
				throw new ConcurrencyTransactionException();
			}
		}
	}
	
	public void release() throws InterruptedException{
		this.semaphore.release();
	}
	
	public boolean hasQueuedThreads(){
		return this.semaphore.hasQueuedThreads();
	}
	
	private boolean isSameThread() {
		return Thread.currentThread().getName().equals(this.threadName);
	}
	
}