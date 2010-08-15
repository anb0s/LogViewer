package de.anbos.eclipse.logviewer.plugin.preferences.rule;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.RuleFactory;

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

public class RuleDialog extends StatusDialog {

    // Attribute --------------------------------------------------------------------
    
    private RulePreferenceData data;
    private boolean edit;
    
//    private Text priority;
    private Button enabledCheckBox;
    private Button caseInsensitiveCheckBox;
    private Button coloringEnabledCheckBox;
    private CCombo ruleTypeCombo;
    private CCombo matchModeCombo;
    private ColorSelector backgroundColorSelector;
    private ColorSelector foregroundColorSelector;
    private Text valueText;

    // Constructor ------------------------------------------------------------------
    
    public RuleDialog(Shell parent, RulePreferenceData data, boolean edit) {
        super(parent);
        this.data = data;
        this.edit = edit;
        // do layout and title
        setShellStyle(getShellStyle() | SWT.MAX);
        // set title
        String title = null;
        if(edit) {
            title = LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.edit.title"); //$NON-NLS-1$
        } else {
            title = LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.new.title"); //$NON-NLS-1$
        }
        setTitle(title);
    }

    
    // Public -----------------------------------------------------------------------
    
    public Control createDialogArea(Composite parent) {
    	Composite pageComponent = new Composite(parent,SWT.NULL);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
        layout0.makeColumnsEqualWidth = true;
        layout0.marginWidth = 5;
        layout0.marginHeight = 4;
        pageComponent.setLayout(layout0);    	
        GridData data0 = new GridData(GridData.FILL_HORIZONTAL);
        pageComponent.setLayoutData(data0);
        pageComponent.setFont(parent.getFont());
    	// define group1 
    	Group pageGroup1 = new Group(pageComponent, SWT.SHADOW_ETCHED_IN);
    	pageGroup1.setText("Line selection");
        GridLayout layout1 = new GridLayout();
        layout1.numColumns = 2;
        layout1.makeColumnsEqualWidth = true;
        layout1.marginWidth = 5;
        layout1.marginHeight = 4;
        pageGroup1.setLayout(layout1);
        GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
        pageGroup1.setLayoutData(data1);
        pageGroup1.setFont(parent.getFont());
        // create priority valueText
//        createPriorityText(pageComponent);
        // create activity checkbox
        createEnabledCheckBox(pageGroup1);
        // create rule type combo
        createRuleCombo(pageGroup1);
        // create input valueText field
        createValueTextField(pageGroup1);
        // create case insensitive checkbox
        createCaseInsensitiveCheckBox(pageGroup1);
        // create match mode combo
        createMatchModeCombo(pageGroup1);
    	// define group2        
    	Group pageGroup2 = new Group(pageComponent, SWT.SHADOW_ETCHED_IN);
    	pageGroup2.setText("Actions");
        GridLayout layout2 = new GridLayout();
        layout2.numColumns = 1;
        layout2.makeColumnsEqualWidth = true;
        layout2.marginWidth = 5;
        layout2.marginHeight = 4;
        pageGroup2.setLayout(layout2);
        GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
        pageGroup2.setLayoutData(data2);
        pageGroup2.setFont(parent.getFont());
    	// define group2.1
    	Group pageGroup21 = new Group(pageGroup2, SWT.SHADOW_ETCHED_IN);
    	pageGroup21.setText("Highlighting");
        GridLayout layout21 = new GridLayout();
        layout21.numColumns = 2;
        layout21.makeColumnsEqualWidth = true;
        layout21.marginWidth = 5;
        layout21.marginHeight = 4;
        pageGroup21.setLayout(layout21);
        GridData data21 = new GridData(GridData.FILL_HORIZONTAL);
        pageGroup21.setLayoutData(data21);
        pageGroup21.setFont(parent.getFont());
        // enabled
        createColoringEnabledCheckBox(pageGroup21);
        // create background color button
        createBackgroundColorSelector(pageGroup21);
        // create foreground color button
        createForegroundColorSelector(pageGroup21);
    	// define group2.2
        /*
    	Group pageGroup22 = new Group(pageGroup2, SWT.SHADOW_ETCHED_IN);
    	pageGroup22.setText("Filter");
        GridLayout layout22 = new GridLayout();
        layout22.numColumns = 2;
        layout22.makeColumnsEqualWidth = true;
        layout22.marginWidth = 5;
        layout22.marginHeight = 4;
        pageGroup22.setLayout(layout22);
        GridData data22 = new GridData(GridData.FILL_HORIZONTAL);
        pageGroup22.setLayoutData(data22);
        pageGroup22.setFont(parent.getFont());
        */
        
        //if (edit) {
	    	// send event to refresh matchMode
	    	Event event = new Event();
			event.item = null;
			ruleTypeCombo.notifyListeners(SWT.Selection, event);
        //}

        return pageComponent;
    }

    // Protected --------------------------------------------------------------------
    
    protected void okPressed() {
        int position = -1;

        if(ruleTypeCombo.getText() == null || ruleTypeCombo.getText().length() <= 0) {
        	MessageDialog.openError(getShell(),
        			LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.error.incompletedata.title"), //$NON-NLS-1$
        			LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.error.rule.text")); //$NON-NLS-1$
            return;
        }
        if(valueText.getText() == null || valueText.getText().length() <= 0) {
        	MessageDialog.openError(getShell(),
        			LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.error.incompletedata.title"), //$NON-NLS-1$
        			LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.error.value.text")); //$NON-NLS-1$
            return;
        }
        data.setPosition(position);
        data.setEnabled(enabledCheckBox.getSelection());
        data.setRuleNameShort(ruleTypeCombo.getText());
        data.setRuleValue(valueText.getText());
        data.setCaseInsensitive(caseInsensitiveCheckBox.getSelection());
        data.setMatchMode(matchModeCombo.getText());
        data.setColoringEnabled(coloringEnabledCheckBox.getSelection());
        data.setBackgroundColor(backgroundColorSelector.getColorValue());
        data.setForegroundColor(foregroundColorSelector.getColorValue());
        super.okPressed();
    }

    // Private ----------------------------------------------------------------------

    private void createEnabledCheckBox(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.active.label")); //$NON-NLS-1$
        // draw checkbox
        enabledCheckBox = new Button(parent,SWT.CHECK);
        if(edit) {
            enabledCheckBox.setSelection(this.data.isEnabled());
        } else {
        	enabledCheckBox.setSelection(true);
        }
    }

    private void createCaseInsensitiveCheckBox(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.case.label")); //$NON-NLS-1$
        // draw checkbox
        caseInsensitiveCheckBox = new Button(parent,SWT.CHECK);
        if(edit) {
        	caseInsensitiveCheckBox.setSelection(this.data.isCaseInsensitive());
        }
    }

    private void createColoringEnabledCheckBox(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.coloring.label")); //$NON-NLS-1$
        // draw checkbox
        coloringEnabledCheckBox = new Button(parent,SWT.CHECK);
        if(edit) {
        	coloringEnabledCheckBox.setSelection(this.data.isColoringEnabled());
        }
    	// TODO: remove after implementing more filters / actions !!!
    	coloringEnabledCheckBox.setSelection(true);
    	coloringEnabledCheckBox.setEnabled(false);        
    }
    
    private void createRuleCombo(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.combo.label")); //$NON-NLS-1$
        // draw combo
        ruleTypeCombo = new CCombo(parent,SWT.BORDER);
        ruleTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ruleTypeCombo.setEditable(false);
        ruleTypeCombo.setItems(RuleFactory.getAllRulesAsComboNames());
        ruleTypeCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				String text = ruleTypeCombo.getItem(ruleTypeCombo.getSelectionIndex());
				// word / jakarta regexp support only 'find' mode				
				if (text.toLowerCase().indexOf("word")    !=-1 ||
					text.toLowerCase().indexOf("jakarta") !=-1  ) {
					matchModeCombo.setEnabled(false);
					matchModeCombo.select(0);
				}
				else {					
					matchModeCombo.setEnabled(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        if(edit) {
            String[] items = ruleTypeCombo.getItems();
            for(int i = 0 ; i < items.length ; i++) {
                if(items[i].equals(this.data.getRuleNameShort())) {
                    ruleTypeCombo.select(i);
                    return;
                }
            }
        } else {
        	ruleTypeCombo.select(0);
        }
    }

    private void createMatchModeCombo(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.matchmode.label")); //$NON-NLS-1$
        // draw combo
        matchModeCombo = new CCombo(parent,SWT.BORDER);
        matchModeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        matchModeCombo.setEditable(false);
        String[] matchModes = {"Find sequence", "Match complete text"};
        matchModeCombo.setItems(matchModes);
        if(edit) {
            String[] items = matchModeCombo.getItems();
            for(int i = 0 ; i < items.length ; i++) {
                if(items[i].toLowerCase().indexOf(this.data.getMatchMode())!=-1) {
                	matchModeCombo.select(i);
                    return;
                }
            }
        }
    }

    private void createBackgroundColorSelector(Composite parent) {
    	// Fix for issue 38: Cannot enter colors using Mac OS X
    	createEmptyTable(parent,2);    	
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.background.label")); //$NON-NLS-1$
        // draw selector
        backgroundColorSelector = new ColorSelector(parent);
        backgroundColorSelector.setColorValue(new RGB(255,255,255));
        backgroundColorSelector.getButton().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        if(edit) {
            backgroundColorSelector.setColorValue(this.data.getBackgroundColor());
        }
    }

    private void createForegroundColorSelector(Composite parent) {
    	// Fix for issue 38: Cannot enter colors using Mac OS X
    	createEmptyTable(parent,2);
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.foreground.label")); //$NON-NLS-1$
        // draw selector
        foregroundColorSelector = new ColorSelector(parent);
        foregroundColorSelector.setColorValue(new RGB(0,0,0));
        foregroundColorSelector.getButton().setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
        if(edit) {
            foregroundColorSelector.setColorValue(this.data.getForegroundColor());
        }      
    }
    
	// workaround for bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=279840
	// Bug 279840 -  ColorDialog fails to return a selection
	// Eclipse 3.5, Mac OSX with cocoa
	// issue 38: Cannot enter colors using Mac OS X
    protected Table createEmptyTable(Composite composite, int horizontalSpan)
    {
        Table table = new Table(composite, SWT.None);

        GridData gridData = new GridData();
        gridData.horizontalSpan = horizontalSpan;
        gridData.verticalSpan = 1;
        gridData.widthHint = 1;
        gridData.heightHint = 1;
        table.setLayoutData(gridData);
        table.setBackground (composite.getBackground());
        return table;
    }
    
    private void createValueTextField(Composite parent) {
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(LogViewerPlugin.getResourceString("preferences.ruleseditor.dialog.value.label"));    //$NON-NLS-1$
        // draw textfield
        valueText = new Text(parent,SWT.BORDER);
        valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if(edit) {
            valueText.setText(this.data.getRuleValue());
        }
    }
}
