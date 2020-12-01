package ir.alifaraji.ceit.cn;


import static java.lang.Long.signum;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import jdk.jshell.execution.Util;


public class Utils {

	public static byte[] concatByteArrays(byte[] a, byte[] b) {

		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	public static byte[] getStandardSizeByteArray(byte[] bytesOfMessage) {

		if (bytesOfMessage.length > Constants.UDP_BUFFER_SIZE)
			throw new IllegalStateException("Cannot send UDP message more than " + Constants.UDP_BUFFER_SIZE + " Bytes size.");

		return concatByteArrays(bytesOfMessage, new byte[Constants.UDP_BUFFER_SIZE - bytesOfMessage.length]);
	}

	public static void createAndSendUDPGetRequests(String fileName) {

		try{
			Log.print("Sending GET reuqests ...");

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			NodeListDto nodesList = new NodeListDto();
			FileUtils.readOrUpdateNodesListFile(nodesList, ModeType.READ);

			GetRequestDto requestDto = new GetRequestDto();
			requestDto.setFileName(fileName);
			requestDto.setNodeName(Main.configData.getNodeName());

			objectOutputStream.writeObject(requestDto);
			byte[] objectBytes = byteArrayOutputStream.toByteArray();
			byte[] objectLengthBytes_Header = ByteBuffer.allocate(4).putInt(objectBytes.length).array();
			byte[] messageType_Header = ByteBuffer.allocate(4).putInt(Constants.GET_REQUEST_MESSAGE_TYPE).array();

			byte[] buffer = Utils.concatByteArrays(Utils.concatByteArrays(objectLengthBytes_Header, messageType_Header), objectBytes);

			for (NodeItem item : nodesList.getNodeSet()){
				DatagramPacket datagramPacket = new DatagramPacket(Utils.getStandardSizeByteArray(buffer), Constants.UDP_BUFFER_SIZE, new InetSocketAddress(item.getAddress(), item.getPort()));
				DatagramSocket datagramSocket = new DatagramSocket(null);
				datagramSocket.send(datagramPacket);
				datagramSocket.close();
				objectOutputStream.close();
				byteArrayOutputStream.close();
				Log.print("GET request sent to " + item);
			}

			Log.print("GET request sent to all member of cluster. Waiting to responses...");
		}
		catch (Exception e){
			e.printStackTrace();
			Log.print("Error in sending GET request:" + e.getMessage());
		}
	}

	public static GetResponseDto checkResponseListAndReturnBestResponse() {

		if (ClientSide.responsesSyncQueue.isEmpty())
			return null;

		synchronized (ClientSide.responsesSyncQueue){
			Iterator<GetResponseDto> iterator = ClientSide.responsesSyncQueue.iterator();

			GetResponseDto bestResponse = iterator.next();

			while (iterator.hasNext()){
				GetResponseDto temp = iterator.next();

				if (temp.getSendTime() < bestResponse.getSendTime())
					bestResponse = temp;
			}
			ClientSide.responsesSyncQueue.clear();
			return bestResponse;
		}
	}

	public static boolean sendTCPRequestAndReceiveFile(GetResponseDto bestResponse, String fileName) {

		NodeItem serverNode = FileUtils.getNodeItemByName(bestResponse.getNodeName());
		try (Socket socket = new Socket(serverNode.getAddress(), bestResponse.getTcpPort());
				DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				FileOutputStream file = new FileOutputStream(new File(Constants.DATA_FOLDER_PATH + Main.configData.getFilesfolderPath() + "/" + fileName))){

			bufferedWriter.write(Main.configData.getNodeName() + " " + fileName + "\n");
			bufferedWriter.flush();

			byte[] buffer = new byte[1024]; // 1KB buffer
			int bytesCount = 0;

			// Copy requested file into the socket's output stream.
			while ((bytesCount = dataInputStream.read(buffer)) != -1){
				file.write(buffer, 0, bytesCount);
			}

			file.flush();

			States.decreaseDelay(serverNode.getName());

			return true;
		}
		catch (Exception e){
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static void createAndSendUDPGetResponse(NodeItem nodeItem, int tcpPornNumber) {

		try{
			Log.print("Sending GET Response ...");

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			GetResponseDto response = new GetResponseDto();
			response.setNodeName(Main.configData.getNodeName());
			response.setSendTime(System.currentTimeMillis());
			response.setTcpPort(tcpPornNumber);

			objectOutputStream.writeObject(response);
			byte[] objectBytes = byteArrayOutputStream.toByteArray();
			byte[] objectLengthBytes_Header = ByteBuffer.allocate(4).putInt(objectBytes.length).array();
			byte[] messageType_Header = ByteBuffer.allocate(4).putInt(Constants.GET_RESPONSE_MESSAGE_TYPE).array();

			byte[] buffer = Utils.concatByteArrays(Utils.concatByteArrays(objectLengthBytes_Header, messageType_Header), objectBytes);

			DatagramPacket datagramPacket = new DatagramPacket(Utils.getStandardSizeByteArray(buffer), Constants.UDP_BUFFER_SIZE, new InetSocketAddress(nodeItem.getAddress(), nodeItem.getPort()));
			DatagramSocket datagramSocket = new DatagramSocket(null);
			datagramSocket.send(datagramPacket);
			datagramSocket.close();
			objectOutputStream.close();
			byteArrayOutputStream.close();
			Log.print("GET response sent to " + nodeItem);

		}
		catch (Exception e){
			e.printStackTrace();
			Log.print("Error in sending GET Response:" + e.getMessage());
		}
	}
}
