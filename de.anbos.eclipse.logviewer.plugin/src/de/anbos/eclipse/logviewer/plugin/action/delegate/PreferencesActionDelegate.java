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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import de.anbos.eclipse.logviewer.plugin.LogViewer;


public class PreferencesActionDelegate implements ILogViewerActionDelegate {

	public void run(LogViewer view, Shell shell) {
		
		/* 
		 * first try: create dialog with preference page
		 * 
		IPreferencePage page = new RulePreferencePage();
		PreferenceManager mgr = new PreferenceManager();
		IPreferenceNode node = new PreferenceNode("1", page);
		mgr.addToRoot(node);
		PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
		dialog.create();
		dialog.setMessage(page.getTitle());
		dialog.open();
		*/

		/*
		 * second try: use command framework 
		 */

		// get services
		IWorkbenchWindow window = view.getSite().getWorkbenchWindow();
		ICommandService cS = (ICommandService)window.getService(ICommandService.class);
		IHandlerService hS = (IHandlerService)window.getService(IHandlerService.class);
		
		// get command for preference pages
		Command cmd        = cS.getCommand("org.eclipse.ui.window.preferences");

		// add parameter preferencePageId
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("preferencePageId", "de.anbos.eclipse.logviewer.plugin.LogViewer.page1.3");

		// create command with parameters
		ParameterizedCommand pC = ParameterizedCommand.generateCommand(cmd, params);
		
		// execute parametrized command
		try {
			hS.executeCommand(pC, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
