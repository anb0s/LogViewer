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

package de.anbos.eclipse.logviewer.plugin;

public class LogFile {

    // Attribute ---------------------------------------------------------------

	public enum LogFileType {
		LOGFILE_SYSTEM_FILE,
		LOGFILE_ECLIPSE_CONSOLE,
		LOGFILE_STREAM
	};

    private String path;
    private LogFileType type;
    private String namePattern;
    private String encoding;
    private boolean monitor;

    // Constructor -------------------------------------------------------------

    public LogFile(LogFileType type, String path, String namePattern, String encoding, boolean monitor) {
    	this.type = type;
        this.path = path;
        setNamePattern(namePattern);
        setEncoding(encoding);
        setMonitor(monitor);
    }

    // Public ------------------------------------------------------------------

    public LogFileType getType() {
		return type;
	}

    public String getPath() {
        return path;
    }

	public String getNamePattern() {
        return namePattern;
    }

    public String getEncoding() {
		return encoding;
	}

    public String getKey() {
        switch(type) {
            case LOGFILE_SYSTEM_FILE: return type.toString() + System.getProperty("file.separator") + namePattern;
            default: return path;
        }
    }

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean getMonitor() {
        return monitor;
    }

    public String getName() {
        int index = path.lastIndexOf(System.getProperty("file.separator"));
        return index != -1 ? path.substring(index + 1) : path;
    }

    public void setNamePattern(String namePattern) {
        if ((namePattern == null) || namePattern.isEmpty()) {
        	this.namePattern = getName();
        } else {
        	this.namePattern = namePattern;
        }
    }

    public void setMonitor(boolean monitor) {
    	this.monitor = monitor;
    }
}
