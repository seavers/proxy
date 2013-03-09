package proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;

/**
 * 网络代理服务器, 将发送到(本地IP, 本地端口)为(localIP, localPort)的数据转发到 (服务器IP,
 * 服务器端口)为(remoteIP, remotePort)的服务器上.
 * 
 * 该线程中将会接收远方请求, 当收到请求后, 产生两个线程来对接双方的输入输出流.
 * 
 * @author lianghaijun
 * 
 */
public class NetProxyServer extends Thread {

	private String localIP;

	private int localPort;

	private String remoteIP;

	private int remotePort;

	public NetProxyServer(String localIP, int localPort, String remoteIP,
			int remotePort) {
		this.localIP = localIP;
		this.localPort = localPort;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
	}

	@Override
	public void run() {
		// while (true) {
		try {
			run0();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
	}

	private void run0() throws IOException, UnknownHostException {
		Logger.info("[启动监听]\t: " + localIP + ":" + localPort
				+ " -> " + remoteIP + ":" + remotePort);
		ServerSocket server = new ServerSocket(localPort, 0, InetAddress
				.getByName(localIP));

		while (true) {
			Socket socket = server.accept();
			try {
				Socket dest = new Socket(remoteIP, remotePort);
				NetProxy proxy = new NetProxy(socket, dest);
				Logger.info("[接收连接]\t: " + proxy.index + "\t" + socket + " -> " + dest);
				proxy.proxy();
			} catch (Exception e) {
				//Logger.error("[发生异常]\t: " + socket);
				try {
					socket.close();
				} catch (Exception ne) {
					//ignore...
				}
			}
		}
	}


	/**
	 * 解析proxy.properties文件, 配置格式为 [loalIP:]localPort=[remoteIP:]remtoePort
	 * 程序会对每一个配置启动一个线程.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//Statistics.single.start();
		
		Properties pp = new Properties();
		pp.load(ClassLoader.getSystemResourceAsStream("proxy.properties"));
		
		for (Iterator it = pp.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			String value = pp.getProperty(key);

			IPORT translateKey = translate(key);
			IPORT translateValue = translate(value);
			new NetProxyServer(translateKey.ip, translateKey.port, translateValue.ip, translateValue.port).start();
		}

	}
	
	public static class IPORT {

		public String ip = "localhost";

		public int port;

		public IPORT(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

	}


	private static IPORT translate(String iport) {
		if (iport == null) {
			throw new IllegalArgumentException();
		}
		String[] iports = iport.split("[:_@!#&]");
		if (iports == null || iports.length == 0) {
			throw new IllegalArgumentException();
		}
		if (iports.length == 1) {
			return new IPORT("localhost", Integer.parseInt(iports[0]));
		} else {
			return new IPORT(iports[0], Integer.parseInt(iports[1]));
		}
	}

}
