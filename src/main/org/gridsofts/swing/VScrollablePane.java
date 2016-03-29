/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class VScrollablePane extends JPanel implements Scrollable {
	private static final long serialVersionUID = 1L;

	public VScrollablePane(LayoutManager layout) {
		super(layout);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(0, 0);
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 0;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 0;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}