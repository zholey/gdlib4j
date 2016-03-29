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
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JToolBar.Separator;
import javax.swing.event.MouseInputAdapter;

import org.gridsofts.util.PagingList;
import org.gridsofts.util.PagingList.PagingEvent;
import org.gridsofts.util.PagingList.PagingListener;

public class PagingBar extends JPanel implements PagingListener {
	private static final long serialVersionUID = 1L;

	private UnderlineLabel status;

	private UnderlineLabel firPage;
	private UnderlineLabel prePage;
	private UnderlineLabel nexPage;
	private UnderlineLabel lasPage;
	private UnderlineLabel setPage;

	private JTextField pageCapacity;

	private PagingList<?> pagingList;

	public PagingBar(PagingList<?> list) {
		super(new BorderLayout());

		pagingList = list;

		setOpaque(false);

		initUI();

		initEventListener();
	}

	private void initUI() {

		status = new UnderlineLabel();
		status.setUnderline(false);
		status.setEnabled(false);
		status.setDisabledColor(new Color(0x888888));

		firPage = new UnderlineLabel("首页");
		firPage.setForeground(new Color(0x333333));
		firPage.setDisabledColor(new Color(0x888888));

		prePage = new UnderlineLabel("上一页");
		prePage.setForeground(new Color(0x333333));
		prePage.setDisabledColor(new Color(0x888888));

		nexPage = new UnderlineLabel("下一页");
		nexPage.setForeground(new Color(0x333333));
		nexPage.setDisabledColor(new Color(0x888888));

		lasPage = new UnderlineLabel("末页");
		lasPage.setForeground(new Color(0x333333));
		lasPage.setDisabledColor(new Color(0x888888));

		setPage = new UnderlineLabel("设置");
		setPage.setUnderline(true);
		setPage.setCursor(new Cursor(Cursor.HAND_CURSOR));

		pageCapacity = new JTextField();
		pageCapacity.setForeground(new Color(0x555555));
		pageCapacity.setPreferredSize(new Dimension(40, 20));
		pageCapacity.setHorizontalAlignment(SwingConstants.RIGHT);
		pageCapacity.setFont(new Font(pageCapacity.getFont().getFamily(), Font.PLAIN, 11));
		pageCapacity.setBorder(BorderFactory.createLineBorder(new Color(0xefefef)));

		JPanel pCtrl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pCtrl.setOpaque(false);

		pCtrl.add(firPage);
		pCtrl.add(prePage);
		pCtrl.add(nexPage);
		pCtrl.add(lasPage);

		pCtrl.add(new Separator(new Dimension(20, 0)));

		pCtrl.add(pageCapacity);

		pCtrl.add(setPage);

		add(status, BorderLayout.WEST);
		add(pCtrl, BorderLayout.EAST);
	}

	private void initEventListener() {

		// 注册监听
		pagingList.addEventListener(this);

		firPage.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent event) {
				pagingList.firstPage();
			}
		});
		prePage.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent event) {
				pagingList.prevPage();
			}
		});
		nexPage.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent event) {
				pagingList.nextPage();
			}
		});
		lasPage.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent event) {
				pagingList.lastPage();
			}
		});

		pageCapacity.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {
				if (KeyEvent.VK_ENTER == event.getKeyCode()) {
					pagingList.setPageCapacity(Integer.parseInt(pageCapacity.getText()));
				}
			}
		});
		setPage.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent event) {
				pagingList.setPageCapacity(Integer.parseInt(pageCapacity.getText()));
			}
		});
	}

	private void setBtnsStatus(PagingList<?> list) {

		firPage.setEnabled(true);
		firPage.setUnderline(true);

		prePage.setEnabled(true);
		prePage.setUnderline(true);

		nexPage.setEnabled(true);
		nexPage.setUnderline(true);

		lasPage.setEnabled(true);
		lasPage.setUnderline(true);

		pageCapacity.setText(String.valueOf(list.getPageCapacity()));

		status.setText("总计" + list.size() + "行    " + list.getPageNum() + "/" + list.getPageCount());

		if (list.getPageNum() <= 1) {
			firPage.setEnabled(false);
			firPage.setUnderline(false);

			prePage.setEnabled(false);
			prePage.setUnderline(false);
		}

		if (list.getPageNum() >= list.getPageCount()) {
			nexPage.setEnabled(false);
			nexPage.setUnderline(false);

			lasPage.setEnabled(false);
			lasPage.setUnderline(false);
		}
	}

	@Override
	public void dataChanged(PagingEvent event) {
	}

	@Override
	public void pageChanged(PagingEvent event) {
		setBtnsStatus((PagingList<?>) event.getSource());
	}
}
