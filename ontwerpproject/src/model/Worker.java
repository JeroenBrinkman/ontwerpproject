package model;

import global.Globals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.intelligence.Intelligence.ClosedException;
import model.intelligence.WorkerIntelligence;

/**
 * Represents a worker in the query system
 * 
 * @author Jeroen
 *
 */
public class Worker extends Component {

	public Worker(InetSocketAddress addr, Connection con, Model mod)
			throws ClosedException {
		super(addr, con);
		intel = new WorkerIntelligence(this, mod, con);
		collumnList = Globals.WORKER_COLS;

		String sql = "INSERT INTO " + getTableName() + " VALUES( ?,  ?";
		for (int i = 0; i < collumnList.length; ++i) {
			sql += ",  ?";
		}
		sql += ")";
		try {
			insert = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] parseInput(String message) {
		String[] parts;
		List<String> result = new ArrayList<String>();
		String currentLine;
		try {
			BufferedReader br = new BufferedReader(new FileReader(message));
			while ((currentLine = br.readLine()) != null) {
				if (!currentLine.contains("last")) {
					parts = currentLine.split(":");
					currentLine = parts[1];
					currentLine = currentLine.replace(" ", "");
					result.add(currentLine);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: weet niet of hier nog een email error zou moeten ofzo?
		}
		return (String[]) result.toArray();
	}

	@Override
	public String getTableName() {
		return "w" + super.getTableName();
	}
	
	@Override
	public String[] getCalls() {
		return Globals.WorkerCalls;
	}
}
