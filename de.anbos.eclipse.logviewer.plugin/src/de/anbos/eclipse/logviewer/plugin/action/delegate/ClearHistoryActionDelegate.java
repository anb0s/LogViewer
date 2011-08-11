/*
 * Copyright 2009 - 2010 by Andre Bossert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.preferences.FileHistoryTracker;

public class ClearHistoryActionDelegate implements ILogViewerActionDelegate {

	// Attribute ---------------------------------------------------------------

	// Constructor -------------------------------------------------------------

	public ClearHistoryActionDelegate() {
	}

	// Public ------------------------------------------------------------------

	public void run(LogViewer view, Shell shell) {
		if (askClear(shell)) {
			FileHistoryTracker.getInstance().clearFiles();
		}
	}

    private boolean askClear(Shell shell) {
    	return MessageDialog.openQuestion(shell,
    			LogViewerPlugin.getResourceString("misc.clearhisrory.title"),
    			LogViewerPlugin.getResourceString("misc.clearhisrory.text"));
    }

}
