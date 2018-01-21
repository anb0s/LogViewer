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

package de.anbos.eclipse.logviewer.plugin.file;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.eclipse.ui.PartInitException;

import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;

public class BackgroundReader {

	private FileTail fileTail;
	private ConsoleTail consoleTail;
	private LogFileType type;

	public BackgroundReader(LogFileType type, String path, String namePattern, Charset charset, IFileChangedListener listener) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, PartInitException {
		this.type = type;
		if (type == LogFileType.LOGFILE_SYSTEM_FILE) {
			fileTail = new FileTail(path, charset, listener);
		} else if (type == LogFileType.LOGFILE_ECLIPSE_CONSOLE) {
			consoleTail = new ConsoleTail(path, namePattern, listener);
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
