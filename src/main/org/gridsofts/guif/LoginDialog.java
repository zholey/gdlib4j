/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.gridsofts.guif.components.Copyright;
import org.gridsofts.guif.itf.IAuthentication;
import org.gridsofts.resource.Resources;
import org.gridsofts.swing.JForm;
import org.gridsofts.util.Configure;

/**
 * 登录对话框
 * 
 * @author lei
 */
public class LoginDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	private IAuthentication authenticator;

	private JTextField userId;
	private JPasswordField passWd;

	private JButton btnOk;

	public LoginDialog(IAuthentication authenticator, Configure configure) {
		this.authenticator = authenticator;

		setTitle(configure.getProperty("guif.login.title", "\u7CFB\u7EDF\u767B\u5F55"));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// banner image
		JLabel bannerIcon = new JLabel(new ImageIcon(Resources.Image.get("banner.png")));
		bannerIcon.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		getContentPane().add(bannerIcon, BorderLayout.NORTH);

		//
		userId = new JTextField();
		passWd = new JPasswordField();
		btnOk = new JButton("登录");

		JForm form = new JForm(7, 10);
		form.setAutoCreateContainerGaps(true);
		form.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));
		form.addFormItem("用户名", userId);
		form.addFormItem("密码", passWd);

		JPanel formControlbar = new JPanel(new FlowLayout(FlowLayout.CENTER));
		formControlbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		form.addControlbar(formControlbar).add(btnOk);

		getContentPane().add(form, BorderLayout.CENTER);

		// copyright
		if (!"true".equalsIgnoreCase(configure.getProperty("guif.copyright.hide"))) {
			getContentPane().add(new Copyright(), BorderLayout.SOUTH);
		}

		// resize & location
		pack();

		// 窗口默认位置、尺寸
		int w = getWidth();
		int h = getHeight();

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(scrSize.width / 2 - w / 2, scrSize.height / 2 - h / 2, w, h);

		// association actionListener
		userId.addKeyListener(new NextFocusAction(passWd));
		passWd.addKeyListener(new NextFocusAction(btnOk));
		btnOk.addActionListener(new BtnOkAction());
	}

	/**
	 * 按回车键，让下一个组件获得焦点
	 */
	class NextFocusAction extends KeyAdapter {

		private JComponent nextFocusComp;

		public NextFocusAction(JComponent nextFocusComp) {
			this.nextFocusComp = nextFocusComp;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			super.keyPressed(e);

			if (KeyEvent.VK_ENTER == e.getKeyCode()) {
				nextFocusComp.requestFocus();

				// 如果下一个获得焦点的组件是按钮，则立即触发按钮动作
				if (nextFocusComp instanceof JButton && ((JButton) nextFocusComp).getActionListeners() != null) {
					ActionListener[] actionListeners = ((JButton) nextFocusComp).getActionListeners();

					for (ActionListener listener : actionListeners) {
						listener.actionPerformed(new ActionEvent(nextFocusComp, ActionEvent.ACTION_FIRST, "click"));
					}
				}
			}
		}
	}

	/**
	 * 点击“登录”按钮的动作处理
	 */
	class BtnOkAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent event) {

			if (authenticator.execute(userId.getText(), new String(passWd.getPassword()))) {

				// 登录成功，关闭登录对话框
				LoginDialog.this.dispose();

				// 事件
				authenticator.afterAuthenticated();
			} else {
				JOptionPane.showMessageDialog(LoginDialog.this, "您输入的用户名或密码错误");
			}
		}
	}
}
