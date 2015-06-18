package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import model.Model;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.*;
import org.apache.xmlrpc.webserver.WebServer;

import global.Globals;
import global.Logger;

public class Controller {
	public static Scheduler scheduler = null;
	public static Model model = null;
	public static WebServer webServer;
	public static XmlRpcServer server;
	
	public static boolean exit = false;
	
	public static Boolean connectDatabase() {
		model = new Model();
		Connection conn = null;
		while((conn = model.createConnection()) == null) {
			Logger.log("Could not connect to SQL Database!");
			Logger.log("Press enter to try again or exit to quit.");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(input.equals("exit")) {
				return false;
			}
		}
		try {
			conn.close();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return true;
	}
	
	public static void createScheduler() {
		scheduler = new Scheduler();	
	}
	
	public static void createServers() {
		webServer = new WebServer(Globals.XMLRPC_PORT);
		server = webServer.getXmlRpcServer();
		
		PropertyHandlerMapping phm = new PropertyHandlerMapping();
		try {
			phm.load(Thread.currentThread().getContextClassLoader(), "server.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Logger.log("Server supports the following functions: " + Arrays.toString(phm.getListMethods()));
		} catch (XmlRpcException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		server.setHandlerMapping(phm);

		XmlRpcServerConfigImpl config = (XmlRpcServerConfigImpl) server.getConfig();
		config.setEnabledForExceptions(true);
		config.setContentLengthOptional(false);
		
		Logger.log_debug("Starting webServer...");
		
		try {
			webServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.log("Couldn't start the webserver!");
			e.printStackTrace();
			return;
		}		
	}
	
	public static void restart() {
		Retriever[] retList = scheduler.getAllRetrievers(Globals.POLLINGINTERVAL);
		InetSocketAddress[] addressList = null;
		int[] typeList = null;
		if(retList != null) {
			addressList = new InetSocketAddress[retList.length];
			typeList	= new int[retList.length];
			for(int index = 0; index < retList.length; index++) {
				addressList[index] = retList[index].getComponent().getAddress();
				typeList[index] = retList[index].getComponent().getType();			
			}
		}
		
		
		quit();
		start();
		
		//Add them all
		if(retList != null){
			ServerHandler handler = new ServerHandler();
			for(int index = 0; index < typeList.length; index++) 
				handler.add(typeList[index], addressList[index].getHostName(), addressList[index].getPort());
		}
	}
	
	public static void quit() {		
		scheduler.destroy();
		model.destroy();
		webServer.shutdown();
		
		while(Thread.activeCount()  <= 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		scheduler = null;
		model = null;
		webServer = null;
		server = null;
	}
	
	public static void start() {
		Globals.loadConfig();
		Logger.log("Starting program with the following global settings: ");
		Logger.log(Globals.staticToString());
		
		if(!connectDatabase()) {
			Logger.log("Failed to start the database.");
			exit = true;
			return;
		}
		Logger.log("Database connection succesfull");		
		createScheduler();
		Logger.log("Scheduler created");
		createServers();
		Logger.log("Server created and online");
	}
	
	public static void main(String[] args) {
		start();
	
		/*while(!exit) {
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String line = null;
	        try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        switch(line) {
		        case "restart": restart(); break;
		        case "quit": 	quit(); exit = true; break;
		        case "debug on": Globals.PRINT_DEBUG = true; break;
		        case "debug off": Globals.PRINT_DEBUG = false; break;
		        case "debug rec on": Globals.PRINT_DEBUG_RECEIVER = true; break;
		        case "debug rec off": Globals.PRINT_DEBUG_RECEIVER = false; break;
		        case "debug con on": Globals.PRINT_DEBUG_CONCURRENT = true; break;
		        case "debug con off": Globals.PRINT_DEBUG_CONCURRENT = false; break;
		        case "globals": System.out.println(Globals.staticToString()); break;
		        default: System.out.println("command not recognized"); break;
	        }
		}*/
	}
}
