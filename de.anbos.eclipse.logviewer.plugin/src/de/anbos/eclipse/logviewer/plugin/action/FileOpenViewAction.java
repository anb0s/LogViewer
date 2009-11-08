package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogFileViewConstants;
import de.anbos.eclipse.logviewer.plugin.LogFileView;
import de.anbos.eclipse.logviewer.plugin.LogFileViewPlugin;
import de.anbos.eclipse.logviewer.plugin.UIImages;
import de.anbos.eclipse.logviewer.plugin.action.delegate.FileOpenActionDelegate;

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

public class FileOpenViewAction extends AbstractViewAction {
	
	// Constructor ------------------------------------------------------------------
	
	public FileOpenViewAction(LogFileView view, Shell shell) {
		super(view,shell,new FileOpenActionDelegate());
	}
	
	// Public -----------------------------------------------------------------------
	
	public void init() {
        this.setText(LogFileViewPlugin.getResourceString("menu.file.open.text")); //$NON-NLS-1$
        this.setToolTipText(LogFileViewPlugin.getResourceString("menu.file.open.tooltip")); //$NON-NLS-1$
        this.setImageDescriptor(UIImages.getImageDescriptor(ILogFileViewConstants.IMG_OPEN_FILE_ACTIVE));
        this.setDisabledImageDescriptor(UIImages.getImageDescriptor(ILogFileViewConstants.IMG_OPEN_FILE_PASSIVE));
	}
}