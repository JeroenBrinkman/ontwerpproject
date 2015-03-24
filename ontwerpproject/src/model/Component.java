package model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.lang.String;
import java.util.Date;

public abstract class Component {
	private InetAddress adr;
	private int id;
	protected String[] keyList;

	protected class DatabaseEntry {
		public Date date;
		public String key;
		public int value;
	}

	public Component(String ip, int id) {
		this.id = id;
		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component : " + id);
		}
	}

	public int getID() {
		return id;
	}

	public InetAddress getInet() {
		return adr;
	}

	public abstract void compressDatabase();// hoeft wss geen abstraact te
											// blijven

	public void update(String message) {
		DatabaseEntry[] entries = parseInput(message);
		for (DatabaseEntry a : entries) {
			update(a);
		}
	}

	protected void update(DatabaseEntry a) {
		System.out.println("updaten bitch");
	}

	protected abstract DatabaseEntry[] parseInput(String message);
}
