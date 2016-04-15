/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.gridsofts.swing.wizardClasses.IWizardListener;
import org.gridsofts.swing.wizardClasses.WizardEvent;
import org.gridsofts.swing.wizardClasses.WizardStep;
import org.gridsofts.util.EventDispatcher;

/**
 * 向导面板
 * 
 * @author lei
 */
public abstract class JWizardDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private EventDispatcher<IWizardListener, WizardEvent> eventDispatcher;

	private JToolBar toolBar;
	private JPanel contentPane;

	private JButton btnFirst;
	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnComplete;
	private JButton btnCancel;

	private List<WizardStep> history;
	private WizardStep firstStep;
	private WizardStep currentStep;
	private int currentStepIndex;

	private String title;

	public JWizardDialog(JFrame owner, String title) {
		super(owner, title, true);
		this.title = title;

		eventDispatcher = new EventDispatcher<>();

		history = new ArrayList<>();

		// UI
		contentPane = new JPanel(new BorderLayout());
		add(contentPane, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				eventDispatcher.dispatchEvent("wizardCancel", new WizardEvent(JWizardDialog.this));
			}
		});

		// 控制栏
		initCtrlBar();
	}

	public void addWizardListener(IWizardListener l) {
		eventDispatcher.addEventListener(l);
	}

	private void initCtrlBar() {

		JPanel ctrlBar = new JPanel(new BorderLayout());
		contentPane.add(ctrlBar, BorderLayout.SOUTH);

		ctrlBar.add(new JSeparator(), BorderLayout.NORTH);

		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ctrlBar.add(toolBar, BorderLayout.EAST);

		btnFirst = new JButton("第一步");
		toolBar.add(btnFirst);
		btnFirst.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				setCurrentStep(0);
			}
		});

		toolBar.addSeparator(new Dimension(10, 0));

		btnPrevious = new JButton("上一步");
		toolBar.add(btnPrevious);
		btnPrevious.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (currentStepIndex > 0) {
					setCurrentStep(currentStepIndex - 1);
				}
			}
		});

		btnNext = new JButton("下一步");
		toolBar.add(btnNext);
		btnNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				// 在进行下一步之前，要检查当前步骤是否已经符合要求
				if (currentStep != null && currentStep.preComplete()) {
					onNext();
				}
			}
		});

		toolBar.addSeparator(new Dimension(10, 0));

		btnComplete = new JButton("完成");
		toolBar.add(btnComplete);
		btnComplete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				if (currentStep != null && currentStep.preComplete() && preComplete()) {
					eventDispatcher.dispatchEvent("wizardComplete", new WizardEvent(JWizardDialog.this));
					dispose();
				}
			}
		});

		toolBar.addSeparator(new Dimension(10, 0));

		btnCancel = new JButton("取消");
		toolBar.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				eventDispatcher.dispatchEvent("wizardCancel", new WizardEvent(JWizardDialog.this));
				dispose();
			}
		});
	}

	private synchronized void onNext() {

		WizardStep step = history.get(currentStepIndex);
		int historySize = history.size();

		if (step.isLastStep() || step.getNextStepClass() == null) {
			return;
		}

		if (currentStepIndex < historySize - 1
				&& step.getNextStepClass().equals(history.get(currentStepIndex + 1).getClass())) {

			setCurrentStep(currentStepIndex + 1);

		} else {

			WizardStep newStep = null;

			try {
				newStep = step.getNextStepClass().newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}

			if (currentStepIndex < historySize - 1) {
				history.remove(currentStepIndex + 1);
				history.add(currentStepIndex + 1, newStep);
			} else {
				history.add(newStep);
			}

			setCurrentStep(currentStepIndex + 1);
		}
	}

	private void setCurrentStep(int index) {

		currentStepIndex = index;

		setTitle(title + " - 第 " + (currentStepIndex + 1) + " 步");

		if (currentStep != null) {
			contentPane.remove((Component) currentStep);
		}

		currentStep = history.get(currentStepIndex);

		if (currentStep instanceof Component) {
			contentPane.add((Component) currentStep, BorderLayout.CENTER);
		}

		// 判断控制按钮可用性
		btnFirst.setEnabled(false);
		btnPrevious.setEnabled(false);
		btnNext.setEnabled(false);
		btnComplete.setEnabled(false);

		if (index > 0) {
			btnFirst.setEnabled(true);
			btnPrevious.setEnabled(true);
		}

		if (currentStep.isLastStep()) {
			btnComplete.setEnabled(true);
		} else {
			btnNext.setEnabled(true);
		}

		contentPane.updateUI();

		// 抛出步骤变更事件
		eventDispatcher.dispatchEvent("stepChanged", new WizardEvent(this));
	}

	public List<WizardStep> getStepList() {
		return history;
	}

	public WizardStep getCurrentStep() {

		if (currentStepIndex < history.size()) {
			return history.get(currentStepIndex);
		}

		return null;
	}

	public WizardStep getFirstStep() {
		return firstStep;
	}

	public void setFirstStep(WizardStep step) {
		firstStep = step;

		history.removeAll(history);
		history.add(firstStep);

		setCurrentStep(0);
	}

	/**
	 * 在向导完成之前调用，用以检查所有步骤是否已经符合要求
	 * 
	 * @return
	 */
	protected abstract boolean preComplete();
}
