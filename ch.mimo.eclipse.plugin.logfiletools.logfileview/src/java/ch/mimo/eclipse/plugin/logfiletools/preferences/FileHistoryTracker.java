package ch.mimo.eclipse.plugin.logfiletools.preferences;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ch.mimo.eclipse.plugin.logfiletools.ILogFileViewConstants;
import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;

/*
 * Copyright (c) 2006 by Michael Mimo Moratti
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

public class FileHistoryTracker {

	// Constant ----------------------------------------------------------------
	
	private static final FileHistoryTracker instance = new FileHistoryTracker();
	
	// Attribute ---------------------------------------------------------------
	
	private List files;
	
	// Constructor -------------------------------------------------------------
	
	private FileHistoryTracker() {
	}
	
	// Static ------------------------------------------------------------------
	
	public static FileHistoryTracker getInstance() {
		return instance;
	}
	
	// Public ------------------------------------------------------------------
	
	public void storeFile(String path) {
		init();
		if(containsThenIncrement(path)) {
            LogFileViewPlugin.getDefault().getPreferenceStore().setValue(ILogFileViewConstants.PREF_HISTORY_FILES,PreferenceValueConverter.asString(files));
			return;
		}
		HistoryFile file = new HistoryFile(path,0);
		if(files.size() == ILogFileViewConstants.MAX_FILES_IN_HISTORY) {
			files.remove(ILogFileViewConstants.MAX_FILES_IN_HISTORY - 1);
			files.add(file);
		} else {
			files.add(file);		
		}
		Collections.sort(files,new HistoryFileComparator());
		LogFileViewPlugin.getDefault().getPreferenceStore().setValue(ILogFileViewConstants.PREF_HISTORY_FILES,PreferenceValueConverter.asString(files));
	}
	
	public List getFiles() {
		init();
		return files;
	}
	
	// Private -----------------------------------------------------------------
	
	private void init() {
		// initial load
		String historyFilesPrefString = LogFileViewPlugin.getDefault().getPreferenceStore().getString(ILogFileViewConstants.PREF_HISTORY_FILES);
		if(historyFilesPrefString != null && historyFilesPrefString.length() <= 0) {
			files = new Vector();
			return;
		}
		files = PreferenceValueConverter.asUnsortedHistoryFileList(historyFilesPrefString);
		Collections.sort(files,new HistoryFileComparator());
	}
	
	private boolean containsThenIncrement(String path) {
		Iterator it = files.iterator();
		while(it.hasNext()) {
			HistoryFile file = (HistoryFile)it.next();
			if(file.getPath().equals(path)) {
				file.incrementCount();
				Collections.sort(files,new HistoryFileComparator());
				return true;
			}
		}
		return false;
	}
}
