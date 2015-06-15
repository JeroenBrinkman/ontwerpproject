package controller;

import global.Globals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import model.Component;
import model.intelligence.Intelligence.ClosedException;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Retriever {
	private XMLRPCClient		client;
	private int[]			data;
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
	 * @param id The ID of this retriever */
	public Retriever(Component comp){
		this.comp = comp;
		this.data = new int[comp.getKeys().length];
		
		URL xmlrpcUrl = null;
		try {
			xmlrpcUrl = new URL("http", comp.getAddress().getHostName(), comp.getAddress().getPort(), "/RPC2");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		client = new XMLRPCClient(xmlrpcUrl);		
	}
	
	/**
	 * Updates a single field of the array which keeps track of the stats of the individual components
	 * @require key >=0 && key <= 4
	 * @ensure data[] contains new data
	 * @param key The key value of the array, in other words, the index of the array
	 * @param value The value related to a particular index
	 */
	public void updateData(int index, int value){
		data[index] = value;
	}
	
	/**
	 * Method to return the data[] variable
	 * @return data
	 */
	public int[] getData(){
		return data;
	}
	
	public Component getComponent() {
		return this.comp;
	}
	
	public XMLRPCClient getClient() {
		return this.client;
	}
	
	public void retrieveAllData() throws XMLRPCException{
		String[] keys = comp.getKeys();
		for(int index = 0; index < keys.length; index++) {
			try {
				updateData(index, retrieveData(keys[index]));
			} catch (XMLRPCServerException e) {
				if(Globals.DEBUGOUTPUT)
					System.err.println(e.toString());
			}
		}
		String thedata = (String)client.call("getData");
		System.out.println(thedata);
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
