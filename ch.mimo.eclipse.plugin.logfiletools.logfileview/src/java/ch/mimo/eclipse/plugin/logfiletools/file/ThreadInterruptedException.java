package ch.mimo.eclipse.plugin.logfiletools.file;

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

public class ThreadInterruptedException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ThreadInterruptedException() {
		super();
	}
	/**
	 * @param message
	 */
	public ThreadInterruptedException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public ThreadInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public ThreadInterruptedException(Throwable cause) {
		super(cause);
	}
}
