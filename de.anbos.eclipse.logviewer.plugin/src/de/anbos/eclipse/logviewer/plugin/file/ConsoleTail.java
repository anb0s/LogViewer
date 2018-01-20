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

import java.io.FileNotFoundException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.ui.progress.UIJob;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.Logger;
import de.anbos.eclipse.logviewer.plugin.ResourceUtils;

public class ConsoleTail implements IDocumentListener, Runnable {

    private Logger logger;	
	private String fullName;
	private IFileChangedListener listener;
	private IDocument doc;
	private IConsole con;
	private ITextViewer viewer;
	private boolean isRunning;
	private boolean isFirstTimeRead;

	// Constructor -------------------------------------------------------------

	public ConsoleTail(String myName, IFileChangedListener myListener) {
		logger = LogViewerPlugin.getDefault().getLogger();
		fullName = myName;
		listener = myListener;
		isRunning = false;
		isFirstTimeRead = true;
		doc = null;
	}

	// Public ------------------------------------------------------------------

	public void setMonitorStatus(boolean monitor) {
        if(isRunning == monitor) {
            return;
        }
        isRunning = monitor;
        if(isRunning) {
            Thread tailThread = new Thread(this);
            tailThread.setDaemon(true);
            tailThread.start();
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
	
	public synchronized void run() {
		isRunning = true;
		try {
			int readwait = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_READWAIT);
			doc = openConsole();
			if(doc != null) {
				doc.addDocumentListener(this);
				if (isFirstTimeRead) {
					listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file",new String[]{fullName}).toCharArray(),true);
					DocumentEvent event = new DocumentEvent();
					event.fText = doc.get();
					documentAboutToBeChanged(event);
					documentChanged(event);
				}			
			} else {
				throw new ThreadInterruptedException("document was null"); //$NON-NLS-1$
			}
			while(isRunning) {
				wait(readwait);
			}
		} catch(ThreadInterruptedException tie) {
			logger.logError(tie);
			listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.error",new String[]{fullName}).toCharArray(),true);
		} catch(InterruptedException ie) {
			logger.logError(ie);
		} catch(NullPointerException npe) {
			logger.logError(npe);
			npe.printStackTrace();
		} finally {
			try {
				if(doc != null) {
					doc.removeDocumentListener(this);
					isFirstTimeRead = true;					
				}
			} catch(Exception e) {
				// ignore this
			}
		}
		isRunning = false;
	}

	// Private -----------------------------------------------------------------

	private synchronized IDocument openConsole() throws ThreadInterruptedException {
		IDocument myDoc = null;
		boolean firstExec = true;
		while(isRunning) {
			try {
				con = findConsole(getName());
				if (con != null) {
					myDoc = getConsoleDocument();
				}
				isFirstTimeRead = true;
				return myDoc;
			} catch(FileNotFoundException fnfe) {
				try {
					if (firstExec) {
						listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.warning",new String[]{fullName}).toCharArray(),true);
						firstExec = false;
					}
					wait(ILogViewerConstants.TAIL_FILEOPEN_ERROR_WAIT);
				} catch(InterruptedException ie) {
					throw new ThreadInterruptedException(ie);
				}
			}
		}
		throw new ThreadInterruptedException("no console found"); //$NON-NLS-1$
	}
	
	private IConsole findConsole(String name) throws FileNotFoundException {
		ConsolePlugin conPlugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = conPlugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (existing[i].getName().contains(name)) {
				return existing[i];
			}
		}
		throw new FileNotFoundException("no console found");
	}

	/*
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
	*/
	
	private IDocument getConsoleDocument() throws FileNotFoundException {
		if (con != null) {
			if(con instanceof TextConsole) {
				return ((TextConsole)con).getDocument();
			} else {				
				// Now open the view and console in UI-Thread
				UIJob uiJob = new UIJob("Update UI") {
					@Override
				    public IStatus runInUIThread(IProgressMonitor monitor) {
						IConsoleView view = null;
						try {
							view = (IConsoleView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IConsoleConstants.ID_CONSOLE_VIEW);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
						if (view != null) {
							// show it 
							view.display(con);
					        IViewPart vp =(IViewPart)view;
					        if (vp instanceof PageBookView) {
					            IPage page = ((PageBookView) vp).getCurrentPage();
					            viewer = ResourceUtils.getViewer(page);
					            //if (viewer != null)
					            //	return viewer.getDocument();
					        }
						}
				        return Status.OK_STATUS;
				    }
				};
				uiJob.schedule();
	            if (viewer != null)
	            	return viewer.getDocument();				
			}
		}
		throw new FileNotFoundException("no document found");
	}
}
