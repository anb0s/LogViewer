package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.UIImages;
import de.anbos.eclipse.logviewer.plugin.action.delegate.FileEncodingActionDelegate;

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

public class FileEncondingViewAction extends AbstractViewAction {

    // Constructor ------------------------------------------------------------------
    
    public FileEncondingViewAction(LogViewer view, Shell shell) {
        super(view,shell,new FileEncodingActionDelegate());
    }
    
    // Public -----------------------------------------------------------------------
    
    public void run() {
        super.run();
        FileEncodingActionDelegate delegate = (FileEncodingActionDelegate)super.actionDelegate;
        this.setText(LogViewerPlugin.getResourceString("menu.encodingchange.text",new Object[] { delegate.getEncoding()})); //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see de.anbos.eclipse.logviewer.plugin.action.AbstractViewAction#init()
     */
    public void init() {
		String encoding = LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_ENCODING);
		this.setText(LogViewerPlugin.getResourceString("menu.encodingchange.text",new Object[] {encoding})); //$NON-NLS-1$
		this.setToolTipText(LogViewerPlugin.getResourceString("menu.encodingchange.tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_ENCODING_ACTIVE));
    }

}
