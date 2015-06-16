package controller;

import global.Globals;

import java.net.InetSocketAddress;

import de.timroes.axmlrpc.XMLRPCException;
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
		if(Globals.DEBUGOUTPUT)
			System.out.println("add called");
		
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
			try {
				model.addComponent(comp);
			} catch (Exception e1) {
				System.out.println("Failed to added to model, canceling...");
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			Retriever ret;
			try {
				ret = new Retriever(comp);
			} catch (XMLRPCException e) {
				model.removeComponent(comp);
				e.printStackTrace();
				return false;
			}	
			
			scheduler.addRetriever(Globals.POLLINGINTERVAL, ret);	
			System.out.println("Component " + comp.getTableName() + " added");
			
			return true;
		}
		catch(ClosedException e) {
			e.printStackTrace();
		}
		return false;		
	}
	
	public boolean remove(String hostname, int port) {
		if(Globals.DEBUGOUTPUT)
			System.out.println("Remove called!");
		
		Retriever ret = scheduler.getRetriever(hostname, port);
		synchronized (scheduler) {	
			if(ret == null) {
				if(Globals.DEBUGOUTPUT)
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
