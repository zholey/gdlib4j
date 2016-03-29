/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.fileSelectorClasses;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EventListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.gridsofts.resource.Resources;

public class FilePane extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextPane fileLabel;
	private JToolBar toolbar;

	private JButton btnRemove;

	private File file;
	private IFilePaneListener listener;

	public FilePane(File f, IFilePaneListener l) {
		super(new BorderLayout());

		file = f;
		listener = l;

		setOpaque(false);

		fileLabel = new JTextPane();
		fileLabel.setText(file.getPath());
		fileLabel.setEditable(false);
		fileLabel.setOpaque(false);
		add(fileLabel, BorderLayout.CENTER);

		toolbar = new JToolBar();
		toolbar.setOpaque(false);
		toolbar.setFloatable(false);

		JPanel tp = new JPanel(new BorderLayout());
		tp.setOpaque(false);
		add(tp, BorderLayout.EAST);
		tp.add(toolbar, BorderLayout.SOUTH);

		btnRemove = new JButton(new ImageIcon(Resources.Image.get("delete.png")));
		btnRemove.setOpaque(false);
		toolbar.add(btnRemove);

		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				listener.onRemoveFile(FilePane.this);
			}
		});
	}

	public File getFile() {
		return file;
	}

	/**
	 * 动作接口
	 * 
	 * @author zholey
	 * 
	 */
	public static interface IFilePaneListener extends EventListener {

		public void onRemoveFile(FilePane fp);
	}
}
