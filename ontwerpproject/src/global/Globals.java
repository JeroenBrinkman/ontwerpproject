package global;

import java.lang.reflect.Array;

public class Globals {
	public static int POLLINGINTERVAL = 5000; // once every minute

	public static Boolean GUI = true;
	/**
	 * Array indices to send from retriever to the update method.
	 * The retriever receives data from a component in text format.
	 * The retriever parses the text input into an array.
	 * The indices of each attribute of the component are set.
	 * I.e. each attribute has a fixed position in the array.
	 * Note that for different component types, different lengths of arrays are needed.
	 * This can be traced back through the UPDATE_COMP_TYPE.
	 */
	public static final int UPDATE_INDEX_COMP_TYPE = 0;
	public static final int UPDATE_INDEX_TIME = 1;
	public static final int UPDATE_INDEX_CPU = 2;
	public static final int UPDATE_INDEX_MEM = 3;
	public static final int UPDATE_INDEX_DISK = 4;

	
	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL_DETAIL = "jdbc:mysql://localhost/detail";
	public static final String DB_URL_RRD = "jdbc:mysql://localhost/detail";
	public static final String USER = "henk";
	public static final String PASS = "henk";
	//amount of records in the database of a certain type
	public static final int SQLMAXmin = 10080;//minimum = 60
	public static final int SQLMAXhour = 8544;//minimum = 24

	
	//Intelligence shit
	public static long LAST_DATABASE_ERROR = -1;
	public static final int MIN_DATABASE_ERROR_DELAY = 30000;// max 1 keer per half uur
	
	
	/* LUUKS SHIT */
	public static final int SchedulerTimerTimeout = 5000;
	public static final int SchedulerTimerThreads = 16;
	public static final int SchedulerThreads = 4;
	public static final int XMLRPC_PORT = 8000;
	public static final boolean DEBUGOUTPUT = true;
	
	public static final String[] WORKER_CALLS 	= {"time", "hdd", "mem", "cpu"};
	public static final String[] MANAGER_CALLS 	= {"time", "hdd", "mem", "cpu"};
	public static final String[] DATABASE_CALLS = {"time", "hdd", "mem", "cpu"};
	
	public static final String SET_POLLING_TIME = "setPollingTime";


	public static long LAST_UPDATE = 0;
	public static long LAST_COMPONENT = 0;
	public static long AMOUNT_UPDATES = 0;
	
	public static void newUpdate(){
		LAST_UPDATE = System.currentTimeMillis();
		AMOUNT_UPDATES++;
	}
	
	public static <T> T[] concat (T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}

	public static final String[] WORKER_COLS= { "ws_version", "ws_connstate", "ws_worker_start_ts",
		"ws_chunk_start_ts", "ws_worker_now_ts",
		"ws_measure_queue_len", "ws_result_queue_len",
		"ws_total_scanned", "ws_worker_state", "ws_total_q_count",
		"ws_current_q_count", "ws_curr_succ_q_count_SOA",
		"ws_curr_succ_q_count_A",
		"ws_curr_succ_q_count_AAAA",
		"ws_curr_succ_q_count_NS",
		"ws_curr_succ_q_count_MX",
		"ws_curr_succ_q_count_TXT",
		"ws_curr_succ_q_count_SPF",
		"ws_curr_succ_q_count_DS",
		"ws_curr_succ_q_count_DNSKEY",
		"ws_curr_succ_q_count_NSEC",
		"ws_curr_succ_q_count_NSEC3",
		"ws_curr_succ_q_count_NSEC3PARAM",
		"ws_curr_fail_q_count_SOA",
		"ws_curr_fail_q_count_A",
		"ws_curr_fail_q_count_AAAA",
		"ws_curr_fail_q_count_NS",
		"ws_curr_fail_q_count_MX",
		"ws_curr_fail_q_count_TXT",
		"ws_curr_fail_q_count_SPF",
		"ws_curr_fail_q_count_DS",
		"ws_curr_fail_q_count_DNSKEY",
		"ws_curr_fail_q_count_NSEC",
		"ws_curr_fail_q_count_NSEC3",
		"ws_curr_fail_q_count_NSEC3PARAM", "ws_writer_threadcount",
		"ws_writer_total_out_size", "ws_writer_files_written"};


	public static final int GUI_UPDATE = 5000;
}
