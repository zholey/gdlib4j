/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

/**
 * 一个实现了纵向流式布局的Panel
 * 
 * @author zholey
 * 
 */
public class VerticalLayoutPane extends VScrollablePane {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private GroupLayout layout;
	private GroupLayout.ParallelGroup hGroup;
	private GroupLayout.SequentialGroup vGroup;

	private int vgap;

	public VerticalLayoutPane() {
		this(0);
	}

	public VerticalLayoutPane(int vgap) {
		super(new BorderLayout());

		this.vgap = vgap;

		contentPane = new JPanel();
		contentPane.setOpaque(false);
		add(contentPane, BorderLayout.NORTH);

		layout = new GroupLayout(contentPane);
		contentPane.setLayout(layout);

		layout.setAutoCreateGaps(true);

		hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		vGroup = layout.createSequentialGroup();

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);
	}

	public void setAutoCreateContainerGaps(boolean containerGaps) {
		layout.setAutoCreateContainerGaps(containerGaps);
	}

	public int getChildCount() {
		return contentPane.getComponentCount();
	}

	public Component getChild(int index) {
		return contentPane.getComponent(index);
	}

	public void removeChild(Component comp) {

		if (contentPane != null) {
			contentPane.remove(comp);
		}
	}

	@Override
	public Component add(Component comp) {

		hGroup.addComponent(comp);
		vGroup.addComponent(comp);
		
		if (vgap > 0) {
			vGroup.addGap(vgap);
		}

		return comp;
	}

	@Override
	public void removeAll() {

		if (contentPane != null) {
			contentPane.removeAll();

			hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			vGroup = layout.createSequentialGroup();

			layout.setHorizontalGroup(hGroup);
			layout.setVerticalGroup(vGroup);
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();

		if (contentPane != null) {
			contentPane.updateUI();
		}
	}
}
