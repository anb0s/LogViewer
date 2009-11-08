package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogFileView;
import de.anbos.eclipse.logviewer.plugin.LogFileViewPlugin;
import de.anbos.eclipse.logviewer.plugin.action.delegate.HistoryFileOpenActionDelegate;
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

public class HistoryFileOpenViewAction extends AbstractViewAction {
	
	// Constructor -------------------------------------------------------------
	
	public HistoryFileOpenViewAction(HistoryFile file, LogFileView view, Shell shell) {
		super(view,shell,new HistoryFileOpenActionDelegate(file));
		this.setText(file.getFileName());
	}
	
	// Public ------------------------------------------------------------------
	
	public void init() {
		// TODO make this dynamic
        this.setToolTipText(LogFileViewPlugin.getResourceString("menu.file.open.tooltip")); //$NON-NLS-1$

	}

}
