/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JToolBar.Separator;
import javax.swing.event.MouseInputAdapter;

import org.gridsofts.resource.Resources;
import org.gridsofts.swing.Kalendar.KalendarEvent;
import org.gridsofts.swing.Kalendar.KalendarListener;
import org.gridsofts.swing.border.ScatterLineBorder;
import org.gridsofts.util.DateTime;
import org.gridsofts.util.EventDispatcher;

public class JDateField extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private EventDispatcher<DateFieldListener, DateFieldEvent> dispatcher;

	private DateTime selectedDate;
	private String formatStr;

	private JTextField dateField;
	private JLabel btnChoose;

	private JDialog chooseDialog;
	private Kalendar kalen;
	private HourChooser hourChoose;
	private MinuteChooser minuteChoose;

	public JDateField() {
		this(DateTime.getCurrentTime(), "yyyy/mm/dd hh:mi");
	}

	public JDateField(String formatStr) {
		this(DateTime.getCurrentTime(), formatStr);
	}

	public JDateField(DateTime defaultValue, String formatStr) {
		super(new FlowLayout(FlowLayout.LEFT));
		
		dispatcher = new EventDispatcher<DateFieldListener, DateFieldEvent>();

		this.selectedDate = defaultValue == null ? DateTime.getCurrentTime() : defaultValue;
		this.formatStr = formatStr;

		initComponent();

		initChooseDialog();
	}

	@Override
	public int getBaseline(int width, int height) {
		return dateField.getBaseline(width, height);
	}

	/**
	 * 返回选择的日期值
	 * 
	 * @return
	 */
	public DateTime getValue() {
		return selectedDate;
	}

	public void setValue(DateTime value) {
		selectedDate = value;
		dateField.setText(selectedDate.toString(formatStr));
	}

	public void addDateFieldListener(DateFieldListener listener) {
		dispatcher.addEventListener(listener);
	}

	private void initComponent() {

		dateField = new JTextField(selectedDate.toString(formatStr));

		dateField.setEditable(false);
		dateField.setBackground(Color.white);

		FontMetrics fm = dateField.getFontMetrics(dateField.getFont());
		dateField.setPreferredSize(new Dimension(fm.stringWidth(formatStr + "  "), fm.getHeight() + 4));

		dateField.addMouseListener(new MouseInputAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				if (dateField.isEnabled()) {
					showDateChooseDialog();
				}
			}
		});

		add(dateField);

		Icon icon = new ImageIcon(Resources.Image.get("calendar.gif"));
		btnChoose = new JLabel(icon);
		btnChoose.setToolTipText("选择");
		btnChoose.setCursor(new Cursor(Cursor.HAND_CURSOR));

		add(btnChoose);

		btnChoose.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent event) {
				if (btnChoose.isEnabled()) {
					showDateChooseDialog();
				}
			}
		});
	}

	private void initChooseDialog() {

		chooseDialog = new JDialog();
		chooseDialog.setModal(false);
		chooseDialog.setResizable(false);
		chooseDialog.setTitle("日历选择器");
		chooseDialog.setUndecorated(true);

		chooseDialog.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowGainedFocus(WindowEvent arg0) {
			}

			@Override
			public void windowLostFocus(WindowEvent arg0) {
				chooseDialog.dispose();
			}
		});

		JPanel pDialog = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Insets getInsets() {
				Insets insets = super.getInsets();

				return insets;
			}
		};
		pDialog.setOpaque(false);
		pDialog.setBorder(BorderFactory.createLineBorder(new Color(0x1b376c)));

		chooseDialog.add(pDialog, BorderLayout.CENTER);

		// 日历
		kalen = new Kalendar();
		kalen.setBorder(BorderFactory.createEmptyBorder());
		kalen.setSelectedDate(new Kalendar.Date(selectedDate.getYear(), selectedDate.getMonth(), selectedDate
				.getDayOfMonth()));
		kalen.addKalendarListener(new KalendarListener() {

			@Override
			public void onChanged(KalendarEvent event) {
				locationChooseDialog();
			}

			@Override
			public void onDblClicked(KalendarEvent event) {
				onOk(false);
			}
		});
		pDialog.add(kalen, BorderLayout.CENTER);

		// 时间
		JPanel bottomPane = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Insets getInsets() {
				Insets insets = super.getInsets();

				insets.top += 3;
				insets.left += 3;
				insets.bottom += 3;
				insets.right += 3;

				return insets;
			}
		};
		bottomPane.setBackground(Color.white);
		bottomPane.setBorder(new ScatterLineBorder(ScatterLineBorder.TOP, Kalendar.KBLC));

		hourChoose = new HourChooser(selectedDate.getHour());
		minuteChoose = new MinuteChooser(selectedDate.getMinute());

		JPanel hmPane = new JPanel(new BorderLayout());

		hmPane.add(hourChoose, BorderLayout.WEST);
		hmPane.add(minuteChoose, BorderLayout.EAST);

		bottomPane.add(hmPane, BorderLayout.WEST);

		bottomPane.add(new Separator(new Dimension(10, 0)), BorderLayout.CENTER);

		JButton bok = new JButton("今天");
		bottomPane.add(bok, BorderLayout.EAST);
		bok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onOk(true);
			}
		});

		pDialog.add(bottomPane, BorderLayout.SOUTH);
	}

	/**
	 * 弹出日历选择框
	 */
	public void showDateChooseDialog() {
		locationChooseDialog();

		chooseDialog.setVisible(true);
	}

	/**
	 * 定位
	 */
	private void locationChooseDialog() {

		chooseDialog.pack();

		Point location = new Point();
		SwingUtilities.convertPointToScreen(location, dateField);
		chooseDialog.setLocation(location.x, location.y + dateField.getHeight());
	}

	/**
	 * 点击确定按钮
	 */
	private void onOk(boolean isToday) {

		Kalendar.Date date = kalen.getSelectedDate();

		if (isToday) {
			selectedDate = new DateTime();
		} else {
			selectedDate = new DateTime(date.getYear(), date.getMonth(), date.getDayOfMonth(), hourChoose
					.getSelectedIndex(), minuteChoose.getSelectedIndex(), 0);
		}

		dateField.setText(selectedDate.toString(formatStr));
		
		// 派发valueChanged事件
		dispatcher.dispatchEvent("valueChanged", new DateFieldEvent(this));

		// 关闭窗口
		chooseDialog.dispose();
	}

	@Override
	public void setEnabled(boolean enabled) {

		dateField.setEnabled(enabled);
		btnChoose.setEnabled(enabled);
	}

	/**
	 * 小时选择下拉框
	 * 
	 * @author zholey
	 * 
	 */
	class HourChooser extends JComboBox {

		private static final long serialVersionUID = 1L;

		public HourChooser(int defaultMonth) {
			super();

			for (int i = 0; i <= 23; i++) {
				addItem(String.valueOf(i) + " 时");
			}

			setSelectedItem(String.valueOf(defaultMonth) + " 时");
		}
	}

	/**
	 * 分钟选择下拉框
	 * 
	 * @author zholey
	 * 
	 */
	class MinuteChooser extends JComboBox {

		private static final long serialVersionUID = 1L;

		public MinuteChooser(int defaultMonth) {
			super();

			for (int i = 0; i <= 59; i++) {
				addItem(String.valueOf(i) + " 分");
			}

			setSelectedItem(String.valueOf(defaultMonth) + " 分");
		}
	}

	/**
	 * 日期域事件接口
	 * 
	 * @author Lei
	 * 
	 */
	public static interface DateFieldListener extends EventListener {
		public void valueChanged(DateFieldEvent event);
	}

	/**
	 * 日期域事件
	 * 
	 * @author Lei
	 * 
	 */
	public static class DateFieldEvent extends EventObject {
		private static final long serialVersionUID = 1L;

		public DateFieldEvent(Object source) {
			super(source);
		}
	}
}
