package ch.mimo.eclipse.plugin.logfiletools;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import ch.mimo.eclipse.plugin.logfiletools.action.CloseAllFilesViewAction;
import ch.mimo.eclipse.plugin.logfiletools.action.CopyToClipboardAction;
import ch.mimo.eclipse.plugin.logfiletools.action.FileCloseViewAction;
import ch.mimo.eclipse.plugin.logfiletools.action.FileEncondingViewAction;
import ch.mimo.eclipse.plugin.logfiletools.action.FileOpenViewAction;
import ch.mimo.eclipse.plugin.logfiletools.action.RefreshCurrentFileViewAction;
import ch.mimo.eclipse.plugin.logfiletools.action.StartTailOnCurrentFileViewAction;
import ch.mimo.eclipse.plugin.logfiletools.action.StopTailOnCurrentFileViewAction;
import ch.mimo.eclipse.plugin.logfiletools.file.document.LogDocument;
import ch.mimo.eclipse.plugin.logfiletools.ui.menu.LocalPullDownMenu;
import ch.mimo.eclipse.plugin.logfiletools.viewer.LogFileViewer;

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

public class LogFileView extends ViewPart {

    // Attribute ---------------------------------------------------------------
    
    private Logger logger;
    private Composite parent;

    
    private TabFolder tabfolder;
    private LogFileViewer viewer;
    
    private Map logTab;
    
    private ViewDocumentListener documentListener;
    
    private FileOpenViewAction fileOpenAction;
    private FileCloseViewAction fileCloseAction;
    private CloseAllFilesViewAction closeAllFilesAction;
    private RefreshCurrentFileViewAction refreshCurrentFileAction;
    private StartTailOnCurrentFileViewAction startTailOnCurrentFile;
    private StopTailOnCurrentFileViewAction stopTailOnCurrentFile;
    private FileEncondingViewAction fileEncodingAction;
    private CopyToClipboardAction copyAction;
    
    // Constructor -------------------------------------------------------------
    
    public LogFileView() {
        logger = LogFileViewPlugin.getDefault().getLogger();
        logTab = new Hashtable();
    }
    
    // Public ------------------------------------------------------------------
    
    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        this.parent = parent;
        documentListener = new ViewDocumentListener();
        tabfolder = new TabFolder(parent,0);
        tabfolder.addSelectionListener(new TabSelectionListener());
        viewer = new LogFileViewer(tabfolder,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		// fill the menues
        makeActions();
        hookContextMenu();
        contributeToActionBars();
    }
    
    public void closeCurrentLogFile() {
        try {
            LogFileTab tab = getSelectedTab();
            tab.close();
            logTab.remove(tab.getKey());
        } catch(IOException e) {
            logger.logError("unable to remove the current; active tab"); //$NON-NLS-1$
        }
        int index = tabfolder.getSelectionIndex();
        getSelectedItem().dispose();
        if(tabfolder.getItemCount() == 0) {
        		fileCloseAction.setEnabled(false);
        		closeAllFilesAction.setEnabled(false);
            refreshCurrentFileAction.setEnabled(false);
            fileEncodingAction.setEnabled(false);
            startTailOnCurrentFile.setEnabled(false);
            stopTailOnCurrentFile.setEnabled(false);
        } else
            if(index == 0) {
                tabfolder.setSelection(0);
            } else {
                tabfolder.setSelection(index - 1);
            }        
    }
    
    public void closeAllLogFiles() {
    	Iterator keyIterator = logTab.keySet().iterator();
    	while(keyIterator.hasNext()) {
    		Object key = keyIterator.next();
    		LogFileTab tab = (LogFileTab)logTab.get(key);
    		tab.getItem().dispose();
    	}
    	logTab.clear();
    	if(tabfolder.getItemCount() == 0) {
    		fileCloseAction.setEnabled(false);
    		closeAllFilesAction.setEnabled(false);
	        refreshCurrentFileAction.setEnabled(false);
	        fileEncodingAction.setEnabled(false);
	        startTailOnCurrentFile.setEnabled(false);
	        stopTailOnCurrentFile.setEnabled(false);  
    	}
    }
    
    public void refreshCurrentLogFile() {
        try {
        	getSelectedTab().getDocument().synchronize();
        	startTailOnCurrentFile.setEnabled(false);
        	stopTailOnCurrentFile.setEnabled(true);
        } catch(Exception e) {
            logger.logError("unable to refresh the current tab's content",e); //$NON-NLS-1$
        }
    }
    
    public void startTail() {
        try {
        	getSelectedTab().getDocument().setMonitor(true);
            stopTailOnCurrentFile.setEnabled(true);
            startTailOnCurrentFile.setEnabled(false);
        } catch(Exception e) {
            logger.logError("unable to start tailing",e); //$NON-NLS-1$
        }    	
    }
    
    public void stopTail() {
        try {
        	getSelectedTab().getDocument().setMonitor(false);
            stopTailOnCurrentFile.setEnabled(false);
            startTailOnCurrentFile.setEnabled(true);
        } catch(Exception e) {
            logger.logError("unable to stop tailing",e); //$NON-NLS-1$
        }      	
    }
    
    public boolean hasLogFile(LogFile file) {
        if(file == null) {
            return false;
        }
        return logTab.containsKey(file.getFileName());
    }
    
    public void openLogFile(LogFile file) {
        String key = file.getFileName();
        if(!logTab.containsKey(key)) {
            try {
            	String encoding = LogFileViewPlugin.getDefault().getPreferenceStore().getString(ILogFileViewConstants.PREF_ENCODING);
            	LogDocument document = new LogDocument(file,encoding);
                File logFile = new File(file.getFileName());
                TabItem item = new TabItem(tabfolder,0);
                item.setControl(viewer.getControl());
                item.setText(logFile.getName());
                item.setToolTipText(logFile.getPath());
                logTab.put(key,new LogFileTab(key,item,document));
                document.addDocumentListener(documentListener);
                document.setMonitor(true);
                stopTailOnCurrentFile.setEnabled(true);
                refreshCurrentFileAction.setEnabled(true);
                fileEncodingAction.setEnabled(true);
                fileCloseAction.setEnabled(true);
                closeAllFilesAction.setEnabled(true);
            } catch(Exception e) {
                logger.logError("unable to open the selected logfile",e); //$NON-NLS-1$
                LogFileViewPlugin.getDefault().showErrorMessage(LogFileViewPlugin.getResourceString("main.error.open.file",new String[]{file.getFileName()})); //$NON-NLS-1$
                return;
            }
        }
        // show active document
        LogFileTab tab = (LogFileTab)logTab.get(key);
        try {
            showDocument(tab.getDocument(),true);
        	tabfolder.setSelection(new TabItem[] {tab.getItem()});
        } catch(Exception e) {
            logger.logError("showing the document has lead to the following exception",e); //$NON-NLS-1$
        }
    }
    
    public boolean isAvailable() {
        return viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        tabfolder.setFocus();
    }
    
    public LogDocument getCurrentDocument() {
        return getSelectedTab().getDocument();
    }
    
    public LogFileViewer getViewer() {
    	return viewer;
    }
    
    // Private -----------------------------------------------------------------
    
	private void hookContextMenu() {
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});

		Menu menu = manager.createContextMenu(viewer.getControl());		
		viewer.getControl().setMenu(menu);
		
		getSite().registerContextMenu(manager,null);
	}
    
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		LocalPullDownMenu menu = new LocalPullDownMenu(manager,this,parent.getShell());
		menu.addAction(fileOpenAction);
		menu.addFilelist();
		menu.addSeparator();
		menu.addAction(startTailOnCurrentFile);
		menu.addAction(stopTailOnCurrentFile);
		menu.addSeparator();
		menu.addAction(refreshCurrentFileAction);
		menu.addSeparator();
		menu.addAction(fileEncodingAction);
		menu.addSeparator();
		menu.addAction(fileCloseAction);
		menu.addAction(closeAllFilesAction);
		menu.finalize();
	}

	private void fillContextMenu(IMenuManager manager) {		
		manager.add(refreshCurrentFileAction);
		manager.add(new Separator());
		manager.add(fileEncodingAction);
		manager.add(new Separator());
		manager.add(startTailOnCurrentFile);
		manager.add(stopTailOnCurrentFile);
		manager.add(new Separator());
		manager.add(fileCloseAction);
		manager.add(new Separator());
		manager.add(copyAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fileOpenAction);
		manager.add(new Separator());
		manager.add(startTailOnCurrentFile);
		manager.add(stopTailOnCurrentFile);
		manager.add(new Separator());
		manager.add(refreshCurrentFileAction);
		manager.add(new Separator());
		manager.add(fileCloseAction);
		manager.add(closeAllFilesAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
    protected void showDocument(LogDocument document, boolean monitor) {
        viewer.setDocument(document);
        viewer.getActualViewer().setTopIndex(document.getNumberOfLines());
	}
	
    private LogFileTab getSelectedTab(TabItem item) {
        if(item != null) {
            for(Iterator iter = logTab.values().iterator(); iter.hasNext();) {
                LogFileTab logTab = (LogFileTab)iter.next();
                if(logTab.getItem() == item) {
                    return logTab;
                }
            }        	
        }
        return null;       	
    }
    
    private LogFileTab getSelectedTab() {
        TabItem item = getSelectedItem();
        return getSelectedTab(item);
    }
    
    private TabItem getSelectedItem() {
        TabItem items[] = tabfolder.getSelection();
        if(items.length > 0) {
            return items[0];
        } else {
            return null;
        }
	}
    
    private void makeActions() {
    		// open
    		fileOpenAction = new FileOpenViewAction(this,parent.getShell());
    		fileOpenAction.setEnabled(true);
        // close
    		fileCloseAction = new FileCloseViewAction(this,parent.getShell());
    		fileCloseAction.setEnabled(false);
    		// close all
    		closeAllFilesAction = new CloseAllFilesViewAction(this,parent.getShell());
    		closeAllFilesAction.setEnabled(false);		
		// refresh
    		refreshCurrentFileAction = new RefreshCurrentFileViewAction(this,parent.getShell());
    		refreshCurrentFileAction.setEnabled(false);
		// start tail
    		startTailOnCurrentFile = new StartTailOnCurrentFileViewAction(this,parent.getShell());
    		startTailOnCurrentFile.setEnabled(false);
		// stop tail
    		stopTailOnCurrentFile = new StopTailOnCurrentFileViewAction(this,parent.getShell());
    		stopTailOnCurrentFile.setEnabled(false);
		// encoding action
    		fileEncodingAction = new FileEncondingViewAction(this,parent.getShell());
    		fileEncodingAction.setEnabled(false);
       // copy action
    		copyAction = new CopyToClipboardAction(this,parent.getShell());
    		copyAction.setEnabled(true);
    }
	
	// Inner Class -------------------------------------------------------------
	
    private class ViewDocumentListener implements IDocumentListener {

            public void documentAboutToBeChanged(DocumentEvent documentevent) {
            }

            public void documentChanged(DocumentEvent event) {
                if(!isAvailable()) {
                    return;
                }
                LogFileTab tab = getSelectedTab();
                if(logTab != null && event.getDocument() == tab.getDocument() && viewer.getDocument() != null) {
                    viewer.getActualViewer().refresh();
                    viewer.getActualViewer().setTopIndex(event.getDocument().getNumberOfLines());
                }
            }

            protected ViewDocumentListener() {
            }
    }
    
    private class TabSelectionListener implements SelectionListener {
    	
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			TabItem item = (TabItem)e.item;
			if(item == null) {
				return;
			}
			LogFileTab tab = getSelectedTab(item);
			if(tab == null || tab.getDocument() == null) {
				return;
			}
			fileEncodingAction.setText(LogFileViewPlugin.getResourceString("menu.encodingchange.text",new Object[] {tab.getDocument().getEncoding()})); //$NON-NLS-1$
			showDocument(tab.getDocument(),false);
			startTailOnCurrentFile.setEnabled(!tab.getDocument().isMonitor());
			stopTailOnCurrentFile.setEnabled(tab.getDocument().isMonitor());
			refreshCurrentFileAction.setEnabled(true);
		}
    }
}
