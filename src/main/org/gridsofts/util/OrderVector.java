/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.util;

import java.text.Collator;
import java.util.Vector;

public class OrderVector<E> extends Vector<E> {
	private static final long serialVersionUID = 1L;

	public void addElement(E obj) {
		Collator col = Collator.getInstance(java.util.Locale.CHINA);

		if (size() == 0) {
			super.addElement(obj);
			return;
		}

		E e = null;
		for (int i = 0; i < size(); i++) {
			e = get(i);
			if (col.compare(e.toString(), obj.toString()) > 0) {
				insertElementAt(obj, i);
				return;
			}
		}

		super.addElement(obj);
	}

	public void reorder() {
		OrderVector<E> v = new OrderVector<>();
		for (E e : this) {
			v.addElement(e);
		}

		removeAllElements();

		for (E e : v) {
			super.addElement(e);
		}
	}
}
