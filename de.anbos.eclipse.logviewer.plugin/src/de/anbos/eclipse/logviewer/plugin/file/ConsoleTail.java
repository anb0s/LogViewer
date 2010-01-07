/*
 * Copyright 2009, 2010 by Andre Bossert
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

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;

import de.anbos.eclipse.logviewer.plugin.ConsolePageParticipant;

public class ConsoleTail implements IDocumentListener {

	private String fullName;
	private IFileChangedListener listener;
	IDocument doc;

	private boolean isRunning;
	private boolean isFirstTimeRead;

	// Constructor -------------------------------------------------------------

	public ConsoleTail(String myName, IFileChangedListener myListener) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, PartInitException {
		fullName = myName;
		listener = myListener;
		findDocument(fullName);		
		isFirstTimeRead = true;
	}

	// Public ------------------------------------------------------------------

	public void setMonitorStatus(boolean monitor) {
		if (isRunning == monitor) {
			return;
		}
		isRunning = monitor;
		if (isRunning) {
			doc.addDocumentListener(this);
			if (isFirstTimeRead) {
				DocumentEvent event = new DocumentEvent();
				event.fText = doc.get();
				documentAboutToBeChanged(event);
				documentChanged(event);
			}
		} else {
			doc.removeDocumentListener(this);
			isFirstTimeRead = true;
		}
	}

	// Private -----------------------------------------------------------------

	private IConsole findConsole(String name) {
		ConsolePlugin conPlugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = conPlugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return existing[i];
			}
		}
		return null;
	}

	private IConsole createConsole(String className) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class [] classParm = null;
		Object [] objectParm = null;
		//try {
			Class cl = Class.forName(className);
			java.lang.reflect.Constructor co = cl.getConstructor(classParm);
			return (IConsole)co.newInstance(objectParm);
		//}
		//catch (Exception e) {
		//}
		//return null;
	}

	private void findDocument(String fullName) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, PartInitException {
		doc = null;
		String name = getName();
		IConsole con = findConsole(name);
		if (con == null) {
			String className = getClassName();
			con = createConsole(className);
		}
		// check and open
		if (con != null) {
			if(con instanceof TextConsole) {
				doc = ((TextConsole)con).getDocument();
			} else {
				// Now open the view and console
				IConsoleView view = null;
				//try {
					view = (IConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
				//} catch (PartInitException e) {
				//	e.printStackTrace();
				//}
				if (view != null) {
					// show it 
					view.display(con);
			        IViewPart vp =(IViewPart)view;
			        if (vp instanceof PageBookView) {
			            IPage page = ((PageBookView) vp).getCurrentPage();
			            ITextViewer viewer = ConsolePageParticipant.getViewer(page);
			            if (viewer != null)
			            	doc = viewer.getDocument();
			        }
				}
			}
		}
	}

	public String getFullName() {
		return fullName;
	}

	public String getName() {		
		return fullName.substring(fullName.lastIndexOf(System.getProperty("file.separator")) + 1);
	}

	public String getClassName() {
		int idx = fullName.indexOf(System.getProperty("file.separator"));
		return idx != -1 ? fullName.substring(0, idx) : fullName;
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		listener.contentAboutToBeChanged();
	}

	public void documentChanged(DocumentEvent event) {
		listener.fileChanged(event.getText().toCharArray(), isFirstTimeRead);
		isFirstTimeRead = false;
	}
}
