package controller;

import global.Globals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {
	
	class SchedulerTimer extends TimerTask {		
		private long period;
		
		public SchedulerTimer(long period) {
			this.period = period;
		}

		@Override
		public void run() {			
			queueMap.get(period).addAll(retrieverMap.get(period));
			ConcurrentLinkedQueue<Retriever> queue 	= queueMap.get(period);
			ConcurrentLinkedQueue<Retriever> failed	= new ConcurrentLinkedQueue<Retriever>();
			ExecutorService threadPool				= Executors.newFixedThreadPool(Globals.SchedulerTimerThreads);
			Stack<Future<?>> results				= new Stack<Future<?>>();
			
			for (Retriever r; (r = queue.poll()) != null;){
				results.add(threadPool.submit(new RetrieverThread(r, failed)));			
			}
			
			try {
				if(!threadPool.awaitTermination(Globals.SchedulerTimerTimeout, TimeUnit.MILLISECONDS)) {
					System.out.println("Threads has been interupptedksdlk, please call an adult");
				}
				List<Runnable> list = threadPool.shutdownNow();
				if(!list.isEmpty()) {
					System.out.println("Shutdown now not empty");
				}
			} catch (InterruptedException e1) {
				System.out.println("Interrupted Exeception of threadPool in SchedulerTimer: ");
				e1.printStackTrace();
			}
			
			/*while(!results.isEmpty()) {
				System.out.println("Popping");
				Future<?> ftr = results.pop();
				System.out.println("Popping2");
				try {
					if(!ftr.isDone()) {
						System.out.println("Cancel ftr");
						ftr.cancel(true);
					}
					System.out.println("ftr.get");
					if(ftr.get() != null) {
						System.out.println("This future failed: " + ftr.toString());
					}
					System.out.println("After if");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					System.out.println("Timeout Failed ofzo");
				}
			}*/
			
			System.out.println("For loop thingie");
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
			
			System.out.println("Stopping Timer Task");
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
		timer = new ScheduledThreadPoolExecutor(Globals.SchedulerThreads);
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
			if(Globals.DEBUGOUTPUT)
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
			
			if(Globals.DEBUGOUTPUT)
				System.out.println("Schedule added at " + milliseconds + " milliseconds");
			timer.scheduleAtFixedRate(taskMap.get(milliseconds), milliseconds, milliseconds, TimeUnit.MILLISECONDS);			
		}
	}	
}
