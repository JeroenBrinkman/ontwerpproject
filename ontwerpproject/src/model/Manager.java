package model;

public class Manager extends Component {

	public Manager(String ip, int id) {
		super(ip, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void compressSQLDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	protected DatabaseEntry[] parseInput(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createTableSQL() {
		//TODO add missing collumns
		String sql ="CREATE TABLE " + this.adr.toString() +
                " (date DATE not NULL, " +
                " cpu INTEGER, " + 
                " mem INTEGER, " + 
                " disk INTEGER, " + 
                " PRIMARY KEY ( date ))";
		return sql;
	}

}
