/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.gridsofts.guif.event.MenuEvent;

/**
 * 系统菜单栏
 * 
 * @author lei
 */
public class Menubar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	private static class SingletonHolder {
		private static Menubar instance = new Menubar();
	}

	public static Menubar getInstance() {
		return SingletonHolder.instance;
	}

	private Map<String, JMenu> menuNameMap = new HashMap<>();

	private Menubar() {
	}

	public JMenuItem addMenuItem(String menuName, String itemName, Action action) {

		JMenu menu = null;
		if (menuNameMap.containsKey(menuName)) {
			menu = menuNameMap.get(menuName);
		} else {
			menuNameMap.put(menuName, menu = add(new JMenu(menuName)));
		}

		JMenuItem menuItem = menu.add(new JMenuItem(itemName));
		menuItem.addActionListener(new MenuItemActionAdapter(action));

		return menuItem;
	}

	public JMenu addMenuSeparator(String menuName) {

		JMenu menu = null;
		if (menuNameMap.containsKey(menuName)) {
			menu = menuNameMap.get(menuName);
		} else {
			menuNameMap.put(menuName, menu = add(new JMenu(menuName)));
		}

		menu.addSeparator();

		return menu;
	}

	private class MenuItemActionAdapter implements ActionListener {

		private Action action;

		public MenuItemActionAdapter(Action action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EvtDispatcher.getInstance().dispatchEvent(MenuEvent.Action, "menuItemClicked",
					new MenuEvent(e.getSource(), action));
		}
	}

	/**
	 * 菜单指令
	 * 
	 * @author lei
	 */
	public static class Action {

		public static enum Type {
			COMMAND, // 执行一个动作
			WORKBENCH, // 打开一个工作面板
			DIALOG, // 弹出一个对话框
		}

		private Type type;
		private Object action;

		public Action(Type type, Object action) {
			this.type = type;
			this.action = action;

			// check parameters

			if (type == null) {
				throw new IllegalArgumentException("type should be not null");
			}

			if (action == null) {
				throw new IllegalArgumentException("action should be not null");
			}

			if (Type.COMMAND == type && !Runnable.class.isAssignableFrom(action.getClass())
					&& (Class.class.isAssignableFrom(action.getClass())
							&& !Runnable.class.isAssignableFrom((Class<?>) action))) {
				throw new IllegalArgumentException("if type == Type.COMMAND, then action should be a Runnable");
			}

			if (Type.WORKBENCH == type && !Component.class.isAssignableFrom(action.getClass())
					&& (Class.class.isAssignableFrom(action.getClass())
							&& !Component.class.isAssignableFrom((Class<?>) action))) {
				throw new IllegalArgumentException("if type == Type.WORKBENCH, then action should be a Component");
			}

			if (Type.DIALOG == type && !Component.class.isAssignableFrom(action.getClass())
					&& (Class.class.isAssignableFrom(action.getClass())
							&& !Component.class.isAssignableFrom((Class<?>) action))) {
				throw new IllegalArgumentException("if type == Type.DIALOG, then action should be a Component");
			}
		}

		/**
		 * @return the type
		 */
		public Type getType() {
			return type;
		}

		/**
		 * @return the action
		 */
		public Object getAction() {
			return action;
		}
	}
}
