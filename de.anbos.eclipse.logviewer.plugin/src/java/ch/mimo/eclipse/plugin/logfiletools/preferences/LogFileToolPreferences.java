package ch.mimo.eclipse.plugin.logfiletools.preferences;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.mimo.eclipse.plugin.logfiletools.ILogFileViewConstants;
import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;
import ch.mimo.eclipse.plugin.logfiletools.ui.EncodingComboEditor;

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

public class LogFileToolPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	// Attribute ---------------------------------------------------------------
	
	private IntegerFieldEditor backlogEditor;
	private IntegerFieldEditor bufferEditor;
	private IntegerFieldEditor readWaitEditor;
	
	private IPropertyChangeListener validityChangeListener;
	
	private EncodingComboEditor encodingComboEditor;
	private ColorFieldEditor colorFieldEditor;
	private FontFieldEditor fontTypeEditor;
	
	// Public ------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		validityChangeListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) 
					updateValidState();
			}
		};
	}
	
	// Protected ---------------------------------------------------------------
	
	protected Control createContents(Composite parent) {
		
        Composite pageComponent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        pageComponent.setLayout(layout);
        pageComponent.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true));

		Group tailGroup = new Group(pageComponent,SWT.NONE);
		tailGroup.setText(LogFileViewPlugin.getResourceString("preferences.main.tailsettings.title")); //$NON-NLS-1$
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		tailGroup.setLayoutData(data);
		
		createBacklogField(tailGroup);
		createReadBufferField(tailGroup);
		createReadWaitField(tailGroup);
		
		Group viewerGroup = new Group(pageComponent,SWT.NONE);
		viewerGroup.setText(LogFileViewPlugin.getResourceString("preferences.main.viewersettings.title")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		viewerGroup.setLayoutData(data);
		layout = new GridLayout(2,true);
		viewerGroup.setLayout(layout);
		
        createFontSettings(viewerGroup);
        createColorChooser(viewerGroup);
		createEncodingCombo(viewerGroup);
		
		updateValidState();
		
		return pageComponent;
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return LogFileViewPlugin.getDefault().getPreferenceStore();
	}
	
	protected void performDefaults() {
		backlogEditor.loadDefault();
		bufferEditor.loadDefault();
		encodingComboEditor.loadDefault();
		colorFieldEditor.loadDefault();
		fontTypeEditor.loadDefault();
	}
	
	protected void performApply() {
		performOk();
	}
	
	// Public ------------------------------------------------------------------
	
	public boolean performOk() {
		backlogEditor.store();		
		bufferEditor.store();
		encodingComboEditor.store();
		colorFieldEditor.store();
		fontTypeEditor.store();
		return super.performOk();
	}
	
	// Private -----------------------------------------------------------------
	
	private void createBacklogField(Composite composite) {
		
		
		
		backlogEditor = new IntegerFieldEditor(ILogFileViewConstants.PREF_BACKLOG,LogFileViewPlugin.getResourceString("preferences.backlog.label.text"),composite); //$NON-NLS-1$

		backlogEditor.setPreferenceStore(doGetPreferenceStore());
		backlogEditor.setPage(this);
		backlogEditor.setTextLimit(Integer.toString(ILogFileViewConstants.MAX_BACKLOG).length());
		backlogEditor.setErrorMessage(LogFileViewPlugin.getResourceString("preferences.backlog.label.errortext",new Object[]{new Integer(ILogFileViewConstants.MAX_BACKLOG)})); //$NON-NLS-1$
		backlogEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		backlogEditor.setValidRange(0,ILogFileViewConstants.MAX_BACKLOG);
		backlogEditor.load();
		backlogEditor.setPropertyChangeListener(validityChangeListener);
	}
	
	private void createReadBufferField(Composite composite) {
		
		bufferEditor = new IntegerFieldEditor(ILogFileViewConstants.PREF_BUFFER,LogFileViewPlugin.getResourceString("preferences.buffer.label.text"),composite); //$NON-NLS-1$

		bufferEditor.setPreferenceStore(doGetPreferenceStore());
		bufferEditor.setPage(this);
		bufferEditor.setTextLimit(Integer.toString(ILogFileViewConstants.MAX_TAIL_BUFFER_SIZE).length());
		bufferEditor.setErrorMessage(LogFileViewPlugin.getResourceString("preferences.buffer.label.errortext",new Object[]{new Integer(ILogFileViewConstants.MAX_TAIL_BUFFER_SIZE)})); //$NON-NLS-1$
		bufferEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		bufferEditor.setValidRange(0,ILogFileViewConstants.MAX_TAIL_BUFFER_SIZE);
		bufferEditor.load();
		bufferEditor.setPropertyChangeListener(validityChangeListener);
	}
	
	private void createReadWaitField(Composite composite) {
		readWaitEditor = new IntegerFieldEditor(ILogFileViewConstants.PREF_READWAIT,LogFileViewPlugin.getResourceString("preferences.readwait.label.text"),composite); //$NON-NLS-1$
		
		readWaitEditor.setPreferenceStore(doGetPreferenceStore());
		readWaitEditor.setPage(this);
		readWaitEditor.setTextLimit(Integer.toString(ILogFileViewConstants.MAX_READWAIT_SIZE).length());
		readWaitEditor.setErrorMessage(LogFileViewPlugin.getResourceString("preferences.readwait.label.errortext",new Object[]{new Integer(ILogFileViewConstants.MAX_READWAIT_SIZE)})); //$NON-NLS-1$
		readWaitEditor.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		readWaitEditor.setValidRange(0,ILogFileViewConstants.MAX_TAIL_BUFFER_SIZE);
		readWaitEditor.load();
		readWaitEditor.setPropertyChangeListener(validityChangeListener);		
	}
	
	private void updateValidState() {
		if (!backlogEditor.isValid()) {
			setErrorMessage(backlogEditor.getErrorMessage());
			setValid(false);
		} else if (!bufferEditor.isValid()) {
			setErrorMessage(bufferEditor.getErrorMessage());
			setValid(false);			
		} else {
			setValid(true);
		}
	}
	
	private void createEncodingCombo(Composite composite) {
		encodingComboEditor = new EncodingComboEditor(ILogFileViewConstants.PREF_ENCODING,LogFileViewPlugin.getResourceString("preferences.contenteditor.combo.label.text"),composite); //$NON-NLS-1$
		
		encodingComboEditor.setPreferenceStore(doGetPreferenceStore());
		encodingComboEditor.setPage(this);
		encodingComboEditor.load();
	}
	
	private void createColorChooser(Composite composite) {
		colorFieldEditor = new ColorFieldEditor(ILogFileViewConstants.PREF_CURSORLINE_COLOR,LogFileViewPlugin.getResourceString("preferences.contenteditor.cursorline.color.chooser.text"),composite); //$NON-NLS-1$
		
		colorFieldEditor.setPreferenceStore(doGetPreferenceStore());
		colorFieldEditor.setPage(this);
		colorFieldEditor.load();
	}
	
	private void createFontSettings(Composite composite) {
		Composite panel = new Composite(composite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 4;
		panel.setLayout(layout);
		GridData data = new GridData();
		data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = SWT.CENTER;
		panel.setLayoutData(data);
		
		fontTypeEditor = new FontFieldEditor(ILogFileViewConstants.PREF_EDITOR_FONT_STYLE,LogFileViewPlugin.getResourceString("preferences.contenteditor.font.style.text"),panel); //$NON-NLS-1$
		fontTypeEditor.setChangeButtonText(LogFileViewPlugin.getResourceString("preferences.contenteditor.fontl.style.button.text")); //$NON-NLS-1$
		fontTypeEditor.setPreferenceStore(doGetPreferenceStore());
		fontTypeEditor.setPage(this);
		fontTypeEditor.load();
	}
}
