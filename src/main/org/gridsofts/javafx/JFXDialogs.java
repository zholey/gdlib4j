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
 * @author lei
 */
public final class JFXDialogs {

	public static void showMessageDialog(Window owner, String title, String message, Modality modality) {
		Alert dlg = new Alert(AlertType.INFORMATION, title);
		dlg.initOwner(owner);
		dlg.initModality(modality);
		dlg.setContentText(message);
		dlg.showAndWait();
	}
}
