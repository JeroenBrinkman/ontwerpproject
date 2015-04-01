package global;

public class Globals {
	public static final String[] componentTypes = { "swag", "yolo" };
	public static final String[] workerKeys = { "key1", "key2" };
	
	/**
	 * Array indices test
	 */
	public static final int UPDATE_COMP = 0;
	public static final int UPDATE_TIME = 1;
	public static final int UPDATE_CPU = 2;
	public static final int UPDATE_MEM = 3;
	public static final int UPDATE_DISK = 4;

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
	public static final String USER = "henk";
	public static final String PASS = "henk";

}
