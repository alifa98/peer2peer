package ir.alifaraji.ceit.cn;


import java.io.IOException;
import java.util.Scanner;


public class Main {

	public static NodeConfigData configData;
	public static volatile int tcpPort;

	public static void main(String[] args) throws IOException {

		try{
			if (NodeConfigData.isExist()){
				configData = NodeConfigData.getSavedConfig();
			}
			else {
				Scanner input = new Scanner(System.in);

				System.out.println("Enter a valid udp port for this node?");
				int port = input.nextInt();

				System.out.println("Enter a name for this node?");
				String name = input.next();

				System.out.println("Enter nodes list file for this node like \"filname.txt\" --> Path:data/filename.txt ?‌");
				String filename = input.next();

				System.out.println("Enter this node's files repository directory path like \"files\" --> Path:data/files ? ");
				String filesPath = input.next();

				System.out.println("Enter number of seconds for discovery message sending period?");
				int dsicoveryPeriod = input.nextInt();

				System.out.println("Enter number of seconds for GET request waiting time?");
				int waitingTime = input.nextInt();

				System.out.println("Enter number of maximum concurrent users?");
				int maximumUsers = input.nextInt();

				configData = NodeConfigData.createAndSaveConfigFile(port, name, dsicoveryPeriod, waitingTime, filesPath, maximumUsers, filename);
			}
			if (args.length > 0 && args[0] != null && args[0].equalsIgnoreCase("debug")){
				Log.debug = true;
			}

			Log.print(configData);

			runTCPServerSide();
			runUDPServerSide();
			runClientSide();
		}
		catch (ClassNotFoundException e){
			System.err.println("ERROR: Error in handling config file.");
		}
	}

	private static void runClientSide() {

		ClientSide client = new ClientSide();
		client.startDiscoveryService();
		client.start();
		System.exit(0);

	}

	private static void runUDPServerSide() throws IOException {

		new Thread(new UdpServerSide()).start();
	}

	private static void runTCPServerSide() throws IOException {

		new Thread(new TcpServerSide()).start();
	}
}
