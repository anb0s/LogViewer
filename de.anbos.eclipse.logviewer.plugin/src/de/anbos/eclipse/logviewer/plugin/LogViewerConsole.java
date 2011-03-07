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

package de.anbos.eclipse.logviewer.plugin;

import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class LogViewerConsole extends IOConsole implements Runnable {

    private IOConsoleOutputStream outStream;
    private IOConsoleInputStream inStream;
    private boolean isRunning; 
    	
	public LogViewerConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		outStream = null;
	}

	public void run() {
		isRunning = true;
		try {
			int readwait = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_READWAIT);
			inStream = getInputStream();
			while(isRunning) {
				int available = inStream.available();
				if (available > 0) {
					byte[] readed = new byte[available];
					inStream.read(readed, 0, available);
					outStream.write(readed);
					//continue;
				}
				wait(readwait);
			}
		} catch(InterruptedException ie) {
		} catch(NullPointerException npe) {
		} catch (IOException e) {
		} finally {
		}
		isRunning = false;
	}

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

	public IOConsoleOutputStream getOutStream() {
		if (outStream == null)
			outStream = newOutputStream();
		return outStream;
	}
}
