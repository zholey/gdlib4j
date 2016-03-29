/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JForm extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private GroupLayout layout;

	private GroupLayout.ParallelGroup labelGroup;
	private GroupLayout.ParallelGroup itemGroup;

	private GroupLayout.SequentialGroup vGroup;

	private int rowGap;
	private GroupLayout.Alignment vAlign;

	public JForm() {
		this(5, 5, GroupLayout.Alignment.BASELINE);
	}

	public JForm(int rowGap, int colGap) {
		this(rowGap, colGap, GroupLayout.Alignment.BASELINE);
	}

	public JForm(int rowGap, int colGap, GroupLayout.Alignment vAlign) {
		super(new BorderLayout());

		this.rowGap = rowGap;
		this.vAlign = vAlign;

		contentPane = new JPanel();
		contentPane.setOpaque(false);
		add(contentPane, BorderLayout.NORTH);

		layout = new GroupLayout(contentPane);

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		GroupLayout.SequentialGroup hg = layout.createSequentialGroup();
		vGroup = layout.createSequentialGroup();

		labelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		itemGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		layout.setHorizontalGroup(hg);
		layout.setVerticalGroup(vGroup);

		hg.addGroup(labelGroup);

		if (colGap > 0) {
			hg.addGap(colGap);
		}

		hg.addGroup(itemGroup);

		contentPane.setLayout(layout);
	}
	
	public void setAutoCreateGaps(boolean autoCreatePadding) {
		layout.setAutoCreateGaps(autoCreatePadding);
	}

	public void setAutoCreateContainerGaps(boolean containerGaps) {
		layout.setAutoCreateContainerGaps(containerGaps);
	}

	@Override
	public void removeAll() {
		contentPane.removeAll();
	}
	
	public JComponent addControlbar(JComponent comp) {
		add(comp, BorderLayout.SOUTH);
		return comp;
	}

	public void addFormItem(JComponent label, JComponent comp) {

		labelGroup.addComponent(label);
		itemGroup.addComponent(comp);

		GroupLayout.ParallelGroup vg = layout.createParallelGroup(vAlign);

		vGroup.addGroup(vg.addComponent(label).addComponent(comp));

		if (rowGap > 0) {
			vGroup.addGap(rowGap);
		}
	}

	public void addFormItem(String label, JComponent comp) {

		JLabel lab = new JLabel(label);

		addFormItem(lab, comp);
	}

	public void addFormItem(Icon icon, JComponent comp) {

		JLabel lab = new JLabel(icon) {
			private static final long serialVersionUID = 1L;

			@Override
			public int getBaseline(int width, int height) {
				int baseLine = super.getBaseline(width, height);

				if (baseLine < 0) {
					baseLine = height * 3 / 5;
				}

				return baseLine;
			}
		};

		addFormItem(lab, comp);
	}
}
