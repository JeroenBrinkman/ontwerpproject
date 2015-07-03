package global;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Globals {

	// Load config file
	public static final String CONFIG_PATH = "config";

	public static void loadConfig() {
		List<String> config = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(CONFIG_PATH))) {
			String line;
			while ((line = br.readLine()) != null)
				config.add(line);
		} catch (FileNotFoundException e) {
			Logger.log("Could not find " + CONFIG_PATH
					+ ", going with default settings");
			return;
		} catch (IOException e) {
			Logger.log("IOException in " + CONFIG_PATH
					+ ", going with default settings");
			return;
		}

		for (String line : config) {
			line = line.replaceAll("\\s+", "");
			String[] split = line.split(":");
			if(split.length == 2) {
				switch(split[0]) {
					case "print_debug":
						PRINT_DEBUG = Boolean.parseBoolean(split[1]); break;
					case "print_debug_concurrent":
						PRINT_DEBUG_CONCURRENT = Boolean.parseBoolean(split[1]); break;
					case "print_debug_receiver":
						PRINT_DEBUG_RECEIVER = Boolean.parseBoolean(split[1]); break;
					case "print_to_console":
						TO_CONSOLE = Boolean.parseBoolean(split[1]); break;
					case "mysql_user":
						USER = split[1]; break;
					case "mysql_password":
						PASS = split[1]; break;
					case "xmlrpc_async": 			
						ASYNC = Boolean.parseBoolean(split[1]); break;
					case "xmlrpc_timeout_in_sec": 	
						XMLRPCTIMEOUT_IN_SECONDS = Integer.parseInt(split[1]); break;
					case "xmlrpc_port":
						XMLRPC_PORT = Integer.parseInt(split[1]); break;
					case "client_threads":
						CLIENT_THREADS = Integer.parseInt(split[1]); break;
					case "scheduler_threads":
						SCHEDULER_THREADS = Integer.parseInt(split[1]); break;
					case "pollinginterval":
						POLLINGINTERVAL = Integer.parseInt(split[1]); break;
					case "worker_calls":
						WORKER_CALLS = split[1].split(","); break;
					case "manager_calls":
						MANAGER_CALLS = split[1].split(","); break;
					case "database_calls":
						DATABASE_CALLS = split[1].split(","); break;
					case "worker_stats":
						WORKER_STATS = split[1].split(","); break;
					case "manager_stats":
						MANAGER_STATS = split[1].split(","); break;
					case "database_stats":
						DATABASE_STATS = split[1].split(","); break;
					case "mail_pass": 
						MAILPASS = split[1]; break;
					case "mail":
						MAILACCOUNT = split[1]; break;
					case "mail_target":
						MAILTARGET = split[1]; break;
					case "cpu_critical":
						CPUCRIT = Long.parseLong(split[1]); break;
					case "mem_critical":
						MEMCRIT = Long.parseLong(split[1]); break;
					case "hdd_critical":
						HDDCRIT = Long.parseLong(split[1]); break;
					case "query_ratio":
						QUERYRATIO = Double.parseDouble(split[1]); break;
					default:
						Logger.log("Unrecognized setting: " + split[0]);
				}
			}
		}
	}

	// constants
	public static Boolean GUI = true;
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL_DETAIL = "jdbc:mysql://localhost/detail";
	public static final String DB_URL_RRD = "jdbc:mysql://localhost/detail";
	public static String USER = Default.USER;
	public static String PASS = Default.PASS;
	public static final int SQLMAXmin = 10080;// minimum = 60
	public static final int SQLMAXhour = 8544;// minimum = 24
	public static int POLLINGINTERVAL = Default.POLLINGINTERVAL; // once every
																	// minute
	//logging
	public static boolean PRINT_DEBUG = Default.PRINT_DEBUG;
	public static boolean PRINT_DEBUG_CONCURRENT = Default.PRINT_DEBUG_CONCURRENT;
	public static boolean PRINT_DEBUG_RECEIVER = Default.PRINT_DEBUG_RECEIVER;
	public static boolean TO_CONSOLE = Default.TO_CONSOLE;

	// Intelligence shit
	public static String MAILPASS = Default.MAILPASS; 
	public static String MAILACCOUNT = Default.MAILACCOUNT;
	public static String MAILTARGET = Default.MAILTARGET;
	public static long LAST_MAIL = -1;
	public static final int MIN_MAIL_DELAY = 30000;// max 1 keer per
	// critical values, ook intelligence// half uur
	public static long CPUCRIT = Default.CPUCRIT;
	public static long MEMCRIT = Default.MEMCRIT;
	public static long HDDCRIT = Default.HDDCRIT;
	public static double QUERYRATIO = Default.QUERYRATIO;

	// XMLRPC
	public static int XMLRPCTIMEOUT_IN_SECONDS = Default.XMLRPCTIMEOUT_IN_SECONDS;
	public static int XMLRPC_PORT = Default.XMLRPC_PORT;
	public static String XMLRPC_PATH = Default.XMLRPC_PATH;
	public static String XMLRPC_GETDATA = Default.XMLRPC_GETDATA;
	public static boolean ASYNC = Default.ASYNC;

	// Retriever
	public static int CLIENT_THREADS = Default.CLIENT_THREADS;
	public static int SCHEDULER_THREADS = Default.SCHEDULER_THREADS;

	// Stuff to call for the python client
	public static String[] WORKER_CALLS = Default.WORKER_CALLS;
	public static String[] MANAGER_CALLS = Default.MANAGER_CALLS;
	public static String[] DATABASE_CALLS = Default.DATABASE_CALLS;

	// IDS of workers, managers and databases
	// TODO: Make this into a enum, maybe?
	public static final int ID_WORKER = 0;
	public static final int ID_MANAGER = 1;
	public static final int ID_DATABASE = 2;

	public static String[] MANAGER_STATS = Default.MANAGER_COLS;
	public static String[] WORKER_STATS = Default.WORKER_COLS;
	public static String[] DATABASE_STATS = Default.DATABASE_COLS;

	public static final int GUI_UPDATE = 5000;

	// interface metadata
	public static long LAST_UPDATE = 0;
	public static long LAST_COMPONENT = 0;
	public static long AMOUNT_UPDATES = 0;

	public static synchronized void newUpdate() {
		LAST_UPDATE = System.currentTimeMillis();
		AMOUNT_UPDATES++;
	}

	public static class Default {
		public static final String USER = "henk";
		public static final String PASS = "henk";

		public static final int POLLINGINTERVAL = 60000; // once every minute
		
		public static final boolean PRINT_DEBUG = false;
		public static final boolean PRINT_DEBUG_CONCURRENT = false;
		public static final boolean PRINT_DEBUG_RECEIVER = false;
		public static final boolean TO_CONSOLE = false;

		public static final int XMLRPCTIMEOUT_IN_SECONDS = 1;
		public static final int XMLRPC_PORT = 8000;
		public static final String XMLRPC_PATH = "/RPC2";
		public static final String XMLRPC_GETDATA = "getData";
		public static final boolean ASYNC = true;

		public static final int CLIENT_THREADS = 16;
		public static final int SCHEDULER_THREADS = 1;

		public static final String MAILPASS = " "; 
		public static final String MAILACCOUNT = " ";
		public static final String MAILTARGET = " ";

		public static final long CPUCRIT = 99999999;
		public static final long MEMCRIT = 99999999;
		public static final long HDDCRIT = 99999999;
		public final static double QUERYRATIO = 0.95;
		
		// Stuff to call for the python client
		public static final String[] WORKER_CALLS = { "time", "hdd", "mem",
				"cpu" };
		public static final String[] MANAGER_CALLS = { "time", "hdd", "mem",
				"cpu" };
		public static final String[] DATABASE_CALLS = { "time", "hdd", "mem",
				"cpu" };

		public static final String[] MANAGER_COLS = { "cms_version",
				"cms_start_time", "cms_now_time", "cms_tot_domains",
				"cms_doms_last_day", "cms_doms_today", "cms_worker_count" };

		public static final String[] WORKER_COLS = { "ws_version",
				"ws_connstate", "ws_worker_start_ts", "ws_chunk_start_ts",
				"ws_worker_now_ts", "ws_measure_queue_len",
				"ws_result_queue_len", "ws_total_scanned", "ws_worker_state",
				"ws_total_q_count", "ws_current_q_count",
				"ws_curr_succ_q_count_SOA", "ws_curr_succ_q_count_A",
				"ws_curr_succ_q_count_AAAA", "ws_curr_succ_q_count_NS",
				"ws_curr_succ_q_count_MX", "ws_curr_succ_q_count_TXT",
				"ws_curr_succ_q_count_SPF", "ws_curr_succ_q_count_DS",
				"ws_curr_succ_q_count_DNSKEY", "ws_curr_succ_q_count_NSEC",
				"ws_curr_succ_q_count_NSEC3",
				"ws_curr_succ_q_count_NSEC3PARAM", "ws_curr_fail_q_count_SOA",
				"ws_curr_fail_q_count_A", "ws_curr_fail_q_count_AAAA",
				"ws_curr_fail_q_count_NS", "ws_curr_fail_q_count_MX",
				"ws_curr_fail_q_count_TXT", "ws_curr_fail_q_count_SPF",
				"ws_curr_fail_q_count_DS", "ws_curr_fail_q_count_DNSKEY",
				"ws_curr_fail_q_count_NSEC", "ws_curr_fail_q_count_NSEC3",
				"ws_curr_fail_q_count_NSEC3PARAM", "ws_writer_threadcount",
				"ws_writer_total_out_size", "ws_writer_files_written" };

		public static final String[] DATABASE_COLS = {};
	}

	public static String staticToString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(Globals.class.getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = Globals.class.getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				if (field.get(null) instanceof Object[])
					result.append(Arrays.toString((Object[]) field.get(null)));
				else
					result.append(field.get(null));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}
}
