package ir.alifaraji.ceit.cn;


import java.rmi.MarshalledObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class States {

	private static volatile boolean allowGetResponses = false;

	private static volatile int connectedUsers = 0;
	private static volatile Map<String, Integer> delayList = Collections.synchronizedMap(new HashMap<>());

	synchronized static void setAllowGetResponses(boolean value) {

		allowGetResponses = value;
	}

	static boolean getAllowGetResponses() {

		return allowGetResponses;
	}

	synchronized static void decreaseConnectedUsers() {

		connectedUsers--;
	}

	synchronized static void increaseConnectedUsers() {

		connectedUsers++;
	}

	static int getConnectedUsers() {

		return connectedUsers;
	}

	static boolean isExistInDelayList(String nodeName) {

		Integer delayCoefficient = delayList.get(nodeName);
		return !(delayCoefficient == null || delayCoefficient.equals(0));
	}

	static synchronized void increaseDelay(String nodeName) {

		Integer delayCoefficient = delayList.get(nodeName);
		delayList.put(nodeName, delayCoefficient == null ? 1 : ++delayCoefficient);
	}

	static synchronized void decreaseDelay(String nodeName) {

		Integer delayCoefficient = delayList.get(nodeName);
		if (delayCoefficient != null && delayCoefficient > 0)
			delayList.put(nodeName, --delayCoefficient);
	}

	static synchronized Integer getDelay(String nodeName) {

		return delayList.get(nodeName);
	}
}
