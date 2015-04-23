package clienttest;

import java.net.URL;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class Client {
	public static void main(String[] args) {
		try {
			System.out.println("Creating client...");
		    XMLRPCClient client = new XMLRPCClient(new URL("http://localhost:8000/rpc2"));

		    System.out.println("Calling client: \"isServerOk\"");
		    Boolean b = (Boolean)client.call("isServerOk");
		    		    
		    System.out.println("Calling client: \"add\", 5, 10");
		    Integer i = (Integer)client.call("add", 5, 10);
		    
		    System.out.println("Is Server Ok?: " + b);
		    System.out.println("Add Integer: " + i);
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
