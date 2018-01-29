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
 *    Artur Wozniak - clear file
 *
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin.file;

import java.io.FileNotFoundException;
import java.util.regex.Pattern;

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
	private String path;
	private String namePattern;
	private IFileChangedListener listener;
	private IDocument doc;
	private IConsole con;
	private ITextViewer viewer;
	private boolean isRunning;
	private boolean isFirstTimeRead;
	private Pattern regexp;

	// Constructor -------------------------------------------------------------

	public ConsoleTail(String path, String namePattern, IFileChangedListener listener) {
		logger = LogViewerPlugin.getDefault().getLogger();
		this.path = path;
		if ((namePattern == null) || namePattern.isEmpty()) {
		    int index = path.lastIndexOf(System.getProperty("file.separator"));
		    namePattern = index != -1 ? path.substring(index + 1) : path;
		} else {
		    this.namePattern = namePattern;
		}
		this.listener = listener;
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

	public String getPath() {
		return path;
	}

    public String getConsolePath(IConsole console) {
        return console.getClass().toString().replaceFirst("class ", "") + System.getProperty("file.separator") + console.getName();
	}

	public String getNamePattern() {
		return namePattern;
	}

	public String getClassName() {
		int idx = path.indexOf(System.getProperty("file.separator"));
		return idx != -1 ? path.substring(0, idx) : path;
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
					listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file",new String[]{path}).toCharArray(),true);
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
			listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.error",new String[]{path}).toCharArray(),true);
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
				con = findConsole();
				if (con != null) {
					myDoc = getConsoleDocument();
				}
				isFirstTimeRead = true;
				return myDoc;
			} catch(FileNotFoundException fnfe) {
				try {
					if (firstExec) {
						listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.warning",new String[]{path}).toCharArray(),true);
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

	private IConsole findConsole() throws FileNotFoundException {
		ConsolePlugin conPlugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = conPlugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		int nameFoundIndex = -1;
		for (int i = 0; i < existing.length; i++) {
		    // check full name first
			if (getConsolePath(existing[i]).equals(getPath())) {
				return existing[i];
			}
			// check short name if not already found
	        int flags = 0;
            flags = java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE;
	        regexp = Pattern.compile(getNamePattern(), flags);
            if ((nameFoundIndex == -1) && regexp.matcher(existing[i].getName()).matches()) {
                nameFoundIndex = i;
            }
		}
		if (nameFoundIndex != -1) {
		    return existing[nameFoundIndex];
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

	public IConsole getConsole() {
		return con;
	}
}
