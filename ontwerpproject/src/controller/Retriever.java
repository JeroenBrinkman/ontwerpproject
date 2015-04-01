package controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Retriever {
	private InetAddress adr;
	private int id;
	private long[] data;
	
	/**
	 * @require 
	 * @ensure
	 * @param ip
	 * @param id
	 */
	public Retriever(String ip, int id){
		this.id = id;
		
		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component : " + id);
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void updateData(int key, long value){
		data[key] = value;
	}
	
	/**
	 * 
	 */
	public void getData(){
		
	}
	
	/**
	 * 
	 */
	public void pushData(){
		
	}
}
