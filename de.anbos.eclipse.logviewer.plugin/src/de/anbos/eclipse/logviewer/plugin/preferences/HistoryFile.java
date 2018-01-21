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

import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;

public class HistoryFile {

	// Attribute ---------------------------------------------------------------

	private String path;
	private String namePattern;
	private LogFileType type;
	private int count;

	// Constructor -------------------------------------------------------------

	public HistoryFile(String path, String namePattern, LogFileType type, int count) {
		this.path = path;
		this.namePattern = namePattern;
		if (namePattern == null || namePattern.isEmpty()) {
		    this.namePattern = getFileName();
		}
		this.type = type;
		this.count = count;
	}

	// Public ------------------------------------------------------------------

	public String getPath() {
		return path;
	}

    public String getNamePattern() {
        return namePattern;
    }

	public LogFileType getType() {
		return type;
	}

	private String getFileName() {
		return path.substring(path.lastIndexOf(System.getProperty("file.separator")) + 1);
	}

	public String getName() {
		String name = null;
		if (type == LogFileType.LOGFILE_SYSTEM_FILE) {
			name = "File: ";
		} else if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {
			name = "Console: ";
		}
		return name + getNamePattern();
	}

	public int getCount() {
		return count;
	}

	public void incrementCount() {
		count++;
	}

	public boolean equals(Object object) {
		if(object instanceof HistoryFile) {
			HistoryFile file = (HistoryFile)object;

			return file.getPath().equals(this.getPath()) &&
			       file.getType() == this.getType();
		}
		return false;
	}
}
