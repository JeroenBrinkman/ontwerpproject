package controller;

import global.Globals;
import global.Logger;

import java.net.InetSocketAddress;

import de.timroes.axmlrpc.XMLRPCException;
import model.Component;
import model.Database;
import model.Manager;
import model.Worker;
import model.intelligence.Intelligence.ClosedException;

/**
 * ServerHandler handles all the methods
 * that can be called through the XMLRPC server
 * @author Luuk
 *
 */
public class ServerHandler {
	/**
	 * A debug method, always returns true.
	 * @return true
	 */
	public boolean online() {
		Logger.log_debug("online called");
		return true;
	}

	/**
	 * Adds a component to the scheduler. Does not adds the component to the scheduler
	 * if the ip and port combination already exists in the scheduler
	 * 
	 * @param type See {@link Globals#ID_DATABASE}, {@link Globals#ID_MANAGER} and {@link Globals#ID_WORKER}
	 * @param ip 	the ip of the component
	 * @param port	the port of the component
	 * @return true if the component was added to the scheduler, else false
	 */
	public boolean add(int type, String ip, int port) {
		Logger.log_debug("add called");

		if (Controller.scheduler == null || Controller.model == null) {
			Logger.log("Scheduler or model is null, please initiate!");
			return false;
		}

		InetSocketAddress adr= new InetSocketAddress(ip, port);
		
		if(Controller.scheduler.getRetriever(ip, port) != null) {
			Logger.log("Add called for one that already exists!, hostname: " + ip + ", port: " + port);
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
			Logger.log("Component (" + ip + "," + port + ") constructor failed");
			return false;
		}
			
		try {
			Controller.model.addComponent(comp);
		} catch (ClosedException e1) {
			e1.printStackTrace();
			Logger.log("Component " + comp.getTableName() + " failed to add to component");
			return false;
		}
		
		Retriever ret;
		try {
			ret = new Retriever(comp);
		} catch (XMLRPCException e) {
			Controller.model.removeComponent(comp);
			Logger.log("Component " + comp.getTableName() + " failed to create retriever");
			return false;
		}

		Controller.scheduler.addRetriever(Globals.POLLINGINTERVAL, ret);
		Logger.log("Component " + comp.getTableName() + " added in the scheduler");

		return true; 
	}

	/**
	 * Removes the component from the scheduler using 
	 * the hostname and the port
	 * @param hostname hostname of the retriever that should be removed
	 * @param port port of the retriever that should be removed
	 * @return true
	 */
	public boolean remove(String hostname, int port) {
		Logger.log_debug("Remove called!");

		Retriever ret = Controller.scheduler.getRetriever(hostname, port);
		synchronized (Controller.scheduler) {
			if (ret == null) {
				Logger.log_debug("Retriever not found (hostname, port): ("
									+ hostname + ", " + port + ")");
				return false;
			}
			Controller.scheduler.removeRetriever(ret);
		}

		Controller.model.removeComponent(ret.getComponent());

		return true;
	}
}
