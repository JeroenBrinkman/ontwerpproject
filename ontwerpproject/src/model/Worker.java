package model;

import global.Globals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.intelligence.Intelligence.ClosedException;
import model.intelligence.WorkerIntelligence;

/**
 * Represents a worker in the query system
 * @author Jeroen
 *
 */
public class Worker extends Component {

	public Worker(InetSocketAddress addr, Connection con, Model mod) throws ClosedException {
		super(addr, con);
		intel = new WorkerIntelligence(this, mod, con);
		//TODO temp currently placeholder
		String[] temp = {"ws_version", "ws_connstate", "ws_worker_start_ts", "ws_chunk_start_ts", "ws_worker_now_ts", "ws_measure_queue_len", "ws_result_queue_len", "ws_total_scanned", "ws_worker_state", "ws_total_q_count", "ws_current_q_count", "ws_curr_succ_q_count[       SOA]", "ws_curr_succ_q_count[         A]", "ws_curr_succ_q_count[      AAAA]", "ws_curr_succ_q_count[        NS]", "ws_curr_succ_q_count[        MX]", "ws_curr_succ_q_count[       TXT]", "ws_curr_succ_q_count[       SPF]", "ws_curr_succ_q_count[        DS]", "ws_curr_succ_q_count[    DNSKEY]", "ws_curr_succ_q_count[      NSEC]", "ws_curr_succ_q_count[     NSEC3]", "ws_curr_succ_q_count[NSEC3PARAM]", "ws_curr_fail_q_count[       SOA]", "ws_curr_fail_q_count[         A]", "ws_curr_fail_q_count[      AAAA]", "ws_curr_fail_q_count[        NS]", "ws_curr_fail_q_count[        MX]", "ws_curr_fail_q_count[       TXT]", "ws_curr_fail_q_count[       SPF]", "ws_curr_fail_q_count[        DS]", "ws_curr_fail_q_count[    DNSKEY]", "ws_curr_fail_q_count[      NSEC]", "ws_curr_fail_q_count[     NSEC3]", "ws_curr_fail_q_count[NSEC3PARAM]", "ws_writer_threadcount", "ws_writer_total_out_size", "ws_writer_files_written"};
		collumnList = temp;
		
		String sql = "INSERT INTO " + getTableName()
				+ " VALUES( ?,  ?";
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
		try{
			BufferedReader br = new BufferedReader(new FileReader(message));
			while((currentLine = br.readLine()) != null){
				if(!currentLine.contains("last")){
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
		return "w" + super.getTableName();
	}
	
	@Override
	public String[] getCalls() {
		return Globals.WorkerCalls;
	}
}
