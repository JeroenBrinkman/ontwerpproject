package controller;

import global.Globals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.timroes.axmlrpc.XMLRPCException;
import model.Model;
import model.Worker;

public class Scheduler {
	
	class Wub extends TimerTask {
		
		private long period;
		
		public Wub(long period) {
			this.period = period;
		}

		@Override
		public void run() {
			synchronized (retrieverMap) {
				List<Retriever> list = retrieverMap.get(period);
				List<Retriever> failedList = new ArrayList<Retriever>();
				
				for(Retriever r : list) {
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
						r.pushData();
						if(Globals.DEBUGOUTPUT) System.out.println("Pushed data from \"" + r.getComponent().getTableName() + "\"");
					}
					
				}
				
				for(Retriever r : failedList) {
					removeRetriever(r);
				}
				
				failedList.clear();
			}
		}
		
	}
	
	private Map<Long, List<Retriever>> retrieverMap;
	private Map<Long, Wub> taskMap;
	private Timer timer;
	
	public Scheduler() {
		retrieverMap = new HashMap<Long, List<Retriever>>();
		taskMap = new HashMap<Long, Wub>();
		timer = new Timer();
	}
	
	public void addRetriever(long milliseconds, Retriever r) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);		
			this.retrieverMap.get(milliseconds).add(r);		
		}
	}
	
	public synchronized void addRetrievers(long milliseconds, Retriever[] rs) {
		synchronized (retrieverMap) {
			checkAndCreate(milliseconds);		
			for(Retriever r : rs)
				this.retrieverMap.get(milliseconds).add(r);
		}
	}
	
	public synchronized void addRetrievers(long milliseconds, Collection<Retriever> rs) {
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
	
	public synchronized void removeRetriever(Retriever ret) {
		for(long key : retrieverMap.keySet())
			removeRetriever(key, ret);
	}
	
	public synchronized void removeRetriever(long milliseconds, Retriever ret) {
		retrieverMap.get(milliseconds).remove(ret);
		checkAndDestroy(milliseconds);
	}
	
	private void checkAndDestroy(long milliseconds) {		
		if(retrieverMap.get(milliseconds).isEmpty()) {
			System.out.println("Retriever map is empty, destroying the thread");
			retrieverMap.remove(milliseconds);
			
			taskMap.get(milliseconds).cancel();
			taskMap.remove(milliseconds);
			timer.purge();
		}
	}
	
	private void checkAndCreate(long milliseconds) {
		if(!retrieverMap.containsKey(milliseconds)) {
			retrieverMap.put(milliseconds, new ArrayList<Retriever>());
			taskMap.put(milliseconds, new Wub(milliseconds));
			
			timer.scheduleAtFixedRate(taskMap.get(milliseconds), milliseconds, milliseconds);			
		}
	}
	
	// TODO remove test main after im done with testing
	public static void main(String[] args) {
		Model model = new Model();
		Connection conn = null;
		while((conn = model.createConnection()) == null) {
			System.out.println("Could not connect to SQL Database!");
			System.out.println("Press enter to try again or exit to quit.");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(input.equals("exit")) {
				System.out.println("Lololol doesn't work!");
			}
		}
		
		Worker work1 = new Worker(new InetSocketAddress("localhost", 8000), model.createConnection());
		Worker work2 = new Worker(new InetSocketAddress("localhost", 7999), model.createConnection());
		Worker work3 = new Worker(new InetSocketAddress("localhost", 7998), model.createConnection());
		
		System.out.println(work1.getTableName());
		System.out.println(work2.getTableName());
		System.out.println(work3.getTableName());
		
		model.addComponent(work1);
		model.addComponent(work2);
		model.addComponent(work3);
		
		Retriever[] retList = new Retriever[3];
		retList[0] = new Retriever(work1);
		retList[1] = new Retriever(work2);
		retList[2] = new Retriever(work3);
		
		Scheduler sched = new Scheduler();
		sched.addRetrievers(1000, retList);
	}
	
}
