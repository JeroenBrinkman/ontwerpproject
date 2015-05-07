package clienttest;

import java.net.URL;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Client {
	final static String ding = "cpu";
	
	public static void main(String[] args) {
		try {
			System.out.println("Creating client...");
			URL test = new URL("http", "145.100.180.125", 8000, "/RPC2");
		    XMLRPCClient client = new XMLRPCClient(test);

		    System.out.println("Calling client: \"system.listMethods\"");
		    Object[] os = (Object[])client.call("system.listMethods");
		    
		    System.out.println("Calling client: \"system.methodHelp\"");
		    String s = (String)client.call("system.methodHelp", "time");
		    
		    System.out.println("Calling client: \"" + ding + "\"");
		    Double d = (Double)client.call(ding);
		    		    
		    //System.out.println("Calling client: \"add\", 5, 10");
		    //Integer i = (Integer)client.call("add", 5, 10);
		    
		    //System.out.println("Is Server Ok?: " + b);
		    for(Object o : os)
		    	System.out.println(" " + o);
		    
		    System.out.println(ding + " " + d);
		    
		    System.out.println("methodHelp: " + s);
		} catch(XMLRPCServerException ex) {
			// The server throw an error.
			System.out.println("XMLRPCServerException: " + ex.getMessage());		    
		} catch(XMLRPCException ex) {
		    // An error occured in the client.
			System.out.println("XMLRPCException: " + ex.getMessage());	
		} catch(Exception ex) {
		    // Any other exception
			System.out.println("Exception: " + ex.getMessage());	
		}
	}
}
