/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 为Node接口提供实用方法
 * 
 * @author zholey
 * 
 */
public class NodeUtil {

	/**
	 * 获取属性节点值
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static String getAttribute(Node attrNode) {

		StringBuffer value = new StringBuffer();

		if (attrNode != null && attrNode.getChildNodes().getLength() > 0) {

			int childCount = attrNode.getChildNodes().getLength();
			for (int i = 0; i < childCount; i++) {
				value.append(attrNode.getChildNodes().item(i).getNodeValue());
			}
		}

		return value.toString().trim();
	}

	/**
	 * 获取节点属性值
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static String getAttribute(Node node, String name) {

		if (!node.hasAttributes()) {
			return null;
		}

		StringBuffer value = new StringBuffer();

		Node attrNode = node.getAttributes().getNamedItem(name);
		if (attrNode != null && attrNode.getChildNodes().getLength() > 0) {

			int childCount = attrNode.getChildNodes().getLength();
			for (int i = 0; i < childCount; i++) {
				value.append(attrNode.getChildNodes().item(i).getNodeValue());
			}
		}

		return value.toString().trim();
	}

	/**
	 * 获取节点值，包括TEXT,CDATA内容
	 * 
	 * @param node
	 * @return
	 */
	public static String getValue(Node node) {

		StringBuffer value = new StringBuffer();

		if (node != null) {

			int childCount = node.getChildNodes().getLength();

			for (int i = 0; i < childCount; i++) {
				Node childNode = node.getChildNodes().item(i);

				switch (childNode.getNodeType()) {

				case Node.TEXT_NODE:
				case Node.CDATA_SECTION_NODE:

					value.append(childNode.getNodeValue());
				}
			}
		}

		return value.toString();
	}

	/**
	 * 获取与指定标签名称相同的节点
	 * 
	 * @param root
	 * @param tagName
	 * @return
	 */
	public static List<Node> getElementsByTagName(Node root, String tagName) {

		List<Node> nodeList = new ArrayList<>();

		if (root != null && root.getChildNodes().getLength() > 0) {

			int childCount = root.getChildNodes().getLength();
			for (int i = 0; i < childCount; i++) {

				Node node = root.getChildNodes().item(i);

				if (tagName.equals(node.getNodeName())) {
					nodeList.add(node);
				}
			}
		}

		return nodeList;
	}

	/**
	 * 获取第一个与指定标签名称相同的节点
	 * 
	 * @param root
	 * @param tagName
	 * @return
	 */
	public static Node firstChildByTagName(Node root, String tagName) {

		if (root != null && root.getChildNodes().getLength() > 0) {

			int childCount = root.getChildNodes().getLength();
			for (int i = 0; i < childCount; i++) {

				if (tagName.equals(root.getChildNodes().item(i).getNodeName())) {
					return root.getChildNodes().item(i);
				}
			}
		}

		return null;
	}

	/**
	 * 将Node表示的XML数据转换为JSON格式字符串
	 * 
	 * @param root
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String transformToJson(Node root) {

		StringBuffer json = new StringBuffer();

		// begin
		json.append("{ ");

		// attributes
		NamedNodeMap attrs = root.getAttributes();
		for (int i = 0; attrs != null && i < attrs.getLength(); i++) {
			json.append(getJsonAttribute(attrs.item(i).getNodeName(), getAttribute(attrs.item(i)), null) + ",");
		}

		// node value
		json.append(getJsonAttribute("value", getValue(root), null) + ",");

		// child nodes
		Map<String, Object> childCacheMap = new HashMap<>();

		NodeList childList = root.getChildNodes();
		for (int i = 0; childList != null && i < childList.getLength(); i++) {
			Node child = childList.item(i);

			if (Node.ELEMENT_NODE == child.getNodeType()) {

				String childName = child.getNodeName();

				if (childCacheMap.containsKey(childName)) {

					// 如果已经存在当前节点名称的数组，则加入数组
					if (List.class.isInstance(childCacheMap.get(childName))) {

						((List<String>) childCacheMap.get(childName)).add(transformToJson(child));
					}
					// 如果已经存在当前节点名称，则转换为数组存入
					else if (String.class.isInstance(childCacheMap.get(childName))) {

						List<String> childAry = new ArrayList<>();

						childAry.add(childCacheMap.remove(childName).toString());
						childAry.add(transformToJson(child));

						childCacheMap.put(childName, childAry);
					}
				}
				// 新节点
				else {
					childCacheMap.put(childName, transformToJson(child));
				}
			}
		}

		Set<String> childNameSet = childCacheMap.keySet();
		if (childNameSet != null) {

			for (String childName : childNameSet) {

				// 单个子节点
				if (String.class.isInstance(childCacheMap.get(childName))) {
					json.append(getJsonAttribute(childName, null, childCacheMap.get(childName).toString()) + ",");
				}
				// 子节点数组
				else if (List.class.isInstance(childCacheMap.get(childName))) {
					List<String> childAry = (List<String>) childCacheMap.get(childName);

					StringBuffer childValue = new StringBuffer();

					childValue.append("[ ");
					for (String child : childAry) {
						childValue.append(child + ",");
					}
					childValue.append(" ]");

					String objValue = childValue.toString().replaceFirst(",\\s*\\]$", " ]");

					json.append(getJsonAttribute(childName, null, objValue + ","));
				}
			}
		}

		// end
		json.append(" }");

		return json.toString().replaceFirst(",\\s*\\}$", " }");
	}

	private static String getJsonAttribute(String name, String txtValue, String objValue) {

		StringBuffer json = new StringBuffer();

		json.append("\"" + name + "\":");

		if (txtValue != null && objValue == null) {
			json.append("\"" + txtValue.trim() + "\"");
		} else if (txtValue == null && objValue != null) {
			json.append(objValue.trim());
		}

		return json.toString();
	}
}
