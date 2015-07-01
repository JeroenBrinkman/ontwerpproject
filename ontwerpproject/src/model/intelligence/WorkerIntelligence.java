package model.intelligence;

import global.Globals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import model.Component;
import model.Model;

/**
 * intelligence for the worker
 * 
 * @author Jeroen
 *
 */
public class WorkerIntelligence extends Intelligence {
	private long lastsucces = 0;
	private long lastfailed = 0;
	private int interval;
	private int counter = 0;

	public WorkerIntelligence(Component comp, Model mod, Connection conn)
			throws ClosedException {
		super(comp, mod, conn);
		String sql = "SELECT value FROM workervalues WHERE name =  ? ";
		try {
			st = conn.prepareStatement(sql);
		} catch (SQLException e) {
			databaseError(e);
		}
		interval = 60 * 60 * 1000 / Globals.POLLINGINTERVAL;
	}

	@Override
	public void checkCritical(long[] newin) throws ClosedException {
		super.checkCritical(newin);
		// check once per hour, to avoid false positives due to short trends
		if (counter == 0) {
			//create hashmap for easy extending
			HashMap<String, Long> map = new HashMap<String, Long>();
			String[] cols = comp.getKeys();
			for (int i = 0; i < cols.length; ++i) {
				map.put(cols[i], newin[i]);
			}

			// check ratio of failedqueries
			long succes = map.get("ws_curr_succ_q_count_SOA");
			succes += map.get("ws_curr_succ_q_count_A");
			succes += map.get("ws_curr_succ_q_count_AAAA");
			succes += map.get("ws_curr_succ_q_count_NS");
			succes += map.get("ws_curr_succ_q_count_MX");
			succes += map.get("ws_curr_succ_q_count_TXT");
			succes += map.get("ws_curr_succ_q_count_SPF");
			succes += map.get("ws_curr_succ_q_count_DS");
			succes += map.get("ws_curr_succ_q_count_DNSKEY");
			succes += map.get("ws_curr_succ_q_count_NSEC");
			succes += map.get("ws_curr_succ_q_count_NSEC3");
			succes += map.get("ws_curr_succ_q_count_NSEC3PARAM");

			long failure = map.get("ws_curr_succ_q_count_SOA");
			failure += map.get("ws_curr_succ_q_count_A");
			failure += map.get("ws_curr_succ_q_count_AAAA");
			failure += map.get("ws_curr_succ_q_count_NS");
			failure += map.get("ws_curr_succ_q_count_MX");
			failure += map.get("ws_curr_succ_q_count_TXT");
			failure += map.get("ws_curr_succ_q_count_SPF");
			failure += map.get("ws_curr_succ_q_count_DS");
			failure += map.get("ws_curr_succ_q_count_DNSKEY");
			failure += map.get("ws_curr_succ_q_count_NSEC");
			failure += map.get("ws_curr_succ_q_count_NSEC3");
			failure += map.get("ws_curr_succ_q_count_NSEC3PARAM");

			// NOTE: its succes/failure, and not total/failure
			if (((double) (succes - lastsucces))
					/ ((double) (failure - lastfailed)) >= Globals.QUERYRATIO) {
				// errorstate, too many queries failing
				String message = "Queries performed by worker "
						+ comp.getTableName()
						+ " have a disproportionate amount of failures";
				errorMail(message, "High failure count");
				errorNotification("ws_current_q_count", message);
			}
			lastsucces = succes;
			lastfailed = failure;

		}
		counter = (counter + 1) % interval;
	}
}
