package controller;

import model.Component;
import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class RetrieverListeners {
	
	public static class Data implements XMLRPCCallback {
		Integer counter;
		Retriever ret;
		
		public Data(Retriever ret, Integer counter) {
			this.ret = ret;
			this.counter = counter;
		}
		
		@Override
		public void onError(long arg0, XMLRPCException arg1) {
			synchronized(counter) {
				counter++;
			}
			counter.notify();
		}
		
		@Override
		public void onResponse(long id, Object result) {
			Component comp = ret.getComponent();
			long[] parsed = comp.parseInput((String)result);
			for(int index = 0; index < parsed.length; index++) { 
				ret.updateData(comp.getCalls().length + index, parsed[index]);
			}
			synchronized(counter) {
				counter++;
			}	
			counter.notify();
		}
		
		@Override
		public void onServerError(long arg0, XMLRPCServerException arg1) {
			synchronized(counter) {
				counter++;
			}	
			counter.notify();
		}		
	}
	
	public static class Calls implements XMLRPCCallback {
		Integer counter;
		Retriever ret;
		int index;
		
		public Calls(Retriever ret, int index, Integer counter) {
			this.ret = ret;
			this.counter = counter;
			this.index = index;
		}
		
		@Override
		public void onError(long arg0, XMLRPCException arg1) {
			synchronized(counter) {
				counter++;
			}	
			counter.notify();
		}
		
		@Override
		public void onResponse(long id, Object result) {
			ret.updateData(index, Retriever.parse(result));
			synchronized(counter) {
				counter++;
			}		
			counter.notify();
		}
		
		@Override
		public void onServerError(long arg0, XMLRPCServerException arg1) {
			synchronized(counter) {
				counter++;
			}	
			counter.notify();
		}
	}

}
