/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.guif;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.gridsofts.guif.event.EventObject;
import org.gridsofts.guif.itf.IMenuListener;
import org.gridsofts.util.EventDispatcher;

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

	/** 事件驱动 器 */
	private EventDispatcher<IMenuListener, EventObject<Action>> evtDispatcher;

	private Map<String, JMenu> menuNameMap = new HashMap<String, JMenu>();

	private Menubar() {
		evtDispatcher = new EventDispatcher<IMenuListener, EventObject<Action>>();
	}

	public Menubar addMenuItem(String menuName, String itemName, Action action) {

		JMenu menu = null;
		if (menuNameMap.containsKey(menuName)) {
			menu = menuNameMap.get(menuName);
		} else {
			menuNameMap.put(menuName, menu = add(new JMenu(menuName)));
		}

		JMenuItem menuItem = menu.add(new JMenuItem(itemName));
		menuItem.addActionListener(new MenuItemActionAdapter(action));

		return this;
	}

	public void addMenuListener(IMenuListener listener) {
		evtDispatcher.addEventListener(listener);
	}

	private class MenuItemActionAdapter implements ActionListener {

		private Action action;

		public MenuItemActionAdapter(Action action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			evtDispatcher.dispatchEvent("menuItemClicked", new EventObject<Action>(e.getSource(), action));
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
			
			if (Type.COMMAND == type && !Runnable.class.isAssignableFrom(action.getClass())) {
				throw new IllegalArgumentException("if type == Type.COMMAND, then action is a Runnable");
			}
			
			if (Type.WORKBENCH == type && !Component.class.isAssignableFrom(action.getClass())) {
				throw new IllegalArgumentException("if type == Type.WORKBENCH, then action is a Component");
			}
			
			if (Type.DIALOG == type && !Dialog.class.isAssignableFrom(action.getClass())) {
				throw new IllegalArgumentException("if type == Type.DIALOG, then action is a Dialog");
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
