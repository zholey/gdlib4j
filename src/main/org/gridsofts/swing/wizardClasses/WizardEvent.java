/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing.wizardClasses;

import org.gridsofts.event.Event;
import org.gridsofts.event.EventType;
import org.gridsofts.swing.JWizardDialog;

public class WizardEvent extends Event {
	private static final long serialVersionUID = 1L;

	public static final EventType<WizardEvent> Action = new EventType<>(WizardEvent.class, "WizardAction");

	public WizardEvent(JWizardDialog source) {
		super(source);
	}
}
