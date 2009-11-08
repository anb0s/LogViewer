package ch.mimo.eclipse.plugin.logfiletools.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import ch.mimo.eclipse.plugin.logfiletools.LogFileView;
import ch.mimo.eclipse.plugin.logfiletools.action.delegate.ILogfileActionDelegate;

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

public abstract class AbstractViewAction extends Action {

	// Attribute --------------------------------------------------------------------
	
	protected ILogfileActionDelegate actionDelegate;
	protected LogFileView view;
	protected Shell shell;
	
	// Constructor ------------------------------------------------------------------
	
	public AbstractViewAction(LogFileView view, Shell shell, ILogfileActionDelegate actionDelegate) {
		super();
		this.view = view;
		this.shell = shell;
		this.actionDelegate = actionDelegate;
		init();
	}
	
	// Public -----------------------------------------------------------------------
	
	public void run() {
		actionDelegate.run(view,shell);
	}
	
	public abstract void init();
}
