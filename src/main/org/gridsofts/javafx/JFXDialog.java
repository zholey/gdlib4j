/*
 * 版权所有 ©2011-2016 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Window;

/**
 * 为JavaFX框架程序提供方便的消息弹出框工具
 * 
 * @author lei
 */
public final class JFXDialog {

	/**
	 * 显示消息提示对话框
	 * 
	 * @param owner
	 * @param title
	 * @param message
	 * @param modality
	 */
	public static void showMessageDialog(Window owner, String title, String message, Modality modality) {
		Alert dlg = new Alert(AlertType.INFORMATION, title);
		
		dlg.initOwner(owner);
		dlg.initModality(modality);
		dlg.setContentText(message);
		dlg.showAndWait();
	}
}
