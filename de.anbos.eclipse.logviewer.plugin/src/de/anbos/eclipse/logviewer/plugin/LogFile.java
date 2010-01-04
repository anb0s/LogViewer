package de.anbos.eclipse.logviewer.plugin;

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

public class LogFile {

    // Attribute ---------------------------------------------------------------
    
	public enum LogFileType{
		LOGFILE_SYSTEM_FILE,
		LOGFILE_ECLIPSE_CONSOLE,
		LOGFILE_STREAM
	};

    private String fileName;
    private LogFileType fileType;
    private String tabName;
    private String encoding;
    private boolean monitor;
    
    // Constructor -------------------------------------------------------------
    
    public LogFile(LogFileType fileType, String fileName, String tabName, String encoding, boolean monitor) {
    	this.fileType = fileType;
        this.fileName = fileName;
        setTabName(tabName);
        setEncoding(encoding);
        setMonitor(monitor);
    }
    
    // Public ------------------------------------------------------------------

    public LogFileType getFileType() {
		return fileType;
	}
    
    public String getFileName() {
        return fileName;
    }

	public String getTabName() {
        return tabName;
    }

    public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean getMonitor() {
        return monitor;
    }

    public void setTabName(String tabName) {
        if (tabName == null || tabName.isEmpty()) {
        	this.tabName = fileName.substring(fileName.lastIndexOf(System.getProperty("file.separator")) + 1,fileName.length());
        } else {
        	this.tabName = tabName;
        }
    }

    public void setMonitor(boolean monitor) {
    	this.monitor = monitor;
    }    
}
