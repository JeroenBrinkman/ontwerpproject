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
	public static Scheduler scheduler = null;
	public static Model model = null;

	public boolean online() {
		if(Globals.DEBUGOUTPUT)
			System.out.println("online called");
		return true;
	}

	public boolean add(int type, String ip, int port) {
		if (Globals.DEBUGOUTPUT)
			System.out.println("add called");

		if (Globals.DEBUGOUTPUT && (scheduler == null || model == null)) {
			System.out.println("Scheduler or model is null, please initiate!");
			return false;
		}

		InetSocketAddress adr= new InetSocketAddress(ip, port);
		
		if(scheduler.getRetriever(ip, port) != null) {
			Globals.log("Add called for one that already exists!, hostname: " + ip + ", port: " + port);
			return false;
		}
		
		Component comp = null;
		try {
			switch (type) {
			case Globals.ID_WORKER:
				comp = new Worker(adr, model.createConnection(), model);
				break;
			case Globals.ID_DATABASE:
				comp = new Database(adr, model.createConnection(), model);
				break;
			case Globals.ID_MANAGER:
				comp = new Manager(adr, model.createConnection(), model);
				break;
			default:
				return false;
			}
		} catch (ClosedException e2) {
			e2.printStackTrace();
			Globals.log("Component (" + ip + "," + port + ") constructor failed");
			return false;
		}
			
		try {
			model.addComponent(comp);
		} catch (ClosedException e1) {
			e1.printStackTrace();
			Globals.log("Component " + comp.getTableName() + " failed to add to component");
			return false;
		}
		
		Retriever ret;
		try {
			ret = new Retriever(comp);
		} catch (XMLRPCException e) {
			model.removeComponent(comp);
			Globals.log("Component " + comp.getTableName() + " failed to create retriever");
			return false;
		}

		scheduler.addRetriever(Globals.POLLINGINTERVAL, ret);
		Globals.log("Component " + comp.getTableName() + " added in the scheduler");

		return true; 
	}

	public boolean remove(String hostname, int port) {
		if (Globals.DEBUGOUTPUT)
			System.out.println("Remove called!");

		Retriever ret = scheduler.getRetriever(hostname, port);
		synchronized (scheduler) {
			if (ret == null) {
				if (Globals.DEBUGOUTPUT)
					System.out
							.println("Retriever not found (hostname, port): ("
									+ hostname + ", " + port + ")");
				return false;
			}
			scheduler.removeRetriever(ret);
		}

		model.removeComponent(ret.getComponent());

		return true;
	}
}
