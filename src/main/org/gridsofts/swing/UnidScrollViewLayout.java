/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

public class UnidScrollViewLayout implements LayoutManager, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	private int direction;

	public UnidScrollViewLayout() {
		this(HORIZONTAL);
	}

	public UnidScrollViewLayout(int direction) {

		if (direction != HORIZONTAL && direction != VERTICAL) {
			throw new IllegalArgumentException("direction 取值必须是 UnidScrollViewLayout.HORIZONTAL 或 UnidScrollViewLayout.VERTICAL");
		}

		this.direction = direction;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public void layoutContainer(Container parent) {

		synchronized (parent.getTreeLock()) {

			int nComponents = parent.getComponentCount();
			int width = 0, height = 0;

			Component comp0 = parent.getComponent(0);
			comp0.setSize(comp0.getPreferredSize());

			for (int i = 1; i < nComponents; i++) {
				Component comp = parent.getComponent(i);
				comp.setSize(comp.getPreferredSize());

				switch (direction) {
				case HORIZONTAL:

					comp.setLocation(comp0.getLocation().x + comp0.getWidth() + width, comp0.getLocation().y);
					break;
				case VERTICAL:

					comp.setLocation(comp0.getLocation().x, comp0.getLocation().y + comp0.getHeight() + height);
					break;
				}

				width += comp.getWidth();
				height += comp.getHeight();
			}
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {

		synchronized (parent.getTreeLock()) {

			Insets ins = parent.getInsets();

			// 累加上边界
			Dimension size = new Dimension(ins.left + ins.right, ins.top + ins.bottom);

			Component[] components = parent.getComponents();

			if (components == null || components.length == 0) {
				return size;
			}

			int maxWidth = 0, maxHeight = 0;

			for (Component comp : components) {

				if (comp.isVisible()) {
					Dimension dim = comp.getPreferredSize();

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

			size.width += maxWidth;
			size.height += maxHeight;

			return size;
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {

		synchronized (parent.getTreeLock()) {

			Insets ins = parent.getInsets();

			// 累加上边界
			Dimension size = new Dimension(ins.left + ins.right, ins.top + ins.bottom);

			Component[] components = parent.getComponents();

			if (components == null || components.length == 0) {
				return size;
			}

			int maxWidth = 0, maxHeight = 0, sumWidth = 0, sumHeight = 0;

			for (Component comp : components) {

				if (comp.isVisible()) {
					Dimension dim = comp.getPreferredSize();

					sumWidth += dim.width;
					sumHeight += dim.height;

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

			// 如果是横向滚动，则首选宽度是所有组件的宽度之和，首选高度是所有组件中的最大高度；
			// 如果是纵向滚动，则首选高度是所有组件的高度之和，首选宽度是所有组件中的最大宽度；
			switch (direction) {
			case HORIZONTAL:

				size.width += sumWidth;
				size.height += maxHeight;
				break;
			case VERTICAL:

				size.width += maxWidth;
				size.height += sumHeight;
				break;
			}

			return size;
		}
	}
}
