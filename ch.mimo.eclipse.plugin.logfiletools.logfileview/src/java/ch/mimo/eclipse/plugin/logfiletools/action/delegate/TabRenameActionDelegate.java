package ch.mimo.eclipse.plugin.logfiletools.action.delegate;

import org.eclipse.swt.widgets.Shell;

import ch.mimo.eclipse.plugin.logfiletools.LogFileView;
import ch.mimo.eclipse.plugin.logfiletools.ui.EncodingDialog;
import ch.mimo.eclipse.plugin.logfiletools.ui.TabRenameDialog;

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

public class TabRenameActionDelegate implements ILogfileActionDelegate {

	// Public ------------------------------------------------------------------
	
	public void run(LogFileView view, Shell shell) {
		TabRenameDialog dialog = new TabRenameDialog(shell,view.getCurrentLogFileTabName());
		dialog.setBlockOnOpen(true);
		int retval = dialog.open();
		if(retval == EncodingDialog.OK & dialog.isNewValue()) {
			view.setCurrentLogFileTabName(dialog.getValue());
		}
	}

}
