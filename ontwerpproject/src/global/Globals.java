package global;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;

public class Globals {
	// logging
	public static synchronized void log(String message) {
		if(LOG_TO_SYSTEMOUT) {
			System.out.println(System.currentTimeMillis() + " : " + message);
		}
		else {
			PrintWriter writer;
			try {
				writer = new PrintWriter("the-file-name.txt", "UTF-8");
				writer.println(System.currentTimeMillis() + " : " + message);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
				//shouldnt happen
			}
		}

	}

	// constants
	public static Boolean GUI = true;
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL_DETAIL = "jdbc:mysql://localhost/detail";
	public static final String DB_URL_RRD = "jdbc:mysql://localhost/detail";
	public static final String USER = "henk";
	public static final String PASS = "henk";
	public static final int SQLMAXmin = 10080;// minimum = 60
	public static final int SQLMAXhour = 8544;// minimum = 24
	public static final int POLLINGINTERVAL = 5000; // once every minute

	// Intelligence shit
	public static long LAST_DATABASE_ERROR = -1;
	public static final int MIN_DATABASE_ERROR_DELAY = 30000;// max 1 keer per
																// half uur

	/* LUUKS SHIT */
	public static final int XMLRPCTimeout = 1;
	public static final int XMLRPC_PORT = 8000;
	public static final int SchedulerTimerThreads = 16;
	public static final int SchedulerThreads = 4;
	
	
	public static final boolean DEBUGOUTPUT = true;
	public static final boolean LOG_TO_SYSTEMOUT = true;
	
	
	public static final String[] WORKER_CALLS = { "time", "hdd", "mem", "cpu" };
	public static final String[] MANAGER_CALLS = { "time", "hdd", "mem", "cpu" };
	public static final String[] DATABASE_CALLS = { "time", "hdd", "mem", "cpu" };
	
	public static final int ID_WORKER = 0;
	public static final int ID_MANAGER = 1;
	public static final int ID_DATABASE = 2;

	public static <T> T[] concat(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen
				+ bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static final String[] MANAGER_COLS = { "cms_version",
			"cms_start_time", "cms_now_time", "cms_tot_domains",
			"cms_doms_last_day", "cms_doms_today", "cms_worker_count" };

	public static final String[] WORKER_COLS = { "ws_version", "ws_connstate",
			"ws_worker_start_ts", "ws_chunk_start_ts", "ws_worker_now_ts",
			"ws_measure_queue_len", "ws_result_queue_len", "ws_total_scanned",
			"ws_worker_state", "ws_total_q_count", "ws_current_q_count",
			"ws_curr_succ_q_count_SOA", "ws_curr_succ_q_count_A",
			"ws_curr_succ_q_count_AAAA", "ws_curr_succ_q_count_NS",
			"ws_curr_succ_q_count_MX", "ws_curr_succ_q_count_TXT",
			"ws_curr_succ_q_count_SPF", "ws_curr_succ_q_count_DS",
			"ws_curr_succ_q_count_DNSKEY", "ws_curr_succ_q_count_NSEC",
			"ws_curr_succ_q_count_NSEC3", "ws_curr_succ_q_count_NSEC3PARAM",
			"ws_curr_fail_q_count_SOA", "ws_curr_fail_q_count_A",
			"ws_curr_fail_q_count_AAAA", "ws_curr_fail_q_count_NS",
			"ws_curr_fail_q_count_MX", "ws_curr_fail_q_count_TXT",
			"ws_curr_fail_q_count_SPF", "ws_curr_fail_q_count_DS",
			"ws_curr_fail_q_count_DNSKEY", "ws_curr_fail_q_count_NSEC",
			"ws_curr_fail_q_count_NSEC3", "ws_curr_fail_q_count_NSEC3PARAM",
			"ws_writer_threadcount", "ws_writer_total_out_size", 
			"ws_writer_files_written" };

	public static final int GUI_UPDATE = 5000;

	// interface metadata
	public static long LAST_UPDATE = 0;
	public static long LAST_COMPONENT = 0;
	public static long AMOUNT_UPDATES = 0;

	public static synchronized void newUpdate() {
		LAST_UPDATE = System.currentTimeMillis();
		AMOUNT_UPDATES++;
	}
}
