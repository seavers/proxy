package proxy;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetProxyManager extends Thread {

	private static class Data {
		public long start = System.currentTimeMillis();
		public int in_out_count;
		public int out_in_count;
	}

	private static NetProxyManager single = new NetProxyManager();

	static {
		//启动统计线程...
		single.start();
		//sun.misc.Unsafe.getUnsafe().reallocateMemory(arg0, arg1)(arg0)
	}

	public static NetProxyManager getManager() {
		return single;
	}

	private Map runnning = new ConcurrentHashMap();
	private Map removed = new ConcurrentHashMap();

	public void register(NetProxy proxy) {
		runnning.put(proxy, new Data());
	}

	public Map<NetProxy, Data> getAllProxy() {
		return runnning;
	}

	public void unregister(NetProxy netProxy) {
		removed.put(netProxy, single);
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////
	// 用来统计数据

	@Override
	public void run() {
		while (true) {
			try {
				run0();
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void run0() {
		Map<NetProxy, Data> map = getAllProxy();
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			NetProxy proxy = (NetProxy) it.next();
			Data data = map.get(proxy);
			int in_out_current = proxy.in_out.getTransferedSize();
			int in_out_size = in_out_current - data.in_out_count;
			data.in_out_count = in_out_current;
			int out_in_current = proxy.out_in.getTransferedSize();
			int out_in_size = out_in_current - data.out_in_count;
			data.out_in_count = out_in_current;
			Logger.info("[代理统计]\t: " + proxy.index + "\t"
					+ in_out_current + "\t" + in_out_size + " byte/sec\t"
					+ out_in_current + "\t" + out_in_size + " byte/sec");
			if (removed.containsKey(proxy)) {
				runnning.remove(proxy);
				removed.remove(proxy);
			}
		}
	}

}
