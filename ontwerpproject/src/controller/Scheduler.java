package controller;

import global.Globals;
import global.Logger;

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

//TODO Scheduler is multithreaded, but should probably be singlethreaded, The scheduledthreadtimer may be overkill, but to be fair, i like to keep my options open.
public class Scheduler {
	
	//TODO: OR make this class pretty OR move it from the class if possible
	class SchedulerTimer extends TimerTask {		
		private long period;
		
		public SchedulerTimer(long period) {
			this.period = period;
		}

		@Override
		//TODO: After InvokeAll, should there be a clean up?
		public void run() {			
			//The start sign
			if(Logger.PRINT_DEBUG)
				System.out.println("----------------START @" + System.currentTimeMillis() + "----------------");
			
			
			queueMap.get(period).addAll(retrieverMap.get(period));
			ConcurrentLinkedQueue<Retriever> queue 	= queueMap.get(period);
			ConcurrentLinkedQueue<Retriever> failed	= new ConcurrentLinkedQueue<Retriever>();
			ExecutorService threadPool				= Executors.newFixedThreadPool(Globals.CLIENT_THREADS);
			
			// Get all the retrievers and put them in a list to invoke them all
			ArrayList<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
			for (Retriever r; (r = queue.poll()) != null;){
				tasks.add(new RetrieverThread(r, failed));
			}
			
			//INVOKEALL CALLS ALL THE RETRIEVERS, IF IT TAKES TO LONG IT INTERRUPTS IT
			try {
				threadPool.invokeAll(tasks, Globals.POLLINGINTERVAL, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e2) {
				if(Logger.PRINT_DEBUG) {
					System.out.println("ThreadPool was interrupted (in scheduler)");
					e2.printStackTrace();
				}
			}			
			
			//TODO: When do retrievers fail and when should they be removed from the scheduler?
			// Checks if there are any failed retrievers, if so, remove them from the scheduler
			for(Retriever ret : failed) {
				if(retrieverMap.get(period).contains(ret)) {
					try {
						System.out.println("Connection with " + ret.getComponent().getTableName() + " failed");
						ret.getComponent().getIntelligence().connectionError();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				removeRetriever(ret);
			}
			
			// The stop sign
			if(Logger.PRINT_DEBUG)
				System.out.println("----------------STOP @" + System.currentTimeMillis() + "----------------");
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
	
	public void addRetriever(long milliseconds, Retriever r) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			this.retrieverMap.get(milliseconds).add(r);	
		}
	}
	
	public void addRetrievers(long milliseconds, Retriever[] rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);		
			for(Retriever r : rs) {
				this.retrieverMap.get(milliseconds).add(r);
			}
		}
	}
	
	public void addRetrievers(long milliseconds, Collection<Retriever> rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			this.retrieverMap.get(milliseconds).addAll(rs);
		}
	}
	
	public Retriever getRetriever(String hostname, int port) {
		InetSocketAddress adr = new InetSocketAddress(hostname, port);
		
		for(long key : retrieverMap.keySet()) {
			for(Retriever r : retrieverMap.get(key)) {
				if(r.getComponent().getAddress().equals(adr))
					return r;
			}
		}
		
		return null;
	}
	
	public void removeRetriever(Retriever ret) {
		synchronized (retrieverMap) {
			for(long key : retrieverMap.keySet())
				removeRetriever(key, ret);
		}
	}
	
	public void removeRetriever(long milliseconds, Retriever ret) {
		synchronized (retrieverMap) {
			retrieverMap.get(milliseconds).remove(ret);
			queueMap.get(milliseconds).remove(ret);
			checkAndDestroy(milliseconds);
		}
	}
	
	private void checkAndDestroy(long milliseconds) {		
		if(retrieverMap.get(milliseconds).isEmpty()) {
			if(Logger.PRINT_DEBUG)
				System.out.println("Retriever map is empty, destroying the thread");
			
			retrieverMap.remove(milliseconds);
			queueMap.remove(milliseconds);
			
			timer.remove(taskMap.get(milliseconds));
			taskMap.get(milliseconds).cancel();
			taskMap.remove(milliseconds);			
		}
	}
	
	private void checkAndCreate(long milliseconds) {
		if(!retrieverMap.containsKey(milliseconds)) {
			retrieverMap.put(milliseconds, new ArrayList<Retriever>());
			queueMap.put(milliseconds, new ConcurrentLinkedQueue<Retriever>());
			taskMap.put(milliseconds, new SchedulerTimer(milliseconds));
			
			if(Logger.PRINT_DEBUG)
				System.out.println("Schedule added at " + milliseconds + " milliseconds");
			timer.scheduleAtFixedRate(taskMap.get(milliseconds), milliseconds, milliseconds, TimeUnit.MILLISECONDS);			
		}
	}	
}
