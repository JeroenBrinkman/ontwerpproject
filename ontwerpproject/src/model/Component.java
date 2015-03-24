package model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.lang.String;
import java.util.Date;

public abstract class Component {
	private InetAddress adr;
	private int id;
	protected String[] keyList;

	public Component(String ip, int id) {
		this.id = id;
		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component : " + id);
		}
	}
	protected abstract void update(String type, int value, Date da);//hoeft wss geen abstraact te blijven
	
	public abstract void update(String message);//hoeft wss geen abstraact te blijven
	
	public abstract void parseInput();//waarschijnlijkt een array return
	
	public abstract void compressDatabase();//hoeft wss geen abstraact te blijven
}
