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

package de.anbos.eclipse.logviewer.plugin.preferences;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;

public class FileHistoryTracker {

	// Constant ----------------------------------------------------------------

	private static final FileHistoryTracker instance = new FileHistoryTracker();

	// Attribute ---------------------------------------------------------------

	private List<HistoryFile> files;

	// Constructor -------------------------------------------------------------

	private FileHistoryTracker() {
	}

	// Static ------------------------------------------------------------------

	public static FileHistoryTracker getInstance() {
		return instance;
	}

	// Public ------------------------------------------------------------------

	public void storeFile(LogFileType type, String path, String namePattern) {
		init();
		HistoryFile file = new HistoryFile(path, namePattern, type, 0);
		if(containsThenIncrement(file)) {
            LogViewerPlugin.getDefault().getPreferenceStore().setValue(ILogViewerConstants.PREF_HISTORY_FILES,PreferenceValueConverter.asString(files));
			return;
		}
		if(files.size() == ILogViewerConstants.MAX_FILES_IN_HISTORY) {
			files.remove(ILogViewerConstants.MAX_FILES_IN_HISTORY - 1);
			files.add(file);
		} else {
			files.add(file);
		}
		Collections.sort(files,new HistoryFileComparator());
		LogViewerPlugin.getDefault().getPreferenceStore().setValue(ILogViewerConstants.PREF_HISTORY_FILES,PreferenceValueConverter.asString(files));
	}

	public List<HistoryFile> getFiles() {
		init();
		return files;
	}

	public void clearFiles() {
		init();
		files.clear();
		LogViewerPlugin.getDefault().getPreferenceStore().setValue(ILogViewerConstants.PREF_HISTORY_FILES,PreferenceValueConverter.asString(files));
	}

	// Private -----------------------------------------------------------------

	private void init() {
		// initial load
		String historyFilesPrefString = LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_HISTORY_FILES);
		if(historyFilesPrefString != null && historyFilesPrefString.length() <= 0) {
			files = new Vector<HistoryFile>();
			return;
		}
		files = PreferenceValueConverter.asUnsortedHistoryFileList(historyFilesPrefString);
		Collections.sort(files,new HistoryFileComparator());
	}

	private boolean containsThenIncrement(HistoryFile file) {
		Iterator<HistoryFile> it = files.iterator();
		while(it.hasNext()) {
			HistoryFile fileOld = it.next();
			if(fileOld.equals(file)) {
				fileOld.incrementCount();
				Collections.sort(files,new HistoryFileComparator());
				return true;
			}
		}
		return false;
	}
}
