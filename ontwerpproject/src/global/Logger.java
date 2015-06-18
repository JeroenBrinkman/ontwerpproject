package global;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	public static final boolean PRINT_DEBUG = true;
	public static final boolean TO_CONSOLE = true;
	public static final String LOGFILENAME = "log/log.txt";
	
	public static synchronized void log(String message) {
		if(TO_CONSOLE) {
			System.out.println(System.currentTimeMillis() + " : " + message);
		}
		else {
			try {
				PrintWriter output = new PrintWriter(new FileWriter(
						LOGFILENAME, true));
				output.println(message);
				output.flush();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
				//shouldnt happen
			}
		}
	}
	
	public static synchronized void log(String message, boolean debug) {
		if(debug && PRINT_DEBUG || !debug) 
			log(message);
	}
	
	public static synchronized void log_debug(String message) {
		if(PRINT_DEBUG) 
			log(message);
	}
}
