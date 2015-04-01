package controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Retriever {
	private InetAddress 	adr; 	/**The IP address of the component this retriever is connected to */
	private int 			id;		/**The unique ID of this retriever */
	private long[] 			data;	/**An array which keeps track of the stats of the component */
	
	/**
	 * @require ip is a valid IP address and id is a unique int value
	 * @ensure The retriever gets assigned an id and IP address. 
	 * @param ip The IP address this retriever will connect to 
	 * @param id The ID of this retriever */
	public Retriever(String ip, int id){
		this.id = id;
		
		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component : " + id);
		}
		
	}
	
	/**
	 * Updates a single field of the array which keeps track of the stats of the individual components
	 * @require key >=0 && key <= 4
	 * @ensure data[] contains new data
	 * @param key The key value of the array, in other words, the index of the array
	 * @param value The value related to a particular index
	 */
	public void updateData(int key, long value){
		data[key] = value;
	}
	
	/**
	 * Method to return the data[] variable
	 * @return data
	 */
	public long[] getData(){
		return data;
	}
	
	/**
	 * This method uses XML RPC to retrieve data from a particular component and stores it in the array. 
	 */
	public void retrieveData(){
		
	}
	
	/**
	 * Pushes the data to the Model where it can be updated. 
	 */
	public void pushData(){
		
	}
}
