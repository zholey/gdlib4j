/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GlidePane extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final int LEFT_RIGHT = 1;
	public static final int RIGHT_LEFT = -1;
	public static final int TOP_BOTTOM = 2;
	public static final int BOTTOM_TOP = -2;

	private int direction;
	private int delay;

	private Timer mt;

	public GlidePane() {
		this(LEFT_RIGHT, 50);
	}

	public GlidePane(int direction, int delay) {

		setLayout(null);

		this.direction = direction;
		this.delay = delay;

		mt = new Timer(50, null);
		mt.setInitialDelay(0);
	}

	public void addGlideChild(JComponent comp) {

		if (mt.isRunning()) {
			return;
		}

		Dimension size = getSize();
		MoveListener listen = null;

		switch (direction) {
		case LEFT_RIGHT:

			comp.setBounds(size.width * -1, 0, size.width, size.height);
			listen = new MoveListener() {
				public boolean isEnd(Component c, int s) {
					return c.getLocation().x + s >= 0;
				}
			};

			break;
		case RIGHT_LEFT:

			comp.setBounds(size.width, 0, size.width, size.height);
			listen = new MoveListener() {
				public boolean isEnd(Component c, int s) {
					return c.getLocation().x - s <= 0;
				}
			};

			break;
		case TOP_BOTTOM:

			comp.setBounds(0, size.height * -1, size.width, size.height);
			listen = new MoveListener() {
				public boolean isEnd(Component c, int s) {
					return c.getLocation().y + s >= 0;
				}
			};

			break;
		case BOTTOM_TOP:

			comp.setBounds(0, size.height, size.width, size.height);
			listen = new MoveListener() {
				public boolean isEnd(Component c, int s) {
					return c.getLocation().y - s <= 0;
				}
			};

			break;
		}

		if (Math.abs(direction) == 1) {
			setMtListener(new Move(direction, size.width * mt.getDelay() / delay, listen));
		} else {
			setMtListener(new Move(direction, size.height * mt.getDelay() / delay, listen));
		}

		add(comp);
		comp.updateUI();

		mt.start();
	}

	private void setMtListener(ActionListener listener) {

		for (ActionListener listen : mt.getActionListeners()) {
			mt.removeActionListener(listen);
		}

		mt.addActionListener(listener);
	}

	/**
	 * 动作
	 * 
	 * @author zhaolei
	 * 
	 */
	class Move implements ActionListener {

		private int direction;
		protected int step;

		private MoveListener listen;

		public Move(int direction, int step, MoveListener listen) {
			this.direction = direction;
			this.step = step;
			this.listen = listen;
		}

		public void actionPerformed(ActionEvent event) {
			if (getComponents().length > 1) {

				Component child01 = getComponent(0);
				Component child02 = getComponent(1);

				if (listen.isEnd(child02, step)) {
					remove(child01);
					mt.stop();

					child02.setLocation(0, 0);
				} else {

					if (Math.abs(direction) == 1) {
						child01.setLocation(child01.getLocation().x + direction * step, 0);
						child02.setLocation(child02.getLocation().x + direction * step, 0);
					} else {
						child01.setLocation(child01.getLocation().y + direction / 2 * step, 0);
						child02.setLocation(child02.getLocation().y + direction / 2 * step, 0);
					}
				}
			} else {

				Component child01 = getComponent(0);

				if (listen.isEnd(child01, step)) {
					mt.stop();

					child01.setLocation(0, 0);
				} else {

					if (Math.abs(direction) == 1) {
						child01.setLocation(child01.getLocation().x + direction * step, 0);
					} else {
						child01.setLocation(child01.getLocation().y + direction / 2 * step, 0);
					}
				}
			}
		}
	}

	/**
	 * 滑动事件监听，用于判断是否需要停止
	 * 
	 * @author zholey
	 * 
	 */
	interface MoveListener {
		public boolean isEnd(Component c, int s);
	}
}
