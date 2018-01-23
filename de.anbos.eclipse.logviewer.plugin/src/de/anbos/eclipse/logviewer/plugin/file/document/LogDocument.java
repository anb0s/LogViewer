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

package de.anbos.eclipse.logviewer.plugin.file.document;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.GapTextStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogFile;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.file.BackgroundReader;
import de.anbos.eclipse.logviewer.plugin.file.IFileChangedListener;

public class LogDocument extends AbstractDocument implements IFileChangedListener {

	// Attribute ---------------------------------------------------------------

	private LogFile file;
	private Charset charset;
	private String encoding;
	private BackgroundReader reader;
	private boolean monitor;

	private int backlogLines;

	// Constructor -------------------------------------------------------------

	public LogDocument(LogFile file, String encoding) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, PartInitException {
		super();
		if (file.getEncoding() == null) {
			file.setEncoding(encoding);
		}
		this.file = file;
		this.encoding = file.getEncoding();
		this.charset = Charset.forName(file.getEncoding());
		IPreferenceStore store = LogViewerPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(new PropertyChangeListener());
		backlogLines = store.getInt(ILogViewerConstants.PREF_BACKLOG);
		setTextStore(new GapTextStore(50, 300, 1f));
		setLineTracker(new DefaultLineTracker());
		completeInitialization();
		reader = new BackgroundReader(file.getType(), file.getPath(), file.getNamePattern(), charset,this);
	}

	// Public ------------------------------------------------------------------

	/**
	 * invoking that setter will cause the tail thread to stop and a new FileTail
	 * instance is created with the given charset.
	 */
	public void setEncoding(String encoding) {
		setMonitor(false);
		this.file.setEncoding(encoding);
		this.encoding = encoding;
		this.charset = Charset.forName(encoding);
		try {
			reader = new BackgroundReader(file.getType(), file.getPath(), file.getNamePattern(), charset, this);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setMonitor(true);
	}

	/**
	 * @return the current content encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * invoking that method will cause the tail thread to stop and a new FileTail
	 * instance is create.
	 */
	public void synchronize() {
		setMonitor(false);
		getStore().set("");
		getTracker().set("");
		try {
			reader = new BackgroundReader(file.getType(), file.getPath(), file.getNamePattern(), charset, this);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setMonitor(true);
	}

	/**
	 * setter for the amount of lines that the view has to dislay at any time.
	 * It is although possible that the view can display some more lines for
	 * a short time. But that amount can be consideret unimportant towards the
	 * memory consumption.
	 * @param lines int amount of lines to display
	 */
	public void setBacklog(int lines) {
		backlogLines = lines;
	}

	/**
	 * if the monitor parameter is true and the current FileTail instance thread
	 * is not running the Thread will be inovked and the tail begins to update
	 * this document.
	 *
	 * if the monitor parameter is false the FileTail instance thread is notified
	 * to stop at the next possbile exit point.
	 * @param monitor
	 */
	public void setMonitor(boolean monitorIn) {
		if(monitorIn && !monitor) {
			getStore().set(""); //$NON-NLS-1$
			getTracker().set(""); //$NON-NLS-1$
		}
		monitor = monitorIn;
		reader.setMonitorStatus(monitor);
		file.setMonitor(monitor);
	}

	public boolean isMonitor() {
		return monitor;
	}

	/* (non-Javadoc)
	 * @see de.anbos.eclipse.logviewer.plugin.file.IFileChangedListener#contentAboutToBeChanged()
	 */
	public void contentAboutToBeChanged() {
        Runnable runnable = new Runnable() {
            public void run() {
        		DocumentEvent event = new DocumentEvent(LogDocument.this, getStore().getLength(),0,""); //$NON-NLS-1$
        		fireDocumentAboutToBeChanged(event);
            }
        };
        if(Display.getDefault() != null) {
            Display.getDefault().asyncExec(runnable);
        }
	}

	/* (non-Javadoc)
	 * @see de.anbos.eclipse.logviewer.plugin.file.IFileChangedListener#fileChanged(char[])
	 */
	public void fileChanged(final char[] content, final boolean isFirstTimeRead) {
        Runnable runnable = new Runnable() {
            public void run() {
        		String text = new String(content);
        		int offset = calculateBacklogOffset();
    			String currentText = getStore().get(offset,getStore().getLength() - offset);
        		currentText = currentText.concat(text);
        		getStore().set(currentText);
        		getTracker().set(currentText);
        		if(isFirstTimeRead) {
        			offset = calculateBacklogOffset();
        			currentText = getStore().get(offset,getStore().getLength() - offset);
        			getStore().set(currentText);
        			getTracker().set(currentText);
        		}
        		//int newOffset = getStore().getLength() > content.length ? getStore().getLength() - content.length : 0;
        		DocumentEvent event = new DocumentEvent(LogDocument.this, getStore().getLength(), content.length, text);
        		fireDocumentChanged(event);
            }
        };
        if(Display.getDefault() != null) {
            Display.getDefault().asyncExec(runnable);
        }
	}

	public LogFile getFile() {
		return file;
	}

	// Private -----------------------------------------------------------------

	/**
	 * @return the offset from where we have to read on in the Store
	 */
	private int calculateBacklogOffset() {
		int numberOfLines = getTracker().getNumberOfLines();
		if(numberOfLines <= backlogLines) {
			return 0;
		}
		int overflow = numberOfLines - backlogLines;
		try {
			return getTracker().getLineOffset(overflow);
		} catch(BadLocationException ble) {
			return 0;
		}
	}

	// Inner classes ----------------------------------------------------------------

	private class PropertyChangeListener implements IPropertyChangeListener {

			/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if(event.getProperty().equals(ILogViewerConstants.PREF_BACKLOG)) {
				backlogLines = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_BACKLOG);
			}
		}
	}

	public BackgroundReader getReader() {
		return reader;
	}
}
