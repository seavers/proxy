package proxy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transport extends Thread {
	
	private static final int BUFFER_LENGTH = Integer.getInteger("buffer", 32768);
	private static final boolean RECORD_FILE = Boolean.getBoolean("debug");
	
	static {
		if (RECORD_FILE) {
			File data = new File("data");
			data.mkdirs();
			Logger.info("[调试模式]\t: 调试模式开启, 系统将会记录交互日志!!, 并保存于目录 " + data.getAbsolutePath());
		}
	}

	private NetProxy proxy;
	private InputStream in;
	private OutputStream out;
	private int count;
	private final byte[] BUFFER = new byte[BUFFER_LENGTH];
	
	public Transport(NetProxy proxy, InputStream in, OutputStream out) {
		this.proxy = proxy;
		this.in = in;
		this.out = out; 
		if (RECORD_FILE) {
			try {
				String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
				String fileName = "data/c_" + timestamp + "_" + new Object().hashCode() + ".txt";
				OutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(fileName), BUFFER_LENGTH);
				this.out = new TeeOutputStream(out, fileOutput);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				byte[] b = BUFFER;
				int n = in.read(b);
				if (n == -1) {
					break;
				}
				out.write(b, 0, n);
				count += n;
			}
		} catch (SocketTimeoutException e) {
			//e.printStackTrace();
		} catch (SocketException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			proxy.close();
		}
	}
	
	public int getTransferedSize() {
		return count;
	}
}




