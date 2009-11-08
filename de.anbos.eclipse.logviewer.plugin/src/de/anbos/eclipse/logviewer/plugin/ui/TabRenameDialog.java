package de.anbos.eclipse.logviewer.plugin.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;

/*
 * Copyright (c) 2007 - 2011 by Michael Mimo Moratti
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

public class TabRenameDialog extends Dialog {

	// Attribute ---------------------------------------------------------------

    private Text nameField;

	private String oldValue;
	private String value;
	
	private boolean valueChanged;
	
	// Constructor -------------------------------------------------------------
	/**
	 * @param parentShell
	 */
	public TabRenameDialog(Shell parentShell, String currentTabName) {
		super(parentShell);
		this.oldValue = currentTabName;
	}
	
	// Protected ---------------------------------------------------------------
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			value = nameField.getText();
			if(!value.equals(oldValue)) {
				valueChanged = true;
			} else {
				valueChanged = false;
			}
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
		shell.setText(LogViewerPlugin.getResourceString("dialog.tabrename.title")); //$NON-NLS-1$
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
		//do this here because setting the text will set enablement on the ok
		// button
		nameField.setFocus();
		if (value != null) {
			nameField.setText(value);
		}
	}
	
	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create text input field
		nameField = new Text(composite,SWT.LEFT);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		nameField.setLayoutData(data);
		// fill the text with the current tab name
		nameField.setText(oldValue);
		applyDialogFont(composite);
		return composite;
	}
	
	// Public ------------------------------------------------------------------
	
	public boolean isNewValue() {
		return valueChanged;
	}
	
	public String getValue() {
		return value;
	}}
