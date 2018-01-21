/*******************************************************************************
 * Copyright (c) 2009 - 2018 by Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Bossert - initial API and implementation and/or initial documentation
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin.action.delegate;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;
import de.anbos.eclipse.logviewer.plugin.preferences.FileHistoryTracker;
import de.anbos.eclipse.logviewer.plugin.preferences.HistoryFile;

public class FileOpenViewActionDelegate implements ILogViewerActionDelegate {

	private String parentPath = null;
	private boolean fileOpened = false;
	private LogFileType type;

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public boolean isFileOpened() {
		return fileOpened;
	}

	/* (non-Javadoc)
	 * @see de.anbos.eclipse.logviewer.plugin.action.ILogfileAction#run(de.anbos.eclipse.logviewer.plugin.LogViewer, org.eclipse.swt.widgets.Shell)
	 */
	public void run(LogViewer view, Shell shell) {

		fileOpened = false;

		// log file type
		String typeStr = null;
		String nameStr = null;
		type = LogFileType.LOGFILE_SYSTEM_FILE;

		/*
		String conStr = "Console: ";
	    LogFileTypeDialog typeDialog = new LogFileTypeDialog(shell);
	    typeDialog.setBlockOnOpen(true);
		int retval = typeDialog.open();
		if(retval == EncodingDialog.OK) {
			typeStr = typeDialog.getValue();
			if (typeStr.indexOf(conStr) == 0) {
				type = LogFileType.LOGFILE_ECLIPSE_CONSOLE;
				typeStr = typeStr.substring(conStr.length());
			}
		} else {
			return;
		}
		*/

		if (type == LogFileType.LOGFILE_SYSTEM_FILE) {
		    // load filter extensions
			String filterExtensions = LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_FILTER_EXTENSIONS);
			// opening file(s) in log view
		    FileDialog dialog = new FileDialog(shell,SWT.OPEN|SWT.MULTI);
		    String[] extensions = {
		    		filterExtensions,
		    		"*.*"
		    };

	    	//
		    if (parentPath == null) {
		    	Object[] file_list = FileHistoryTracker.getInstance().getFiles().toArray();
		    	if (file_list.length >= 1)
		    	{
		    		HistoryFile history_file = (HistoryFile)(file_list[file_list.length - 1]);
		    		File file = new File(history_file.getPath());
		    		if (file.isDirectory()) {
		    			parentPath = file.toString();
		    		} else {
		    			parentPath = file.getParent();
		    		}
		    	}
		    }
		    dialog.setFilterPath(parentPath);
		    dialog.setFilterExtensions(extensions);
		    dialog.setFilterIndex(0);
		    String path = dialog.open();
		    if (path != null) {
		    	File tempFile = new File(path);
		    	path = tempFile.isDirectory() ? tempFile.toString() : tempFile.getParent();
		    	String selectedFiles[] = dialog.getFileNames();
		    	for (int i=0;i<selectedFiles.length;i++) {
		    		String fileStr = path.endsWith(File.separator) ? path + selectedFiles[i] : path + File.separator + selectedFiles[i];
		    		if (!view.checkAndOpenFile(type,fileStr, null, true))
		    	        fileOpened = true;
		    	}
		    }
		} else if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {
    		if (!view.checkAndOpenFile(type, typeStr, nameStr, true))
    	        fileOpened = true;
		}
	}
}
