/*
 * Copyright 2009, 2010 by Andre Bossert
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

package de.anbos.eclipse.logviewer.plugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import de.anbos.eclipse.logviewer.plugin.action.ConsoleOpenAction;

public class ConsolePageParticipant implements IConsolePageParticipant {

	private IPageBookViewPage page;

	public void activated() {
		// no op
	}

	public void deactivated() {
		// no op
	}

	public void dispose() {
		page = null;
	}

	public void init(IPageBookViewPage myPage, IConsole console) {
        page = myPage;
        IToolBarManager toolBarManager = page.getSite().getActionBars()
        .getToolBarManager();
        toolBarManager.appendToGroup(IConsoleConstants.OUTPUT_GROUP, new Separator());
        toolBarManager.appendToGroup(IConsoleConstants.OUTPUT_GROUP, new Action(
        		LogViewerPlugin.getResourceString("logviewer.action.openwith.name"),
        		UIImages.getImageDescriptor(ILogViewerConstants.IMG_LOG_VIEWER)) {
            public void run() {
            	ConsolePageParticipant.this.run();
            }
        });
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	private void run() {
		IWorkbenchPart part = page.getSite().getWorkbenchWindow().getActivePage().getActivePart();
		ConsoleOpenAction action = EditorPropertyTester.hasAbstractConsole(part);
		if (action != null)
			action.run(null);
	}

}
