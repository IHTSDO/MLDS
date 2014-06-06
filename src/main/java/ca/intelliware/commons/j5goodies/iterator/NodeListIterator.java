package ca.intelliware.commons.j5goodies.iterator;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator {
	
	
	public static Iterable<Node> nodeIterable(NodeList list) {
		List<Node> result = new ArrayList<Node>();
		for (int i = 0, length = list == null ? 0 : list.getLength(); i < length; i++) {
			result.add(list.item(i));
		}
		return result;
	}
	
	public static Iterable<Element> elementIterable(NodeList list) {
		List<Element> result = new ArrayList<Element>();
		for (int i = 0, length = list == null ? 0 : list.getLength(); i < length; i++) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				result.add((Element) node);
			}
		}
		return result;
	}
}
