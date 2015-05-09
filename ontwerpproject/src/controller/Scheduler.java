package controller;

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
			List<Retriever> list = retrieverMap.get(period);	
			
			for(Retriever r : list) {
				r.retrieveAllData();
				System.out.println("Retrieved data from \"" + r.getComponent().getTableName() + "\": " + Arrays.toString(r.getData()));
				r.pushData();
				System.out.println("Pushed data from \"" + r.getComponent().getTableName() + "\"");
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
		checkAndCreate(milliseconds);		
		this.retrieverMap.get(milliseconds).add(r);		
	}
	
	public void addRetrievers(long milliseconds, Retriever[] rs) {
		checkAndCreate(milliseconds);		
		for(Retriever r : rs)
			this.retrieverMap.get(milliseconds).add(r);
	}
	
	public void addRetrievers(long milliseconds, Collection<Retriever> rs) {
		checkAndCreate(milliseconds);
		this.retrieverMap.get(milliseconds).addAll(rs);
	}
	
	private void checkAndDestroy(long milliseconds) {
		if(retrieverMap.get(milliseconds).isEmpty()) {
			System.out.println("Retriever map is empty, destroying the thread");
			System.out.println("TODO: Create this function...");
		}
	}
	
	private void checkAndCreate(long milliseconds) {
		if(!retrieverMap.containsKey(milliseconds)) {
			retrieverMap.put(milliseconds, new ArrayList<Retriever>());
			taskMap.put(milliseconds, new Wub(milliseconds));
			
			timer.schedule(taskMap.get(milliseconds), milliseconds, milliseconds);			
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
