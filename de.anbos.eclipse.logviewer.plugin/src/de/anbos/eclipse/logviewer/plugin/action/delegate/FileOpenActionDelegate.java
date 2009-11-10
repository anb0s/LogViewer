package de.anbos.eclipse.logviewer.plugin.action.delegate;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogFile;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.preferences.FileHistoryTracker;
import de.anbos.eclipse.logviewer.plugin.preferences.HistoryFile;

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
 *
 *
 * 2009.07.01, Andre Bossert, anb0s(at)freenet(dot)de
 * 	BUG-ID 1681341: added support for multiple file selection and extension filters
 */

public class FileOpenActionDelegate implements ILogViewerActionDelegate {

	private String parentPath = null;
	private boolean fileOpened = false;

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
		// opening file(s) in log view
	    FileDialog dialog = new FileDialog(shell,SWT.OPEN|SWT.MULTI);
	    String[] extensions = {
	    		"*.log;*.txt;*.er?",
	    		"*.*"
	    };
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
	    		if (!view.checkAndOpenFile(fileStr, true))
	    	        fileOpened = true;
	    	}
	    }
	}

}
