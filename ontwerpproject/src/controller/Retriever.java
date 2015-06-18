package controller;

import global.Globals;
import global.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import model.Component;
import model.intelligence.Intelligence.ClosedException;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCTimeoutException;

public class Retriever {
	private XMLRPCClient		client;
	private long[]				data;
	private Component			comp;
	
	// PARE THE OBJECT INTO A LONG
	// SO THE DATABASE CAN HANDLE IT
	// TODO PARSEDOUBLE CAN GO BOOOOOOOOM!
	static long parse(Object object) {
		Long result = null;
		
		if(object.getClass() == String.class) {
			String str = (String)object;
			result = ((Double)Double.parseDouble(str)).longValue();
		}
		else if(object.getClass() == Double.class) {
			result = ((Double)object).longValue();
		}
		else if(object.getClass() == Integer.class) {
			result = ((Integer)object).longValue();
		}
		else if(object.getClass() == Long.class) {
			result = ((Long)object);
		}
		else if(object.getClass() == Boolean.class) {
			result = (long) (((Boolean)object) == true ? 1 : 0);
		}
		else if(object.getClass() == Date.class) {
			result = ((Date)object).getTime();
		}
		else {
			System.err.println("Could not convert tmpObject to a string. tmpObject class: " + object.getClass().toString());
		}
		
		return result;
	}
	
	/**
	 * @require ip is a valid IP address and id is a unique int value
	 * @ensure The retriever gets assigned an id and IP address. 
	 * @param ip The IP address this retriever will connect to 
	 * @param id The ID of this retriever 
	 * @throws XMLRPCException */
	public Retriever(Component comp) throws XMLRPCException{
		this.comp = comp;
		this.data = new long[comp.getKeys().length];
		
		URL xmlrpcUrl = null;
		try {
			xmlrpcUrl = new URL("http", comp.getAddress().getHostName(), comp.getAddress().getPort(), "/RPC2");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		client = new XMLRPCClient(xmlrpcUrl);
		client.setTimeout(Globals.XMLRPCTIMEOUT_IN_SECONDS);
	}
	
	/**
	 * Updates a single field of the array which keeps track of the stats of the individual components
	 * @require key >=0 && key <= 4
	 * @ensure data[] contains new data
	 * @param key The key value of the array, in other words, the index of the array
	 * @param parsed The value related to a particular index
	 */
	public void updateData(int index, long parsed){
		data[index] = parsed;
	}
	
	/**
	 * Method to return the data[] variable
	 * @return data
	 */
	public long[] getData(){
		return data;
	}
	
	public Component getComponent() {
		return this.comp;
	}
	
	public XMLRPCClient getClient() {
		return this.client;
	}
	
	public void retrieveAllData() throws XMLRPCException{
		if(Globals.ASYNC) retrieveAllData_ASync();
		else retrieveAllData_Sync();
	}
	
	public void retrieveAllData_Sync() throws XMLRPCException {
		String[] calls = comp.getCalls();
		
		for(int index = 0; index < calls.length; index++) {
			if(Logger.PRINT_DEBUG) {
				System.out.println("Calling " + calls[index] + " for "+ comp.getTableName());
			}			
			try{
				updateData(index, retrieveData(calls[index]));
			}
			catch(XMLRPCTimeoutException e) {
				Logger.log("Component " + comp.getTableName() + " had a timeout for function: " + calls[index]);
			}
		}
		if(Logger.PRINT_DEBUG)
			System.out.println("Calling getData for "+ comp.getTableName());
		
		String thedata = null;
		try {
			thedata =(String)client.call("getData");
		}
		catch(XMLRPCTimeoutException e) {
			Logger.log("Component " + comp.getTableName() + " had a timeout for function: getDate");
		}
		
		if(thedata != null && !thedata.isEmpty()) {
			long[] parsed = this.comp.parseInput(thedata);
			for(int index = 0; index < parsed.length; index++) { 
				this.updateData(comp.getCalls().length + index, parsed[index]);
			}
		}
	}
	
	public void retrieveAllData_ASync() throws XMLRPCException {
		String[] calls = comp.getCalls();
		
		//Lock to synchronize the AtomicInteger and to use the condition
		Lock lock = new ReentrantLock();
		// Condition is so that this thread can wait till all the data has been retrieved or returned in a error
		Condition condition = lock.newCondition();
		// counter to count the connections that have retrieved an error or retrieved data
		// Starts at the number of calls it makes and goes graduadly down
		AtomicInteger counter = new AtomicInteger(calls.length + 1);
		// If an error was retrieved it goes in this list
		// TODO - This should be a concurrentlist
		List<XMLRPCException> errorList = new ArrayList<XMLRPCException>();
		
		// Retrieve all the data from the functions of the python server on the workers etc.
		for(int index = 0; index < calls.length; index++) {
			if(Logger.PRINT_DEBUG) {
				System.out.println("Calling " + calls[index] + " for "+ comp.getTableName());
			}
			RetrieverListeners.Calls listener = new RetrieverListeners.Calls(this, index, lock, condition, counter, errorList);
			client.callAsync(listener, calls[index]);
		}
		
		if(Logger.PRINT_DEBUG)
			System.out.println("Calling getData for "+ comp.getTableName());
		
		// Retrieve getData is available
		client.callAsync(new RetrieverListeners.Data(this, lock, condition, counter, errorList), "getData");
		
		// Wait for all the data to be retrieved
		// TODO handle interruptions beter!!
		lock.lock();
		try {
			while(counter.get() > 0) {
				condition.await();
			}
			if(!errorList.isEmpty()) {
				Logger.log("Received " + errorList.size() + " error(s) in component " + comp.getTableName());
				for(XMLRPCException e : errorList) {
					Logger.log(e.getMessage());
				}
				Logger.log("Throwing the first...");
				throw errorList.get(0); 
			}
			
		} catch (InterruptedException e) {
			System.out.println("Retriever interrupted");
			e.printStackTrace();
		}
		lock.unlock();
	}
	
	/**
	 * This method uses XML RPC to retrieve data from a particular component and stores it in the array. 
	 * @throws XMLRPCException 
	 */
	public long retrieveData(String key) throws XMLRPCException{
		return parse(client.call(key));
	}
	
	/**
	 * Pushes the data to the Model where it can be updated. 
	 * @throws ClosedException 
	 */
	public void pushData() throws ClosedException{
		comp.update(System.currentTimeMillis(), data);
	}
}
