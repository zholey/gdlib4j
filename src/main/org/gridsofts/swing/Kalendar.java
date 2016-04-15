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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Calendar;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import org.gridsofts.resource.Resources;
import org.gridsofts.swing.border.ScatterLineBorder;
import org.gridsofts.util.DateTime;
import org.gridsofts.util.EventDispatcher;

public class Kalendar extends JComponent {
	private static final long serialVersionUID = 1L;

	public static final Color KBC = new Color(0x6382bf);
	public static final Color KWEC = new Color(0xdc5000);
	public static final Color KBGC = new Color(0xf6faff);
	public static final Color KBLC = new Color(0xdfecfb);
	public static final Color KTBC = new Color(0x95aedf);

	public static final Color KTTBC = new Color(0xb8cfe5);
	public static final Color KTTCBC = new Color(0xffffe1);

	private EventDispatcher<KalendarListener, KalendarEvent> kalendarES;
	private Set<Date> underLineDates;

	private JPanel kalen;

	private JLabel ymLab;
	private JPanel content;
	private MonthControler mc;
	private KalendarDate curKalendarDate;

	private Date sysDate = new Date();
	private Date curDate = new Date();

	// 加载工具提示UI
	static {
		try {
			UIManager.put("ToolTipUI", "org.gridsofts.swing.plaf.MultiLineToolTipUI");
			UIManager.put("org.gridsofts.swing.plaf.MultiLineToolTipUI", Class
					.forName("org.gridsofts.swing.plaf.MultiLineToolTipUI"));
		} catch (ClassNotFoundException cnfe) {
		}
	}

	public Kalendar() {

		underLineDates = new HashSet<>();

		kalendarES = new EventDispatcher<>();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(new Color(0x1b376c)));

		// 月份控制栏
		mc = new MonthControler();
		add(mc, BorderLayout.NORTH);

		// 日历
		kalen = new JPanel(new BorderLayout());
		kalen.setBorder(new ScatterLineBorder(ScatterLineBorder.LEFT | ScatterLineBorder.BOTTOM
				| ScatterLineBorder.RIGHT, Color.white));
		add(kalen, BorderLayout.CENTER);

		// 日历头
		JPanel title = new JPanel(new GridLayout(1, 7, 2, 0));
		title.setBackground(Kalendar.KBLC);
		title.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));

		kalen.add(title, BorderLayout.NORTH);

		title.add(getKalendarHeadItem("S"));
		title.add(getKalendarHeadItem("M"));
		title.add(getKalendarHeadItem("T"));
		title.add(getKalendarHeadItem("W"));
		title.add(getKalendarHeadItem("T"));
		title.add(getKalendarHeadItem("F"));
		title.add(getKalendarHeadItem("S"));

		// 日期控件区域
		content = new ContentPane(new GridLayout(0, 7, 2, 2));
		content.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		content.setBackground(Color.white);
		kalen.add(content, BorderLayout.CENTER);

		// 刷新
		flushBySystemTime();
	}

	private void flush(boolean isSendChangedEvent) {
		ymLab.setText(curDate.toString("yyyy/mm"));

		if (curDate.getDayOfMonth() > DateTime.getMaxDaysOfMonth(curDate.getYear(), curDate.getMonth())) {
			curDate.setDayOfMonth(DateTime.getMaxDaysOfMonth(curDate.getYear(), curDate.getMonth()));
		}

		content.removeAll();

		// 计算当月一号是星期几
		Calendar cal = Calendar.getInstance();
		cal.set(curDate.getYear(), curDate.getMonth() - 1, 1);

		// 在一号之前插入空白
		for (int i = 0; i < cal.get(Calendar.DAY_OF_WEEK) - 1; i++) {
			content.add(new KalendarDate(new Date(curDate.getYear(), curDate.getMonth(), 0)));
		}

		// 生成日期
		for (int i = 0; i < DateTime.getMaxDaysOfMonth(curDate.getYear(), curDate.getMonth()); i++) {

			Date d = new Date(curDate.getYear(), curDate.getMonth(), i + 1);
			KalendarDate kDate = new KalendarDate(d);

			// 判断是否需要显示下划线
			if (isNeedUnderline(d)) {
				kDate.setIsUnderline(true);
			}

			content.add(kDate);
		}

		content.updateUI();

		// 抛出日期改变事件
		if (isSendChangedEvent) {
			kalendarES.dispatchEvent("onChanged", new KalendarEvent(this, curDate));
		}
	}

	/**
	 * 判断指定的日期是否需要显示下划线
	 * 
	 * @param d
	 * @return
	 */
	private boolean isNeedUnderline(Date date) {

		if (underLineDates == null) {
			return false;
		}

		for (Date d : underLineDates) {

			if (date.equals(d)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 设置需要显示下划线的日期
	 * 
	 * @param underLineDates
	 */
	public void setUnderlineDates(Set<Date> underLineDates) {

		this.underLineDates.removeAll(this.underLineDates);

		if (underLineDates != null) {
			this.underLineDates.addAll(underLineDates);
		}

		flush(false);
	}

	/**
	 * 逐个添加需要显示下划线的日期
	 * 
	 * @param underLineDate
	 */
	public void addUnderlineDate(Date underLineDate) {

		underLineDates.add(underLineDate);

		flush(false);
	}

	/**
	 * 注册事件侦听器
	 * 
	 * @param listener
	 */
	public void addKalendarListener(KalendarListener listener) {
		kalendarES.addEventListener(listener);

		// 抛出日期改变事件
		kalendarES.dispatchEvent(listener, "onChanged", new KalendarEvent(this, curDate));
	}

	/**
	 * 移除事件侦听器
	 * 
	 * @param listener
	 */
	public void removeKalendarListener(KalendarListener listener) {
		kalendarES.removeEventListener(listener);
	}

	/**
	 * 刷新日期
	 */
	public void flushBySystemTime() {
		Calendar sysTime = Calendar.getInstance();

		if (!sysDate.equals(Calendar.getInstance())) {

			sysDate.setYear(sysTime.get(Calendar.YEAR));
			sysDate.setMonth(sysTime.get(Calendar.MONTH) + 1);
			sysDate.setDayOfMonth(sysTime.get(Calendar.DAY_OF_MONTH));

			curDate.setYear(sysTime.get(Calendar.YEAR));
			curDate.setMonth(sysTime.get(Calendar.MONTH) + 1);
			curDate.setDayOfMonth(sysTime.get(Calendar.DAY_OF_MONTH));

			flush(true);
		}
	}

	public Date getSelectedDate() {
		return curDate;
	}

	public void setSelectedDate(Date date) {

		if (!curDate.equals(date)) {
			curDate = date;
			flush(true);
		}
	}

	private Component getKalendarHeadItem(String str) {

		JPanel pLabel = new JPanel(new BorderLayout());
		pLabel.setOpaque(false);
		pLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		JLabel label = new JLabel(str, SwingConstants.CENTER);

		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
		label.setForeground(Kalendar.KWEC);
		label.setFont(label.getFont().deriveFont(Font.BOLD));

		pLabel.add(label, BorderLayout.CENTER);

		return pLabel;
	}

	// 日历背景容器
	class ContentPane extends JPanel {
		private static final long serialVersionUID = 1L;

		public ContentPane(LayoutManager layout) {
			super(layout);
		}

		@Override
		protected void paintComponent(Graphics g) {

			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Color c = g2.getColor();
			Font f = g2.getFont();

			g2.setColor(Kalendar.KBLC);

			g2.setFont(Resources.Font.get("Impact.ttf").deriveFont(Font.BOLD, 22));
			FontMetrics fmY = g2.getFontMetrics();
			g2.drawString(String.valueOf(curDate.getYear()), 3, fmY.getAscent());

			g2.setFont(Resources.Font.get("Impact.ttf").deriveFont(Font.BOLD, 78));
			FontMetrics fmM = g2.getFontMetrics();
			g2.drawString(String.valueOf(curDate.getMonth()), getSize().width / 2
					- fmM.stringWidth(String.valueOf(curDate.getMonth())) / 2, fmY.getHeight() - 5
					+ (getSize().height - 30) / 2 - fmM.getHeight() / 2 + fmM.getAscent());

			g2.setColor(c);
			g2.setFont(f);
		}
	}

	// 月份控制栏组件
	class MonthControler extends JPanel {
		private static final long serialVersionUID = 1L;

		public MonthControler() {
			super(new BorderLayout());

			setBackground(new Color(0x244684));
			setBorder(new ScatterLineBorder(ScatterLineBorder.TOP | ScatterLineBorder.LEFT | ScatterLineBorder.RIGHT,
					Color.white));

			// 年、月向上
			JPanel pLeft = new JPanel(new BorderLayout());
			pLeft.setOpaque(false);
			pLeft.setPreferredSize(new Dimension(35, 0));
			add(pLeft, BorderLayout.WEST);

			// 上一年
			JLabel preYear = new JLabel();
			preYear.setOpaque(false);
			preYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
			preYear.setToolTipText("上一年");
			preYear.setIcon(new ImageIcon(Resources.Image.get("pre_year.png")));

			pLeft.add(preYear, BorderLayout.WEST);

			preYear.addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent e) {

					curDate.add(Date.Field.YEAR, -1);

					flush(true);
				}
			});

			// 上一月
			JLabel preMonth = new JLabel();
			preMonth.setOpaque(false);
			preMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
			preMonth.setToolTipText("上一月");
			preMonth.setIcon(new ImageIcon(Resources.Image.get("pre_month.png")));

			pLeft.add(preMonth, BorderLayout.EAST);

			preMonth.addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent e) {

					curDate.add(Date.Field.MONTH, -1);

					flush(true);
				}
			});

			ymLab = new JLabel(curDate.toString("yyyy/mm"), SwingConstants.CENTER);
			ymLab.setFont(ymLab.getFont().deriveFont(Font.BOLD));
			ymLab.setForeground(Color.white);
			ymLab.setOpaque(false);
			ymLab.setToolTipText("双击回“今天”");
			ymLab.setCursor(new Cursor(Cursor.HAND_CURSOR));

			add(ymLab, BorderLayout.CENTER);

			ymLab.addMouseListener(new MouseInputAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() >= 2) {

						if (!curDate.equals(sysDate)) {

							curDate.setDate(sysDate);

							flush(true);
						}
					}
				}
			});

			// 年、月向下
			JPanel pRight = new JPanel(new BorderLayout());
			pRight.setOpaque(false);
			pRight.setPreferredSize(new Dimension(35, 0));
			add(pRight, BorderLayout.EAST);

			// 下一年
			JLabel nexYear = new JLabel();
			nexYear.setOpaque(false);
			nexYear.setCursor(new Cursor(Cursor.HAND_CURSOR));
			nexYear.setToolTipText("下一年");
			nexYear.setIcon(new ImageIcon(Resources.Image.get("nex_year.png")));

			pRight.add(nexYear, BorderLayout.EAST);

			nexYear.addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent e) {

					curDate.add(Date.Field.YEAR, 1);

					flush(true);
				}
			});

			// 下一月
			JLabel nexMonth = new JLabel();
			nexMonth.setOpaque(false);
			nexMonth.setCursor(new Cursor(Cursor.HAND_CURSOR));
			nexMonth.setToolTipText("下一月");
			nexMonth.setIcon(new ImageIcon(Resources.Image.get("nex_month.png")));

			pRight.add(nexMonth, BorderLayout.WEST);

			nexMonth.addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent e) {

					curDate.add(Date.Field.MONTH, 1);

					flush(true);
				}
			});
		}

		@Override
		public Insets getInsets() {
			Insets insets = super.getInsets();

			insets.top += 7;
			insets.left += 5;
			insets.bottom += 6;
			insets.right += 5;

			return insets;
		}
	}

	// 日期控件
	private class KalendarDate extends JPanel {
		private static final long serialVersionUID = 1L;

		private final Border todayBorder = BorderFactory.createLineBorder(Kalendar.KBC);
		private final Border normalBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);

		public Date kDate;

		private UnderlineLabel kLabel;

		public KalendarDate(Date d) {
			super(new BorderLayout());

			kDate = d;

			kLabel = new UnderlineLabel(String.valueOf(kDate.getDayOfMonth()));
			kLabel.setHorizontalAlignment(SwingConstants.CENTER);

			if (isWeekEnd()) {
				setLabelColor(Kalendar.KWEC);
				kLabel.setFont(kLabel.getFont().deriveFont(Font.BOLD, 12));
			}
			add(kLabel, BorderLayout.CENTER);

			setOpaque(false);
			setBackground(Kalendar.KBC);

			if (kDate.equals(sysDate)) {
				kLabel.setFont(kLabel.getFont().deriveFont(Font.BOLD | Font.ITALIC));
				setBorder(todayBorder);
			} else {
				setBorder(normalBorder);
			}

			if (kDate.isNullDate()) {
				kLabel.setText("  ");
			} else {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			// 判断是否是用户选择的日期
			if (kDate.equals(curDate)) {
				curKalendarDate = this;
				setSelected(true);
			}

			addMouseListener(new MouseInputAdapter() {
				public void mousePressed(MouseEvent event) {

					if (kDate.isNullDate()) {
						return;
					}

					curDate = kDate;
					setSelected(true);

					// 清除上一个已选择的日期控件的样式
					if (curKalendarDate != null && !curKalendarDate.equals(event.getSource())) {

						curKalendarDate.setSelected(false);
						curKalendarDate = (KalendarDate) event.getSource();
					}

					// 抛出日期改变事件
					kalendarES.dispatchEvent("onChanged", new KalendarEvent(this, curDate));

					// 双击事件
					if (MouseEvent.BUTTON1 == event.getButton() && event.getClickCount() == 2) {
						kalendarES.dispatchEvent("onDblClicked", new KalendarEvent(this, curDate));
					}
				}
			});
		}

		public void setIsUnderline(boolean underline) {
			kLabel.setUnderline(underline);
		}

		private void setSelected(boolean selected) {

			if (selected) {

				setLabelColor(Color.WHITE);
				setOpaque(true);

			} else {

				if (isWeekEnd()) {
					setLabelColor(Kalendar.KWEC);
				} else {
					setLabelColor(Color.BLACK);
				}
				setOpaque(false);
			}

			updateUI();
		}

		private void setLabelColor(Color fg) {
			kLabel.setForeground(fg);
		}

		private boolean isWeekEnd() {
			Calendar cal = Calendar.getInstance();
			cal.set(kDate.getYear(), kDate.getMonth() - 1, kDate.getDayOfMonth());

			return cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7;
		}
	}

	/**
	 * 日历事件
	 * 
	 * @author zholey
	 * 
	 */
	public static class KalendarEvent extends EventObject {
		private static final long serialVersionUID = 1L;

		private Date date;

		public KalendarEvent(Object source, Date d) {
			super(source);

			date = new Date();
			date.setDate(d);
		}

		public Date getDate() {
			return date;
		}
	}

	/**
	 * 日历事件接口
	 * 
	 * @author zholey
	 * 
	 */
	public static interface KalendarListener extends EventListener {
		public void onChanged(KalendarEvent event);

		public void onDblClicked(KalendarEvent event);
	}

	/**
	 * 日历事件适配器
	 * 
	 * @author zholey
	 * 
	 */
	public static class KalendarAdapter implements KalendarListener {
		public void onChanged(KalendarEvent event) {
		}

		public void onDblClicked(KalendarEvent event) {
		}
	}

	/**
	 * 日历组件的数据模型
	 * 
	 * @author zholey
	 * 
	 */
	public static class Date implements Serializable {
		private static final long serialVersionUID = 1L;

		public static enum Field {
			YEAR, MONTH, DAY_OF_MONTH
		}

		private int year, month, dayOfMonth;

		public Date() {
		}

		public Date(int year, int month, int dayOfMonth) {
			this.year = year;
			this.month = month;
			this.dayOfMonth = dayOfMonth;
		}

		public Date(Calendar d) {
			this.year = d.get(Calendar.YEAR);
			this.month = d.get(Calendar.MONTH) + 1;
			this.dayOfMonth = d.get(Calendar.DAY_OF_MONTH);
		}

		/**
		 * 同步日期
		 * 
		 * @param d
		 */
		public void setDate(Date d) {
			year = d.getYear();
			month = d.getMonth();
			dayOfMonth = d.getDayOfMonth();
		}

		/**
		 * 判断当前日期是否为空
		 * 
		 * @return
		 */
		public boolean isNullDate() {
			return (dayOfMonth == 0);
		}

		/**
		 * 在指定字段加上或减去指定的值，同时会影响其它字段的值
		 * 
		 * @param field
		 * @param n
		 */
		public void add(Field field, int n) {

			if (n == 0) {
				return;
			}

			Calendar c = Calendar.getInstance();

			c.setTimeInMillis(0);
			c.set(year, month - 1, dayOfMonth);

			if (field == Field.YEAR) {

				c.add(Calendar.YEAR, n);
			} else if (field == Field.MONTH) {

				c.add(Calendar.MONTH, n);
			} else {

				c.add(Calendar.DAY_OF_MONTH, n);
			}

			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH) + 1;
			dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		}

		@Override
		public boolean equals(Object obj) {

			if (obj == null || (!(obj instanceof Date) && !(obj instanceof Calendar))) {
				return false;
			}

			if (obj instanceof Date) {

				Date d = (Date) obj;

				return (year == d.getYear()) && (month == d.getMonth()) && (dayOfMonth == d.getDayOfMonth());

			} else {

				Calendar d = (Calendar) obj;

				return (year == d.get(Calendar.YEAR)) && (month - 1 == d.get(Calendar.MONTH))
						&& (dayOfMonth == d.get(Calendar.DAY_OF_MONTH));
			}
		}

		@Override
		public String toString() {
			return toString("yyyy-mm-dd");
		}

		public String toString(String format) {

			format = format.toLowerCase();

			format = format.replaceAll("yyyy", String.valueOf(getYear()));
			format = format.replaceAll("mm", getMonth() < 10 ? ("0" + getMonth()) : String.valueOf(getMonth()));
			format = format.replaceAll("dd", getDayOfMonth() < 10 ? ("0" + getDayOfMonth()) : String
					.valueOf(getDayOfMonth()));

			return format;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public int getDayOfMonth() {
			return dayOfMonth;
		}

		public void setDayOfMonth(int dayOfMonth) {
			this.dayOfMonth = dayOfMonth;
		}
	}

	/**
	 * 农历
	 * 
	 * @author zholey
	 * 
	 */
	public static class Lunar {

		/**
		 * 农历日期资料库 1900 - 2049
		 */
		private static final int[] lunarInfo = { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554,
				0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,
				0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2,
				0x04970, 0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
				0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0,
				0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0, 0x0aea6, 0x0ab50,
				0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
				0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0,
				0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
				0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0,
				0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0,
				0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260,
				0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520,
				0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
				0x14b63 };

		private static final String[] Gan = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
		private static final String[] Zhi = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };
		private static final String[] Ani = { "鼠", "牛", "虎", "免", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
		private static final String[] nStr1 = { "", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊" };

		public Date lDate = new Date();
		public boolean isLeap = false;
		public String cYear;

		public Lunar(Date d) {
			int i, leap = 0, temp = 0;

			lDate.setDate(d);

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c2.setTimeInMillis(c1.getTimeInMillis());

			c1.set(lDate.getYear(), lDate.getMonth() - 1, lDate.getDayOfMonth());
			c2.set(1900, 0, 31);

			long offset = (c1.getTimeInMillis() - c2.getTimeInMillis()) / 86400000;

			for (i = 1900; i < 2050 && offset > 0; i++) {
				temp = lYearDays(i);
				offset -= temp;
			}

			if (offset < 0) {
				offset += temp;
				i--;
			}

			lDate.setYear(i);

			leap = leapMonth(i); // 闰哪个月

			for (i = 1; i < 13 && offset > 0; i++) {
				// 闰月
				if (leap > 0 && i == (leap + 1) && this.isLeap == false) {
					--i;
					this.isLeap = true;
					temp = leapDays(lDate.getYear());
				} else {
					temp = monthDays(lDate.getYear(), i);
				}

				// 解除闰月
				if (this.isLeap == true && i == (leap + 1)) {
					this.isLeap = false;
				}

				offset -= temp;
			}

			if (offset == 0 && leap > 0 && i == leap + 1) {
				if (this.isLeap) {
					this.isLeap = false;
				} else {
					this.isLeap = true;
					--i;
				}
			}

			if (offset < 0) {
				offset += temp;
				--i;
			}

			lDate.setMonth(i);
			lDate.setDayOfMonth((int) offset + 1);

			//
			if (lDate.getMonth() < 2) {
				cYear = cyclical(d.getYear() - 1900 + 36 - 1);
			} else {
				cYear = cyclical(d.getYear() - 1900 + 36);
			}
		}

		public String getZhDate() {
			String zhDate = "";

			zhDate += lDate.getMonth() == 1 ? "正月 " : nStr1[lDate.getMonth()] + "月 ";

			zhDate += lDate.getDayOfMonth() <= 10 ? "初" + nStr1[lDate.getDayOfMonth()]
					: lDate.getDayOfMonth() < 20 ? "十" + nStr1[(int) lDate.getDayOfMonth() % 10] : 20 < lDate
							.getDayOfMonth()
							&& lDate.getDayOfMonth() < 30 ? "廿" + nStr1[(int) lDate.getDayOfMonth() % 10]
							: nStr1[(int) lDate.getDayOfMonth() / 10] + "十" + nStr1[(int) lDate.getDayOfMonth() % 10];

			return zhDate;
		}

		/**
		 * 返回农历 y年的总天数
		 * 
		 * @param y
		 * @return
		 */
		private int lYearDays(int y) {
			int i, sum = 348;

			for (i = 0x8000; i > 0x8; i >>= 1) {
				sum += ((lunarInfo[y - 1900] & i) != 0) ? 1 : 0;
			}

			return (sum + leapDays(y));
		}

		/**
		 * 返回农历 y年闰月的天数
		 * 
		 * @param y
		 * @return
		 */
		private int leapDays(int y) {
			if (leapMonth(y) > 0) {
				return (((lunarInfo[y - 1900] & 0x10000) != 0) ? 30 : 29);
			} else {
				return (0);
			}
		}

		/**
		 * 返回农历 y年闰哪个月 1-12 , 没闰返回 0
		 * 
		 * @param y
		 * @return
		 */
		private int leapMonth(int y) {
			return (lunarInfo[y - 1900] & 0xf);
		}

		/**
		 * 返回农历 y年m月的总天数
		 * 
		 * @param y
		 * @param m
		 * @return
		 */
		private int monthDays(int y, int m) {
			return (((lunarInfo[y - 1900] & (0x10000 >> m)) != 0) ? 30 : 29);
		}

		/**
		 * 传入 offset 返回干支, 0=甲子
		 * 
		 * @param offset
		 * @return
		 */
		private String cyclical(int offset) {
			return (Gan[offset % 10] + Zhi[offset % 12] + "（" + Ani[offset % 12] + "）");
		}
	}
}
