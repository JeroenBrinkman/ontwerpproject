package controller;

import global.Globals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import model.intelligence.Intelligence.ClosedException;

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
			ExecutorService threadPool				= Executors.newFixedThreadPool(16);
			
			for (Retriever r; (r = queue.poll()) != null;){
				threadPool.submit(new RetrieverThread(r, failed));				
			}
						
			try {
				threadPool.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e1) {
				System.out.println("Interrupted Exeception of threadPool in SchedulerTimer: ");
				e1.printStackTrace();
			}
			
			for(Retriever ret : failed) {
				if(retrieverMap.get(period).contains(ret)) {
					try {
						ret.getComponent().getIntelligence().connectionError();
					} catch (ClosedException e) {
						e.printStackTrace();
					}
				}
				removeRetriever(ret);
			}
		}		
	}
	
	private Map<Long, List<Retriever>> retrieverMap;
	private Map<Long, ConcurrentLinkedQueue<Retriever>> queueMap;
	private Map<Long, SchedulerTimer> taskMap;
	private ScheduledExecutorService timer;
	
	public Scheduler() {
		retrieverMap = new HashMap<Long, List<Retriever>>();
		queueMap = new HashMap<Long, ConcurrentLinkedQueue<Retriever>>();
		taskMap = new HashMap<Long, SchedulerTimer>();
		//timer = new Timer();
		timer = Executors.newScheduledThreadPool(4);
	}
	
	public void addRetriever(long milliseconds, Retriever r) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			this.queueMap.get(milliseconds).add(r);
			this.retrieverMap.get(milliseconds).add(r);	
		}
	}
	
	public void addRetrievers(long milliseconds, Retriever[] rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);		
			for(Retriever r : rs) {
				this.queueMap.get(milliseconds).add(r);
				this.retrieverMap.get(milliseconds).add(r);
			}
		}
	}
	
	public void addRetrievers(long milliseconds, Collection<Retriever> rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);
			this.retrieverMap.get(milliseconds).addAll(rs);
			this.queueMap.get(milliseconds).addAll(rs);
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
			System.out.println("Retriever map is empty, destroying the thread");
			retrieverMap.remove(milliseconds);
			queueMap.remove(milliseconds);
			
			taskMap.get(milliseconds).cancel();
			taskMap.remove(milliseconds);
			timer.shutdown();
		}
	}
	
	private void checkAndCreate(long milliseconds) {
		if(!retrieverMap.containsKey(milliseconds)) {
			retrieverMap.put(milliseconds, new ArrayList<Retriever>());
			queueMap.put(milliseconds, new ConcurrentLinkedQueue<Retriever>());
			taskMap.put(milliseconds, new SchedulerTimer(milliseconds));
			
			timer.scheduleAtFixedRate(taskMap.get(milliseconds), milliseconds, milliseconds, TimeUnit.MILLISECONDS);			
		}
	}	
}
