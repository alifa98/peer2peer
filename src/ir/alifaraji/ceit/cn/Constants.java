package ir.alifaraji.ceit.cn;


class Constants {

	static final String DATA_FOLDER_PATH = "data/";
	static final String CONFIG_FILE_RELATIVE_PATH = DATA_FOLDER_PATH + "config.p2p";
	static final int UDP_BUFFER_SIZE = 1 * 62 * 1024; // Bytes
	static final int DISCOVERY_MESSAGE_TYPE = 0x00000000;
	static final int GET_RESPONSE_MESSAGE_TYPE = 0x11000011;
	static final int GET_REQUEST_MESSAGE_TYPE = 0x00111100;
}
