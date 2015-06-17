package controller;

import global.Globals;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import model.Component;
import model.intelligence.Intelligence.ClosedException;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import de.timroes.axmlrpc.XMLRPCTimeoutException;

public class Retriever {
	private XMLRPCClient		client;
	private long[]				data;
	private Component			comp;
	
	static int parse(Object object) {
		Integer result = null;
		
		if(object.getClass() == String.class) {
			result = (int) Double.parseDouble((String)object);
		}
		else if(object.getClass() == Double.class) {
			result = ((Double)object).intValue();
		}
		else if(object.getClass() == Integer.class) {
			result = ((Integer)object);
		}
		else if(object.getClass() == Long.class) {
			result = ((Long)object).intValue();
		}
		else if(object.getClass() == Boolean.class) {
			result = ((Boolean)object) == true ? 1 : 0;
		}
		else if(object.getClass() == Date.class) {
			result = (int) ((Date)object).getTime();
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
		client.setTimeout(Globals.SchedulerTimerTimeout/1000);
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
		String[] calls = comp.getCalls();
		Integer counter = 0;
		for(int index = 0; index < calls.length; index++) {
			if(Globals.DEBUGOUTPUT) {
				System.out.println("Calling " + calls[index] + " for "+ comp.getTableName());
			}
			//RetrieverListeners.Calls listener = new RetrieverListeners.Calls(this, index, counter, waitObject);
			//client.callAsync(listener, calls[index]);
			updateData(index, retrieveData(calls[index]));
		}
		System.out.println("retrieving getData");
		//client.callAsync(new RetrieverListeners.Data(this, counter), "getData");
		
		
		String thedata =(String)client.call("getData"); 
		long[] parsed = this.comp.parseInput(thedata);
		for(int index = 0; index < parsed.length; index++) { 
			System.out.println("Loop " + index + " of " + (parsed.length-1));
			this.updateData(comp.getCalls().length + index, parsed[index]);
			//updateData(comp.getCalls().length + index, Integer.parseInt(parsed[index]));
		}
		System.out.println("Retriever retrieveAllData DONE");
	}
	
	/**
	 * This method uses XML RPC to retrieve data from a particular component and stores it in the array. 
	 * @throws XMLRPCException 
	 */
	public int retrieveData(String key) throws XMLRPCException{
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
