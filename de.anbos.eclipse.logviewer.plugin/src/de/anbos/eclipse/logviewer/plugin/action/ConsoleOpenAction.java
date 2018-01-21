/*******************************************************************************
 * Copyright (c) 2009 - 2018 by Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Bossert - initial API and implementation and/or initial documentation
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;

import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;


public class ConsoleOpenAction implements IObjectActionDelegate {

	IConsole resource[] = null;
	IStructuredSelection currentSelection = null;

	/**
	 * Constructor for EasyExploreAction.
	 */
	public ConsoleOpenAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		if (!isEnabled()) {
			MessageDialog.openInformation(
				new Shell(),
				"Logfile Viewer",
				"Wrong Selection");
			return;
		}

		for (int i=0;i<resource.length;i++) {

			if (resource[i] == null)
				continue;

			String full_path = resource[i].getClass().toString().replaceFirst("class ", "") + System.getProperty("file.separator") + resource[i].getName();
			LogViewer view = null;

			try {
				view = (LogViewer) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.anbos.eclipse.logviewer.plugin.LogViewer");
			} catch (PartInitException e) {
				e.printStackTrace();
			}

			view.checkAndOpenFile(LogFileType.LOGFILE_ECLIPSE_CONSOLE, full_path, null, false);
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	    currentSelection = selection instanceof IStructuredSelection ? (IStructuredSelection)selection : null;
	}

	public boolean isEnabled()
	{
		boolean enabled = false;
		if (currentSelection != null)
		{
			Object[] selectedObjects = currentSelection.toArray();
			if (selectedObjects.length >= 1)
			{
				resource = new IConsole[selectedObjects.length];
				for (int i=0;i<selectedObjects.length;i++) {
					resource[i] = (IConsole)selectedObjects[i];
					if (resource[i] != null)
						enabled=true;
				}
			}
		}
		return enabled;
	}

}
