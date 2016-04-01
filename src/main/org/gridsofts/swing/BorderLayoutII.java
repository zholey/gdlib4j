/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * 先按照BorderLayout的布局规则进行布局，然后再按指定的对齐规则重新布局，参照组件的首选尺寸
 * 
 * @author lei
 */
public class BorderLayoutII extends BorderLayout {
	private static final long serialVersionUID = 1L;

	public static final int HORIZONTAL_LEFT = 0x01;
	public static final int HORIZONTAL_CENTER = 0x02;
	public static final int HORIZONTAL_RIGHT = 0x04;

	public static final int VERTICAL_TOP = 0x10;
	public static final int VERTICAL_CENTER = 0x20;
	public static final int VERTICAL_BOTTOM = 0x40;

	private int alignment;

	public BorderLayoutII(int alignment) {
		this.alignment = alignment;
	}

	@Override
	public void layoutContainer(Container target) {
		super.layoutContainer(target);

		synchronized (target.getTreeLock()) {
			_relayoutByPreferredSize(getLayoutComponent(NORTH));
			_relayoutByPreferredSize(getLayoutComponent(SOUTH));
			_relayoutByPreferredSize(getLayoutComponent(WEST));
			_relayoutByPreferredSize(getLayoutComponent(EAST));
			_relayoutByPreferredSize(getLayoutComponent(CENTER));
		}
	}

	private void _relayoutByPreferredSize(Component c) {

		if (c != null) {
			Rectangle originArea = c.getBounds();
			Dimension preferredSize = c.getPreferredSize();

			int x = originArea.x;
			int y = originArea.y;
			int w = originArea.width;
			int h = originArea.height;

			if (preferredSize.width < w) {
				w = preferredSize.width;
			}

			if (preferredSize.height < h) {
				h = preferredSize.height;
			}

			if ((HORIZONTAL_CENTER & alignment) == HORIZONTAL_CENTER) {
				x = originArea.x + originArea.width / 2 - w / 2;
			} else if ((HORIZONTAL_RIGHT & alignment) == HORIZONTAL_RIGHT) {
				x = originArea.x + originArea.width - w;
			}

			if ((VERTICAL_CENTER & alignment) == VERTICAL_CENTER) {
				y = originArea.y + originArea.height / 2 - h / 2;
			} else if ((VERTICAL_BOTTOM & alignment) == HORIZONTAL_RIGHT) {
				y = originArea.y + originArea.height - h;
			}

			c.setBounds(x, y, w, h);
		}
	}
}
