package ch.mimo.eclipse.plugin.logfiletools;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

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

public class LoggerImpl implements Logger {

	// Attribute --------------------------------------------------------------------
	
	private ILog log;
	private String pluginId;
	
	// Constructor ------------------------------------------------------------------
	
	public LoggerImpl(ILog log, String pluginId) {
		this.log = log;
		this.pluginId = pluginId;
	}
	
	// Public -----------------------------------------------------------------------
	
	public void logInfo(String message) {
		logInfo(message,null);
	}
	
	public void logInfo(Throwable throwable) {
		logInfo("",throwable);
	}
	
	public void logInfo(String message, Throwable throwable) {
		createStatus(IStatus.INFO,IStatus.OK,message,throwable);
	}
	
	public void logWarning(String message) {
		logWarning(message,null);
	}
	
	public void logWarning(Throwable throwable) {
		logWarning("",throwable);
	}
	
	public void logWarning(String message, Throwable throwable) {
		createStatus(IStatus.WARNING,IStatus.OK,message,throwable);
	}
	
	public void logError(String message) {
		logError(message,null);
	}
	
	public void logError(Throwable throwable) {
		logError("",throwable);
	}
	
	public void logError(String message, Throwable throwable) {
		createStatus(IStatus.ERROR,IStatus.OK,message,throwable);
	}
	
	// Private ----------------------------------------------------------------------
	
	/**
	 * main log method
	 * This method transform the incomming data into an @see IStatus object and does
	 * the handover to the @see ILog instance.
	 */
	private void createStatus(int severity, int code, String message, Throwable throwable) {
		IStatus status = new Status(severity,pluginId,code,message,throwable);
		log.log(status);
	}
}
