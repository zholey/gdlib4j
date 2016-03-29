/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

public class WrapFlowLayout extends FlowLayout {
	private static final long serialVersionUID = 1L;

	public WrapFlowLayout(int align) {
		super(align);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {

		synchronized (parent.getTreeLock()) {

			Dimension ps = parent.getSize();
			Insets ins = parent.getInsets();

			Dimension size = new Dimension(0, 0);

			int components = parent.getComponentCount();
			Component comp = null, nexComp = null;
			Dimension dim = null;

			int width = 0, maxHeight = 0;

			for (int i = 0; i < components; i++) {
				comp = parent.getComponent(i);

				if (comp.isVisible()) {
					dim = comp.getPreferredSize();

					// 累加组件的宽度
					width += dim.width + getHgap();

					// 取组件的最大高度
					if (dim.height > maxHeight) {
						maxHeight = dim.height;
					}

					// 如果容器的宽度已经不能容纳下一个组件，则换行
					if (i + 1 < components) {
						nexComp = parent.getComponent(i + 1);

						if (nexComp.isVisible()
								&& nexComp.getPreferredSize().width + getHgap() > ps.width - width - ins.left - ins.right) {
							size.height += maxHeight + getVgap();

							width = 0;
							maxHeight = 0;
						}
					}

				}
			}

			// 加上最后一行的组件的最大高度
			size.height += maxHeight + getVgap() * 2 + ins.top + ins.bottom;

			return size;
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {

		synchronized (parent.getTreeLock()) {

			Insets ins = parent.getInsets();

			Dimension size = new Dimension(0, 0);

			int components = parent.getComponentCount();
			Component comp = null;
			Dimension dim = null;

			int maxWidth = 0, maxHeight = 0;

			for (int i = 0; i < components; i++) {
				comp = parent.getComponent(i);

				if (comp.isVisible()) {
					dim = comp.getPreferredSize();

					// 取组件的最大宽度
					if (dim.width > maxWidth) {
						maxWidth = dim.width;
					}

					// 取组件的最大高度
					if (dim.height > maxHeight) {
						maxHeight = dim.height;
					}
				}
			}

			size.width = maxWidth + getHgap() * 2 + ins.left + ins.right;
			size.height = maxHeight + getVgap() * 2 + ins.top + ins.bottom;

			return size;
		}
	}
}
