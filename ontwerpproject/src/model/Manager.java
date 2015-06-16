package model;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import model.intelligence.Intelligence.ClosedException;
import model.intelligence.ManagerIntelligence;

/**
 * Represents a clustermanager in the query system
 * 
 * @author Jeroen
 *
 */
public class Manager extends Component {

	public Manager(InetSocketAddress addr, Connection con, Model mod) throws ClosedException {
		super(addr, con);
		intel = new ManagerIntelligence(this, mod, con);
		//TODO temp currentlyplaceholder
		String[] temp = {"cms_version", "cms_start_time", "cms_now_time", "cms_tot_domains", "cms_doms_last_day", "cms_doms_today", "cms_worker_count"};
		collumnList = temp;
	}

	@Override
	protected String[] parseInput(String message) {
		String[] parts;
		List<String> result = new ArrayList<String>();
		String currentLine;
		try{
			BufferedReader br = new BufferedReader(new FileReader(message));
			while((currentLine = br.readLine()) != null){
				if(!currentLine.contains("w[")){
					parts = currentLine.split(":");
					currentLine = parts[1];
					currentLine = currentLine.replace(" ", "");
					result.add(currentLine);
				}
			}
			br.close();
		}catch (IOException e){
			e.printStackTrace();
			//TODO: weet niet of hier nog een email error zou moeten ofzo?
		}
		return (String[]) result.toArray();
	}

	@Override
	public String getTableName() {
		return "m" + super.getTableName();
	}

}
