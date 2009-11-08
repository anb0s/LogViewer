package de.anbos.eclipse.logviewer.plugin.preferences;

import java.util.Comparator;

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

public class HistoryFileComparator implements Comparator {

	// Public ------------------------------------------------------------------
	
	public int compare(Object arg0, Object arg1) {
		if(!(arg0 instanceof HistoryFile) || !(arg1 instanceof HistoryFile)) {
			return -1;
		}
		HistoryFile file0 = (HistoryFile)arg0;
		HistoryFile file1 = (HistoryFile)arg1;
		if(file0.getCount() < file1.getCount()) {
			return 1;
		}
		if(file0.getCount() == file1.getCount()) {
			return 0;
		}
		if(file0.getCount() > file1.getCount()) {
			return -1;
		}
		return -1;
	}

}
