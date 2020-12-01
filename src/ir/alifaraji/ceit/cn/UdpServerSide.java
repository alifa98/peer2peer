package ir.alifaraji.ceit.cn;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class UdpServerSide implements Runnable {

	public UdpServerSide() {

		Log.print("LOG: udp server side listening on port " + Main.configData.getUdpPort());
	}

	@Override public void run() {

		try{
			DatagramSocket udpSocket = new DatagramSocket(Main.configData.getUdpPort());
			while (true){
				byte[] udpBuffer = new byte[Constants.UDP_BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(udpBuffer, udpBuffer.length);
				udpSocket.receive(packet);
				new Thread(new UdpMessageHandler(packet)).start();
			}
		}
		catch (SocketException e){
			System.err.println("ERROR: udp server side socket exception... UDP server closed.");
		}
		catch (IOException e){
			System.err.println("ERROR: udp server side packet receiving exception... UDP server closed.");
		}
	}

}
