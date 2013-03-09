package proxy;

import java.io.IOException;
import java.net.Socket;

public class NetProxy {
	
	private static Object lock = new Object();
	private static int max = 0;
	
	
	private Socket in;
	private Socket out;
	
	public int index;
	public Transport in_out;
	public Transport out_in;
	public NetProxy(Socket in, Socket out) {
		synchronized (lock) {
			index = max ++;
		}
		this.in = in;
		this.out = out;
	}
	
	public void proxy() throws IOException {
		in_out = new Transport(this, in.getInputStream(), out.getOutputStream());
		out_in = new Transport(this, out.getInputStream(), in.getOutputStream());
		NetProxyManager.getManager().register(this);
		in_out.start();
		out_in.start();
	}
	
	public void close() {
		try {
			out.close();
		} catch (Exception e) {
		}
		try {
			in.close();
		} catch (Exception e) {
		}
		NetProxyManager.getManager().unregister(this);
	}

	public int getIndex() {
		return index;
	}
}








