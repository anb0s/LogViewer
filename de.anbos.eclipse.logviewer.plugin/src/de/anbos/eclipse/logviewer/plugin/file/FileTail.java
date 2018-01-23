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

package de.anbos.eclipse.logviewer.plugin.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.Logger;

public class FileTail implements Runnable {

    // Constant ----------------------------------------------------------------

    private static final int INITIAL_LOAD_SIZE  = 1000000;

    // Attribute ---------------------------------------------------------------

    private Logger logger;
    private String filePath;
    private IFileChangedListener listener;

    private CharsetDecoder decoder;
    private boolean isRunning;
    private boolean isFirstTimeRead;

    private int bufferCapacity;
    private ByteBuffer mappedBuffer;

    // Constructor -------------------------------------------------------------

    public FileTail(String myFilePath, Charset charset, IFileChangedListener myListener) {
        logger = LogViewerPlugin.getDefault().getLogger();
        filePath = myFilePath;
        listener = myListener;
        decoder = charset.newDecoder();
        decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        bufferCapacity = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_BUFFER);
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

    public synchronized void run() {
        isRunning = true;
        RandomAccessFile file = null;
        try {
            int readwait = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_READWAIT);
            file = openFile();
            if(file == null) {
            	// should not happen, see Issue 55: Improve FileTail's exception handling
            	throw new FileNotFoundException();
            }
            FileChannel channel = file.getChannel();
            while(isRunning) {
                if(channel.size() - channel.position() > 0) {
                    listener.contentAboutToBeChanged();
                    read(channel);
                    continue;
                }
                wait(readwait);
            }
        } catch(FileNotFoundException fnf) {
        	// no exeption if file not found, because it's predictable
        	// see Issue 55: Improve FileTail's exception handling
            //logger.logError(fnf);
            listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.notfound",new String[]{filePath}).toCharArray(),true);        	
        } catch(ThreadInterruptedException tie) {
            logger.logError(tie);
            listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.error",new String[]{filePath}).toCharArray(),true);
        } catch(MalformedInputException mie) {
            logger.logError(mie);
            listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.encoding.error",new String[]{decoder.charset().displayName()}).toCharArray(),true);
        } catch(IOException ioe) {
            logger.logError(ioe);
        } catch(InterruptedException ie) {
            logger.logError(ie);
        } finally {
            try {
                if(file != null) {
                    file.close();
                    
                    if(isRunning==false && mappedBuffer!=null)    
					{
						stopFileMapping(mappedBuffer);
					}
                }
            } catch(Exception e) {
                // ignore this
            }
        }
        isRunning = false;
    }

	public void stopFileMapping(Buffer buffer)throws Exception {
		Method cleaner = buffer.getClass().getMethod("cleaner");
		cleaner.setAccessible(true);
		Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
		clean.setAccessible(true);
		clean.invoke(cleaner.invoke(buffer));
		buffer=null;
	}

    // Private -----------------------------------------------------------------

    private synchronized RandomAccessFile openFile() throws ThreadInterruptedException, FileNotFoundException {
        boolean firstExec = true;        
        while(isRunning) {
            try {
                RandomAccessFile file = new RandomAccessFile(filePath,"r"); //$NON-NLS-1$
                isFirstTimeRead = true;
                return file;
            } catch(FileNotFoundException fnfe) {
                try {
                    if (firstExec) {
                        listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file.warning",new String[]{filePath}).toCharArray(),true);
                        firstExec = false;
                    }
                    wait(ILogViewerConstants.TAIL_FILEOPEN_ERROR_WAIT);
                } catch(InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
            }
        }
        // right exeption, see Issue 55: Improve FileTail's exception handling
        throw new FileNotFoundException();
    }

    /**
     * reads bytes from the currently open nio file input stream
     * @param channel
     * @throws IOException
     */
    private void read(FileChannel channel) throws IOException {
        // first time just print some header info
        if(isFirstTimeRead) {
            listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file",new String[]{filePath}).toCharArray(),true);
            bufferCapacity = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_BUFFER);
        }
        // get positions and size
        long startPosition = channel.position();
        long endPosition = channel.size();
        long size =  endPosition - startPosition;
        // if first time or big change then read only the rest
        if (isFirstTimeRead || size > INITIAL_LOAD_SIZE) {
        	isFirstTimeRead = false;
            synchronized (channel) {
                if(size > INITIAL_LOAD_SIZE) {
                    startPosition = endPosition - INITIAL_LOAD_SIZE;
                    size = INITIAL_LOAD_SIZE;
                }
                mappedBuffer = channel.map(MapMode.READ_ONLY, startPosition, size);
                CharBuffer mappedChars = decoder.decode(mappedBuffer);
                channel.position(endPosition);
                listener.fileChanged(mappedChars.array(), true);
            }
            return;
        }
        mappedBuffer = ByteBuffer.allocate(bufferCapacity);
        channel.read(mappedBuffer);
        mappedBuffer.flip();
        CharBuffer chars = decoder.decode(mappedBuffer);
        listener.fileChanged(chars.array(),false);
    }

	public Buffer getBuffer() {
		return mappedBuffer;
	}
}
