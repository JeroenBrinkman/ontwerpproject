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
	
	public static void main(String[] args) {
		ServerHandler.model = new Model();
		Connection conn = null;
		while((conn = ServerHandler.model.createConnection()) == null) {
			System.out.println("Could not connect to SQL Database!");
			System.out.println("Press enter to try again or exit to quit.");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try {
				input = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(input.equals("exit")) {
				System.out.println("Lololol doesn't work!");
			}
		}
		try {
			conn.close();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println("Model connection created");
		
		ServerHandler.scheduler = new Scheduler();		
		
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
			System.out.println(Arrays.toString(phm.getListMethods()));
		} catch (XmlRpcException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		server.setHandlerMapping(phm);

		XmlRpcServerConfigImpl config = (XmlRpcServerConfigImpl) server.getConfig();
		config.setEnabledForExceptions(true);
		config.setContentLengthOptional(false);
		
		System.out.println("Starting webServer");
		System.out.println(server.toString());
		
		try {
			webServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		System.out.println("Webserver succesfull started!");
		
	}
}
