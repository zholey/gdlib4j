/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import org.gridsofts.util.Base64;
import org.gridsofts.util.MimeTypeFactory;
import org.gridsofts.util.StringUtil;

public class SmtpSender implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Pattern MailExp = Pattern
			.compile("(?i)^[^<]+@([^\\.@]+(\\.[^\\.@>]+)+)$");
	private static final Pattern NamedMailExp = Pattern
			.compile("(?i)^(\\\"?(.+)\\\"?)<([^<]+@([^\\.@]+(\\.[^\\.@>]+)+))>$");

	private static final String Boundary = "==-Boundary.SmtpSender.mail.gridsofts.org-==";

	private static final Pattern ResponseCodeExp = Pattern
			.compile("(?i)^(\\d+).*");

	/**
	 * 邮件发送结束状态
	 */
	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	public static final int FILE_NOT_FOUND = 2;

	private int port; // SMTP端口(默认25)
	private int timeout; // 网络连接的超时时间(毫秒)
	private String charset; // 虚拟机的默认编码

	private String sender; // 发件人名字
	private String senderAddress; // 发件人的E-Mail地址
	private String senderServer; // 发件人的服务器地址

	private InitialDirContext dirContext;// 用于查询DNS记录

	private String lastResponseMsg = ""; // 最后一条响应消息正文

	public SmtpSender(String from) {

		if (StringUtil.isNull(from)) {
			throw new IllegalArgumentException("发件人不能为空");
		}

		Matcher mailMatcher = MailExp.matcher(from);
		Matcher namedMailMatcher = NamedMailExp.matcher(from);

		if (namedMailMatcher.find()) {

			sender = namedMailMatcher.group(2);
			senderAddress = namedMailMatcher.group(3);
			senderServer = namedMailMatcher.group(4);

		} else if (mailMatcher.find()) {

			sender = senderAddress = mailMatcher.group(0);
			senderServer = mailMatcher.group(1);

		} else {
			throw new IllegalArgumentException("发件人地址格式错误");
		}

		if (StringUtil.isNull(sender) || StringUtil.isNull(senderAddress)
				|| StringUtil.isNull(senderServer)) {
			throw new IllegalArgumentException("发件人地址格式错误");
		}

		// 默认值
		port = 25;
		timeout = 30000;
		charset = Charset.defaultCharset().displayName();

		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");

		try {
			dirContext = new InitialDirContext(hashtable);
		} catch (NamingException e) {
		}
	}

	/**
	 * 设置SMTP服务端口
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 设置超时时间（毫秒值）
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 设置字符集
	 * 
	 * @param charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 获取最后一条响应消息
	 * 
	 * @return
	 */
	public String getLastResponseMsg() {
		return lastResponseMsg;
	}

	/**
	 * 发送邮件
	 * 
	 * @param to
	 * @param subject
	 * @param content
	 * @param attachments
	 * @param isHtml
	 * @param isUrgent
	 * @return
	 */
	public int send(String to, String subject, String content,
			File[] attachments, boolean isHtml, boolean isUrgent) {

		if (StringUtil.isNull(to)) {
			throw new IllegalArgumentException("收件人不能为空");
		}

		// 收件人
		String receiver = null;
		String recvAddress = null;
		String recvServer = null;

		Matcher mailMatcher = MailExp.matcher(to);
		Matcher namedMailMatcher = NamedMailExp.matcher(to);

		if (namedMailMatcher.find()) {

			receiver = namedMailMatcher.group(2);
			recvAddress = namedMailMatcher.group(3);
			recvServer = namedMailMatcher.group(4);

		} else if (mailMatcher.find()) {

			receiver = recvAddress = mailMatcher.group(0);
			recvServer = mailMatcher.group(1);

		}

		if (StringUtil.isNull(receiver) || StringUtil.isNull(recvAddress)
				|| StringUtil.isNull(recvServer)) {
			throw new IllegalArgumentException("收件人地址格式错误");
		}

		// 创建邮件接收服务器列表
		String[] recvServers = parseDomain(recvServer);

		if (recvServers == null) {
			return FAILED;
		}

		boolean needBoundary = attachments != null && attachments.length > 0;

		// 准备发送邮件
		Socket socket = null;
		BufferedReader in = null;
		BufferedOutputStream out = null;
		byte[] data;

		try {

			for (int i = 0; i < recvServers.length; i++) {

				// 尝试建立连接
				try {
					socket = new Socket(recvServers[i], port);

					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					out = new BufferedOutputStream(socket.getOutputStream());

					if (readResponseCode(in) != 220) {
						return FAILED;
					}
				} catch (Throwable e) {
					in = null;
					out = null;
				}

				if (in != null && out != null) {
					break;
				}
			}

			// 无法连接邮件接收服务器
			if (in == null || out == null) {
				return FAILED;
			}

			// 设置超时时间
			socket.setSoTimeout(timeout);

			// 发送握手信号
			println("HELO " + senderServer, out);
			if (readResponseCode(in) != 250) {
				return FAILED;
			}

			//
			println("MAIL FROM: <" + senderAddress + ">", out);
			if (readResponseCode(in) != 250) {
				return FAILED;
			}

			println("RCPT TO: <" + recvAddress + ">", out);
			if (readResponseCode(in) != 250) {
				return FAILED;
			}

			// 邮件主体
			println("DATA", out);
			if (readResponseCode(in) != 354) {
				return FAILED;
			}

			// 邮件头
			println("From: "
					+ (getBase64String(sender) + " <" + senderAddress + ">"),
					out);
			println("To: " + getBase64String(receiver) + " <" + recvAddress
					+ ">", out);
			println("Subject: " + getBase64String(subject), out);

			Format dateFormatter = new SimpleDateFormat(
					"EEE, d MMM yyyy HH:mm:ss Z (z)", Locale.US);
			println("Date: " + dateFormatter.format(new Date()), out);

			println("MIME-Version: 1.0", out);

			if (needBoundary) {
				println("Content-Type: multipart/mixed; BOUNDARY=\"" + Boundary
						+ "\"", out);
			} else {
				if (isHtml) {
					println("Content-Type: text/html; charset=\"" + charset
							+ "\"", out);
				} else {
					println("Content-Type: text/plain; charset=\"" + charset
							+ "\"", out);
				}
			}

			println("Content-Transfer-Encoding: base64", out);

			if (isUrgent) {
				println("X-Priority: 1", out);
			} else {
				println("X-Priority: 3", out);
			}

			println("X-Mailer: Grid Mail[Copyright(C) 2011 Grid Software (Beijing) CO.,LTD]",
					out);

			println(null, out);

			if (needBoundary) {
				println("--" + Boundary, out);

				if (isHtml) {
					println("Content-Type: text/html; charset=\"" + charset
							+ "\"", out);
				} else {
					println("Content-Type: text/plain; charset=\"" + charset
							+ "\"", out);
				}

				println("Content-Transfer-Encoding: base64", out);

				println(null, out);
			}

			// 邮件正文
			data = (content != null ? content : "").getBytes(charset);

			for (int i = 0; i < data.length;) {

				println(Base64.encodeBytes(Arrays.copyOfRange(data, i,
						Math.min(data.length, i + 54))), out);

				i += 54;

				if (i >= data.length) {
					i = data.length;
				}
			}

			// 附件
			if (needBoundary) {
				InputStream fileIn = null;
				String fileName;

				data = new byte[54];

				try {
					for (int i = 0; i < attachments.length; i++) {
						fileName = attachments[i].getName();
						fileIn = new BufferedInputStream(new FileInputStream(
								attachments[i]));

						println("--" + Boundary, out);

						println("Content-Type: "
								+ MimeTypeFactory.getMimeType(fileName
										.indexOf(".") == -1 ? "*"
										: fileName.substring(fileName
												.lastIndexOf(".") + 1))
								+ "; name=\""
								+ (fileName = getBase64String(fileName)) + "\"",
								out);
						println("Content-Transfer-Encoding: base64", out);
						println("Content-Disposition: attachment; filename=\""
								+ fileName + "\"", out);

						println(null, out);

						int len = -1;

						while ((len = fileIn.read(data)) != -1) {
							println(Base64.encodeBytes(data, 0, len), out);
						}

						try {
							fileIn.close();
						} catch (Throwable e) {
						}
					}
				} catch (FileNotFoundException e) {
					return FILE_NOT_FOUND;
				} catch (IOException e) {
					return FAILED;
				}

				println("--" + Boundary + "--", out);
			}

			// 邮件结束标志
			println(".", out);
			if (readResponseCode(in) != 250) {
				return FAILED;
			}

			// 发送完毕
			println("QUIT", out);
			if (readResponseCode(in) != 221) {
				return FAILED;
			}
		} catch (IOException e) {
			return FAILED;
		} finally {

			try {
				if (socket != null && socket.isConnected()) {
					socket.close();
				}
			} catch (IOException e) {
			}
		}

		return SUCCESS;
	}

	/**
	 * 获得字符串的Base64加密形式。
	 * 
	 * @param str
	 *            字符串
	 * @return 加密后的字符串
	 * @throws UnsupportedEncodingException
	 */
	private String getBase64String(String str)
			throws UnsupportedEncodingException {
		if (str == null || str.length() == 0) {
			return "";
		}

		StringBuffer tmpStr = new StringBuffer();
		byte[] data = str.getBytes(charset);

		for (int i = 0; i < data.length;) {
			if (i != 0) {
				tmpStr.append(' ');
			}

			tmpStr.append("=?");
			tmpStr.append(charset);
			tmpStr.append("?B?");

			tmpStr.append(Base64.encodeBytes(Arrays.copyOfRange(data, i,
					Math.min(data.length, i + 30))));
			tmpStr.append("?=");

			i += 30;

			if (i >= data.length) {
				i = data.length;
			} else {
				tmpStr.append("\r\n");
			}
		}

		return tmpStr.toString();
	}

	/**
	 * 发送字符串，并回车换行
	 * 
	 * @param str
	 * @param out
	 * @throws IOException
	 */
	private void println(String str, OutputStream out) {

		if (str == null) {
			str = "";
		}

		try {
			out.write((str + "\r\n").getBytes(charset));
			out.flush();
		} catch (IOException e) {
		}
	}

	/**
	 * 读取服务器响应码
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private int readResponseCode(BufferedReader in) {

		try {
			Matcher matcher = ResponseCodeExp.matcher(in.readLine());

			if (matcher.find()) {

				try {
					lastResponseMsg = matcher.group().replaceFirst(
							matcher.group(1) + "\\s*", "");
				} catch (Throwable e) {
					lastResponseMsg = matcher.group();
				}

				return Integer.valueOf(matcher.group(1).trim());
			}
		} catch (Throwable e) {
		}

		return -1;
	}

	private String[] parseDomain(String url) {

		try {
			// 分析mx记录
			NamingEnumeration<?> records = dirContext.getAttributes(url,
					new String[] { "mx" }).getAll();

			String[] address;
			String[] tmpMx;
			MX[] tmpMxArray;
			MX tmp;

			if (records.hasMore()) {
				url = records.next().toString();
				url = url.substring(url.indexOf(": ") + 2);
				address = url.split(",");
				tmpMxArray = new MX[address.length];

				for (int i = 0; i < address.length; i++) {
					tmpMx = address[i].trim().split(" ");
					tmpMxArray[i] = new MX(Integer.parseInt(tmpMx[0]), tmpMx[1]);
				}

				for (int i = 1; i < tmpMxArray.length; i++) {
					for (int j = i; j > 0; j--) {
						if (tmpMxArray[j - 1].pri > tmpMxArray[j].pri) {
							tmp = tmpMxArray[j - 1];
							tmpMxArray[j - 1] = tmpMxArray[j];
							tmpMxArray[j] = tmp;
						}
					}
				}

				for (int i = 0; i < tmpMxArray.length; i++) {
					address[i] = tmpMxArray[i].address;
				}

				return address;
			}

			// 分析A记录
			records = dirContext.getAttributes(url, new String[] { "a" })
					.getAll();

			if (records.hasMore()) {
				url = records.next().toString();
				url = url.substring(url.indexOf(": ") + 2).replace(" ", "");
				address = url.split(",");

				return address;
			}

			return new String[] { url };
		} catch (NamingException e) {
		}

		return null;
	}

	private class MX {
		final int pri;
		final String address;

		MX(int pri, String host) {
			this.pri = pri;
			this.address = host;
		}
	}
}
