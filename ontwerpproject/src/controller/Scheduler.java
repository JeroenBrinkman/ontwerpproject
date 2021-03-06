package controller;

import global.Globals;
import global.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {

	// TODO: OR make this class pretty OR move it from the class if possible
	class SchedulerTimer extends TimerTask {
		private long period;

		public SchedulerTimer(long period) {
			this.period = period;
		}

		@Override
		// TODO: After InvokeAll, should there be a clean up?
		public void run() {
			// The start sign
			Logger.log_debug("---------- START - comps: "
					+ retrieverMap.get(period).size() + " ----------");

			queueMap.get(period).addAll(retrieverMap.get(period));
			ConcurrentLinkedQueue<Retriever> queue = queueMap.get(period);
			ConcurrentLinkedQueue<Retriever> failed = new ConcurrentLinkedQueue<Retriever>();
			ExecutorService threadPool = Executors
					.newFixedThreadPool(Globals.CLIENT_THREADS);

			// Get all the retrievers and put them in a list to invoke them all
			ArrayList<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
			for (Retriever r; (r = queue.poll()) != null;) {
				tasks.add(new RetrieverThread(r, failed));
			}

			// INVOKEALL CALLS ALL THE RETRIEVERS, IF IT TAKES TO LONG IT
			// INTERRUPTS IT
			try {
				threadPool.invokeAll(tasks, Globals.POLLINGINTERVAL,
						TimeUnit.MILLISECONDS);
			} catch (InterruptedException e2) {
				Logger.log("ThreadPool was interrupted (in scheduler)");
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e2.printStackTrace(pw);
				Logger.log_debug_con(sw.toString());
			}

			threadPool.shutdownNow();

			// TODO: When do retrievers fail and when should they be removed
			// from the scheduler?
			// Checks if there are any failed retrievers, if so, remove them
			// from the scheduler
			for (Retriever ret : failed) {
				if (retrieverMap.get(period).contains(ret)) {
					Logger.log("Connection with "
							+ ret.getComponent().getTableName() + " failed");
					ret.getComponent().getIntelligence().connectionError();
				}
				removeRetriever(ret);
			}

			// The stop sign
			Logger.log_debug("----------------STOP----------------");
		}
	}

	private Map<Long, List<Retriever>> retrieverMap;
	private Map<Long, ConcurrentLinkedQueue<Retriever>> queueMap;
	private Map<Long, SchedulerTimer> taskMap;
	private ScheduledThreadPoolExecutor timer;

	public Scheduler() {
		retrieverMap = new HashMap<Long, List<Retriever>>();
		queueMap = new HashMap<Long, ConcurrentLinkedQueue<Retriever>>();
		taskMap = new HashMap<Long, SchedulerTimer>();
		timer = new ScheduledThreadPoolExecutor(Globals.SCHEDULER_THREADS);
	}

	/**
	 * Adds the retriever to the scheduler
	 * 
	 * @require r != null and that milliseconds >
	 *          {@link Globals#XMLRPCTIMEOUT_IN_SECONDS} * 1000
	 * @ensure That the retriever will be called at the given milliseconds
	 * @param milliseconds
	 *            the period that the Retriever object should be called for
	 * @param r
	 *            the Retriever object that will be called
	 */
	public void addRetriever(long milliseconds, Retriever r) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			this.retrieverMap.get(milliseconds).add(r);
		}
	}

	/**
	 * See {link {@link #addRetriever(long, Retriever)}
	 * 
	 * @param milliseconds
	 * @param rs
	 */
	public void addRetrievers(long milliseconds, Retriever[] rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			for (Retriever r : rs) {
				this.retrieverMap.get(milliseconds).add(r);
			}
		}
	}

	/**
	 * See {link {@link #addRetriever(long, Retriever)}
	 * 
	 * @param milliseconds
	 * @param rs
	 */
	public void addRetrievers(long milliseconds, Collection<Retriever> rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			this.retrieverMap.get(milliseconds).addAll(rs);
		}
	}

	/**
	 * Checks all retrievers the scheduler has, and returns a retriever if the
	 * given hostname and port match
	 * 
	 * @return the retriever Object with the same hostname and port or null
	 */
	public Retriever getRetriever(String hostname, int port) {
		InetSocketAddress adr = new InetSocketAddress(hostname, port);

		for (long key : retrieverMap.keySet()) {
			for (Retriever r : retrieverMap.get(key)) {
				if (r.getComponent().getAddress().equals(adr))
					return r;
			}
		}

		return null;
	}

	/**
	 * Returns all the retrievers that have a calling period of the given
	 * milliseconds. Returns null if there are none
	 * 
	 * @param milliseconds
	 * @return null or retrieverMap with size > 0
	 */
	public Retriever[] getAllRetrievers(long milliseconds) {
		Retriever[] result = null;
		if (retrieverMap.containsKey(milliseconds)) {
			result = new Retriever[retrieverMap.get(milliseconds).size()];
			for (int index = 0; index < retrieverMap.get(milliseconds).size(); index++)
				result[index] = retrieverMap.get(milliseconds).get(index);
		}
		return result;
	}

	/**
	 * Removes the given retriever from all possible calling periods
	 * 
	 * @param ret
	 */
	public void removeRetriever(Retriever ret) {
		synchronized (retrieverMap) {
			for (long key : retrieverMap.keySet())
				removeRetriever(key, ret);
		}
	}

	/**
	 * Removes the retriever at the calling period of the given milliseconds
	 * 
	 * @param milliseconds
	 * @param ret
	 */
	public void removeRetriever(long milliseconds, Retriever ret) {
		synchronized (retrieverMap) {
			retrieverMap.get(milliseconds).remove(ret);
			queueMap.get(milliseconds).remove(ret);
			checkAndDestroy(milliseconds);
		}
	}

	/**
	 * Destroys this retriever and all threads it has spawned
	 */
	public void destroy() {
		for (long key : retrieverMap.keySet()) {
			retrieverMap.get(key).clear();
			checkAndDestroy(key);
		}
		this.timer.shutdownNow();
	}

	/**
	 * Checks if there the retrieverMap is empty at the given milliseconds. If
	 * this is true, destroy the timer and threads it has spawned
	 * 
	 * @param milliseconds
	 */
	private void checkAndDestroy(long milliseconds) {
		if (retrieverMap.get(milliseconds).isEmpty()) {
			Logger.log_debug("Retriever map is empty, destroying the thread");

			retrieverMap.remove(milliseconds);
			queueMap.remove(milliseconds);

			timer.remove(taskMap.get(milliseconds));
			taskMap.get(milliseconds).cancel();
			taskMap.remove(milliseconds);
		}
	}

	/**
	 * Checks if the timer already calls at the given milliseconds If not, it
	 * creates it.
	 * 
	 * @param milliseconds
	 */
	private void checkAndCreate(long milliseconds) {
		if (!retrieverMap.containsKey(milliseconds)) {
			retrieverMap.put(milliseconds, new ArrayList<Retriever>());
			queueMap.put(milliseconds, new ConcurrentLinkedQueue<Retriever>());
			taskMap.put(milliseconds, new SchedulerTimer(milliseconds));

			Logger.log_debug("Schedule added at " + milliseconds
					+ " milliseconds");
			timer.scheduleAtFixedRate(taskMap.get(milliseconds), milliseconds,
					milliseconds, TimeUnit.MILLISECONDS);
		}
	}
}
