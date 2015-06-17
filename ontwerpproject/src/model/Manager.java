package model;


import global.Globals;
import java.net.InetSocketAddress;
import java.sql.Connection;


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
		collumnList = Globals.concat(Globals.MANAGER_CALLS, temp);
	}

	@Override
	public long[] parseInput(String message) {
		String[] parts;
		String[] lines = message.split("\n");
		long[] result = new long[lines.length];
		//String[] result = new String[collumnList.length];
		String currentLine;
		for(int i = 0; i < collumnList.length; i++){
			currentLine = lines[i];
			//regels met w[X] erin komen als het goed is alleen voor na alle relevante informatie.
			//if(!currentLine.contains("[w")){
			parts = currentLine.split(":");
			currentLine = parts[1];
			currentLine = currentLine.replaceAll("\\s+", "");
			//niet toepasbaar als w[X] voorkomt voor relevante informatie
			result[i] = Long.parseLong(currentLine);
			//}
		}
		return result;
	}

	@Override
	public String getTableName() {
		return "m" + super.getTableName();
	}
	
	@Override
	public String[] getCalls() {
		return Globals.MANAGER_CALLS;
	}

}
