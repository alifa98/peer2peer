package ir.alifaraji.ceit.cn;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class TcpServerSide implements Runnable {

	private final ExecutorService executorService;
	private final ServerSocket server;

	TcpServerSide() throws IOException {

		server = new ServerSocket(0);
		Log.print("TCP Server Created on Port " + server.getLocalPort());
		Main.tcpPort = server.getLocalPort();
		executorService = Executors.newCachedThreadPool();
	}

	@Override public void run() {

		while (true){
			try{
				Log.print("Waiting TCP request. Concurrrent‌ Users now is " + States.getConnectedUsers());
				if ((States.getConnectedUsers() + 1 < Main.configData.getMaximumConcurrentRequests())){
					executorService.submit(new TcpRequestHandler(server.accept()));
					Log.print("A TCP Connection accepted. Concurrrent‌ Users now is " + States.getConnectedUsers());
				}

			}
			catch (IOException e){
				System.err.println("ERROR: Exception in Acceptiong TCP Request.");
				e.printStackTrace();
			}
		}
	}
}
