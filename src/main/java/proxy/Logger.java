package proxy;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm.ss.SSS");
	public static void info(String log) {
		System.out.println(format.format(new Date()) + " " + log);
	}
}
