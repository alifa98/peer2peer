package ir.alifaraji.ceit.cn;


import java.io.Serializable;


class GetResponseDto implements Serializable {

	private long sendTime;
	private int tcpPort;
	private String nodeName;

	public long getSendTime() {

		return this.sendTime;
	}

	public void setSendTime(final long sendTime) {

		this.sendTime = sendTime;
	}

	public int getTcpPort() {

		return this.tcpPort;
	}

	public void setTcpPort(final int tcpPort) {

		this.tcpPort = tcpPort;
	}

	public String getNodeName() {

		return this.nodeName;
	}

	public void setNodeName(final String nodeName) {

		this.nodeName = nodeName;
	}

	@Override public String toString() {

		return "GetResponseDto{" + "sendTime=" + sendTime + ", tcpPort=" + tcpPort + ", nodeName=" + nodeName + '}';
	}

}
