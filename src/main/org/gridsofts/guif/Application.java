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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import org.gridsofts.guif.Menubar.Action;
import org.gridsofts.guif.itf.IAuthentication;
import org.gridsofts.guif.itf.IDataChangedListener;
import org.gridsofts.guif.itf.IDataProvider;
import org.gridsofts.guif.itf.IMenuListener;
import org.gridsofts.guif.itf.IWindowListener;
import org.gridsofts.guif.support.Session;
import org.gridsofts.swing.JCloseableTabbedPane;
import org.gridsofts.swing.treeClasses.ITreeListener;
import org.gridsofts.swing.treeClasses.ITreeNode;
import org.gridsofts.util.Configure;
import org.gridsofts.util.EventObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用程序入口
 * 
 * @author lei
 */
public class Application extends JFrame implements IMenuListener, IDataChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(Application.class);

	public static Application _instance;

	private ExecutorService _asyncService;

	private Menubar menubar;

	private Statusbar statusbar;

	private JSplitPane splitPane;
	private JCloseableTabbedPane workbenchPane;
	private Map<String, JComponent> _workbenchMap;

	private DiscoveryTree discovery;

	/*******/
	private Configure configure;
	private IAuthentication authenticator = null;
	private IWindowListener windowListener = null;

	private ITreeListener discoveryListener = null;

	/******/
	private final Session session = new Session();

	public Application(Configure configure) {
		_instance = this;
		_asyncService = Executors.newSingleThreadExecutor();
		_workbenchMap = new HashMap<String, JComponent>();

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

	public Application setDiscoveryListener(ITreeListener listener) {
		discoveryListener = listener;
		return this;
	}

	public Application setDiscoveryProvider(IDataProvider<ITreeNode> dataProvider) {

		if (dataProvider != null) {
			discovery.setDataProvider(dataProvider);
			dataProvider.addEventListener(this);
		}
		return this;
	}

	public JMenuItem addMenuItem(String menuName, String itemName, Action action) {
		return menubar.addMenuItem(menuName, itemName, action);
	}

	public JMenu addMenuSeparator(String menuName) {
		return menubar.addMenuSeparator(menuName);
	}

	public Session getSession() {
		return session;
	}

	/**
	 * 启动应用程序
	 */
	public void rockroll() {

		// authentication
		if (authenticator != null) {
			new LoginDialog(authenticator, configure, getIconImage()).setVisible(true);

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

		// association listener
		if (discoveryListener != null) {
			discovery.addTreeListener(discoveryListener);
		}

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
		splitPane.setLeftComponent(discovery = new DiscoveryTree());
		discovery.refresh();

		// 右侧主容器
		splitPane.setRightComponent(workbenchPane = new JCloseableTabbedPane());

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

	@Override
	public void onDataChanged(EventObject<?> event) {

		// 判断当前的数据更新事件源
		if (event.getSource() == discovery.getDataProvider()) {
			discovery.refresh();
		}
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
		
		if (actionObj == null) {
			throw new IllegalArgumentException("action should be not null");
		}

		switch (event.getPayload().getType()) {
		case COMMAND:

			if (Runnable.class.isAssignableFrom(actionObj.getClass())) {
				_asyncService.execute((Runnable) actionObj);
			} else if (Class.class.isAssignableFrom(actionObj.getClass())) {

				try {
					_asyncService.execute((Runnable) ((Class) actionObj).newInstance());
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
			break;
		case WORKBENCH:
			
			JComponent workbenchPane = null;
			// 查找缓存的工作台面板
			if (_workbenchMap.containsKey(actionObj.toString())) {
				workbenchPane = _workbenchMap.get(actionObj.toString());
			} else {
				
				if (JComponent.class.isAssignableFrom(actionObj.getClass())) {
					workbenchPane = (JComponent) actionObj;
				} else if (Class.class.isAssignableFrom(actionObj.getClass())
						&& JComponent.class.isAssignableFrom((Class<?>) actionObj)) {

					try {
						workbenchPane = (JComponent) ((Class) actionObj).newInstance();
					} catch (Throwable e) {
					}
				}
				
				_workbenchMap.put(actionObj.toString(), workbenchPane);
			}
			
			if (workbenchPane != null) {
				appendToWorkbench(workbenchPane);
			}
			
			break;
		case DIALOG:

			if (JComponent.class.isAssignableFrom(actionObj.getClass())) {
				popupModalDialog((JComponent) actionObj);
			} else if (Class.class.isAssignableFrom(actionObj.getClass())) {

				try {
					popupModalDialog((JComponent) ((Class) actionObj).newInstance());
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
	}
}
