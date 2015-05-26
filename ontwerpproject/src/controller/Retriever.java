package controller;

import global.Globals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Component;
import model.Model;
import model.Worker;
import model.intelligence.Intelligence;
import model.intelligence.WorkerIntelligence;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Retriever {
	private XMLRPCClient		client;
	private String[]			data;
	private Component			comp;
	
	static String parse(Object object) {
		String result = null;
		
		if(object.getClass() == String.class) {
			result = (String)object;
		}
		else if(object.getClass() == Double.class) {
			result = "" + ((Double)object);
		}
		else if(object.getClass() == Integer.class) {
			result = ((Integer)object).toString();
		}
		else if(object.getClass() == Long.class) {
			result = ((Long)object).toString();
		}
		else if(object.getClass() == Boolean.class) {
			result = ((Boolean)object).toString();
		}
		else if(object.getClass() == Date.class) {
			result = ((Date)object).toString();
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
		this.data = new String[comp.getKeys().length];
		
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
	}
	
	/**
	 * This method uses XML RPC to retrieve data from a particular component and stores it in the array. 
	 * @throws XMLRPCException 
	 */
	public String retrieveData(String key) throws XMLRPCException{
		return parse(client.call(key));
	}
	
	/**
	 * Pushes the data to the Model where it can be updated. 
	 */
	public void pushData(){
		comp.update(System.currentTimeMillis(), data);
	}
	
	// TODO remove test main after im done with testing
	public static void main(String[] args) throws XMLRPCException {
		Model model = new Model();
		while(model.createConnection() == null) {
			System.out.println("Could not connect to SQL Database!");
			System.out.println("Press enter to try again or exit to quit.");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(input.equals("exit")) {
				System.out.println("Lololol doesn't work!");
			}
		}
		Worker w = new Worker("localhost", model.createConnection());
		model.addComponent(w);
		
		Retriever r = new Retriever(w);
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < 12*60+1; i++) {
			r.retrieveAllData();
			r.pushData();
			System.out.println(i + ": " + Arrays.toString(r.getData()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("endtime inserts: " + (System.currentTimeMillis()-start));
		//model.removeComponent(w);
	}
}
