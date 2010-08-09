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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

import name.pachler.nio.file.FileSystems;
import name.pachler.nio.file.Path;
import name.pachler.nio.file.Paths;
import name.pachler.nio.file.StandardWatchEventKind;
import name.pachler.nio.file.WatchKey;
import name.pachler.nio.file.WatchService;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.Logger;

public class FileTailNotify implements Runnable {
	
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
	
	// Constructor -------------------------------------------------------------
	
	public FileTailNotify(String myFilePath, Charset charset, IFileChangedListener myListener) {
        logger = LogViewerPlugin.getDefault().getLogger();
		filePath = myFilePath;
		listener = myListener;
		decoder = charset.newDecoder();
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
				throw new ThreadInterruptedException("file was null"); //$NON-NLS-1$
			}
			FileChannel channel = file.getChannel();
			while(isRunning) {
				if(channel.size() - channel.position() > 0) {
					listener.contentAboutToBeChanged();
					read(channel);
				} else {
					break;
				}
				wait(readwait);
			}
			long lastPosition = 0;
			lastPosition = channel.position();
			file.close();
			// register notify
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path watchedPath = Paths.get(filePath);
			WatchKey key = null;
			try {
			    key = watchedPath.register(watchService, StandardWatchEventKind.ENTRY_MODIFY);
			} catch (UnsupportedOperationException uox){
			    System.err.println("file watching not supported!");
			    // handle this error here
			}catch (IOException iox){
			    System.err.println("I/O errors");
			    // handle this error here
			}
			// wait for notification
			while(isRunning) {
				
			}
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
				}
			} catch(Exception e) {
				// ignore this
			}
		}
		isRunning = false;
	}

	// Private -----------------------------------------------------------------
	
	private synchronized RandomAccessFile openFile() throws ThreadInterruptedException {
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
		throw new ThreadInterruptedException("no file found"); //$NON-NLS-1$
	}

	/**
	 * reads bytes from the currently open nio file input stream
	 * @param channel
	 * @throws IOException
	 */
	private void read(FileChannel channel) throws IOException {
		if(isFirstTimeRead) {
			listener.fileChanged(LogViewerPlugin.getResourceString("tail.loading.file",new String[]{filePath}).toCharArray(),true);
			synchronized (channel) {
				long startPosition = 0l;
				long size = channel.size();
				if(channel.size() > INITIAL_LOAD_SIZE) {
					startPosition = channel.size() - INITIAL_LOAD_SIZE;
					size = INITIAL_LOAD_SIZE;
				} 
				MappedByteBuffer mappedBuffer = channel.map(MapMode.READ_ONLY,startPosition,size);
				CharBuffer mappedChars = decoder.decode(mappedBuffer);
				channel.position(channel.size());
				listener.fileChanged(mappedChars.array(),true);
			}
			bufferCapacity = LogViewerPlugin.getDefault().getPreferenceStore().getInt(ILogViewerConstants.PREF_BUFFER);
			isFirstTimeRead = false;
			return;
		}
		ByteBuffer buffer = ByteBuffer.allocate(bufferCapacity);
		channel.read(buffer);
		buffer.flip();
		CharBuffer chars = decoder.decode(buffer);
		listener.fileChanged(chars.array(),false);
	}
}
