package de.anbos.eclipse.logviewer.plugin;

import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class LogViewerConsole extends MessageConsole implements Runnable {

    private MessageConsoleStream outStream;
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

	public MessageConsoleStream getOutStream() {
		if (outStream == null)
			outStream = newMessageStream();
		return outStream;
	}
}
