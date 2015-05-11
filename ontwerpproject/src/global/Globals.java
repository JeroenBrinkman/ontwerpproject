package global;

public class Globals {
	public static int POLLINGINTERVAL = 5000; // 5 seconds for now
	
	public static final String[] componentTypes = { "swag", "yolo" };
	public static final String[] workerKeys = { "key1", "key2" };
	
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

	public static String[] getKeys(String cl) {
		if (cl.equals("worker")) {
			return workerKeys;
		} else {
			String[] r = { "a", "b" };
			return r;
		}
	}
	
	//database shizzle

	public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL_DETAIL = "jdbc:mysql://localhost/detail";
	public static final String DB_URL_RRD = "jdbc:mysql://localhost/detail";
	public static final String USER = "henk";
	public static final String PASS = "henk";
	public static final long MYSQLMAXTIME = 3 * 31 * 24 * 60 * 60 * 1000; // 3 * 1 maand * 1dag * 1 uur * 1 minuut * 1 seconde * milliseconden
	//amount of records in the database of a certain type
	public static final int SQLMAXsec = 12;//minimum = 12
	public static final int SQLMAXmin = 60;//minimum = 60
	public static final int SQLMAXhour = 24;//minimum = 24
	public static final int SQLMAXday = 7;// minimum = 7
	public static final int SQLMAXweek = 4;//minimum = 4

}
