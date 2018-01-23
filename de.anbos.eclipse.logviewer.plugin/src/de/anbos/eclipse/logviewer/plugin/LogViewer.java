/*******************************************************************************
 * Copyright (c) 2007 - 2011 by Michael Mimo Moratti
 * Copyright (c) 2012 - 2018 by Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Mimo Moratti - initial API and implementation and/or initial documentation
 *    Andre Bossert - extensions
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.Buffer;
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
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.ViewPart;

import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;
import de.anbos.eclipse.logviewer.plugin.action.ClearHistoryAction;
import de.anbos.eclipse.logviewer.plugin.action.CloseAllFilesViewAction;
import de.anbos.eclipse.logviewer.plugin.action.FileClearAction;
import de.anbos.eclipse.logviewer.plugin.action.FileCloseViewAction;
import de.anbos.eclipse.logviewer.plugin.action.FileEncondingViewAction;
import de.anbos.eclipse.logviewer.plugin.action.FileOpenViewAction;
import de.anbos.eclipse.logviewer.plugin.action.PreferencesViewAction;
import de.anbos.eclipse.logviewer.plugin.action.RefreshCurrentFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StartTailOnAllFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StartTailOnCurrentFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StopTailOnAllFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.StopTailOnCurrentFileViewAction;
import de.anbos.eclipse.logviewer.plugin.action.TabRenameAction;
import de.anbos.eclipse.logviewer.plugin.action.delegate.FileOpenViewActionDelegate;
import de.anbos.eclipse.logviewer.plugin.file.FileTail;
import de.anbos.eclipse.logviewer.plugin.file.document.LogDocument;
import de.anbos.eclipse.logviewer.plugin.preferences.FileHistoryTracker;
import de.anbos.eclipse.logviewer.plugin.preferences.PreferenceValueConverter;
import de.anbos.eclipse.logviewer.plugin.ui.menu.LocalPullDownMenu;
import de.anbos.eclipse.logviewer.plugin.viewer.LogFileViewer;

public class LogViewer extends ViewPart {

    // Attribute ---------------------------------------------------------------

    private Logger logger;
    private Composite parent;

    private LogViewerConsole console;

    private boolean stopAfterChange = false;

    private TabFolder tabfolder;
    private LogFileViewer viewer;

    private Map<String, LogFileTab> logTab;
    private TabItem oldTabItem;

    private ViewDocumentListener documentListener;

    private FileOpenViewAction fileOpenAction;
    private ClearHistoryAction clearHistoryAction;
    private PreferencesViewAction preferencesAction;
    private FileCloseViewAction fileCloseAction;
    private CloseAllFilesViewAction closeAllFilesAction;
    private RefreshCurrentFileViewAction refreshCurrentFileAction;
    private StartTailOnCurrentFileViewAction startTailOnCurrentFile;
    private StopTailOnCurrentFileViewAction stopTailOnCurrentFile;
    private StartTailOnAllFileViewAction startTailOnAllFiles;
    private StopTailOnAllFileViewAction stopTailOnAllFiles;
    private FileEncondingViewAction fileEncodingAction;
    private TabRenameAction tabRenameAction;
    private FileClearAction fileClearAction;

    private int monitorCounter;
    private int monitorCounterMax;

    // Constructor -------------------------------------------------------------

    public LogViewer() {
        logger = LogViewerPlugin.getDefault().getLogger();
        logTab = new Hashtable<String, LogFileTab>();
        oldTabItem = null;
        console = null;
        resetMonitorCounter();
    }

    // Public ------------------------------------------------------------------



    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        //viewer
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
                if (!checkAndOpenFile(LogFileType.LOGFILE_SYSTEM_FILE,((String[])event.data)[0], null, false))
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
            removeMonitorCounter(tab.getDocument().isMonitor());
            tab.close();
            logTab.remove(tab.getKey());
        } catch(IOException e) {
            logger.logError("unable to remove the current; active tab"); //$NON-NLS-1$
        }
       
        int index = tabfolder.getSelectionIndex();
        getSelectedItem().dispose();
        if (!greyAllOutIfNoFiles()) {
            if(index == 0) {
                tabfolder.setSelection(0);
            } else {
                tabfolder.setSelection(index - 1);
            }
        }
    }

	public void clearCurrentLogFile() {
		try {
			LogFileType type = getSelectedTab().getDocument().getFile().getType();
			if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {

				final IConsole con = getSelectedTab().getDocument().getReader().getConsoleTail().getConsole();
				if (con instanceof TextConsole) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							((TextConsole) con).clearConsole();
						}
					});
				} else {
					logger.logWarning("Console" + "[ " + con.getName() + " ]" + " clear not supported");//$NON-NLS-1$
				}
			} else {
				FileTail file = getSelectedTab().getDocument().getReader().getFileTail();
				Buffer buffer = file.getBuffer();
				if (buffer != null) {
					file.stopFileMapping(buffer);
				}
				PrintWriter pw = new PrintWriter(getCurrentLogFilePath());
				pw.write(" ");
				pw.close();
			}
		} catch (Exception e) {
			logger.logError("unable to clear selected file: " + getCurrentLogFilePath()); //$NON-NLS-1$
		}
		refreshCurrentLogFile();
	}

	public void closeAllLogFiles() {
        Iterator<String> keyIterator = logTab.keySet().iterator();
        while(keyIterator.hasNext()) {
            Object key = keyIterator.next();
            LogFileTab tab = (LogFileTab)logTab.get(key);
            try {
                tab.close();
                tab.getItem().dispose();
            } catch (IOException e) {
                logger.logError("unable to remove tab: " + tab.getDocument().getFile().getKey()); //$NON-NLS-1$
            }
        }
        logTab.clear();
        greyAllOutIfNoFiles();
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
        Iterator<String> keyIterator = logTab.keySet().iterator();
        while(keyIterator.hasNext()) {
            Object key = keyIterator.next();
            LogFileTab tab = (LogFileTab)logTab.get(key);
            tab.getDocument().setMonitor(true);
        }
        setMonitorCounterToMax();
        updateTailStartStopButtons(true);
    }

    public void stopTailOnAllDocuments() {
        Iterator<String> keyIterator = logTab.keySet().iterator();
        while(keyIterator.hasNext()) {
            Object key = keyIterator.next();
            LogFileTab tab = (LogFileTab)logTab.get(key);
            tab.getDocument().setMonitor(false);

        }
        setMonitorCounterToMin();
        updateTailStartStopButtons(false);
    }

    public void startTail() {
        try {
            getSelectedTab().getDocument().setMonitor(true);
            increaseMonitorCounter();
            updateTailStartStopButtons(true);
        } catch(Exception e) {
            logger.logError("unable to start tailing",e); //$NON-NLS-1$
        }
    }

    public void stopTail() {
        try {
            getSelectedTab().getDocument().setMonitor(false);
            decreaseMonitorCounter();
            updateTailStartStopButtons(false);
        } catch(Exception e) {
            logger.logError("unable to stop tailing",e); //$NON-NLS-1$
        }
    }

    boolean greyAllOutIfNoFiles() {
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
            fileClearAction.setEnabled(false);
            resetMonitorCounter();
            return true;
        }
        return false;
    }

    void updateTailStartStopButtons(boolean monitor) {
        stopTailOnCurrentFile.setEnabled(monitor);
        startTailOnCurrentFile.setEnabled(!monitor);
        if (monitorCounter == 0)
        {
            startTailOnAllFiles.setEnabled(true);
            stopTailOnAllFiles.setEnabled(false);
        } else if (monitorCounter == monitorCounterMax) {
            startTailOnAllFiles.setEnabled(false);
            stopTailOnAllFiles.setEnabled(true);
        } else {
            startTailOnAllFiles.setEnabled(true);
            stopTailOnAllFiles.setEnabled(true);
        }
    }

    public boolean hasLogFile(LogFile file) {
        if(file == null) {
            return false;
        }
        return logTab.containsKey(file.getKey());
    }

    void increaseMonitorCounter() {
        if (monitorCounter < monitorCounterMax)
            monitorCounter++;
    }

    void decreaseMonitorCounter() {
        if (monitorCounter > 0)
            monitorCounter--;
    }

    void setMonitorCounterToMax() {
        monitorCounter = monitorCounterMax;
    }

    void setMonitorCounterToMin() {
        monitorCounter = 0;
    }

    void resetMonitorCounter() {
        monitorCounter = 0;
        monitorCounterMax = 0;
    }

    void removeMonitorCounter(boolean monitor) {
        if (monitorCounterMax > 0)
            monitorCounterMax--;
        if (monitor)
            decreaseMonitorCounter();
    }

    void addMonitorCounter(boolean monitor) {
          monitorCounterMax++;
        if (monitor)
            increaseMonitorCounter();
    }

    public boolean checkAndOpenFile(LogFileType type, String fullPath, String namePattern, boolean fromAction) {
        File file = new File(fullPath);
        if (!fromAction && file.isDirectory()) {
            FileOpenViewActionDelegate action = new FileOpenViewActionDelegate();
            action.setParentPath(fullPath);
            action.run(this, getSite().getShell());
            return action.isFileOpened();
        }else {
            LogFile logFile = new LogFile(type, fullPath, namePattern, null, true);
            if(!hasLogFile(logFile)) {
                FileHistoryTracker.getInstance().storeFile(type, fullPath, namePattern);
            }
            // open or show file
            openLogFile(logFile);
        }
        return true;
    }

    public void openLogFile(LogFile file) {
        String key = file.getKey();
        if(!logTab.containsKey(key)) {
            try {
                if (file.getNamePattern().equals(LogViewerPlugin.getResourceString("logviewer.plugin.console.name"))) {
                	 createConsole();
                }
                   
                String encoding = LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_ENCODING);
                LogDocument document = new LogDocument(file,encoding);
                TabItem item = new TabItem(tabfolder,0);
                item.setControl(viewer.getControl());
                item.setText(file.getNamePattern());
                item.setToolTipText(file.getPath());
                logTab.put(key,new LogFileTab(key,item,document));
                document.addDocumentListener(documentListener);

                // restore monitor status
                boolean monitorState = file.getMonitor();
                document.setMonitor(monitorState);
                addMonitorCounter(monitorState);
                updateTailStartStopButtons(monitorState);

                refreshCurrentFileAction.setEnabled(true);
                fileEncodingAction.setEnabled(true);
                fileCloseAction.setEnabled(true);
                closeAllFilesAction.setEnabled(true);
                tabRenameAction.setEnabled(true);
                startTailOnAllFiles.setEnabled(true);
                stopTailOnAllFiles.setEnabled(true);
                fileClearAction.setEnabled(true);
            } catch(Exception e) {
                logger.logError("unable to open the selected logfile",e); //$NON-NLS-1$
                LogViewerPlugin.getDefault().showErrorMessage(LogViewerPlugin.getResourceString("main.error.open.file",new String[]{file.getPath()})); //$NON-NLS-1$
                return;
            }
        }
        // show active document
        LogFileTab tab = (LogFileTab)logTab.get(key);
        try {
            showDocument(tab.getDocument(),null,0,true);
            tabfolder.setSelection(new TabItem[] {tab.getItem()});
            oldTabItem = tab.getItem();
            // send event to refresh encoding
            Event event = new Event();
            event.item = tab.getItem();
            tabfolder.notifyListeners(SWT.Selection, event);
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
    @SuppressWarnings("rawtypes")
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
        getSelectedTab().getDocument().getFile().setNamePattern(name);
    }

    public String getCurrentLogFilePath() {
        return  getSelectedTab().getDocument().getFile().getPath();
    }
    
    public void dispose() {
        viewer.removeListeners();
        storeAllCurrentlyOpenFiles();
        super.dispose();
    }

    public IOConsoleOutputStream getConsoleStream() {
        if (console == null)
            createConsole();
        return console.getOutStream();
    }

    public IOConsole getConsole() {
        if (console == null)
            createConsole();
        return console;
    }

    public void printDefaultMessage() {
        if (getConsole().getDocument().get().isEmpty()) {
            //getConsoleStream().println("LogViewer started!");
            try {
                getConsoleStream().write("Paste messages into this console and check rules and filters in LogViewer.\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
        menu.addAction(clearHistoryAction);
        menu.addAction(fileClearAction);
        menu.addSeparator();
        menu.addAction(refreshCurrentFileAction);
        menu.addAction(startTailOnCurrentFile);
        menu.addAction(stopTailOnCurrentFile);
        menu.addAction(fileCloseAction);
        menu.addSeparator();
        menu.addAction(fileEncodingAction);
        menu.addAction(tabRenameAction);
        menu.addSeparator();
        menu.addAction(startTailOnAllFiles);
        menu.addAction(stopTailOnAllFiles);
        menu.addAction(closeAllFilesAction);
        menu.addSeparator();
        menu.addAction(preferencesAction);
        menu.addSeparator();
        menu.finalize();
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(refreshCurrentFileAction);
        manager.add(startTailOnCurrentFile);
        manager.add(stopTailOnCurrentFile);
        manager.add(fileCloseAction);
        manager.add(fileClearAction);
        manager.add(new Separator());
        manager.add(fileEncodingAction);
        manager.add(tabRenameAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(fileOpenAction);
        manager.add(preferencesAction);
        manager.add(fileClearAction);
        manager.add(new Separator());
        manager.add(refreshCurrentFileAction);
        manager.add(startTailOnCurrentFile);
        manager.add(stopTailOnCurrentFile);
        manager.add(fileCloseAction);
        manager.add(new Separator());
        manager.add(startTailOnAllFiles);
        manager.add(stopTailOnAllFiles);
        manager.add(closeAllFilesAction);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    protected void showDocument(LogDocument document, ISelection sel, int index, boolean monitor) {
        viewer.setDocument(document);
        if (monitor) {
            viewer.showTopOrBottomOfFile();
        } else {
            viewer.setSelection(sel);
            if (viewer.isShowTopOfFile())
                viewer.showTopOrBottomOfFile();
            else
                viewer.setTopIndex(index);
        }
    }

    private LogFileTab getSelectedTab(TabItem item) {
        if(item != null) {
            for(Iterator<LogFileTab> iter = logTab.values().iterator(); iter.hasNext();) {
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
            // clear history
            clearHistoryAction = new ClearHistoryAction(this,parent.getShell());
            clearHistoryAction.setEnabled(true);
            // preferences
            preferencesAction = new PreferencesViewAction(this,parent.getShell());
            preferencesAction.setEnabled(true);
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
            // clear file
            fileClearAction = new FileClearAction(this,parent.getShell());
            fileClearAction.setEnabled(true);
    }

    private void storeAllCurrentlyOpenFiles() {
        List<LogFile> fileList = new Vector<LogFile>();
        Iterator<String> keyIterator = logTab.keySet().iterator();
        while(keyIterator.hasNext()) {
            Object key = keyIterator.next();
            LogFileTab tab = (LogFileTab)logTab.get(key);
            LogFile logFile = tab.getDocument().getFile();
            fileList.add(logFile);
        }
        LogViewerPlugin.getDefault().getPreferenceStore().setValue(ILogViewerConstants.PREF_LAST_OPEN_FILES,PreferenceValueConverter.asLogFileListString(fileList));
    }

    private void openAllLastOpenFiles() {
        List<?> logFiles = PreferenceValueConverter.asLogFileList(LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_LAST_OPEN_FILES));
        Iterator<?> it = logFiles.iterator();
        while(it.hasNext()) {
            LogFile logFile = (LogFile)it.next();
            openLogFile(logFile);
        }
    }

    private void createConsole() {
        console = new LogViewerConsole(LogViewerPlugin.getResourceString("logviewer.plugin.console.name"), null);
        //console.activate();
        //console.setMonitorStatus(true);
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{ console });
        printDefaultMessage();
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

                // activate / show the view and tab
                if (viewer.isShowWhenUpdated()) {
                    //LogViewer view = null;
                    try {
                        //view = (LogViewer)
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.anbos.eclipse.logviewer.plugin.LogViewer");
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }

                    // change selection
                    if (event.getDocument() != tab.getDocument()) {
                        // show active document
                        Iterator<String> keyIterator = logTab.keySet().iterator();
                        while(keyIterator.hasNext()) {
                            Object key = keyIterator.next();
                            LogFileTab newTab = (LogFileTab)logTab.get(key);
                            if (event.getDocument() == newTab.getDocument()) {
                                showDocument(newTab.getDocument(),null,0,true);
                                tabfolder.setSelection(new TabItem[] {newTab.getItem()});
                                // send event to refresh encoding
                                Event newEvent = new Event();
                                newEvent.item = newTab.getItem();
                                tabfolder.notifyListeners(SWT.Selection, newEvent);
                                break;
                            }
                        }
                    }
                }

                if(logTab != null && event.getDocument() == tab.getDocument() && viewer.getDocument() != null) {
                    viewer.refresh();
                    viewer.showBottomOfFile();
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
            // get new
            LogFileTab tab = getSelectedTab(item);
            if(tab == null || tab.getDocument() == null) {
                return;
            }
            // save old selection
            if (oldTabItem != null) {
                LogFileTab oldTab = getSelectedTab(oldTabItem);
                if((oldTab != null) && (oldTab.getDocument() != null)) {
                    oldTab.setSelection(viewer.getSelection());
                    oldTab.setTopIndex(viewer.getTopIndex());
                }
            }
            // restore
            fileEncodingAction.setText(LogViewerPlugin.getResourceString("menu.encodingchange.text",new Object[] {tab.getDocument().getEncoding()})); //$NON-NLS-1$
            showDocument(tab.getDocument(), tab.getSelection(), tab.getTopIndex(),false);
            updateTailStartStopButtons(tab.getDocument().isMonitor());
            refreshCurrentFileAction.setEnabled(true);
            // set act tab item
            oldTabItem = item;
        }
    }
}
