package ir.alifaraji.ceit.cn;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;


public class FileUtils {

	private FileUtils() {

		throw new IllegalStateException("Utility Class");
	}

	synchronized static void readOrUpdateNodesListFile(NodeListDto dto, ModeType mode) {

		//Writes File to Data Transfer Object
		if (ModeType.READ.equals(mode)){
			readFileToDto(dto);
		}
		//Writes Data Transfer Object to File
		else if (ModeType.WRITE.equals(mode)){

			// get old node list
			NodeListDto currentList = new NodeListDto();
			readFileToDto(currentList);

			dto.getNodeSet().addAll(currentList.getNodeSet());

			// write merged 2 lists to file (new + old nodes)
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Constants.DATA_FOLDER_PATH + Main.configData.getNodesListFileName()));){
				for (NodeItem node : dto.getNodeSet()){
					bufferedWriter.write(node.getName() + " " + node.getAddress() + " " + node.getPort() + "\n");
				}
				bufferedWriter.flush();
			}
			catch (IOException e){
				System.err.println("ERROR: Cannot Write nodes list file.");
				e.printStackTrace();
			}
		}
	}

	private static void readFileToDto(NodeListDto dto) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.DATA_FOLDER_PATH + Main.configData.getNodesListFileName()))){
			String line;
			while ((line = bufferedReader.readLine()) != null){
				String[] splited = line.split("\\s+");
				if (splited.length == 3){
					dto.addNode(splited[0], splited[1], Integer.parseInt(splited[2]));
				}
				else {
					System.err.println("Error: Reader ignored an invalid line in nodes list. >>" + line);
				}
			}
		}
		catch (FileNotFoundException e){
			System.err.println("ERROR: Nodes list file does not exist.");
			e.printStackTrace();
		}
		catch (IOException e){
			System.err.println("ERROR: Cannot read nodes list file.");
			e.printStackTrace();
		}
	}

	public static boolean checkFileExistance(String filename) {

		return new File(Constants.DATA_FOLDER_PATH + Main.configData.getFilesfolderPath() + "/" + filename).exists();
	}

	public static NodeItem getNodeItemByName(String nodeName) {

		NodeListDto list = new NodeListDto();
		FileUtils.readOrUpdateNodesListFile(list, ModeType.READ);
		for (NodeItem item : list.getNodeSet())
			if (item.getName().equals(nodeName))
				return item;
		return null;

	}

}
