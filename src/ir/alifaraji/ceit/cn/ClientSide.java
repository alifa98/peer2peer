package ir.alifaraji.ceit.cn;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;


class ClientSide {

	public static final Collection<GetResponseDto> responsesSyncQueue = Collections.synchronizedList(new ArrayList<GetResponseDto>());
	private final Scanner scanner = new Scanner(System.in);

	public void start() {

		boolean running = true;
		while (running){
			String command = scanner.nextLine();
			String[] parameters = command.split("\\s");

			switch (parameters[0]) {
				case "LIST":
					listCommandHandler();
					break;

				case "GET":
					if (parameters.length > 1 && parameters[1] != null && !parameters[1].isEmpty()){
						sendUDPGetRequest(parameters[1]);
					}
					else {
						System.err.println("Invalid file name");
					}
					break;
				case "EXIT":
					running = false;
					break;
				default:
					System.err.println("Illegal Command");
					break;
			}
		}
		System.err.println("Good Bye...!");
	}

	private void sendUDPGetRequest(String fileName) {

		try{
			if (!FileUtils.checkFileExistance(fileName)){

				Utils.createAndSendUDPGetRequests(fileName);

				States.setAllowGetResponses(true);
				Thread.sleep(1000 * Main.configData.getGetRequestWaitingTime());
				States.setAllowGetResponses(false);

				GetResponseDto bestResponse = Utils.checkResponseListAndReturnBestResponse();
				if (bestResponse != null){
					System.out.println("Getting " + fileName + " from node " + bestResponse.getNodeName());
					if (Utils.sendTCPRequestAndReceiveFile(bestResponse, fileName)){
						System.out.println(fileName + " Downloaded.");
					}
					else {
						System.err.println("ERROR: Cannot receive file, There is a problem in TCP connection.");
					}
				}
				else {
					System.out.println("There is not such file in our cluster.");
				}
			}
			else {
				System.out.println("File exists in file repository.");
			}
		}
		catch (InterruptedException e){
			System.err.println("ERROR: Cannot wait for responses, Client interrupted:" + e.getMessage());
		}
	}

	private void listCommandHandler() {

		NodeListDto list = new NodeListDto();
		FileUtils.readOrUpdateNodesListFile(list, ModeType.READ);
		for (NodeItem nodeItem : list.getNodeSet()){
			System.out.println(nodeItem.getName() + " " + nodeItem.getAddress() + " " + nodeItem.getPort());
		}
	}

	public void startDiscoveryService() {

		new Thread(new DiscoverySenderService()).start();
	}
}
