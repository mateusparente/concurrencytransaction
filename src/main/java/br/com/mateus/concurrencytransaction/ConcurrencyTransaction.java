package br.com.mateus.concurrencytransaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p> Useful Methods to working with concurrency. </p>
 * 
 * <p> ConcurrencyTransaction are used to restrict the number of threads than can
 * access some. </br>
 * 
 * ConcurrencyTransaction maintains a map of objets and their respective {@code Semaphore}. </br></p>
 * 
 * The number of threads allowed is fixed to <b>one</b>.
 * <pre>
 * 	<i>Example</i>
 * 	ConcurrencyTransaction.block(new Product(1), ConcurrencyAction.WAIT, ConcurrencyUnblock.AFTER_COMPLETION);
 * </pre>
 * 
 * @author Mateus Parente
 */
public class ConcurrencyTransaction {

	private static Map<Object, ConcurrencySemaphore> semaphores = Collections.synchronizedMap(new HashMap<Object, ConcurrencySemaphore>());
	
	/**
     * <p>Lock the entity list, as default ConcurrencyAction.ERROR and 
     * unblocking when ConcurrencyUnblock.AFTER_COMPLETION, based on spring transaction.</p>
     *
     * <pre>
     * 	<i>Example</i>
     * 	ConcurrencyTransaction.block(new ArrayList<>(new Product(1),new Product(2)));
     * </pre>
     *
     * @param entities The Entity List to lock.
     */
	public static <T> void blockList(List<T> entities){
		if(entities != null && !entities.isEmpty())
			entities.forEach(entity -> block(entity));
	}
	
	/**
     * <p>Lock the entity, as default ConcurrencyAction.ERROR and 
     * unblocking when ConcurrencyUnblock.AFTER_COMPLETION, based on spring transaction.</p>
     *
     * <pre>
     * 	<i>Example</i>
     * 	ConcurrencyTransaction.block(new Product(1));
     * </pre>
     *
     * @param entity The Entity to lock.
     */
	public static void block(Object entity){
		block(entity, ConcurrencyAction.ERROR, ConcurrencyUnblock.AFTER_COMPLETION);
	}
	
	/**
     * <p>Lock the entity.</p>
     *
     * <pre>
     * 	<i>Example</i>
     * 	ConcurrencyTransaction.block(new Product(1), ConcurrencyAction.WAIT, ConcurrencyUnblock.AFTER_COMPLETION);
     * </pre>
     *
     * @param entity The Entity to lock.
     * @param action Action when other thread try access to same object locked.
     * @param whenUnblock Optional. Automatic unblock entity based on spring transaction.
     */
	public static void block(Object entity, ConcurrencyAction action, ConcurrencyUnblock whenUnblock) {
		
		try {
			
			ConcurrencySemaphore semaphore = getOrInsert(entity);
			
			if(ConcurrencyAction.ERROR.equals(action)){
				
				semaphore.bruteAcquire();
				
			} else if(ConcurrencyAction.WAIT.equals(action)) {
				
				semaphore.acquire();
				
			}
			
			registerSyncronization(entity, whenUnblock);
			
		} catch (Throwable e) {
			throw new ConcurrencyTransactionException(e.getMessage());
		}
	}
	
	private static void registerSyncronization(Object entity, ConcurrencyUnblock whenUnblock) {
		/*if(whenUnblock != null && TransactionSynchronizationManager.isSynchronizationActive()){
			if(ConcurrencyUnblock.AFTER_COMPLETION.equals(whenUnblock)){
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
					@Override
					public void afterCompletion(int status) {
						ConcurrencyTransaction.unblock(entity);
					}
				});
			} else if(ConcurrencyUnblock.BEFORE_COMMIT.equals(whenUnblock)){
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
					@Override
					public void beforeCommit(boolean readOnly) {
						ConcurrencyTransaction.unblock(entity);
					}
				});
			}
		}*/
	}

	private static ConcurrencySemaphore getOrInsert(Object entity) throws InterruptedException {
		synchronized (semaphores) {
			if(semaphores.containsKey(entity)){
				ConcurrencySemaphore semaphore = semaphores.get(entity);
				return semaphore;
			} else {
				ConcurrencySemaphore semaphore = new ConcurrencySemaphore();
				semaphores.put(entity, semaphore);
				return semaphore;
			}
		}
	}
	
	private static void removeAndRelease(Object entity) throws InterruptedException {
		synchronized (semaphores) {
			if(semaphores.containsKey(entity)){
				
				ConcurrencySemaphore semaphore = semaphores.get(entity);
				semaphore.release();
				
				if(!semaphore.hasQueuedThreads())
					semaphores.remove(entity);
			}
		}
	}

	/**
     * <p>Unlock the entity.</p>
     *
     * <pre>
     * 	<i>Example</i>
     * 	ConcurrencyTransaction.unblock(new Product(1));
     * </pre>
     *
     * @param entity The Entity to unlock.
     */
	public static void unblock(Object entity) {
		try {
			removeAndRelease(entity);
		} catch (Throwable e) {
			throw new ConcurrencyTransactionException("Erro inesperado.");
		}
	}
	
	/**
     * <p>Unlock the entity list.</p>
     *
     * <pre>
     * 	<i>Example</i>
     * 	ConcurrencyTransaction.unblockList(new ArrayList<>(new Product(1),new Product(2)));
     * </pre>
     *
     * @param entities The Entity List to unlock.
     */
	public static <T> void unblockList(List<T> entities) {
		if(entities != null && !entities.isEmpty())
			entities.forEach(entity -> unblock(entity));
	}
}