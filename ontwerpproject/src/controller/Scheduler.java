package controller;

import global.Globals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.timroes.axmlrpc.XMLRPCException;
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
			List<Retriever> failedList 				= new ArrayList<Retriever>();
			
			for (Retriever r; (r = queue.poll()) != null;){
				boolean failed = false;
				
				if(Globals.DEBUGOUTPUT) System.out.println("Retrieving data from " + r.getComponent().getTableName() + "...");
				try {
						r.retrieveAllData();
					} catch (XMLRPCException e) {
					System.out.println(e.getMessage());
					if(e.getMessage().equals("java.net.ConnectException: Connection timed out: connect")) {
						System.out.println("Connection time out: Check IP and if the host is up");
					}
					else if(e.getMessage().equals("java.net.SocketException: Connection reset")) {
						System.out.println("Connection reset, Check if the server is up");
					}
					else if(e.getMessage().equals("java.net.ConnectException: Connection refused: connect")) {
						System.out.println("Connect Exception, Check if the server is up and if the file is correct: " + r.getClient().getURL().getFile());
					}
					else if(e.getMessage().contains("java.io.FileNotFoundException:")) {
						System.out.println("File not found exception, check if the file is correct: " + r.getClient().getURL().getFile());
					}
					else {
						e.printStackTrace();
					}					
					
					failed = true;
					failedList.add(r);
				}
				
				if(!failed) {
					if(Globals.DEBUGOUTPUT) {
						System.out.println("Retrieved data from \"" + r.getComponent().getTableName() + "\": " + Arrays.toString(r.getData()));
						System.out.println("Pushing data from " + r.getComponent().getTableName() + "...");
					}
					try {
						r.pushData();
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					if(Globals.DEBUGOUTPUT) System.out.println("Pushed data from \"" + r.getComponent().getTableName() + "\"");
				}
				
			}
			
			for(Retriever ret : failedList) {
				if(retrieverMap.get(period).contains(ret)) {
					try {
						ret.getComponent().getIntelligence().connectionError();
					} catch (ClosedException e) {
						e.printStackTrace();
					}
				}
				removeRetriever(ret);
			}
			
			failedList.clear();
		}		
	}
	
	private Map<Long, List<Retriever>> retrieverMap;
	private Map<Long, ConcurrentLinkedQueue<Retriever>> queueMap;
	private Map<Long, SchedulerTimer> taskMap;
	private Timer timer;
	
	public Scheduler() {
		retrieverMap = new HashMap<Long, List<Retriever>>();
		queueMap = new HashMap<Long, ConcurrentLinkedQueue<Retriever>>();
		taskMap = new HashMap<Long, SchedulerTimer>();
		timer = new Timer();
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
			timer.purge();
		}
	}
	
	private void checkAndCreate(long milliseconds) {
		if(!retrieverMap.containsKey(milliseconds)) {
			retrieverMap.put(milliseconds, new ArrayList<Retriever>());
			queueMap.put(milliseconds, new ConcurrentLinkedQueue<Retriever>());
			taskMap.put(milliseconds, new SchedulerTimer(milliseconds));
			
			timer.scheduleAtFixedRate(taskMap.get(milliseconds), milliseconds, milliseconds);			
		}
	}	
}
