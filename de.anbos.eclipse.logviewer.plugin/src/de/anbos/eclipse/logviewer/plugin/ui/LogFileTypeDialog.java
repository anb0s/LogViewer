/*
 * Copyright 2009 - 2010 by Andre Bossert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package de.anbos.eclipse.logviewer.plugin.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

public class LogFileTypeDialog extends Dialog {

	// Attribute ---------------------------------------------------------------

	private Combo typeCombo;
	private String value;
	
	// Constructor -------------------------------------------------------------
	/**
	 * @param parentShell
	 */
	public LogFileTypeDialog(Shell parentShell) {
		super(parentShell);
	}
	
	// Protected ---------------------------------------------------------------
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			value = typeCombo.getText();
		} else {
			value = null;
		}
		super.buttonPressed(buttonId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Select Log File Type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID,IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
		// button
		typeCombo.setFocus();
		if (value != null) {
			typeCombo.setText(value);
		}
	}

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
    	String fileTypeName = "File";
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create combo
		typeCombo = new Combo(composite,SWT.LEFT);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		typeCombo.setLayoutData(data);
		// fill
    	IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
    	typeCombo.add(fileTypeName);
    	value = fileTypeName;    	
    	for (int i=0;i<consoles.length;i++) {
    		typeCombo.add("Console: " + consoles[i].getName());
    	}
		applyDialogFont(composite);
		return composite;
	}

	// Public ------------------------------------------------------------------
	
	public String getValue() {
		return value;
	}
}
