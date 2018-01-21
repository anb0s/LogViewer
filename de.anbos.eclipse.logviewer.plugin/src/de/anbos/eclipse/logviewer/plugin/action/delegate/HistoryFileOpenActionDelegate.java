/*******************************************************************************
 * Copyright (c) 2007 - 2011 by Michael Mimo Moratti
 * Copyright (c) 2012 - 2018 by Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Mimo Moratti - initial API and implementation and/or initial documentation
 *    Andre Bossert - extensions
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.preferences.HistoryFile;

public class HistoryFileOpenActionDelegate implements ILogViewerActionDelegate {

	// Attribute ---------------------------------------------------------------

	private HistoryFile file;

	// Constructor -------------------------------------------------------------

	public HistoryFileOpenActionDelegate(HistoryFile file) {
		this.file = file;
	}

	// Public ------------------------------------------------------------------

	public void run(LogViewer view, Shell shell) {
		// opening file in logfile view
	    view.checkAndOpenFile(file.getType(), file.getPath(), file.getNamePattern(), true);
	}
}
