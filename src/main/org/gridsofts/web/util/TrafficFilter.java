/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.web.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.gridsofts.util.DateTime;



/**
 * 流量统计过滤器，将此过滤器在web.xml中进行正确配置后，即可在指定目录内生成流量统计文件。
 * 支持配置參數：traffic-file-dir,slice-type,not-match
 * 
 * @author zholey
 * 
 */
public class TrafficFilter implements javax.servlet.Filter {

	private String trafficFileDir;
	private String sliceType;

	private File trafficFile;
	private BufferedWriter trafficWriter;

	private DateTime trafficTime;

	private Pattern notMatchExp;

	public void destroy() {
		try {
			trafficWriter.close();
		} catch (Throwable t) {
		} finally {
			trafficWriter = null;
			trafficFile = null;
		}
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		try {
			HttpServletRequest request = (HttpServletRequest) req;

			// 如果未配置not-match项，或者与配置的not-match项不符，则记录流量
			if (notMatchExp == null || !notMatchExp.matcher(request.getRequestURI()).find()) {

				saveTrafficLog(request);
			}
		} finally {
			chain.doFilter(req, res);
		}
	}

	public void init(FilterConfig config) throws ServletException {

		trafficFileDir = config.getInitParameter("traffic-file-dir");
		if (trafficFileDir == null) {
			trafficFileDir = "traffic_files";
		}

		trafficFileDir = config.getServletContext().getRealPath("/" + trafficFileDir);

		File fDir = new File(trafficFileDir);
		if (fDir != null && !fDir.exists()) {
			fDir.mkdirs();
		}

		sliceType = config.getInitParameter("slice-type");

		String notMatch = config.getInitParameter("not-match");

		if (notMatch != null) {
			notMatchExp = Pattern.compile(notMatch);
		} else {
			notMatchExp = null;
		}
	}

	private BufferedWriter getTrafficWriter() throws IOException {

		DateTime current = DateTime.getCurrentTime();

		if (trafficTime == null) {

			buildTrafficWriter();

		} else if (sliceType.equalsIgnoreCase("year")) {

			if (trafficTime.getYear() != current.getYear()) {

				buildTrafficWriter();
			}

		} else if (sliceType.equalsIgnoreCase("month")) {

			if (trafficTime.getYear() != current.getYear() || trafficTime.getMonth() != current.getMonth()) {

				buildTrafficWriter();
			}

		} else {

			if (trafficTime.getYear() != current.getYear() || trafficTime.getMonth() != current.getMonth()
					|| trafficTime.getDayOfMonth() != current.getDayOfMonth()) {

				buildTrafficWriter();
			}
		}

		return trafficWriter;
	}

	private void buildTrafficWriter() throws IOException {
		trafficTime = DateTime.getCurrentTime();

		trafficFile = new File(trafficFileDir + "/" + trafficTime.toString("yyyy-mm-dd") + ".log");
		trafficWriter = new BufferedWriter(new FileWriter(trafficFile, true));
	}

	private void saveTrafficLog(HttpServletRequest request) {
		try {
			BufferedWriter writer = getTrafficWriter();

			writer.append(request.getLocalAddr() + "\t" + DateTime.getCurrentTime().toString() + "\n\r");
			writer.flush();
		} catch (Throwable t) {
		}
	}
}
