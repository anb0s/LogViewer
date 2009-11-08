package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogFile;
import de.anbos.eclipse.logviewer.plugin.LogFileView;
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
 */

public class HistoryFileOpenActionDelegate implements ILogfileActionDelegate {

	// Attribute ---------------------------------------------------------------
	
	private HistoryFile file;
	
	// Constructor -------------------------------------------------------------
	
	public HistoryFileOpenActionDelegate(HistoryFile file) {
		this.file = file;
	}
	
	// Public ------------------------------------------------------------------
	
	public void run(LogFileView view, Shell shell) {
		// opening file in logfile view
	    LogFile file = new LogFile(this.file.getPath());
	    if(this.file.getPath() != null && !view.hasLogFile(file)) {
	    	FileHistoryTracker.getInstance().storeFile(this.file.getPath());
	        view.openLogFile(file);
	    }
	}

}
