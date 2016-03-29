/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.authorize;

import java.io.Serializable;

import org.gridsofts.util.DateTime;

public final class Licence implements Serializable {
	private static final long serialVersionUID = 1L;

	public String _serial_number;
	
	public String _user_serial;
	
	public DateTime _validate;
	public String _version;
}
