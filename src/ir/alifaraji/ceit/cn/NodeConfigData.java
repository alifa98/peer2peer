package ir.alifaraji.ceit.cn;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class NodeConfigData implements Serializable {

	private int udpPort;
	private String nodeName;
	private int discoveryPeriod;
	private int getRequestWaitingTime;
	private String filesfolderPath;
	private int maximumConcurrentRequests;
	private String nodesListFileName;

	private NodeConfigData(int udpPort, String nodeName, int discoveryPeriod, int getRequestWaitingTime, String filesfolderPath, int maximumConcurrentRequests, String nodesListFileName) {

		this.udpPort = udpPort;
		this.nodeName = nodeName;
		this.discoveryPeriod = discoveryPeriod;
		this.getRequestWaitingTime = getRequestWaitingTime;
		this.filesfolderPath = filesfolderPath;
		this.maximumConcurrentRequests = maximumConcurrentRequests;
		this.nodesListFileName = nodesListFileName;
	}

	public int getUdpPort() {

		return this.udpPort;
	}

	public String getNodeName() {

		return this.nodeName;
	}

	public String getNodesListFileName() {

		return this.nodesListFileName;
	}

	public int getDiscoveryPeriod() {

		return discoveryPeriod;
	}

	public int getGetRequestWaitingTime() {

		return getRequestWaitingTime;
	}

	public String getFilesfolderPath() {

		return filesfolderPath;
	}

	public int getMaximumConcurrentRequests() {

		return maximumConcurrentRequests;
	}

	public static boolean isExist() {

		return new File(Constants.CONFIG_FILE_RELATIVE_PATH).exists();
	}

	public static NodeConfigData createAndSaveConfigFile(int udpPort,
														 String nodeName,
														 int discoveryPeriod,
														 int getRequestWaitingTime,
														 String filesfolderPath,
														 int maximumConcurrentRequests,
														 String nodesListFileName) throws IOException {

		NodeConfigData self = new NodeConfigData(udpPort, nodeName, discoveryPeriod, getRequestWaitingTime, filesfolderPath, maximumConcurrentRequests, nodesListFileName);
		FileOutputStream fileOutputStream = new FileOutputStream(new File(Constants.CONFIG_FILE_RELATIVE_PATH));
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

		objectOutputStream.writeObject(self);

		objectOutputStream.close();
		fileOutputStream.close();

		return self;
	}

	public static NodeConfigData getSavedConfig() throws IOException, ClassNotFoundException {

		FileInputStream fileInputStream = new FileInputStream(new File(Constants.CONFIG_FILE_RELATIVE_PATH));
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

		NodeConfigData config = NodeConfigData.class.cast(objectInputStream.readObject());

		objectInputStream.close();
		fileInputStream.close();

		return config;
	}

	@Override public String toString() {

		return "NodeConfigData{\n" + "UDP Port=" + udpPort + "\nNode Name=" + nodeName + "\nDiscovery Period=" + discoveryPeriod + "(s)\nGET Request Waiting Time=" + getRequestWaitingTime
			   + "(s)\nFiles Folder=" + filesfolderPath + "\nMaximum Concurrent Users=" + maximumConcurrentRequests + "\nNodes List File=" + nodesListFileName + "\n}";
	}
}
