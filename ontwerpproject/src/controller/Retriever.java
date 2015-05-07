package controller;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Component;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Retriever {
	private XMLRPCClient		client;
	private String[]			data;
	private Component			comp;
	//private String[] 		data;	/**An array which keeps track of the stats of the component */
	
	/**
	 * @require ip is a valid IP address and id is a unique int value
	 * @ensure The retriever gets assigned an id and IP address. 
	 * @param ip The IP address this retriever will connect to 
	 * @param id The ID of this retriever */
	public Retriever(Component comp){
		this.comp = comp;
		this.data = new String[comp.getKeys().length];
		
		URL xmlrpcUrl = null;
		try {
			xmlrpcUrl = new URL("http", comp.getInet().getHostName(), 8000, "/RPC2");
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
	public void updateData(int index, String value){
		data[index] = value;
	}
	
	/**
	 * Method to return the data[] variable
	 * @return data
	 */
	public String[] getData(){
		return (String[]) data;
	}
	
	public void retrieveAllData() {
		int index = 0;
		for(String key : comp.getKeys())  {
			index++;
			updateData(index, retrieveData(key));
		}
		
		pushData();
	}
	
	/**
	 * This method uses XML RPC to retrieve data from a particular component and stores it in the array. 
	 */
	public String retrieveData(String key){
		//Debug
		//System.out.println("Calling client: \"system.methodHelp\"");
		
	   String value = null;
		try {
			Object tmpObject = (Object)client.call(key);
			
			if(tmpObject.getClass() == String.class) {
				value = (String)tmpObject;
			}
			else if(tmpObject.getClass() == Double.class) {
				value = ((Double)tmpObject).toString();
			}
			else if(tmpObject.getClass() == Integer.class) {
				value = ((Integer)tmpObject).toString();
			}
			else if(tmpObject.getClass() == Long.class) {
				value = ((Long)tmpObject).toString();
			}
			else if(tmpObject.getClass() == Boolean.class) {
				value = ((Boolean)tmpObject).toString();
			}
			else if(tmpObject.getClass() == Date.class) {
				value = ((Date)tmpObject).toString();
			}
			else {
				System.err.println("Could not convert tmpObject to a string. tmpObject class: " + tmpObject.getClass().toString());
			}
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		return value;
	}
	
	/**
	 * Pushes the data to the Model where it can be updated. 
	 */
	public void pushData(){
		comp.update(data);
		
		for(int i = 0; i < data.length; i++) {
			data[i] = null;
		}
	}
}
