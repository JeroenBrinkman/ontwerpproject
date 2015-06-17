package model;

import global.Globals;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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

		// TODO temp currently placeholder
		collumnList = Globals.concat(Globals.WORKER_CALLS, Globals.WORKER_COLS);

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
	public long[] parseInput(String message) {
		String[] parts;
		String[] lines = message.split("\n");
		long[] result = new long[Globals.WORKER_COLS.length]; //List<String> result = new ArrayList<String>();
		int resultIndex = 0;
		String currentLine;

		for(int i = 0; i < lines.length; i++){
			currentLine = lines[i];
			if(!currentLine.contains("last")){
				parts = currentLine.split(":");
				currentLine = parts[1];
				currentLine = currentLine.replaceAll("\\s+", "");
				result[resultIndex++] = Long.parseLong(currentLine);
			}
		}
		
		return result;
	}

	@Override
	public String getTableName() {
		return "w" + super.getTableName();
	}
	
	@Override
	public String[] getCalls() {
		return Globals.WORKER_CALLS;
	}
	
	/*
	public static void main(String[] args) {
		String message = "ws_version:                   	1\nws_connstate:                 	0\nws_worker_start_ts:           1428007936\nws_chunk_start_ts:            	1434121394\nws_worker_now_ts:             1434541734\nws_measure_queue_len:         	0\nws_result_queue_len:          	0\nws_total_scanned:             	18700801\nws_worker_state:              	1\nws_total_q_count:            	218482387\nws_last_q_count:              	113419\nws_current_q_count:           	113495\nws_last_succ_q_count[   	SOA]: 8773\nws_last_succ_q_count[     	A]: 26114\nws_last_succ_q_count[  	AAAA]: 25851\nws_last_succ_q_count[    	NS]: 8597\nws_last_succ_q_count[    	MX]: 8556\nws_last_succ_q_count[   	TXT]: 8547\nws_last_succ_q_count[   	SPF]: 8476\nws_last_succ_q_count[    	DS]: 8476\nws_last_succ_q_count[DNSKEY]: 8464\nws_last_succ_q_count[  	NSEC]: 5\nws_last_succ_q_count[ 	NSEC3]: 12\nws_last_succ_q_count[NSEC3PARAM]: 12\nws_last_fail_q_count[   	SOA]: 1227\nws_last_fail_q_count[     	A]: 102\nws_last_fail_q_count[  	AAAA]: 62\nws_last_fail_q_count[    	NS]: 12\nws_last_fail_q_count[    	MX]: 41\nws_last_fail_q_count[   	TXT]: 9\nws_last_fail_q_count[   	SPF]: 71\nws_last_fail_q_count[    	DS]: 0\nws_last_fail_q_count[DNSKEY]: 12\nws_last_fail_q_count[  	NSEC]: 0\nws_last_fail_q_count[ 	NSEC3]: 0\nws_last_fail_q_count[NSEC3PARAM]: 0\nws_curr_succ_q_count[   	SOA]: 8769\nws_curr_succ_q_count[     	A]: 26170\nws_curr_succ_q_count[  	AAAA]: 25883\nws_curr_succ_q_count[    	NS]: 8600\nws_curr_succ_q_count[    	MX]: 8558\nws_curr_succ_q_count[   	TXT]: 8548\nws_curr_succ_q_count[   	SPF]: 8469\nws_curr_succ_q_count[    	DS]: 8469\nws_curr_succ_q_count[	DNSKEY]: 8455\nws_curr_succ_q_count[  	NSEC]: 5\nws_curr_succ_q_count[ 	NSEC3]: 12\nws_curr_succ_q_count[NSEC3PARAM]: 12\nws_curr_fail_q_count[   	SOA]: 1231\nws_curr_fail_q_count[     	A]: 75\nws_curr_fail_q_count[  	AAAA]: 82\nws_curr_fail_q_count[    	NS]: 12\nws_curr_fail_q_count[    	MX]: 42\nws_curr_fail_q_count[   	TXT]: 10\nws_curr_fail_q_count[   	SPF]: 79\nws_curr_fail_q_count[    	DS]: 0\nws_curr_fail_q_count[	DNSKEY]: 14\nws_curr_fail_q_count[  	NSEC]: 0\nws_curr_fail_q_count[ 	NSEC3]: 0\nws_curr_fail_q_count[NSEC3PARAM]: 0\nws_writer_threadcount:        	0\nws_writer_total_out_size:     	3676130603\nws_writer_last_out_size:      	1822959\nws_writer_files_written:      	1926";
		
		String[] parts;
		String[] lines = message.split("\n");
		long[] result = new long[Globals.WORKER_COLS.length];
		int resultIndex = 0;
		//List<Integer> result = new ArrayList<String>();
		String currentLine;
		for(int i = 0; i < lines.length; i++){
			currentLine = lines[i];
			if(!currentLine.contains("last")){
				//System.out.println(currentLine);
				parts = currentLine.split(":");
				currentLine = parts[1];
				currentLine = currentLine.replaceAll("\\s", "");
				System.out.println("id: " + parts[0]);
				result[resultIndex++] = Long.parseLong(currentLine);
			}
		}
		System.out.println(Arrays.toString(result));
	}
	*/
}
