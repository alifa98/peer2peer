package ir.alifaraji.ceit.cn;


import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


public class DiscoverySenderService implements Runnable {

	@Override public void run() {

		while (true){

			try{
				Log.print("Sending UDP Discovery ...");

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

				NodeListDto dto = new NodeListDto();
				FileUtils.readOrUpdateNodesListFile(dto, ModeType.READ);
				NodeItem thisNode = putThisNodeInfoAndReturnIt(dto);

				Log.print("Sending list is " + dto);

				objectOutputStream.writeObject(dto);
				byte[] objectBytes = byteArrayOutputStream.toByteArray();
				byte[] objectLengthBytes_Header = ByteBuffer.allocate(4).putInt(objectBytes.length).array();
				byte[] messageType_Header = ByteBuffer.allocate(4).putInt(Constants.DISCOVERY_MESSAGE_TYPE).array();

				byte[] buffer = Utils.concatByteArrays(Utils.concatByteArrays(objectLengthBytes_Header, messageType_Header), objectBytes);

				for (NodeItem item : dto.getNodeSet()){
					if (item.equals(thisNode))
						continue;
					DatagramPacket datagramPacket = new DatagramPacket(Utils.getStandardSizeByteArray(buffer), Constants.UDP_BUFFER_SIZE, new InetSocketAddress(item.getAddress(), item.getPort()));
					DatagramSocket datagramSocket = new DatagramSocket(null);
					datagramSocket.send(datagramPacket);
					datagramSocket.close();
					objectOutputStream.close();
					byteArrayOutputStream.close();
					Log.print("List Sent to " + item);
				}

				Log.print("List sent to all cluster. now waiting to next period...");
				Thread.sleep(Main.configData.getDiscoveryPeriod() * 1000);
			}
			catch (Exception e){
				e.printStackTrace();
				Log.print("Error in sending discovery message.\n trying again...");
			}
		}
	}

	private NodeItem putThisNodeInfoAndReturnIt(NodeListDto dto) {

		try{
			return dto.addNode(Main.configData.getNodeName(), InetAddress.getLocalHost().getHostAddress(), Main.configData.getUdpPort());
		}
		catch (UnknownHostException e){
			System.err.println("ERROR: Cannot add this host address to discovery message");
		}
		return null;
	}
}
