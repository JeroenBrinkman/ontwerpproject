package global;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
	public static final String LOGFILENAME = "log.txt";
	
	public static synchronized void log(String message) {
		if(Globals.TO_CONSOLE) {
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
	
	public static synchronized void log_debug(String message) {
		if(Globals.PRINT_DEBUG) 
			log(message);
	}
	
	public static synchronized void log_debug_con(String message) {
		if(Globals.PRINT_DEBUG_CONCURRENT)
			log(message);
	}
	
	public static synchronized void log_debug_rec(String message) {
		if(Globals.PRINT_DEBUG_RECEIVER)
			log(message);
	}
}
