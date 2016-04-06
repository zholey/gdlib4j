/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gridsofts.guif.itf.IStateListener;
import org.gridsofts.swing.border.ScatterLineBorder;
import org.gridsofts.util.EventObject;

/**
 * 状态栏
 * 
 * @author lei
 */
public class Statusbar extends JPanel implements IStateListener {
	private static final long serialVersionUID = 1L;
	
	private JLabel statusLab;

	private Statusbar() {
		super(new BorderLayout());
		
		setPreferredSize(new Dimension(0, 30));
		setBorder(new ScatterLineBorder(ScatterLineBorder.TOP, Color.gray));
		
		statusLab = new JLabel();
		statusLab.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
		add(statusLab, BorderLayout.CENTER);
	}
	
	private static class SingletonHolder {
		private static Statusbar instance = new Statusbar();
	}
	
	public static Statusbar getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public void statusMsg(EventObject<String> event) {
		statusLab.setText(event.getPayload());
	}
}
