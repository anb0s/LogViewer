package de.anbos.eclipse.logviewer.plugin.action.delegate;

import java.util.ResourceBundle;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.FindReplaceAction;

import de.anbos.eclipse.logviewer.plugin.LogFileView;
import de.anbos.eclipse.logviewer.plugin.LogFileViewPlugin;

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

public class FindReplaceActionDelegate implements ILogfileActionDelegate {

	// Public ------------------------------------------------------------------
	
	public void run(LogFileView view, Shell shell) {
		ResourceBundle bundle = LogFileViewPlugin.getDefault().getResourceBundle();
		FindReplaceAction action = new FindReplaceAction(bundle,"dialog.findreplace.",view);
		action.run();
	}

}
