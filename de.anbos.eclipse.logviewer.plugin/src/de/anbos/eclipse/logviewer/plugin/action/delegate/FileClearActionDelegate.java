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

package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;

public class FileClearActionDelegate implements ILogViewerActionDelegate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.anbos.eclipse.logviewer.plugin.action.ILogfileAction#run(de.anbos.
	 * eclipse.logviewer.plugin.LogViewer, org.eclipse.swt.widgets.Shell)
	 */
	public void run(LogViewer view, Shell shell) {

		LogFileType type = view.getCurrentDocument().getFile().getType();
		if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {
			view.clearCurrentLogFile();
		} else if (confirmFileClear(shell)) {
			view.clearCurrentLogFile();
		}
	}

	private boolean confirmFileClear(Shell shell) {
		return MessageDialog.openQuestion(shell, LogViewerPlugin.getResourceString("dialog.file.clear.title"), //$NON-NLS-1$
				LogViewerPlugin.getResourceString("dialog.file.clear.text")); //$NON-NLS-1$
	}
}
