package controller;

import java.net.InetSocketAddress;

import model.Component;
import model.Database;
import model.Manager;
import model.Model;
import model.Worker;

public class ServerHandler {
	static Scheduler 	scheduler = null;
	static Model		model = null;
	
	public boolean online() {
		System.out.println("oline called");
		return true;
	}
	
	public boolean add(int type, String ip, int port) {
		if(scheduler == null || model == null) {
			System.out.println("Scheduler or model is null, please initiate!");
			return false;
		}
		
		InetSocketAddress adr = new InetSocketAddress(ip, port);
		Component comp;
		switch(type) {
			case 0:
				comp = new Worker(adr, model.createConnection());
				break;
			case 1:
				comp = new Database(adr, model.createConnection());
				break;
			case 2:
				comp = new Manager(adr, model.createConnection());
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
	
	public boolean remove(String ip, int port) {
		scheduler.removeRetriever(ip, port);
		return true;
	}
}
