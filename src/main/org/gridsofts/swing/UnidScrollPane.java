/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * 提供单向滚动的可滚动面板，支持横向(HORIZONTAL)、纵向(VERTICAL)滚动
 * 
 * @author zholey
 */
public class UnidScrollPane extends JPanel {
	private static final long serialVersionUID = 1L;

	private int direction;

	private int unitIncrement = 30;

	private ScrollButton btnL_T;
	private ScrollButton btnR_B;

	private Timer timerL_T;
	private Timer timerR_B;

	private JComponent content;
	private JPanel viewport;
	private JPanel viewPortPane;

	public UnidScrollPane(int direction) {
		this.direction = direction;

		setOpaque(false);
		setLayout(new BorderLayout());
		initComponent();
	}

	public UnidScrollPane() {
		this(UnidScrollViewLayout.HORIZONTAL);
	}

	public UnidScrollPane(JComponent contentPane) {
		this(UnidScrollViewLayout.HORIZONTAL);

		setContentPane(contentPane);
	}

	/**
	 * 设置视窗内容
	 * 
	 * @param contentPane
	 */
	public void setContentPane(JComponent contentPane) {
		content = contentPane;

		viewport.removeAll();
		viewport.add(content);
	}

	/**
	 * 添加左侧或上部扩展组件
	 * 
	 * @param comp
	 */
	public void addLeftOrTopExtComponent(JComponent comp) {
		viewPortPane.add(comp, direction == UnidScrollViewLayout.HORIZONTAL ? BorderLayout.WEST : BorderLayout.NORTH);
	}

	/**
	 * 添加可侧或下部扩展组件
	 * 
	 * @param comp
	 */
	public void addRightOrBottomExtComponent(JComponent comp) {
		viewPortPane.add(comp, direction == UnidScrollViewLayout.HORIZONTAL ? BorderLayout.EAST : BorderLayout.SOUTH);
	}

	/**
	 * 移除扩展组件
	 * 
	 * @param comp
	 */
	public void removeExtComponent(JComponent comp) {
		viewPortPane.remove(comp);
	}

	/**
	 * 设置每滚动一个单位的位移量
	 * 
	 * @param unitIncrement
	 */
	public void setUnitIncrement(int unitIncrement) {
		this.unitIncrement = unitIncrement;
	}

	public int getUnitIncrement() {
		return unitIncrement;
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {

		switch (direction) {
		case UnidScrollViewLayout.HORIZONTAL:

			btnL_T = new ScrollButton(ScrollButton.SCROLL_LEFT);
			btnR_B = new ScrollButton(ScrollButton.SCROLL_RIGHT);

			add(btnL_T, BorderLayout.WEST);
			add(btnR_B, BorderLayout.EAST);
			break;
		case UnidScrollViewLayout.VERTICAL:

			btnL_T = new ScrollButton(ScrollButton.SCROLL_UP);
			btnR_B = new ScrollButton(ScrollButton.SCROLL_DOWN);

			add(btnL_T, BorderLayout.NORTH);
			add(btnR_B, BorderLayout.SOUTH);
			break;
		}

		viewport = new JPanel(new UnidScrollViewLayout());
		viewport.setOpaque(false);
		viewport.setBackground(Color.red);

		// 此容器的存在是为了在视窗的两侧（左、右或上、下），预留出扩展工具栏的位置
		viewPortPane = new JPanel(new BorderLayout());
		add(viewPortPane, BorderLayout.CENTER);
		viewPortPane.setOpaque(false);

		viewPortPane.add(viewport, BorderLayout.CENTER);

		// init listener's
		btnL_T.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				onScrollL_T();

				timerL_T.start();
			}

			public void mouseReleased(MouseEvent event) {
				timerL_T.stop();
			}

			public void mouseExited(MouseEvent event) {
				timerL_T.stop();
			}
		});
		btnR_B.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				onScrollR_B();

				timerR_B.start();
			}

			public void mouseReleased(MouseEvent event) {
				timerR_B.stop();
			}

			public void mouseExited(MouseEvent event) {
				timerR_B.stop();
			}
		});

		// init timer's
		timerL_T = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onScrollL_T();
			}
		});
		timerR_B = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onScrollR_B();
			}
		});

		timerL_T.setInitialDelay(500);
		timerR_B.setInitialDelay(500);
	}

	/**
	 * 移至最左侧或最顶端
	 */
	public void moveLeftOrTop() {

		switch (direction) {
		case UnidScrollViewLayout.HORIZONTAL:

			content.setLocation(0, content.getLocation().y);
			break;
		case UnidScrollViewLayout.VERTICAL:

			content.setLocation(content.getLocation().x, 0);
			break;
		}
	}

	/**
	 * 移至最右侧或最底端
	 */
	public void moveRightOrBottom() {

		switch (direction) {
		case UnidScrollViewLayout.HORIZONTAL:

			if (content.getWidth() > viewport.getWidth()) {
				content.setLocation(viewport.getWidth() - content.getWidth(), content.getLocation().y);
			}
			break;
		case UnidScrollViewLayout.VERTICAL:

			if (content.getHeight() > viewport.getHeight()) {
				content.setLocation(content.getLocation().x, viewport.getHeight() - content.getHeight());
			}
			break;
		}
	}

	private void onScrollL_T() {

		switch (direction) {
		case UnidScrollViewLayout.HORIZONTAL:

			if (content.getLocation().x * -1 >= unitIncrement) {
				content.setLocation(content.getLocation().x + unitIncrement, content.getLocation().y);
			} else {
				content.setLocation(0, content.getLocation().y);
			}
			break;
		case UnidScrollViewLayout.VERTICAL:

			if (content.getLocation().y * -1 >= unitIncrement) {
				content.setLocation(content.getLocation().x, content.getLocation().y + unitIncrement);
			} else {
				content.setLocation(content.getLocation().x, 0);
			}
			break;
		}
	}

	private void onScrollR_B() {

		switch (direction) {
		case UnidScrollViewLayout.HORIZONTAL:

			if (content.getLocation().x + content.getWidth() - viewport.getWidth() >= unitIncrement) {
				content.setLocation(content.getLocation().x - unitIncrement, content.getLocation().y);
			} else {
				content.setLocation(viewport.getWidth() - content.getWidth(), content.getLocation().y);
			}
			break;
		case UnidScrollViewLayout.VERTICAL:

			if (content.getLocation().y + content.getHeight() - viewport.getHeight() >= unitIncrement) {
				content.setLocation(content.getLocation().x, content.getLocation().y - unitIncrement);
			} else {
				content.setLocation(content.getLocation().x, viewport.getHeight() - content.getHeight());
			}
			break;
		}
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);

		if (containts(btnL_T)) {
			remove(btnL_T);
		}
		if (containts(btnR_B)) {
			remove(btnR_B);
		}

		switch (direction) {
		case UnidScrollViewLayout.HORIZONTAL:

			if (content != null && viewport != null && viewport.getWidth() < content.getWidth()) {
				add(btnL_T, BorderLayout.WEST);
				add(btnR_B, BorderLayout.EAST);
			} else if (content != null && viewport != null) {
				moveLeftOrTop();
			}
			break;
		case UnidScrollViewLayout.VERTICAL:

			if (content != null && viewport != null && viewport.getWidth() < content.getWidth()) {
				add(btnL_T, BorderLayout.NORTH);
				add(btnR_B, BorderLayout.SOUTH);
			} else if (content != null && viewport != null) {
				moveLeftOrTop();
			}
			break;
		}
		
		revalidate();
	}

	/**
	 * 测试是否包含某组件
	 * 
	 * @param c
	 * @return
	 */
	public boolean containts(Component c) {
		Component[] components = getComponents();

		for (Component comp : components) {
			if (comp.equals(c)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 滚动按钮
	 * 
	 * @author zholey
	 * 
	 */
	private static class ScrollButton extends JComponent {
		private static final long serialVersionUID = 1L;

		public static final int SCROLL_LEFT = 0;
		public static final int SCROLL_RIGHT = 1;
		public static final int SCROLL_UP = 2;
		public static final int SCROLL_DOWN = 3;

		private static final int MOUSE_ENTERED = 0;
		private static final int MOUSE_EXITED = 1;
		private static final int MOUSE_PRESSED = 2;
		private static final int MOUSE_RELEASED = 3;

		// 配色方案
		private static HashMap<String, Color> enabledColorScheme = new HashMap<String, Color>();
		private static HashMap<String, Color> disabledColorScheme = new HashMap<String, Color>();

		static {
			// 有效
			enabledColorScheme.put("bgColor0", new Color(0xffffff));
			enabledColorScheme.put("bgColor1", new Color(0x94b0dd));

			enabledColorScheme.put("arrowColor", new Color(0x4d6185));

			enabledColorScheme.put("bgEnteredColor0", new Color(0xfdffff));
			enabledColorScheme.put("bgEnteredColor1", new Color(0xb9dafb));

			enabledColorScheme.put("borderEnteredColor", new Color(0xa5b8e7));

			enabledColorScheme.put("bgExitedColor0", new Color(0xe1eafe));
			enabledColorScheme.put("bgExitedColor1", new Color(0xaec8f7));

			enabledColorScheme.put("borderExitedColor", new Color(0xc4d2f7));

			enabledColorScheme.put("bgPressedColor0", new Color(0xaec8f7));
			enabledColorScheme.put("bgPressedColor1", new Color(0xb9dafb));

			enabledColorScheme.put("borderPressedColor", new Color(0x919ada));

			// 置灰
			disabledColorScheme.put("bgColor0", new Color(0xffffff));
			disabledColorScheme.put("bgColor1", new Color(0xc4c4af));

			disabledColorScheme.put("arrowColor", new Color(0xc9c9c2));

			disabledColorScheme.put("bgEnteredColor0", new Color(0xf7f7f3));
			disabledColorScheme.put("bgEnteredColor1", new Color(0xe6e6dd));

			disabledColorScheme.put("borderEnteredColor", new Color(0xeaeae2));

			disabledColorScheme.put("bgExitedColor0", new Color(0xf7f7f3));
			disabledColorScheme.put("bgExitedColor1", new Color(0xe6e6dd));

			disabledColorScheme.put("borderExitedColor", new Color(0xeaeae2));

			disabledColorScheme.put("bgPressedColor0", new Color(0xf7f7f3));
			disabledColorScheme.put("bgPressedColor1", new Color(0xe6e6dd));

			disabledColorScheme.put("borderPressedColor", new Color(0xeaeae2));
		}

		// 当前选择的颜色方案
		private HashMap<String, Color> currentColorScheme = enabledColorScheme;

		private int mouseState = MOUSE_EXITED;

		private int direction;

		public ScrollButton(int direction) {

			this.direction = direction;

			setLayout(new BorderLayout());
			setCursor(new Cursor(Cursor.HAND_CURSOR));
			setPreferredSize(new Dimension(18, 18));
			setMinimumSize(new Dimension(18, 18));

			addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					mouseState = MOUSE_ENTERED;
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					mouseState = MOUSE_EXITED;
					repaint();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					mouseState = MOUSE_PRESSED;
					repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					mouseState = MOUSE_RELEASED;
					repaint();
				}
			});
		}

		@Override
		protected void processMouseEvent(MouseEvent e) {
			if (isEnabled()) {
				super.processMouseEvent(e);
			}
		}

		@Override
		public void setEnabled(boolean enabled) {

			if (enabled) {
				currentColorScheme = enabledColorScheme;
			} else {
				currentColorScheme = disabledColorScheme;
			}

			super.setEnabled(enabled);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Paint p = g2.getPaint();
			Color c = g2.getColor();

			// 渐变边框
			g2.setPaint(new GradientPaint(0, getHeight() / 2, currentColorScheme.get("bgColor0"), getWidth(),
					getHeight(), currentColorScheme.get("bgColor1")));
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 3, 3);

			// 白色背景
			g2.setPaint(p);
			g2.setColor(Color.white);
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 3, 3);

			// 按钮
			switch (mouseState) {
			case MOUSE_ENTERED:

				// 背景
				if (direction == ScrollButton.SCROLL_LEFT || direction == ScrollButton.SCROLL_RIGHT) {
					g2.setPaint(new GradientPaint(1, getHeight() / 2 - 2, currentColorScheme.get("bgEnteredColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgEnteredColor1")));
				} else {
					g2.setPaint(new GradientPaint(getWidth() / 2 - 2, 1, currentColorScheme.get("bgEnteredColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgEnteredColor1")));
				}
				g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 3, 3);

				// 边框
				g2.setColor(currentColorScheme.get("borderEnteredColor"));
				g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 3, 3);
				break;
			case MOUSE_EXITED:

				// 背景
				if (direction == ScrollButton.SCROLL_LEFT || direction == ScrollButton.SCROLL_RIGHT) {
					g2.setPaint(new GradientPaint(1, getHeight() / 2 - 2, currentColorScheme.get("bgExitedColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgExitedColor1")));
				} else {
					g2.setPaint(new GradientPaint(getWidth() / 2 - 2, 1, currentColorScheme.get("bgExitedColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgExitedColor1")));
				}
				g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 3, 3);

				// 边框
				g2.setColor(currentColorScheme.get("borderExitedColor"));
				g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 3, 3);
				break;
			case MOUSE_PRESSED:

				// 背景
				if (direction == ScrollButton.SCROLL_LEFT || direction == ScrollButton.SCROLL_RIGHT) {
					g2.setPaint(new GradientPaint(1, getHeight() / 2 - 2, currentColorScheme.get("bgPressedColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgPressedColor1")));
				} else {
					g2.setPaint(new GradientPaint(getWidth() / 2 - 2, 1, currentColorScheme.get("bgPressedColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgPressedColor1")));
				}
				g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 3, 3);

				// 边框
				g2.setColor(currentColorScheme.get("borderPressedColor"));
				g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 3, 3);
				break;
			case MOUSE_RELEASED:

				// 背景
				if (direction == ScrollButton.SCROLL_LEFT || direction == ScrollButton.SCROLL_RIGHT) {
					g2.setPaint(new GradientPaint(1, getHeight() / 2 - 2, currentColorScheme.get("bgEnteredColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgEnteredColor1")));
				} else {
					g2.setPaint(new GradientPaint(getWidth() / 2 - 2, 1, currentColorScheme.get("bgEnteredColor0"),
							getWidth() / 2 - 2, getHeight() / 2 - 2, currentColorScheme.get("bgEnteredColor1")));
				}
				g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 3, 3);

				// 边框
				g2.setColor(currentColorScheme.get("borderEnteredColor"));
				g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, 3, 3);
				break;
			}

			// 横向箭头（6*9）
			Rectangle horRect = new Rectangle(0, 0, 6, 9);
			horRect.x = (getWidth() - 2) / 2 - horRect.width / 2;
			horRect.y = (getHeight() - 2) / 2 - horRect.height / 2;

			// 纵向箭头（9*6）
			Rectangle verRect = new Rectangle(0, 0, 9, 6);
			verRect.x = (getWidth() - 2) / 2 - verRect.width / 2;
			verRect.y = (getHeight() - 2) / 2 - verRect.height / 2;

			g2.setColor(currentColorScheme.get("arrowColor"));

			switch (direction) {
			case SCROLL_LEFT:

				g2.drawLine(horRect.x, horRect.y + horRect.height / 2, horRect.x + horRect.width - 2, horRect.y);
				g2
						.drawLine(horRect.x + 1, horRect.y + horRect.height / 2, horRect.x + horRect.width - 2,
								horRect.y + 1);
				g2
						.drawLine(horRect.x + 2, horRect.y + horRect.height / 2, horRect.x + horRect.width - 1,
								horRect.y + 1);

				g2.drawLine(horRect.x, horRect.y + horRect.height / 2, horRect.x + horRect.width - 2, horRect.y
						+ horRect.height - 1);
				g2.drawLine(horRect.x + 1, horRect.y + horRect.height / 2, horRect.x + horRect.width - 2, horRect.y
						+ horRect.height - 2);
				g2.drawLine(horRect.x + 2, horRect.y + horRect.height / 2, horRect.x + horRect.width - 1, horRect.y
						+ horRect.height - 2);

				break;
			case SCROLL_RIGHT:

				g2
						.drawLine(horRect.x + 1, horRect.y + 1, horRect.x + horRect.width - 2, horRect.y
								+ horRect.height / 2);
				g2
						.drawLine(horRect.x + 2, horRect.y + 1, horRect.x + horRect.width - 1, horRect.y
								+ horRect.height / 2);
				g2.drawLine(horRect.x + 2, horRect.y, horRect.x + horRect.width - 0, horRect.y + horRect.height / 2);

				g2.drawLine(horRect.x + 1, horRect.y + horRect.height - 2, horRect.x + horRect.width - 2, horRect.y
						+ horRect.height / 2);
				g2.drawLine(horRect.x + 2, horRect.y + horRect.height - 2, horRect.x + horRect.width - 1, horRect.y
						+ horRect.height / 2);
				g2.drawLine(horRect.x + 2, horRect.y + horRect.height - 1, horRect.x + horRect.width - 0, horRect.y
						+ horRect.height / 2);

				break;
			case SCROLL_UP:

				g2
						.drawLine(verRect.x + 1, verRect.y + verRect.height - 1, verRect.x + verRect.width / 2,
								verRect.y + 2);
				g2
						.drawLine(verRect.x + 1, verRect.y + verRect.height - 2, verRect.x + verRect.width / 2,
								verRect.y + 1);
				g2.drawLine(verRect.x, verRect.y + verRect.height - 2, verRect.x + verRect.width / 2, verRect.y + 0);

				g2.drawLine(verRect.x + verRect.width - 2, verRect.y + verRect.height - 1, verRect.x + verRect.width
						/ 2, verRect.y + 2);
				g2.drawLine(verRect.x + verRect.width - 2, verRect.y + verRect.height - 2, verRect.x + verRect.width
						/ 2, verRect.y + 1);
				g2.drawLine(verRect.x + verRect.width - 1, verRect.y + verRect.height - 2, verRect.x + verRect.width
						/ 2, verRect.y + 0);

				break;
			case SCROLL_DOWN:

				g2
						.drawLine(verRect.x + 1, verRect.y + 1, verRect.x + verRect.width / 2, verRect.y
								+ verRect.height - 2);
				g2
						.drawLine(verRect.x + 1, verRect.y + 2, verRect.x + verRect.width / 2, verRect.y
								+ verRect.height - 1);
				g2.drawLine(verRect.x, verRect.y + 2, verRect.x + verRect.width / 2, verRect.y + verRect.height - 0);

				g2.drawLine(verRect.x + verRect.width - 2, verRect.y + 1, verRect.x + verRect.width / 2, verRect.y
						+ verRect.height - 2);
				g2.drawLine(verRect.x + verRect.width - 2, verRect.y + 2, verRect.x + verRect.width / 2, verRect.y
						+ verRect.height - 1);
				g2.drawLine(verRect.x + verRect.width - 1, verRect.y + 2, verRect.x + verRect.width / 2, verRect.y
						+ verRect.height - 0);

				break;
			}

			g2.setColor(c);
			g2.setPaint(p);
		}
	}
}
