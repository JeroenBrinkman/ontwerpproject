package controller;

import global.Globals;
import global.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
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
		Logger.log_debug("Retrieving data from " + ret.getComponent().getTableName() + "...");
		try {
				ret.retrieveAllData();
		}catch (XMLRPCTimeoutException e) {
			Logger.log("Component " + ret.getComponent().getTableName() + " had a timeout!");
			if(Thread.interrupted()) {
				return false;
			}			
		}catch (XMLRPCException e) {
			if(e.getMessage().contains("java.net.ConnectException")) {
				Logger.log("Connection Exception: Check IP and if the host is up");
			}
			else if(e.getMessage().equals("java.net.SocketException: Connection reset")) {
				Logger.log("Connection reset, Check if the server is up");
			}
			else if(e.getMessage().equals("java.net.ConnectException: Connection refused: connect")) {
				Logger.log("Connect Exception, Check if the server is up and if the file is correct: " + ret.getClient().getURL().getFile());
			}
			else if(e.getMessage().contains("java.io.FileNotFoundException:")) {
				Logger.log("File not found exception, check if the file is correct: " + ret.getClient().getURL().getFile());
			}
			else if(e.getMessage().contains("<class 'subprocess.CalledProcessError'>")) {
				Logger.log("I don't even know");
			}
			else {
				Logger.log("Unexpected exception: " + e.getMessage());
				Logger.log(e.getStackTrace().toString());
				if(Globals.PRINT_DEBUG)
					e.printStackTrace();
			}		
			
			failed = true;
			failedList.add(ret);
		}
		catch(Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Logger.log(sw.toString());
		}
		
		if(!failed) {
				Logger.log_debug("Retrieved data from \"" + ret.getComponent().getTableName() + "\": " + Arrays.toString(ret.getData()));
				Logger.log_debug("Pushing data from " + ret.getComponent().getTableName() + "...");
			try {
				ret.pushData();
			}
			catch(Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				Logger.log(sw.toString());
			}
			Logger.log_debug("Pushed data from \"" + ret.getComponent().getTableName() + "\"");
		}
		
		Logger.log_debug_con("Completed thread run, joining...");
		
		return failed;
	}

}
