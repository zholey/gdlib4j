package org.gridsofts.logging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 基于Log4j实现的定制Appender，实现向Mysql数据库写入日志的功能。为提高并发性能，支持异步方式写入
 *
 * @author lei
 */
public class MysqlAppender extends AppenderSkeleton {
	
	protected Priority threshold = Level.INFO;

	private ExecutorService executeService = Executors.newFixedThreadPool(1);

	private byte[] mutex = new byte[0];

	private Connection dbConn;
	private PreparedStatement dbState;

	private String driverName;
	private String url;
	private String userid;
	private String passwd;

	/**
	 * @param driverName
	 *            the driverName to set
	 */
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @param passwd
	 *            the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	private void connectToMysql() {

		try {
			if (dbState == null || dbState.isClosed()) {
				Class.forName(driverName);

				dbConn = DriverManager.getConnection(url, userid, passwd);
				dbState = dbConn.prepareStatement("insert into gateway_logs "
						+ "(thread_name, log_level, log_time, log_content) values(?, ?, ?, ?)");
			}
		} catch (Throwable e) {
		}
	}

	@Override
	protected void append(LoggingEvent logEvt) {

		if (logEvt.getLevel().toInt() >= getThreshold().toInt()) {
			executeService.execute(new AppenderTask(logEvt));
		}
	}

	@Override
	public synchronized void close() {
		if (closed)
			return;

		closed = true;

		try {
			if (dbState != null) {
				dbState.close();
			}

			if (dbConn != null) {
				dbConn.close();
			}
		} catch (Throwable e) {
		}
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * 日志输出任务
	 *
	 * @author lei
	 */
	private class AppenderTask implements Runnable {

		private LoggingEvent logEvt;

		private AppenderTask(LoggingEvent logEvt) {
			this.logEvt = logEvt;
		}

		@Override
		public void run() {

			synchronized (mutex) {

				connectToMysql();

				try {
					dbState.setString(1, logEvt.getThreadName());
					dbState.setString(2, logEvt.getLevel().toString());
					dbState.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
					dbState.setString(4, logEvt.getMessage().toString());

					dbState.executeUpdate();
				} catch (Throwable e) {
				}
			}
		}
	}
}
