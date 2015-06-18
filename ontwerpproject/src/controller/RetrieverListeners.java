package controller;

import global.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import model.Component;
import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;

public class RetrieverListeners {
	
	public static class Data implements XMLRPCCallback {
		Retriever ret;
		Lock lock;
		Condition condition;
		List<XMLRPCException> errors;
		AtomicInteger counter;
		
		public Data(Retriever ret, Lock lock, Condition condition, AtomicInteger counter, List<XMLRPCException> errors) {
			this.ret = ret;
			this.lock = lock;
			this.condition = condition;
			this.errors = errors;
			this.counter = counter;
		}
		
		@Override
		public void onError(long arg0, XMLRPCException arg1) {
			synchronized(errors) {
				errors.add(arg1);
			}
			signalRetriever();
		}
		
		@Override
		public void onResponse(long id, Object result) {
			Component comp = ret.getComponent();
			if(!((String)result).isEmpty()) {
				long[] parsed = comp.parseInput((String)result);
				for(int index = 0; index < parsed.length; index++) { 
					ret.updateData(comp.getCalls().length + index, parsed[index]);
				}
			}
			
			Logger.log_debug_rec("Received getData");
			
			signalRetriever();
		}
		
		@Override
		public void onServerError(long arg0, XMLRPCServerException arg1) {
			synchronized(errors) {
				errors.add(arg1);
			}
			signalRetriever();
		}	
		
		private void signalRetriever() {
			lock.lock();
			if(counter.decrementAndGet() == 0) {
				Logger.log_debug_con("Everything is received, waking up retriever...");
				condition.signal();
			}
			lock.unlock();
		}
	}
	
	public static class Calls implements XMLRPCCallback {
		Retriever ret;
		int index;
		Lock lock;
		Condition condition;
		List<XMLRPCException> errors;
		AtomicInteger counter;
		
		public Calls(Retriever ret, int index, Lock lock, Condition condition, AtomicInteger counter, List<XMLRPCException> errors) {
			this.ret = ret;
			this.index = index;
			this.lock = lock;
			this.condition = condition;
			this.errors = errors;
			this.counter = counter;
		}
		
		@Override
		public void onError(long arg0, XMLRPCException arg1) {
			synchronized(errors) {
				errors.add(arg1);
			}
			signalRetriever();
		}
		
		@Override
		public void onResponse(long id, Object result) {
			ret.updateData(index, Retriever.parse(result));			
			Logger.log_debug_rec("Received " + ret.getComponent().getKeys()[index] + ": " + result.toString());			
			signalRetriever();
		}
		
		@Override
		public void onServerError(long arg0, XMLRPCServerException arg1) {
			synchronized(errors) {
				errors.add(arg1);
			}
			signalRetriever();
		}
		
		private void signalRetriever() {
			lock.lock();
			if(counter.decrementAndGet() == 0) {
				Logger.log_debug_con("Everything is received, waking up retriever...");
				condition.signal();
			}
			lock.unlock();
		}
	}

}
