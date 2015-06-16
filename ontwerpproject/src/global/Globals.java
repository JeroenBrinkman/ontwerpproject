package global;

public class Globals {
	public static int POLLINGINTERVAL = 1000; // once every minute

	
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
	public static final int SchedulerTimeout = 1000;
	public static final int SchedulerTimerThreads = 16;
	public static final int SchedulerThreads = 4;
	public static final int XMLRPC_PORT = 8000;
	public static final boolean DEBUGOUTPUT = true;


	public static long LAST_UPDATE = 0;
	public static long LAST_COMPONENT = 0;
	public static long AMOUNT_UPDATES = 0;
	
	public static void newUpdate(){
		LAST_UPDATE = System.currentTimeMillis();
		AMOUNT_UPDATES++;
	}

}
