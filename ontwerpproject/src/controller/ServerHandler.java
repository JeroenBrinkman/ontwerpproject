package controller;

import global.Globals;

import java.net.InetSocketAddress;

import de.timroes.axmlrpc.XMLRPCException;
import model.Component;
import model.Database;
import model.Manager;
import model.Worker;
import model.intelligence.Intelligence.ClosedException;

public class ServerHandler {
	public boolean online() {
		if(Globals.DEBUGOUTPUT)
			System.out.println("online called");
		return true;
	}

	public boolean add(int type, String ip, int port) {
		if (Globals.DEBUGOUTPUT)
			System.out.println("add called");

		if (Globals.DEBUGOUTPUT && (Controller.scheduler == null || Controller.model == null)) {
			System.out.println("Scheduler or model is null, please initiate!");
			return false;
		}

		InetSocketAddress adr= new InetSocketAddress(ip, port);
		
		if(Controller.scheduler.getRetriever(ip, port) != null) {
			Globals.log("Add called for one that already exists!, hostname: " + ip + ", port: " + port);
			return false;
		}
		
		Component comp = null;
		try {
			switch (type) {
			case Globals.ID_WORKER:
				comp = new Worker(adr, Controller.model.createConnection(), Controller.model);
				break;
			case Globals.ID_DATABASE:
				comp = new Database(adr, Controller.model.createConnection(), Controller.model);
				break;
			case Globals.ID_MANAGER:
				comp = new Manager(adr, Controller.model.createConnection(), Controller.model);
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
			Controller.model.addComponent(comp);
		} catch (ClosedException e1) {
			e1.printStackTrace();
			Globals.log("Component " + comp.getTableName() + " failed to add to component");
			return false;
		}
		
		Retriever ret;
		try {
			ret = new Retriever(comp);
		} catch (XMLRPCException e) {
			Controller.model.removeComponent(comp);
			Globals.log("Component " + comp.getTableName() + " failed to create retriever");
			return false;
		}

		Controller.scheduler.addRetriever(Globals.POLLINGINTERVAL, ret);
		Globals.log("Component " + comp.getTableName() + " added in the scheduler");

		return true; 
	}

	public boolean remove(String hostname, int port) {
		if (Globals.DEBUGOUTPUT)
			System.out.println("Remove called!");

		Retriever ret = Controller.scheduler.getRetriever(hostname, port);
		synchronized (Controller.scheduler) {
			if (ret == null) {
				if (Globals.DEBUGOUTPUT)
					System.out
							.println("Retriever not found (hostname, port): ("
									+ hostname + ", " + port + ")");
				return false;
			}
			Controller.scheduler.removeRetriever(ret);
		}

		Controller.model.removeComponent(ret.getComponent());

		return true;
	}
}
