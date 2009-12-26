/*
 * Copyright 2009 by Andre Bossert
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

package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.UIImages;
import de.anbos.eclipse.logviewer.plugin.action.delegate.PreferencesActionDelegate;

public class PreferencesViewAction extends AbstractViewAction {
	
	// Constructor ------------------------------------------------------------------
	
	public PreferencesViewAction(LogViewer view, Shell shell) {
		super(view,shell,new PreferencesActionDelegate());
	}
	
	// Public -----------------------------------------------------------------------
	
	public void init() {
        this.setText(LogViewerPlugin.getResourceString("menu.preferences.text")); //$NON-NLS-1$
        this.setToolTipText(LogViewerPlugin.getResourceString("menu.preferences.tooltip")); //$NON-NLS-1$
        this.setImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_PREFERENCES_ACTIVE));
        this.setDisabledImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_PREFERENCES_PASSIVE));
	}
}
