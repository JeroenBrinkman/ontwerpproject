package global;

public class Globals {
	public static final String[] componentTypes = { "swag", "yolo" };
	public static final String[] workerKeys = { "key1", "key2" };

	public static String[] getKeys(String cl) {
		if (cl.equals("worker")) {
			return workerKeys;
		} else {
			String[] r = { "a", "b" };
			return r;
		}
	}

}
