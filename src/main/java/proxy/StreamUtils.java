package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

public class StreamUtils {
	
	private static Object lock = new Object();
	private static int n = 0;

	public static void copy(final InputStream in, final OutputStream out, boolean newThread) throws IOException {
		if (newThread) {
			new Thread(new Runnable() {
			
				public void run() {
					copy0(in, out);
				}
			
			}).start();
		} else {
			copy0(in, out);
		}
	}

	protected static void copy0(InputStream in, OutputStream out) {
		
		int nn = 0;
		
		synchronized (lock) {
			nn = n++;
		}
		
		try {
			while (true) {
				int b = in.read();
				out.write(b);
			}
		} catch (SocketException e) {
			System.out.println("nnn: " + nn + "\t: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				System.out.println("c: " + nn + "\t: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}








