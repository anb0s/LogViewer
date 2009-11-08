package de.anbos.eclipse.logviewer.plugin.preferences.color;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.Logger;
import de.anbos.eclipse.logviewer.plugin.preferences.ColorItemReadWriter;

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

public class ColorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	// Constant ---------------------------------------------------------------------
	
	private static final int TABLE_WIDTH = 400;
	
    // Attribute --------------------------------------------------------------------
    
    private Logger logger;
    private Table table;
    private ItemMover itemMover;
    private ColorStore store;
    private CheckboxTableViewer tableViewer;

    private Button addButton;
    private Button editButton;
    private Button upButton;
    private Button downButton;
    private Button importButton;
    private Button exportButton;
    private Button removeButton;
    
    // Constructor ------------------------------------------------------------------
    
    // Public -----------------------------------------------------------------------
    
    public void init(IWorkbench workbench) {
        logger = LogViewerPlugin.getDefault().getLogger();
    }
    
    public boolean performOk() {
        store.save();
        return true;
    }
    
    // Protected --------------------------------------------------------------------
    
    protected void performDefaults() {
        store.loadDefault();
        tableViewer.refresh();
    }
    
    protected void performApply() {
    	performOk();
    }
    
    protected Control createContents(Composite parent) {
        Font font = parent.getFont();
        // define default grid
        Composite pageComponent = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        pageComponent.setLayout(layout);
        
        // list
        GridData data = new GridData(GridData.FILL_BOTH);
        // create table
        table = new Table(pageComponent,SWT.MULTI | SWT.CHECK | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setFont(parent.getFont());
        
        TableColumn column1 = new TableColumn(table,SWT.LEFT);
        column1.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.table.header.column0.title")); //$NON-NLS-1$
        column1.setResizable(false);

        TableColumn column2 = new TableColumn(table,SWT.LEFT);
        column2.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.table.header.column1.title")); //$NON-NLS-1$
        column2.setResizable(false);     
        
        int availableRows = availableRows(pageComponent);
        data.heightHint = table.getItemHeight()* (availableRows / 8);
        data.widthHint = TABLE_WIDTH;
        table.setLayoutData(data);
        
        new TableItemColorController(table);
        
        tableViewer = new CheckboxTableViewer(table);
        tableViewer.setLabelProvider(new ColorLableProvider());
        tableViewer.setContentProvider(new ColorContentProvider());
        store = new ColorStore(LogViewerPlugin.getDefault().getPreferenceStore());
        store.load();
        tableViewer.setInput(store);
        tableViewer.setAllChecked(false);
        tableViewer.setCheckedElements(store.getAllCheckedColorDetails());
        
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                edit();
            }
        });
        
        tableViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                ColorPreferenceData data = (ColorPreferenceData)event.getElement();
                data.setChecked(event.getChecked());
            }
        });
        
        tableViewer.setSorter(new ViewerSorter() {
            public int compare(Viewer viewer, Object object1, Object object2) {
                if(!(object1 instanceof ColorPreferenceData) || !(object2 instanceof ColorPreferenceData)) {
                    return super.compare(viewer,object1,object2);
                }
                ColorPreferenceData data1 = (ColorPreferenceData)object1;
                ColorPreferenceData data2 = (ColorPreferenceData)object2;
                if(data1.getPosition() > data2.getPosition()) {
                    return 1;
                }
                if(data1.getPosition() < data2.getPosition()) {
                    return -1;
                }
                if(data1.getPosition() == data2.getPosition()) {
                    return 0;
                }
                return super.compare(viewer, object1, object2);
            }

            public boolean isSorterProperty(Object element, String property) {
                return true;
            }
        });
        
        itemMover = new ItemMover(table,store);
        
        // button pageComponent
        Composite groupComponent= new Composite(pageComponent, SWT.NULL);
        GridLayout groupLayout = new GridLayout();
        groupLayout.marginWidth = 0;
        groupLayout.marginHeight = 0;
        groupComponent.setLayout(groupLayout);
        data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        groupComponent.setLayoutData(data);
        groupComponent.setFont(font);
        
        // buttons
        addButton = new Button(groupComponent, SWT.PUSH);
        addButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.add")); //$NON-NLS-1$
        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                add();
            }
        });
        addButton.setLayoutData(data);
        addButton.setFont(font);
        setButtonLayoutData(addButton);
        
        editButton = new Button(groupComponent, SWT.PUSH);
        editButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.edit")); //$NON-NLS-1$
        editButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                edit();
            }
        });
        editButton.setLayoutData(data);
        editButton.setFont(font);
        setButtonLayoutData(editButton);
        
        removeButton = new Button(groupComponent, SWT.PUSH);
        removeButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.remove")); //$NON-NLS-1$
        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                remove();
            }
        });
        removeButton.setFont(font);
        setButtonLayoutData(removeButton);
        
        upButton = new Button(groupComponent, SWT.PUSH);
        upButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.up")); //$NON-NLS-1$
        upButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                up();
            }
        });
        upButton.setFont(font);
        setButtonLayoutData(upButton);
        
        downButton = new Button(groupComponent, SWT.PUSH);
        downButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.down")); //$NON-NLS-1$
        downButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                down();
            }
        });
        downButton.setFont(font);
        setButtonLayoutData(downButton);
        
        exportButton = new Button(groupComponent, SWT.PUSH);
        exportButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.export")); //$NON-NLS-1$
        exportButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	export();
            }
        });      
        exportButton.setFont(font);
        setButtonLayoutData(exportButton);
        
        importButton = new Button(groupComponent, SWT.PUSH);
        importButton.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.button.import")); //$NON-NLS-1$
        importButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                inport();
            }
        });      
        importButton.setFont(font);
        setButtonLayoutData(importButton);
        
        configureTableResizing(table);
        
        Dialog.applyDialogFont(pageComponent);
        // trigger the resize
        table.getHorizontalBar().setVisible(true);
        
        return pageComponent;
    }
    
    // Private ----------------------------------------------------------------------
    
    private int availableRows(Composite parent) {
        int fontHeight = (parent.getFont().getFontData())[0].getHeight();
        int displayHeight = parent.getDisplay().getClientArea().height;
        return displayHeight / fontHeight;
    }
    
    /**
     * Correctly resizes the table so no phantom columns appear
     * 
     * @param table the table
     * @since 3.1
     */
    private void configureTableResizing(final Table table) {
            ControlAdapter resizer= new ControlAdapter() {
                private boolean fIsResizing= false;
//                private final int[] fWidths= {100, 70, 70, 130, 45};
                private final int[] fWidths = {120,280};
                private int fSum= TABLE_WIDTH;
                public void controlResized(ControlEvent e) {
                    if (fIsResizing)
                        return;
                    try {
                        fIsResizing= true;
                        int clientAreaWidth= table.getClientArea().width;
                        TableColumn[] columns= table.getColumns();
                        int calculatedtableWidth= 0;
                        
                        if (e.widget == table) {
                            int initial[]= {120,280};
                            int minimums[]= new int[columns.length];
                            int minSum= 0;
                            for (int i= 0; i < columns.length; i++) {
                                // don't make a column narrower than the minimum, 
                                // or than what it is currently if less than the minimum
                                minimums[i]= Math.min(fWidths[i], initial[i]);
                                minSum+= minimums[i];
                            }
                            
                            int newWidth= fSum < clientAreaWidth ? clientAreaWidth : Math.max(clientAreaWidth, minSum);
                            final int toDistribute= newWidth - fSum;
                            int lastPart= toDistribute;
                            if (toDistribute != 0) {
                                int[] iteration= {0,1}; // give the description column all the rest
                                for (int i= 0; i < iteration.length; i++) {
                                    int c= iteration[i];
                                    int width;
                                    if (fSum > 0) {
                                        int part;
                                        if (i == iteration.length - 1)
                                            part= lastPart;
                                        else
                                            // current width is the weight for the distribution of the extra space
                                            part= toDistribute * fWidths[c] / fSum;
                                        lastPart-= part;
                                        width= Math.max(minimums[c], fWidths[c] + part);
                                    } else {
                                        width= toDistribute * initial[c] / TABLE_WIDTH;
                                    }
                                    columns[c].setWidth(width);
                                    fWidths[c]= width;
                                    calculatedtableWidth+= width;
                                }
                                fSum= calculatedtableWidth;
                            }
                        } else {
                            // column being resized
                            // on GTK, the last column gets auto-adapted - ignore this
                            if (e.widget == columns[2])
                                return;
                            for (int i= 0; i < columns.length; i++) {
                                fWidths[i]= columns[i].getWidth();
                                calculatedtableWidth+= fWidths[i];
                            }
                            fSum= calculatedtableWidth;
                        }
                        
                        // set scroll bar visible
                        table.getHorizontalBar().setVisible(calculatedtableWidth > clientAreaWidth);
                    } finally {
                        fIsResizing= false;
                    }
                }
            };
            table.addControlListener(resizer);
            TableColumn[] columns= table.getColumns();
            for (int i= 0; i < columns.length; i++) {
                columns[i].addControlListener(resizer);
            }
        }
    
    private void add() {
        ColorPreferenceData data = new ColorPreferenceData();
        ColorDialog dialog = new ColorDialog(getShell(),data,false);
        if(dialog.open() == Window.OK) {
            store.add(data);
            tableViewer.refresh();
            tableViewer.setChecked(data,data.isChecked());
            tableViewer.setSelection(new StructuredSelection(data));
            return;
        }
    }
    
    private void remove() {
        IStructuredSelection selection= (IStructuredSelection)tableViewer.getSelection();

        Iterator elements= selection.iterator();
        while (elements.hasNext()) {
            ColorPreferenceData data = (ColorPreferenceData)elements.next();
            store.delete(data);
        }
        tableViewer.refresh();
    }
    
    private void edit() {
        IStructuredSelection selection= (IStructuredSelection)tableViewer.getSelection();
        ColorPreferenceData data = (ColorPreferenceData)selection.getFirstElement();
        ColorDialog dialog = new ColorDialog(getShell(),data,true);
        if(dialog.open() == Window.OK) {
            tableViewer.refresh();
            tableViewer.setChecked(data,data.isChecked());
            tableViewer.setSelection(new StructuredSelection(data));
            return;
        }
    }
    
    private void up() {
    	itemMover.moveCurrentSelectionUp();
    	tableViewer.refresh();
     }
    
    private void down() {
    	itemMover.moveCurrentSelectionDown();
    	tableViewer.refresh();
    }
    
    private void export() {
    	IStructuredSelection selection= (IStructuredSelection)tableViewer.getSelection();
    	if(selection.isEmpty()) {
        	MessageDialog.openError(getShell(),
        			LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.title"), //$NON-NLS-1$
        			LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.select.items.text")); //$NON-NLS-1$
    		return;
    	}
    	Collection itemArray= new ArrayList();
    	itemArray.addAll(selection.toList());
    	export((ColorPreferenceData[])itemArray.toArray(new ColorPreferenceData[itemArray.size()]));
    }
    
    private void export(ColorPreferenceData[] data) {
		FileDialog dialog= new FileDialog(getShell(), SWT.SAVE);
		dialog.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.export.title")); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] {LogViewerPlugin.getResourceString("preferences.coloringeditor.export.extension")}); //$NON-NLS-1$
		dialog.setFileName(LogViewerPlugin.getResourceString("preferences.coloringeditor.export.filename")); //$NON-NLS-1$
		String path= dialog.open();

		if (path == null) {
			return;
		}

		File file= new File(path);
		
		if (file.isHidden()) {
			String title= LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.title"); //$NON-NLS-1$
			String message= LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.hidden.text", new String[]{file.getAbsolutePath()}); //$NON-NLS-1$
			MessageDialog.openError(getShell(),title,message);
			return;
		}

		if (file.exists() && !file.canWrite()) {
			String title= LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.title"); //$NON-NLS-1$
			String message= LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.cannotWrite.text", new String[]{file.getAbsolutePath()}); //$NON-NLS-1$
			MessageDialog.openError(getShell(),title,message);
			return;
		}
		
		if (!file.exists() || confirmOverwrite(file)) {
			OutputStream output= null;
			try {
				output= new BufferedOutputStream(new FileOutputStream(file));
				ColorItemReadWriter writer = new ColorItemReadWriter();
				writer.write(data,output);
				output.close();
			} catch (IOException e) {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e2) {
                        logger.logError("unable to close existing file while attempting to export color items",e2); //$NON-NLS-1$
					}
				}
				openWriteErrorDialog(e);
			}
		}
    }
    
    private void inport() {
		FileDialog dialog= new FileDialog(getShell());
		dialog.setText(LogViewerPlugin.getResourceString("preferences.coloringeditor.import.title")); //$NON-NLS-1$
		dialog.setFilterExtensions(new String[] {LogViewerPlugin.getResourceString("preferences.coloringeditor.import.extension")}); //$NON-NLS-1$
		String path= dialog.open();

		if (path == null) {
			return;
		}

		try {
			ColorItemReadWriter reader = new ColorItemReadWriter();
			File file= new File(path);
			if (file.exists()) {
				InputStream input= new BufferedInputStream(new FileInputStream(file));
				try {
					ColorPreferenceData[] datas= reader.read(input);
					if(askOverwriteImport()) {
						store.removeAll();
					}
					for (int i= 0; i < datas.length; i++) {
						ColorPreferenceData data= datas[i];
						store.add(data);
					}
				} finally {
					try {
						input.close();
					} catch (IOException x) {
                        logger.logError("unable to close existing file while attempting to import color items",x); //$NON-NLS-1$
					}
				}
			}

			tableViewer.refresh();
			tableViewer.setAllChecked(false);
			tableViewer.setCheckedElements(store.getAllCheckedColorDetails());

		} catch (FileNotFoundException e) {
			openReadErrorDialog(e);
		} catch (IOException e) {
			openReadErrorDialog(e);
		}
    }
    
    private boolean askOverwriteImport() {
    	return MessageDialog.openQuestion(getShell(),
    			LogViewerPlugin.getResourceString("preferences.coloringeditor.import.dialog.overwrite.title"),
    			LogViewerPlugin.getResourceString("preferences.coloringeditor.import.dialog.overwrite.text"));
    }
    
	private boolean confirmOverwrite(File file) {
		return MessageDialog.openQuestion(getShell(),
				LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.title"), //$NON-NLS-1$
				LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.exists.text",new String[]{file.getAbsolutePath()})); //$NON-NLS-1$
	}
	
	private void openWriteErrorDialog(Exception e) {
		String title= LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.title"); //$NON-NLS-1$
		String message= LogViewerPlugin.getResourceString("preferences.coloringeditor.export.error.general.text"); //$NON-NLS-1$
		MessageDialog.openError(getShell(),title,message);
	}
	
	private void openReadErrorDialog(Exception e) {
		String title= LogViewerPlugin.getResourceString("preferences.coloringeditor.import.error.title"); //$NON-NLS-1$
		String message= LogViewerPlugin.getResourceString("preferences.coloringeditor.import.error.text"); //$NON-NLS-1$
		MessageDialog.openError(getShell(),title,message);
	}
}
