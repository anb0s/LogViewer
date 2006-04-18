package ch.mimo.eclipse.plugin.logfiletools.action.delegate;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import ch.mimo.eclipse.plugin.logfiletools.LogFile;
import ch.mimo.eclipse.plugin.logfiletools.LogFileView;
import ch.mimo.eclipse.plugin.logfiletools.preferences.FileHistoryTracker;

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

public class FileOpenActionDelegate implements ILogfileActionDelegate {

	/* (non-Javadoc)
	 * @see ch.mimo.eclipse.plugin.logfiletools.action.ILogfileAction#run(ch.mimo.eclipse.plugin.logfiletools.LogFileView, org.eclipse.swt.widgets.Shell)
	 */
	public void run(LogFileView view, Shell shell) {
		// opening file in logfile view
	    FileDialog dialog = new FileDialog(shell);
	    String path = dialog.open();
	    LogFile file = new LogFile(path);
	    if(path != null && !view.hasLogFile(file)) {
	        view.openLogFile(file);
            FileHistoryTracker.getInstance().storeFile(path);
	    }
	}

}
