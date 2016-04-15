/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.xml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 使用这个类来生成XML文档
 */
public class XML {

	public static String toString(Document doc) {
		return toString(doc, null, "UTF-8");
	}

	public static String toString(Document doc, String docType, String encoding) {

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			toStream(doc, docType, encoding, baos);
			
			return baos.toString(encoding);
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		} catch (UnsupportedEncodingException e) {
		}

		return null;
	}

	public static void toStream(Document doc, Writer w) throws TransformerFactoryConfigurationError,
			TransformerException {
		toStream(doc, null, "UTF-8", w);
	}

	public static void toStream(Document doc, OutputStream os) throws TransformerFactoryConfigurationError,
			TransformerException {
		toStream(doc, null, "UTF-8", os);
	}

	public static void toStream(Document doc, String docType, String encoding, Writer w)
			throws TransformerFactoryConfigurationError, TransformerException {

		getTransformer(docType, encoding).transform(new DOMSource(doc), new StreamResult(w));
	}

	public static void toStream(Document doc, String docType, String encoding, OutputStream os)
			throws TransformerFactoryConfigurationError, TransformerException {

		getTransformer(docType, encoding).transform(new DOMSource(doc), new StreamResult(os));
	}

	private static Transformer getTransformer(String docType, String encoding)
			throws TransformerConfigurationException, TransformerFactoryConfigurationError {

		Transformer tf = TransformerFactory.newInstance().newTransformer();

		tf.setOutputProperty(OutputKeys.METHOD, "xml");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty(OutputKeys.ENCODING, encoding);

		if (docType != null) {
			tf.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType);
		}

		return tf;
	}

	/**
	 * 为 XML.toXml 提供通用的数据模型
	 * 
	 * @author 赵磊
	 * @version 1.0
	 */
	public static class Node implements Serializable {
		private static final long serialVersionUID = 1L;

		private String namespaceURI;

		private String name;
		private String value;

		private String docType;
		private String encoding = "UTF-8";

		private List<Node> childs;
		private Map<String, String> attributes;

		public Node(String name) {
			this(name, null);
		}

		public Node(String name, String value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * 添加子节点
		 * 
		 * @param child
		 */
		public Node addChild(Node child) {

			// 从父节点继承命名空间
			if (child != null && child.getNamespaceURI() == null) {
				child.setNamespaceURI(getNamespaceURI());
			}

			if (childs == null) {
				childs = new Vector<>();
			}
			childs.add(child);
			
			return this;
		}

		/**
		 * 获取子节点
		 * 
		 * @param index
		 * @return
		 */
		public Node getChild(int index) {
			if (childs == null || childs.size() <= index) {
				return null;
			}
			return childs.get(index);
		}

		/**
		 * 获取子节点数
		 * 
		 * @return
		 */
		public int getChildCount() {
			if (childs == null) {
				return 0;
			}
			return childs.size();
		}

		/**
		 * 获取当前节点的所有子节点的迭代器
		 * 
		 * @return
		 */
		public Iterator<Node> childIterator() {
			if (childs == null) {
				return null;
			}
			return childs.iterator();
		}

		/**
		 * 为节点添加属性值对
		 * 
		 * @param name
		 *            属性名
		 * @param value
		 *            属性值
		 */
		public Node setAttribute(String name, Object value) {

			if (attributes == null) {
				attributes = new HashMap<>();
			}

			attributes.put(name, String.valueOf(value));
			
			return this;
		}

		/**
		 * 通过给定的属性名，获取属性值
		 * 
		 * @param name
		 * @return
		 */
		public String getAttribute(String name) {

			if (attributes == null) {
				return null;
			}

			return attributes.get(name);
		}

		/**
		 * 获取当前节点所有属性名称的枚举
		 * 
		 * @return
		 */
		public Set<String> attributeNames() {

			if (attributes == null) {
				return null;
			}

			return attributes.keySet();
		}

		@Override
		public String toString() {

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				Document doc = dbf.newDocumentBuilder().newDocument();

				doc.appendChild(loadXmlElement(this, doc));

				return XML.toString(doc, docType, encoding);

			} catch (Throwable t) {
			}

			return null;
		}

		/**
		 * 生成树状XML结果
		 * 
		 * @param w
		 *            输出
		 */
		public void toStream(Writer w) {

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				Document doc = dbf.newDocumentBuilder().newDocument();

				doc.appendChild(loadXmlElement(this, doc));

				XML.toStream(doc, docType, encoding, w);

			} catch (Throwable t) {
				t.printStackTrace(new PrintWriter(w));
			}
		}

		/**
		 * 生成树状XML结果
		 * 
		 * @param os
		 *            输出
		 */
		public void toStream(OutputStream os) {

			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				Document doc = dbf.newDocumentBuilder().newDocument();

				doc.appendChild(loadXmlElement(this, doc));

				XML.toStream(doc, docType, encoding, os);

			} catch (Throwable t) {
				t.printStackTrace(new PrintWriter(new OutputStreamWriter(os)));
			}
		}

		/**
		 * 加载所有的节点（子节点）及其属性
		 * 
		 * @param namespaceURI
		 *            指定文档的命名空间
		 * @param node
		 * @param doc
		 * @return
		 */
		private Element loadXmlElement(Node node, Document doc) {

			Element element = doc.createElementNS(node.getNamespaceURI(), node.getName());

			// value
			if (node.getValue() != null) {
				element.appendChild(doc.createCDATASection(node.getValue()));
			}

			// attributes
			Set<String> names = node.attributeNames();

			if (names != null) {
				Iterator<String> iterator = names.iterator();

				if (iterator != null) {

					String name = "";

					while (iterator.hasNext()) {
						name = iterator.next();
						element.setAttribute(name, node.getAttribute(name));
					}
				}
			}

			// child node
			int nodeCount = node.getChildCount();

			for (int i = 0; i < nodeCount; i++) {
				element.appendChild(loadXmlElement(node.getChild(i), doc));
			}

			return element;
		}

		public String getNamespaceURI() {
			return namespaceURI;
		}

		public void setNamespaceURI(String namespaceURI) {
			this.namespaceURI = namespaceURI;
		}

		public String getName() {
			return name;
		}

		public Node setName(String name) {
			this.name = name;
			
			return this;
		}

		public String getValue() {
			return value;
		}

		public Node setValue(String value) {
			this.value = value;
			
			return this;
		}

		public String getDocType() {
			return docType;
		}

		public Node setDocType(String docType) {
			this.docType = docType;
			
			return this;
		}

		public String getEncoding() {
			return encoding;
		}

		public Node setEncoding(String encoding) {
			this.encoding = encoding;
			
			return this;
		}

		public List<Node> getChilds() {
			return childs;
		}

		public Node setChilds(List<Node> childs) {
			this.childs = childs;
			
			return this;
		}

		public Map<String, String> getAttributes() {
			return attributes;
		}

		public Node setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
			
			return this;
		}
	}
}
