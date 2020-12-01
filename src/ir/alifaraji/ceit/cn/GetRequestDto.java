package ir.alifaraji.ceit.cn;


import java.io.Serializable;


class GetRequestDto implements Serializable {

	private String fileName;
	private String nodeName;

	public String getFileName() {

		return fileName;
	}

	public void setFileName(String fileName) {

		this.fileName = fileName;
	}

	public String getNodeName() {

		return nodeName;
	}

	public void setNodeName(String nodeName) {

		this.nodeName = nodeName;
	}

	@Override public String toString() {

		return "GetRequestDto{" + "fileName='" + fileName + '\'' + ", nodeName='" + nodeName + '\'' + '}';
	}
}
