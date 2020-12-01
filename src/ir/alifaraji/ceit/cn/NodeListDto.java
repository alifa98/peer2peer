package ir.alifaraji.ceit.cn;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.w3c.dom.Node;


public class NodeListDto implements Serializable {

	private HashSet<NodeItem> nodeSet = new HashSet<>();
	
	public HashSet<NodeItem> getNodeSet() {

		return nodeSet;
	}

	public NodeItem addNode(String name, String address, int port) {

		if (nodeSet == null)
			nodeSet = new HashSet<>();
		NodeItem newNode = new NodeItem(name, address, port);
		nodeSet.add(newNode);
		return newNode;
	}

	public boolean isInList(String name, String address, int port) {

		return nodeSet.contains(new NodeItem(name, address, port));
	}

	public void removeNode(String name, String address, int port) {

		nodeSet.remove(new NodeItem(name, address, port));
	}

	@Override public String toString() {

		return "NodeListDto{" + "nodeSet=" + nodeSet + '}';
	}
}


class NodeItem implements Serializable {

	private String name, address;
	private int port;

	public NodeItem(String name, String address, int port) {

		this.name = name;
		this.address = address;
		this.port = port;
	}

	public String getAddress() {

		return address;
	}

	public int getPort() {

		return port;
	}

	public String getName() {

		return name;
	}

	@Override public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		NodeItem nodeItem = (NodeItem) o;
		return getPort() == nodeItem.getPort() && getName().equals(nodeItem.getName()) && getAddress().equals(nodeItem.getAddress());
	}

	@Override public int hashCode() {

		return Objects.hash(getName(), getAddress(), getPort());
	}

	@Override public String toString() {

		return "NodeItem{" + "name='" + name + '\'' + ", address='" + address + '\'' + ", port=" + port + '}';
	}
}
