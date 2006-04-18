package ch.mimo.eclipse.plugin.logfiletools.preferences.color;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;
import ch.mimo.eclipse.plugin.logfiletools.viewer.rule.RuleFactory;

/*
 * Copyright (c) 2006 by Michael Mimo Moratti
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

public class ColorDialog extends StatusDialog {

    // Attribute --------------------------------------------------------------------
    
    private ColorPreferenceData data;
    private boolean edit;
    
//    private Text priority;
    private Button status;
    private CCombo ruleTypeCombo;
    private ColorSelector backgroundColorSelector;
    private ColorSelector foregroundColorSelector;
    private Text text;
    
    // Constructor ------------------------------------------------------------------
    
    public ColorDialog(Shell parent, ColorPreferenceData data, boolean edit) {
        super(parent);
        this.data = data;
        this.edit = edit;
        // do layout and title
        setShellStyle(getShellStyle() | SWT.MAX);
        // set title
        String title = null;
        if(edit) {
            title = LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.edit.title"); //$NON-NLS-1$
        } else {
            title = LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.new.title"); //$NON-NLS-1$
        }        
        setTitle(title);
    }

    
    // Public -----------------------------------------------------------------------
    
    public Control createDialogArea(Composite parent) {
        // define default grid
        Composite pageComponent = new Composite(parent,SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = true;
        layout.marginWidth = 5;
        layout.marginHeight = 4;
        pageComponent.setLayout(layout);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        pageComponent.setLayoutData(data);
        pageComponent.setFont(parent.getFont());
        // create priority text
//        createPriorityText(pageComponent);
        // create activity checkbox
        createCheckBox(pageComponent);
        // create rule type combo
        createRuleCombo(pageComponent);
        // create background color button
        createBackgroundColorSelector(pageComponent);
        // create foreground color button
        createForegroundColorSelector(pageComponent);
        // create input text field
        createValueTextField(pageComponent);
        return pageComponent;
    }
    
    // Protected --------------------------------------------------------------------
    
    protected void okPressed() {
        int position = -1;
//        try {
//            position = Integer.parseInt(priority.getText());
//        } catch (NumberFormatException e) {
//        	MessageDialog.openError(getShell(),
//        			LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.error.incompletedata.title"), //$NON-NLS-1$
//        			LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.error.priority.text")); //$NON-NLS-1$
//            return;
//        }

        if(ruleTypeCombo.getText() == null || ruleTypeCombo.getText().length() <= 0) {
        	MessageDialog.openError(getShell(),
        			LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.error.incompletedata.title"), //$NON-NLS-1$
        			LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.error.rule.text")); //$NON-NLS-1$
            return;
        }
        if(text.getText() == null || text.getText().length() <= 0) {
        	MessageDialog.openError(getShell(),
        			LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.error.incompletedata.title"), //$NON-NLS-1$
        			LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.error.value.text")); //$NON-NLS-1$
            return;
        }
        data.setPosition(position);
        data.setChecked(status.getSelection());
        data.setRule(ruleTypeCombo.getText());
        data.setBackground(backgroundColorSelector.getColorValue());
        data.setForeground(foregroundColorSelector.getColorValue());
        data.setValue(text.getText());
        super.okPressed();
    }
    
    // Private ----------------------------------------------------------------------
    
//    private void createPriorityText(Composite parent) {
//        // draw label
//        Label comboLabel = new Label(parent,SWT.LEFT);
//        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//        comboLabel.setText(LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.priority.label"));    //$NON-NLS-1$
//        // draw textfield
//        priority = new Text(parent,SWT.BORDER);
//        priority.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        if(edit) {
//            priority.setText(Integer.toString(this.data.getPosition()));
//        } else {
//            priority.setText("0"); //$NON-NLS-1$
//        }
//    }
    
    private void createCheckBox(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.active.label")); //$NON-NLS-1$
        // draw checkbox
        status = new Button(parent,SWT.CHECK);
        if(edit) {
            status.setSelection(this.data.isChecked());
        }
    }
    
    private void createRuleCombo(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.combo.label")); //$NON-NLS-1$
        // draw combo
        ruleTypeCombo = new CCombo(parent,SWT.BORDER);
        ruleTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ruleTypeCombo.setEditable(false);
        ruleTypeCombo.setItems(RuleFactory.getAllRulesAsComboNames());
        if(edit) {
            String[] items = ruleTypeCombo.getItems();
            for(int i = 0 ; i < items.length ; i++) {
                if(items[i].equals(this.data.getRule())) {
                    ruleTypeCombo.select(i);
                    return;
                }
            }
        }
    }
    
    private void createBackgroundColorSelector(Composite parent) {
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.background.label")); //$NON-NLS-1$
        // draw selector
        backgroundColorSelector = new ColorSelector(parent);
        backgroundColorSelector.setColorValue(new RGB(255,255,255));
        backgroundColorSelector.getButton().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        if(edit) {
            backgroundColorSelector.setColorValue(this.data.getBackground());
        }
    }
    
    private void createForegroundColorSelector(Composite parent) {
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.foreground.label")); //$NON-NLS-1$
        // draw selector
        foregroundColorSelector = new ColorSelector(parent);
        foregroundColorSelector.setColorValue(new RGB(0,0,0));
        foregroundColorSelector.getButton().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        if(edit) {
            foregroundColorSelector.setColorValue(this.data.getForeground());
        }      
    }
    
    private void createValueTextField(Composite parent) {
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(LogFileViewPlugin.getResourceString("preferences.coloringeditor.dialog.value.label"));    //$NON-NLS-1$
        // draw textfield
        text = new Text(parent,SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if(edit) {
            text.setText(this.data.getValue());
        }
    }
}
