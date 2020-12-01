package ir.alifaraji.ceit.cn;


public class Log {

	public static boolean debug = false;

	static void print(Object msg) {

		if (debug && msg != null)
			System.out.println(msg.toString());
	}
}
