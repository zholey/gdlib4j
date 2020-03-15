/**
 * 版权所有 ©2011-2020 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.mq;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author lei
 */
public class Message extends HashMap<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String KEY = "Message.KEY";
	public static final String VALUE = "Message.VALUE";
	
	public Message(java.lang.String key, Object value) {
		put(KEY, key);
		put(VALUE, value);
	}

	@Override
	public java.lang.String toString() {

		if (get(VALUE) == null) {
			return null;
		}

		return get(VALUE).toString();
	}
}
