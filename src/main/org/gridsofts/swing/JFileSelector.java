/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.gridsofts.resource.Resources;
import org.gridsofts.swing.fileSelectorClasses.FilePane;
import org.gridsofts.swing.fileSelectorClasses.FilePane.IFilePaneListener;


/**
 * 文件选择编辑器
 * 
 * @author zholey
 * 
 */
public class JFileSelector extends JPanel implements IFilePaneListener {
	private static final long serialVersionUID = 1L;

	private VerticalLayoutPane fileListPane;

	private JToolBar toolbar;
	private JButton btnAdd;
	
	private String btnLabel = "添加";

	// 前置过滤开关。默认不过滤，如果设置为True，则还需指定相应的过滤器
	private boolean forceFilte;
	private FileFilter filter;
	
	// 允许目录选择开关。默认不允许
	private boolean allowDirectory;
	
	public JFileSelector() {
		this(false, false);
	}

	public JFileSelector(boolean forceFilte, boolean allowDirectory) {
		super(new BorderLayout());

		this.forceFilte = forceFilte;
		this.allowDirectory = allowDirectory;

		setOpaque(false);

		fileListPane = new VerticalLayoutPane(0) {
			private static final long serialVersionUID = 1L;

			@Override
			public Insets getInsets() {
				Insets insets = super.getInsets();

				insets.bottom += 5;

				return insets;
			}
		};
		fileListPane.setOpaque(false);
		add(fileListPane, BorderLayout.CENTER);

		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.setOpaque(false);
		add(toolbar, BorderLayout.SOUTH);

		btnAdd = new JButton(btnLabel);
		btnAdd.setIcon(new ImageIcon(Resources.Image.get("list-add.png")));
		btnAdd.setOpaque(false);

		toolbar.add(btnAdd);

		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				onAppendFile();
			}
		});
	}

	/**
	 * 返回选择的文件
	 * 
	 * @return
	 */
	public File[] getSelectedFiles() {

		List<File> files = new ArrayList<>();

		int count = fileListPane.getChildCount();
		for (int i = 0; i < count; i++) {
			FilePane fp = (FilePane) fileListPane.getChild(i);

			files.add(fp.getFile());
		}

		return files.toArray(new File[0]);
	}

	/**
	 * 返回选择的文件名
	 * 
	 * @return
	 */
	public String[] getSelectedFileName() {

		List<String> files = new ArrayList<>();

		int count = fileListPane.getChildCount();
		for (int i = 0; i < count; i++) {
			FilePane fp = (FilePane) fileListPane.getChild(i);

			files.add(fp.getFile().getName());
		}

		return files.toArray(new String[0]);
	}

	/**
	 * 返回选择的文件路径
	 * 
	 * @return
	 */
	public String[] getSelectedFilePath() {

		List<String> files = new ArrayList<>();

		int count = fileListPane.getChildCount();
		for (int i = 0; i < count; i++) {
			FilePane fp = (FilePane) fileListPane.getChild(i);

			files.add(fp.getFile().getPath());
		}

		return files.toArray(new String[0]);
	}

	/**
	 * 设置选定的文件列表
	 * 
	 * @param files
	 */
	public void addSelectedFiles(File[] files) {

		for (int i = 0; files != null && i < files.length; i++) {

			if (filter == null || !forceFilte || filter.accept(files[i])) {

				fileListPane.add(new FilePane(files[i], JFileSelector.this));
			}
		}

		fileListPane.updateUI();
	}

	/**
	 * 清除选定的文件列表
	 */
	public void cleanSelectedFiles() {

		fileListPane.removeAll();
		fileListPane.updateUI();
	}

	public String getBtnLabel() {
		return btnLabel;
	}

	public void setBtnLabel(String btnLabel) {
		this.btnLabel = btnLabel;
		btnAdd.setText(btnLabel);
	}

	public FileFilter getFilter() {
		return filter;
	}

	public void setFilter(FileFilter filter) {
		this.filter = filter;
	}

	/**
	 * 添加文件
	 */
	private void onAppendFile() {

		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(true);
		
		if (allowDirectory) {
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else {
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}

		if (filter != null) {
			jfc.setFileFilter(filter);
		}

		if (JFileChooser.APPROVE_OPTION == jfc.showOpenDialog(JFileSelector.this.getRootPane())) {
			addSelectedFiles(jfc.getSelectedFiles());
		}
	}

	/**
	 * 删除文件
	 */
	@Override
	public synchronized void onRemoveFile(FilePane fp) {

		fileListPane.removeChild(fp);
		fileListPane.updateUI();
	}
}
