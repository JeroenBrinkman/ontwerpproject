package controller;

import global.Globals;

import java.net.InetSocketAddress;

import model.Component;
import model.Database;
import model.Manager;
import model.Model;
import model.Worker;
import model.intelligence.Intelligence.ClosedException;

public class ServerHandler {
	public static Scheduler 	scheduler = null;
	public static Model			model = null;
	
	public boolean online() {
		System.out.println("online called");
		return true;
	}
	
	public boolean add(int type, String ip, int port) {
		if(Globals.DEBUGOUTPUT && (scheduler == null || model == null)) {
			System.out.println("Scheduler or model is null, please initiate!");
			return false;
		}
		
		InetSocketAddress adr = new InetSocketAddress(ip, port);
		Component comp = null;
		try {
			switch(type) {
				case 0:
					comp = new Worker(adr, model.createConnection(), model);
					break;
				case 1:
					comp = new Database(adr, model.createConnection(), model);
					break;
				case 2:
					comp = new Manager(adr, model.createConnection(), model);
					break;
				default:
					return false;
			};
			
			model.addComponent(comp);
			Retriever ret = new Retriever(comp);
			
			scheduler.addRetriever(1000, ret);
			
			System.out.println("Component " + comp.getTableName() + " added");
			
			return true;
		}
		catch(ClosedException e) {
			e.printStackTrace();
		}
		return false;		
	}
	
	public boolean remove(String hostname, int port) {
		System.out.println("Remove called!");
		
		Retriever ret = scheduler.getRetriever(hostname, port);
		synchronized (scheduler) {	
			if(ret == null) {
				System.out.println("Retriever not found (hostname, port): (" + hostname + ", " + port + ")");
				return false;			
			}
			scheduler.removeRetriever(ret);
		}
		
		try {
			model.removeComponent(ret.getComponent());
		} catch (ClosedException e) {
			e.printStackTrace();
		}		
		return true;
	}
}
