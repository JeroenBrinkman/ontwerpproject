package model;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Component {
	private InetAddress adr;
	private int id;

	public Component(String ip, int id) {
		this.id = id;

		try {
			adr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println("invalid ip on component : " + id);
		}
	}

}
