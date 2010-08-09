/*
 * Copyright 2009 - 2010 by Andre Bossert
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


package de.anbos.eclipse.logviewer.plugin.file;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.eclipse.ui.PartInitException;

import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;

public class BackgroundReader {

	private FileTail fileTail;
	private ConsoleTail consoleTail;
	private LogFileType type;

	public BackgroundReader(LogFileType type, String name, Charset charset, IFileChangedListener listener) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, PartInitException {
		this.type = type;
		if (type == LogFileType.LOGFILE_SYSTEM_FILE) {
			fileTail = new FileTail(name,charset,listener);
		} else if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {
			consoleTail = new ConsoleTail(name,listener);
		}
	}
	
	public void setMonitorStatus(boolean monitor) {
		if (type == LogFileType.LOGFILE_SYSTEM_FILE) {
			fileTail.setMonitorStatus(monitor);
		} else if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {
			consoleTail.setMonitorStatus(monitor);
		}
	}
}
