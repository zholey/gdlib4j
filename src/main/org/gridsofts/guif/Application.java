/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.gridsofts.guif.Menubar.Action;
import org.gridsofts.guif.event.EventObject;
import org.gridsofts.guif.itf.IAuthentication;
import org.gridsofts.guif.itf.IDataProvider;
import org.gridsofts.guif.itf.IDiscoveryListener;
import org.gridsofts.guif.itf.IMenuListener;
import org.gridsofts.guif.itf.INode;
import org.gridsofts.guif.itf.IWindowListener;
import org.gridsofts.swing.JCloseableTabbedPane;
import org.gridsofts.util.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用程序入口
 * 
 * @author lei
 */
public class Application extends JFrame implements IMenuListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static Application _instance;

	private ExecutorService _asyncService;

	private Menubar menubar;

	private Statusbar statusbar;

	private JSplitPane splitPane;
	private JCloseableTabbedPane workbenchPane;

	private DiscoveryTree discovery;

	/*******/
	private Configure configure;
	private IAuthentication authenticator = null;
	private IWindowListener windowListener = null;

	private IDiscoveryListener discoveryListener = null;

	public Application(Configure configure) {
		_instance = this;
		_asyncService = Executors.newSingleThreadExecutor();

		this.configure = configure;

		_initComponents();
	}

	public Application setAuthenticator(IAuthentication authenticator) {
		this.authenticator = authenticator;
		return this;
	}

	public Application setWindowListener(IWindowListener listener) {
		windowListener = listener;
		return this;
	}

	public Application setDiscoveryListener(IDiscoveryListener listener) {
		discoveryListener = listener;
		return this;
	}

	public Application setDiscoveryProvider(IDataProvider<INode> dataProvider) {
		discovery.setDataProvider(dataProvider);
		return this;
	}

	public Menubar addMenuItem(String menuName, String itemName, Action action) {
		return menubar.addMenuItem(menuName, itemName, action);
	}

	/**
	 * 启动应用程序
	 */
	public void rockroll() {

		// authentication
		if (authenticator != null) {
			new LoginDialog(authenticator, configure).setVisible(true);

			try {
				logger.debug("需要验证用户身份，等待登录完成 ...");
				
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
				logger.error("主线程无法转入等待状态", e);
				
				System.exit(-1);
			}
		}

		// association listener
		if (discoveryListener != null) {
			discovery.addDiscoveryListener(discoveryListener);
		}

		if (windowListener == null) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent event) {

					if (windowListener.windowClosing()) {
						super.windowClosing(event);
					}
				}
			});
		}

		// show
		setVisible(true);

		// init data
		discovery.refresh();
	}

	/**
	 * 初始化应用程序窗口
	 */
	private void _initComponents() {

		// 窗口默认位置、尺寸
		int w = configure.getInt("guif.width", 1000);
		int h = configure.getInt("guif.height", 700);

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(scrSize.width / 2 - w / 2, scrSize.height / 2 - h / 2, w, h);

		// 窗口属性
		setTitle(configure.getProperty("guif.title", "Application"));

		// 布局管理器
		getContentPane().setLayout(new BorderLayout());

		// 菜单栏
		setJMenuBar(menubar = Menubar.getInstance());
		menubar.addMenuListener(this);

		// 分割容器
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		getContentPane().add(splitPane, BorderLayout.CENTER);

		// 左侧树面板
		splitPane.add(discovery = new DiscoveryTree());
		discovery.refresh();

		// 右侧主容器
		workbenchPane = new JCloseableTabbedPane();
		splitPane.add(workbenchPane);

		// 状态栏
		getContentPane().add(statusbar = Statusbar.getInstance(), BorderLayout.SOUTH);
	}

	/**
	 * 在工作台显示工作面板
	 * 
	 * @param tabbedComp
	 */
	protected void appendToWorkbench(JComponent tabbedComp) {

		int tabIndex = workbenchPane.indexOfComponent(tabbedComp);
		if (tabIndex < 0) {
			workbenchPane.addCloseableTab(tabbedComp.getName(), tabbedComp);
		}

		// 选中页签并更新标题
		workbenchPane.setSelectedComponent(tabbedComp);
		workbenchPane.setTitleAt(workbenchPane.indexOfComponent(tabbedComp), tabbedComp.getName());
	}

	/**
	 * 弹出一个模态对话框
	 * 
	 * @param dialogContentComp
	 */
	protected void popupModalDialog(JComponent dialogContentComp) {
		JDialog dialog = new JDialog(_instance, dialogContentComp.getName(), true);
		dialog.add(dialogContentComp);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.setVisible(true);
	}

	/*
	 * 用户点击菜单项
	 * 
	 * @see
	 * org.gridsofts.guif.itf.IMenuListener#menuItemClicked(org.gridsofts.guif.
	 * event.EventObject)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void menuItemClicked(EventObject<Menubar.Action> event) {
		Object actionObj = event.getPayload().getAction();

		switch (event.getPayload().getType()) {
		case COMMAND:

			if (actionObj != null && Runnable.class.isAssignableFrom(actionObj.getClass())) {
				_asyncService.execute((Runnable) actionObj);
			} else if (actionObj != null && Class.class.isAssignableFrom(actionObj.getClass())) {
				
				try {
					_asyncService.execute((Runnable) ((Class) actionObj).newInstance());
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
			break;
		case WORKBENCH:

			if (actionObj != null && JComponent.class.isAssignableFrom(actionObj.getClass())) {
				appendToWorkbench((JComponent) actionObj);
			} else if (actionObj != null && Class.class.isAssignableFrom(actionObj.getClass())) {
				
				try {
					appendToWorkbench((JComponent) ((Class) actionObj).newInstance());
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
			break;
		case DIALOG:

			if (actionObj != null && JComponent.class.isAssignableFrom(actionObj.getClass())) {
				popupModalDialog((JComponent) actionObj);
			} else if (actionObj != null && Class.class.isAssignableFrom(actionObj.getClass())) {
				
				try {
					popupModalDialog((JComponent) ((Class) actionObj).newInstance());
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
	}
}
