package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import model.Model;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.*;
import org.apache.xmlrpc.webserver.WebServer;

import global.Globals;

public class Controller {
	
	public static void connectDatabase() {
		ServerHandler.model = new Model();
		Connection conn = null;
		while((conn = ServerHandler.model.createConnection()) == null) {
			Globals.log("Could not connect to SQL Database!");
			Globals.log("Press enter to try again or exit to quit.");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(input.equals("exit")) {
				return;
			}
		}
		try {
			conn.close();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	public static void createScheduler() {
		ServerHandler.scheduler = new Scheduler();	
	}
	
	public static void createServers() {
		WebServer webServer = new WebServer(Globals.XMLRPC_PORT);
		XmlRpcServer server = webServer.getXmlRpcServer();
		
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
			Globals.log("Server supports the following functions: " + Arrays.toString(phm.getListMethods()));
		} catch (XmlRpcException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		server.setHandlerMapping(phm);

		XmlRpcServerConfigImpl config = (XmlRpcServerConfigImpl) server.getConfig();
		config.setEnabledForExceptions(true);
		config.setContentLengthOptional(false);
		
		if(Globals.DEBUGOUTPUT)
			System.out.println("Starting webServer...");
		
		try {
			webServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Globals.log("Couldn't start the webserver!");
			e.printStackTrace();
			return;
		}		
	}
	
	public static void main(String[] args) {
		connectDatabase();
		Globals.log("Database connection succesfull");		
		createScheduler();
		Globals.log("Scheduler created");
		createServers();
		Globals.log("Server created and online");
	}
}
