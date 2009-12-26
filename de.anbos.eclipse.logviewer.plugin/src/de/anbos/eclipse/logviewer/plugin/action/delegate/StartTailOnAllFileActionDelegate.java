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

package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogViewer;

public class StartTailOnAllFileActionDelegate implements ILogViewerActionDelegate {

    // Public ------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see de.anbos.eclipse.logviewer.plugin.action.delegate.ILogViewerActionDelegate#run(de.anbos.eclipse.logviewer.plugin.LogViewer, org.eclipse.swt.widgets.Shell)
     */
    public void run(LogViewer view, Shell shell) {
        view.startTailOnAllDocuments();
    }
}
