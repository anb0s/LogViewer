package ch.mimo.eclipse.plugin.logfiletools.file.document;

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

import ch.mimo.eclipse.plugin.logfiletools.ILogFileViewConstants;
import ch.mimo.eclipse.plugin.logfiletools.LogFile;
import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;
import ch.mimo.eclipse.plugin.logfiletools.file.IFileChangedListener;
import ch.mimo.eclipse.plugin.logfiletools.file.Tail;

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

public class LogDocument extends AbstractDocument implements IFileChangedListener {
	
	// Attribute ---------------------------------------------------------------
	
	private LogFile file;
	private Charset charset;
	private String encoding;
	private Tail tail;
	private boolean monitor;
	
	private int backlogLines; 
	
	// Constructor -------------------------------------------------------------
	
	public LogDocument(LogFile file, String encoding) {
		super();
		this.file = file;
		this.encoding = encoding;
		this.charset = Charset.forName(encoding);
		IPreferenceStore store = LogFileViewPlugin.getDefault().getPreferenceStore();
		store.addPropertyChangeListener(new PropertyChangeListener());
		backlogLines = store.getInt(ILogFileViewConstants.PREF_BACKLOG);
		setTextStore(new GapTextStore(50, 300));
		setLineTracker(new DefaultLineTracker());
		completeInitialization();
		tail = new Tail(file.getFileName(),charset,this);
	}
	
	// Public ------------------------------------------------------------------
	
	/**
	 * invoking that setter will cause the tail thread to stop and a new Tail
	 * instance is created with the given charset.
	 */
	public void setEncoding(String encoding) {
		setMonitor(false);
		this.encoding = encoding;
		this.charset = Charset.forName(encoding);
		tail = new Tail(file.getFileName(),charset,this);
		setMonitor(true);
	}
	
	/**
	 * @return the current content encoding
	 */
	public String getEncoding() {
		return encoding;
	}
	
	/**
	 * invoking that method will cause the tail thread to stop and a new Tail
	 * instance is create.
	 */
	public void synchronize() {
		setMonitor(false);
		getStore().set(""); //$NON-NLS-1$
		getTracker().set(""); //$NON-NLS-1$
		tail = new Tail(file.getFileName(),charset,this);
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
	 * if the monitor parameter is true and the current Tail instance thread
	 * is not running the Thread will be inovked and the tail begins to update
	 * this document.
	 * 
	 * if the monitor parameter is false the Tail instance thread is notified
	 * to stop at the next possbile exit point.
	 * @param monitor
	 */
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
		if(monitor) {
			getStore().set(""); //$NON-NLS-1$
			getTracker().set(""); //$NON-NLS-1$
		}
		tail.setMonitorStatus(monitor);
	}
	
	public boolean isMonitor() {
		return monitor;
	}
	
	/* (non-Javadoc)
	 * @see ch.mimo.eclipse.plugin.logfiletools.file.IFileChangedListener#contentAboutToBeChanged()
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
	 * @see ch.mimo.eclipse.plugin.logfiletools.file.IFileChangedListener#fileChanged(char[])
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
        		DocumentEvent event = new DocumentEvent(LogDocument.this,getStore().getLength(),content.length,text);
        		fireDocumentChanged(event);
            }
        };
        if(Display.getDefault() != null) {
            Display.getDefault().asyncExec(runnable);
        }
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
	
//	private void truncateLines() {
//		int amount = getTracker().getNumberOfLines();
//		for(int i = 0 ; i < amount ; i++) {
//			IRegion region = getTracker().getLineInformation(i);
//			region.
//		}
//	}
	
	// Inner classes ----------------------------------------------------------------
	
	private class PropertyChangeListener implements IPropertyChangeListener {
		
			/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if(event.getProperty().equals(ILogFileViewConstants.PREF_CURSORLINE_COLOR)) {
				backlogLines = LogFileViewPlugin.getDefault().getPreferenceStore().getInt(ILogFileViewConstants.PREF_BACKLOG);
			}
		}
	}
}
