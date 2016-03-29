/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.authorize;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.gridsofts.util.Archives;
import org.gridsofts.util.DateTime;


public class Authorizing {

	private static Authorizing instance;

	private static Licence licence;

	private AuthInfo authInfo;

	private Authorizing(AuthInfo authInfo) {
		this.authInfo = authInfo;

		try {
			licence = Archives.load(new File(authInfo.getLicencePath()), Licence.class);
		} catch (Throwable t) {
		}
	}

	public static Authorizing getInstance(AuthInfo authInfo) {
		if (instance == null || licence == null) {
			instance = new Authorizing(authInfo);
		}

		return instance;
	}

	public Licence getLicence() {
		return licence;
	}

	public void createTryingLicence() {

		licence = new Licence();

		licence._serial_number = authInfo.getProductSerial();
		licence._user_serial = authInfo.getUserSerial();
		licence._version = authInfo.getProductVersion();
		licence._validate = DateTime.getDateTime(System.currentTimeMillis() + 3 * 30 * 24 * 60 * 60 * 1000l);
		
		try {
			Archives.save(licence, new File(authInfo.getLicencePath()));
		} catch (IOException e) {
		}
	}

	public boolean check(boolean isHint) {

		if (licence == null) {

			if (isHint) {
				JOptionPane.showMessageDialog(null, "请使用合法的授权文件", "无法完成授权认证", JOptionPane.ERROR_MESSAGE);
			}

			return false;
		}

		if (!licence._serial_number.equals(authInfo.getProductSerial())) {

			if (isHint) {
				JOptionPane.showMessageDialog(null, "请使用合法的授权文件", "无法完成授权产品认证", JOptionPane.ERROR_MESSAGE);
			}

			licence = null;
			return false;
		}

		if (!licence._user_serial.equals(authInfo.getUserSerial())) {

			if (isHint) {
				JOptionPane.showMessageDialog(null, "您指定的授权文件不属于当前用户", "无法完成授权用户认证", JOptionPane.ERROR_MESSAGE);
			}

			licence = null;
			return false;
		}

		if (!licence._version.contains(authInfo.getProductVersion())) {

			if (isHint) {
				JOptionPane.showMessageDialog(null, "您指定的授权文件不适用于当前软件版本", "无法完成授权版本认证", JOptionPane.ERROR_MESSAGE);
			}

			licence = null;
			return false;
		}

		if (licence._validate.compareTo(DateTime.getCurrentTime()) < 0) {

			if (isHint) {
				JOptionPane.showMessageDialog(null, "您指定的授权文件已经过期", "无法完成授权认证", JOptionPane.ERROR_MESSAGE);
			}

			licence = null;
			return false;
		}

		return true;
	}

	public static interface AuthInfo {
		public String getLicencePath();

		public String getProductSerial();

		public String getProductVersion();

		public String getUserSerial();
	}
}
