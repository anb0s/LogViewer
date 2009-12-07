package de.anbos.eclipse.logviewer.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import de.anbos.eclipse.logviewer.plugin.action.CloseAllFilesViewAction;
import de.anbos.eclipse.logviewer.plugin.action.FileCloseViewAction;
import de.anbos.eclipse.logviewer.plugin.action.FileEncondingViewAction;
import de.anbos.eclipse.logviewer.plugin.action.FileOpenViewAction;
import de.anbos.eclipse.logviewer.plugin.action.RefreshCurrentFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StartTailOnAllFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StartTailOnCurrentFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StopTailOnAllFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StopTailOnCurrentFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.TabRenameAction;
import de.anbos.eclipse.logviewer.plugin.action.delegate.FileOpenActionDelegate;
import de.anbos.eclipse.logviewer.plugin.file.document.LogDocument;
import de.anbos.eclipse.logviewer.plugin.preferences.FileHistoryTracker;
import de.anbos.eclipse.logviewer.plugin.preferences.PreferenceValueConverter;
import de.anbos.eclipse.logviewer.plugin.ui.menu.LocalPullDownMenu;
import de.anbos.eclipse.logviewer.plugin.viewer.LogFileViewer;

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

public class LogViewer extends ViewPart {

    // Attribute ---------------------------------------------------------------
    
    private Logger logger;
    private Composite parent;

    private boolean stopAfterChange = false;
    
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
    private StartTailOnAllFileViewAction startTailOnAllFiles;
    private StopTailOnAllFileViewAction stopTailOnAllFiles;
    private FileEncondingViewAction fileEncodingAction;
    private TabRenameAction tabRenameAction;
    
    // Constructor -------------------------------------------------------------
    
    public LogViewer() {
        logger = LogViewerPlugin.getDefault().getLogger();
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
        
        // DnD
    	DropTarget target = new DropTarget(parent, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    	target.setTransfer(new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance()});
    	target.addDropListener (new DropTargetAdapter() {
    		public void dragEnter(DropTargetEvent e) {
    			if (e.detail == DND.DROP_NONE)
    				e.detail = DND.DROP_COPY;
    		}
    		public void drop(DropTargetEvent event) {
    			if (event.data == null || ((String[])event.data).length < 1) {
    				event.detail = DND.DROP_NONE;
    				return;
    			}
    			//File file = new File(((String[])event.data)[0]);
    			if (!checkAndOpenFile(((String[])event.data)[0], false))
    				event.detail = DND.DROP_NONE;
    		}
    	});

		// fill the menues
        makeActions();
        hookContextMenu();
        contributeToActionBars();
        openAllLastOpenFiles();
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
	        startTailOnAllFiles.setEnabled(false);
	        stopTailOnAllFiles.setEnabled(false);
            tabRenameAction.setEnabled(false);
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
    		try {
				tab.close();
				tab.getItem().dispose();
			} catch (IOException e) {
				logger.logError("unable to remove tab: " + tab.getDocument().getFile().getFileName()); //$NON-NLS-1$
			}
    	}
    	logTab.clear();
    	if(tabfolder.getItemCount() == 0) {
    		fileCloseAction.setEnabled(false);
    		closeAllFilesAction.setEnabled(false);
	        refreshCurrentFileAction.setEnabled(false);
	        fileEncodingAction.setEnabled(false);
	        startTailOnCurrentFile.setEnabled(false);
	        stopTailOnCurrentFile.setEnabled(false);
	        startTailOnAllFiles.setEnabled(false);
	        stopTailOnAllFiles.setEnabled(false);
	        tabRenameAction.setEnabled(false);
    	}
    }
    
    public void refreshCurrentLogFile() {
        try {
        	boolean wasMonitor = getSelectedTab().getDocument().isMonitor();
        	if (!wasMonitor)
        		stopAfterChange = true;
        	getSelectedTab().getDocument().synchronize();        	
        } catch(Exception e) {
            logger.logError("unable to refresh the current tab's content",e); //$NON-NLS-1$
        }
    }
    
    public void startTailOnAllDocuments() {
    	Iterator keyIterator = logTab.keySet().iterator();
    	while(keyIterator.hasNext()) {
    		Object key = keyIterator.next();
    		LogFileTab tab = (LogFileTab)logTab.get(key);
    		tab.getDocument().setMonitor(true);
    	}    	
    	stopTailOnCurrentFile.setEnabled(true);
    	startTailOnCurrentFile.setEnabled(false);
    }
    
    public void stopTailOnAllDocuments() {
    	Iterator keyIterator = logTab.keySet().iterator();
    	while(keyIterator.hasNext()) {
    		Object key = keyIterator.next();
    		LogFileTab tab = (LogFileTab)logTab.get(key);
    		tab.getDocument().setMonitor(false);
    	}    	
    	stopTailOnCurrentFile.setEnabled(false);
    	startTailOnCurrentFile.setEnabled(true);
    }
    
    public void startTail() {
        try {
        	getSelectedTab().getDocument().setMonitor(true);
        	getSelectedTab().getDocument().getFile().setMonitor(true);
            stopTailOnCurrentFile.setEnabled(true);
            startTailOnCurrentFile.setEnabled(false);
        } catch(Exception e) {
            logger.logError("unable to start tailing",e); //$NON-NLS-1$
        }    	
    }
    
    public void stopTail() {
        try {
        	getSelectedTab().getDocument().setMonitor(false);
        	getSelectedTab().getDocument().getFile().setMonitor(false);
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

    public boolean checkAndOpenFile(String fullPath, boolean fromAction) {
    	File file = new File(fullPath);
		if (!fromAction && file.isDirectory()) {
			FileOpenActionDelegate action = new FileOpenActionDelegate();
			action.setParentPath(fullPath);
			action.run(this, getSite().getShell());
			return action.isFileOpened();
		}else {
    	    LogFile logFile = new LogFile(fullPath,null,true);
    	    if(!hasLogFile(logFile)) {
                FileHistoryTracker.getInstance().storeFile(fullPath);    	        
    	    }
    	    // open or show file
    	    openLogFile(logFile);
		}
        return true;
    }

    public void openLogFile(LogFile file) {
        String key = file.getFileName();
        if(!logTab.containsKey(key)) {
            try {
            	String encoding = LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_ENCODING);
            	LogDocument document = new LogDocument(file,encoding);
                File logFile = new File(file.getFileName());
                TabItem item = new TabItem(tabfolder,0);
                item.setControl(viewer.getControl());
                item.setText(file.getTabName());
                item.setToolTipText(logFile.getPath());
                logTab.put(key,new LogFileTab(key,item,document));
                document.addDocumentListener(documentListener);

            	// restore monitor status
                document.setMonitor(file.getMonitor());
                stopTailOnCurrentFile.setEnabled(file.getMonitor());
                startTailOnCurrentFile.setEnabled(!file.getMonitor());                
                
                refreshCurrentFileAction.setEnabled(true);
                fileEncodingAction.setEnabled(true);
                fileCloseAction.setEnabled(true);
                closeAllFilesAction.setEnabled(true);
                tabRenameAction.setEnabled(true);
    	        startTailOnAllFiles.setEnabled(true);
    	        stopTailOnAllFiles.setEnabled(true);
    	        
            } catch(Exception e) {
                logger.logError("unable to open the selected logfile",e); //$NON-NLS-1$
                LogViewerPlugin.getDefault().showErrorMessage(LogViewerPlugin.getResourceString("main.error.open.file",new String[]{file.getFileName()})); //$NON-NLS-1$
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

        // check if file should be refreshed
        if (!file.getMonitor())
        	refreshCurrentLogFile();
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
    
    /**
     * makes shure that the correct find/replace target is returned.
     * the actual viewer is returned if an adapter of type 
     * FindReplaceTarget is searched
     */
    public Object getAdapter(Class adapter) {
    	Object object = super.getAdapter(adapter);
    	if(object != null) {
    		return object;
    	}
    	if(adapter.equals(IFindReplaceTarget.class)) {
    		return viewer.getActualViewer().getFindReplaceTarget();
    	}
    	return null;
    }
    
    public String getCurrentLogFileTabName() {
    	return getSelectedTab().getItem().getText();
    }
    
    public void setCurrentLogFileTabName(String name) {
    	getSelectedTab().getItem().setText(name);
    	getSelectedTab().getDocument().getFile().setTabName(name);
    }
    
    public void dispose() {
    	storeAllCurrentlyOpenFiles();
    	super.dispose();
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
		menu.addAction(startTailOnAllFiles);
		menu.addAction(stopTailOnAllFiles);
		menu.addSeparator();
		menu.addAction(refreshCurrentFileAction);
		menu.addSeparator();
		menu.addAction(fileEncodingAction);
		menu.addSeparator();
		menu.addAction(tabRenameAction);
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
		manager.add(tabRenameAction);
		manager.add(new Separator());
		manager.add(fileCloseAction);
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
		manager.add(startTailOnAllFiles);
		manager.add(stopTailOnAllFiles);
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
    	// start all tail
    	    startTailOnAllFiles = new StartTailOnAllFileViewAction(this,parent.getShell());
    	    startTailOnAllFiles.setEnabled(false);
    	// stop all tail
    	    stopTailOnAllFiles = new StopTailOnAllFileViewAction(this,parent.getShell());
    	    stopTailOnAllFiles.setEnabled(false);
		// encoding action
    		fileEncodingAction = new FileEncondingViewAction(this,parent.getShell());
    		fileEncodingAction.setEnabled(false);
    		// tab rename action
    		tabRenameAction = new TabRenameAction(this,parent.getShell());
    		tabRenameAction.setEnabled(false);
    }
    
    private void storeAllCurrentlyOpenFiles() {
    	List fileList = new Vector();
    	Iterator keyIterator = logTab.keySet().iterator();
    	while(keyIterator.hasNext()) {
    		Object key = keyIterator.next();
    		LogFileTab tab = (LogFileTab)logTab.get(key);
    		LogFile logFile = tab.getDocument().getFile();
    		fileList.add(logFile);
    	}
    	LogViewerPlugin.getDefault().getPreferenceStore().setValue(ILogViewerConstants.PREF_LAST_OPEN_FILES,PreferenceValueConverter.asLogFileListString(fileList));
    }
    
    private void openAllLastOpenFiles() {
    	List logFiles = PreferenceValueConverter.asLogFileList(LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_LAST_OPEN_FILES));
    	Iterator it = logFiles.iterator();
    	while(it.hasNext()) {
    		LogFile logFile = (LogFile)it.next();
    		openLogFile(logFile);
    	}
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
        		if (stopAfterChange) {
            		stopAfterChange = false;
        			stopTail();
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
			fileEncodingAction.setText(LogViewerPlugin.getResourceString("menu.encodingchange.text",new Object[] {tab.getDocument().getEncoding()})); //$NON-NLS-1$
			showDocument(tab.getDocument(),false);
			startTailOnCurrentFile.setEnabled(!tab.getDocument().isMonitor());
			stopTailOnCurrentFile.setEnabled(tab.getDocument().isMonitor());
			refreshCurrentFileAction.setEnabled(true);
		}
    }
}
