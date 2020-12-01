package ir.alifaraji.ceit.cn;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class TcpRequestHandler implements Runnable {

	private final Socket socket;

	public TcpRequestHandler(Socket clientSocket) {

		this.socket = clientSocket;
	}

	@Override public void run() {

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))){

			States.increaseConnectedUsers();
			Log.print("TCP request accepted...");

			String requestLine = bufferedReader.readLine();
			String[] parameters = requestLine.split("\\s+");
			if (parameters.length == 2 && FileUtils.checkFileExistance(parameters[1])){
				States.increaseDelay(parameters[0]);

				FileInputStream file = new FileInputStream(new File(Constants.DATA_FOLDER_PATH + Main.configData.getFilesfolderPath() + "/" + parameters[1]));

				byte[] buffer = new byte[1024]; // 1KB buffer
				int bytesCount = 0;

				// Copy requested file into the socket's output stream.
				while ((bytesCount = file.read(buffer)) != -1){
					dataOutputStream.write(buffer, 0, bytesCount);
				}
				dataOutputStream.flush();
				file.close();
			}

			Log.print("TCP request servicing finished.");
		}
		catch (IOException e){
			System.err.println(String.format("ERROR: I/O problem in TCP socket"));
			System.out.println(e.getMessage());
		}
		finally{
			States.decreaseConnectedUsers();
		}
	}

}
