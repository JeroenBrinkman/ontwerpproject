package controller;

import global.Globals;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCTimeoutException;

public class RetrieverThread implements Callable<Boolean> {
	private Retriever ret;
	private boolean failed;
	private ConcurrentLinkedQueue<Retriever> failedList;
	
	
	public RetrieverThread(Retriever r, ConcurrentLinkedQueue<Retriever> failedList) {
		this.ret = r;
		failed = false;
		this.failedList = failedList;
	}

	@Override
	public Boolean call() throws Exception {
		if(Globals.DEBUGOUTPUT) System.out.println("Retrieving data from " + ret.getComponent().getTableName() + "...");
		try {
				ret.retrieveAllData();
		}catch (XMLRPCTimeoutException e) {
			if(Thread.interrupted()) {		
				if(Globals.DEBUGOUTPUT)
					System.out.println("Dubbel Timeout!: Returning");
				return false;
			}			
		}catch (XMLRPCException e) {
			if(e.getMessage().equals("java.net.ConnectException: Connection timed out: connect")) {
				Globals.log("Connection time out: Check IP and if the host is up");
			}
			else if(e.getMessage().equals("java.net.SocketException: Connection reset")) {
				Globals.log("Connection reset, Check if the server is up");
			}
			else if(e.getMessage().equals("java.net.ConnectException: Connection refused: connect")) {
				Globals.log("Connect Exception, Check if the server is up and if the file is correct: " + ret.getClient().getURL().getFile());
			}
			else if(e.getMessage().contains("java.io.FileNotFoundException:")) {
				Globals.log("File not found exception, check if the file is correct: " + ret.getClient().getURL().getFile());
			}
			else if(e.getMessage().contains("<class 'subprocess.CalledProcessError'>")) {
				Globals.log("I don't even know");
			}
			else {
				Globals.log("Unexpected exception: " + e.getMessage());
				if(Globals.DEBUGOUTPUT)
					e.printStackTrace();
			}		
			
			failed = true;
			failedList.add(ret);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(!failed) {
			if(Globals.DEBUGOUTPUT) {
				System.out.println("Retrieved data from \"" + ret.getComponent().getTableName() + "\": " + Arrays.toString(ret.getData()));
				System.out.println("Pushing data from " + ret.getComponent().getTableName() + "...");
			}
			try {
				ret.pushData();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			if(Globals.DEBUGOUTPUT) System.out.println("Pushed data from \"" + ret.getComponent().getTableName() + "\"");
		}
		
		if(Globals.DEBUGOUTPUT)
			System.out.println("Completed thread run, joining...");
		
		return failed;
	}

}
