package ir.alifaraji.ceit.cn;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

import jdk.jshell.execution.Util;


public class UdpMessageHandler implements Runnable {

	private final DatagramPacket packet;

	public UdpMessageHandler(DatagramPacket packet) {

		this.packet = packet;
	}

	@Override public void run() {

		try{
			switch (getMessageType()) {
				case Constants.DISCOVERY_MESSAGE_TYPE: // discovery message
					handlePacketAsDiscoveryMessage();
					break;
				case Constants.GET_REQUEST_MESSAGE_TYPE: // get request message
					handlePacketAsGetRequest();
					break;
				case Constants.GET_RESPONSE_MESSAGE_TYPE: // get response message
					handlePacketAsGetResponse();
					break;
				default:
					Log.print("LOG: a packet received with unknown type.");
					break;
			}
		}
		catch (IOException | ClassNotFoundException | InterruptedException e){
			System.err.println("ERROR: Cannot handle a message in UDP message handler.");
			e.printStackTrace();
		}
	}

	private void handlePacketAsGetResponse() throws IOException, ClassNotFoundException {

		if (States.getAllowGetResponses()){
			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getMessagePayload()); ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);){
				GetResponseDto dto = GetResponseDto.class.cast(objectInputStream.readObject());
				dto.setSendTime(System.currentTimeMillis() - dto.getSendTime());
				ClientSide.responsesSyncQueue.add(dto);
				Log.print("Received New Get Response: " + dto);
			}
		}
	}

	private void handlePacketAsGetRequest() throws IOException, ClassNotFoundException, InterruptedException {

		if ((States.getConnectedUsers() + 1 < Main.configData.getMaximumConcurrentRequests())){
			try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getMessagePayload()); ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);){
				GetRequestDto dto = GetRequestDto.class.cast(objectInputStream.readObject());
				Log.print("Received New Get Request: " + dto);
				if (States.isExistInDelayList(dto.getNodeName())){
					long delay = States.getDelay(dto.getNodeName()) * 100;
					Log.print("GET response custom dealy is " + delay + " ms");
					Thread.sleep(delay);
				}
				if (FileUtils.checkFileExistance(dto.getFileName())){

					// check requester is in my list or not
					NodeItem clientNode = FileUtils.getNodeItemByName(dto.getNodeName());
					if (clientNode != null)
						Utils.createAndSendUDPGetResponse(clientNode, Main.tcpPort);
				}

			}

		}

	}

	private void handlePacketAsDiscoveryMessage() throws IOException, ClassNotFoundException {

		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getMessagePayload()); ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);){
			NodeListDto dto = NodeListDto.class.cast(objectInputStream.readObject());
			dto.removeNode(Main.configData.getNodeName(), InetAddress.getLocalHost().getHostAddress(), Main.configData.getUdpPort()); //Remove This Node Infos
			FileUtils.readOrUpdateNodesListFile(dto, ModeType.WRITE);
			Log.print("Received New Node List:" + dto);
		}
	}

	private int getMessageType() {

		byte[] typeBytes = new byte[] { packet.getData()[4], packet.getData()[5], packet.getData()[6], packet.getData()[7] };
		return ByteBuffer.wrap(typeBytes).getInt();
	}

	private int getPayloadLength() {

		byte[] lengthBytes = new byte[] { packet.getData()[0], packet.getData()[1], packet.getData()[2], packet.getData()[3] };
		return ByteBuffer.wrap(lengthBytes).getInt();
	}

	private byte[] getMessagePayload() {

		return Arrays.copyOfRange(packet.getData(), 8, 8 + getPayloadLength());
	}
}
