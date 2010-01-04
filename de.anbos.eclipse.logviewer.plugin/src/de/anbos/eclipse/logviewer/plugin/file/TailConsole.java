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

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.TextConsole;

//import org.eclipse.cdt.internal.ui.buildconsole.CBuildConsole;
//import org.eclipse.cdt.ui.*;

public class TailConsole implements IDocumentListener {

	private String name;
	private IFileChangedListener listener;
	//MessageConsole con;
	TextConsole con;
	IDocument doc;
	
	//private CharsetDecoder decoder;
	private boolean isRunning;
	private boolean isFirstTimeRead;
	
	// Constructor -------------------------------------------------------------
	
	public TailConsole(String name, IFileChangedListener listener) {
		this.name = name;
		this.listener = listener;
		doc = findConsoleText(name).getDocument();
		isFirstTimeRead = true;
	}
	
	// Public ------------------------------------------------------------------
	
	public void setMonitorStatus(boolean monitor) {
        if(isRunning == monitor) {
            return;
        }
        isRunning = monitor;
        if(isRunning) {
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

	private TextConsole findConsoleText(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
		   if (name.equals(existing[i].getName()))
		   {
			   return (TextConsole)existing[i];		   
		   }
		}
		return null;
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
		   if (name.equals(existing[i].getName()))
		      return (MessageConsole) existing[i];
		//no console found, so create a new one      
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
   }
		//listener.fileChanged(chars.array(),false);

	public void documentAboutToBeChanged(DocumentEvent event) {		
		listener.contentAboutToBeChanged();
	}

	public void documentChanged(DocumentEvent event) {
		listener.fileChanged(event.getText().toCharArray(), isFirstTimeRead);
		isFirstTimeRead = false;
	}
}
