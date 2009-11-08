
package de.anbos.eclipse.logviewer.plugin;

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

public interface ILogFileViewConstants {

	// Tail settings -----------------------------------------------------------
	
	public static final long TAIL_FILEOPEN_ERROR_WAIT		= 500;
	
	// Images ------------------------------------------------------------------
	
	public static final String IMG_OPEN_FILE_ACTIVE			= "IMG_OPEN_FILE_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_OPEN_FILE_PASSIVE		= "IMG_OPEN_FILE_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_CLOSE_FILE_ACTIVE		= "IMG_CLOSE_FILE_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_CLOSE_FILE_PASSIVE		= "IMG_CLOSE_FILE_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_CLOSEALL_ACTIVE			= "IMG_CLOSEALL_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_CLOSEALL_PASSIVE			= "IMG_CLOSEALL_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_REFRESH_FILE_ACTIVE		= "IMG_REFRESH_FILE_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_REFRESH_FILE_PASSIVE		= "IMG_REFRESH_FILE_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_START_TAIL_ACTIVE		= "IMG_START_TAIL_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_START_TAIL_PASSIVE		= "IMG_START_TAIL_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_START_ALL_TAIL_ACTIVE	= "IMG_START_ALL_TAIL_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_START_ALL_TAIL_PASSIVE	= "IMG_START_ALL_TAIL_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_STOP_TAIL_ACTIVE			= "IMG_STOP_TAIL_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_STOP_TAIL_PASSIVE		= "IMG_STOP_TAIL_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_STOP_ALL_TAIL_ACTIVE		= "IMG_STOP_ALL_TAIL_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_STOP_ALL_TAIL_PASSIVE	= "IMG_STOP_ALL_TAIL_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_FILTER_ACTIVE			= "IMG_FILTER_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_FILTER_PASSIVE			= "IMG_FILTER_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_ENCODING_ACTIVE			= "IMG_ENCODING_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_FIND_ACTIVE				= "IMG_FIND_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_FIND_PASSIVE				= "IMG_FIND_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_RENAME_ACTIVE			= "IMG_RENAME_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_RENAME_PASSIVE			= "IMG_RENAME_PASSIVE"; //$NON-NLS-1$
	public static final String IMG_COPY_ACTIVE				= "IMG_COPY_ACTIVE"; //$NON-NLS-1$
	public static final String IMG_COPY_PASSIVE				= "IMG_COPY_PASSIVE"; //$NON-NLS-1$
	
	// Preferences -------------------------------------------------------------
	
	/* defines the maximal amount of rows that are backloged */
	public static final int 	MAX_BACKLOG					= 200000;
	public static final int 	DEFAULT_BACKLOG				= 4000;
	public static final String 	PREF_BACKLOG				= "BACKLOG"; //$NON-NLS-1$
	
	public static final int 	MAX_TAIL_BUFFER_SIZE		= 4096;
	public static final int 	DEFAULT_BUFFER_CAPACITY		= 1024;
	public static final String 	PREF_BUFFER					= "BUFFER"; //$NON-NLS-1$
	
	public static final int		MAX_READWAIT_SIZE			= 1000000;
	public static final int		DEFAULT_READWAIT_SIZE		= 250;
	public static final String	PREF_READWAIT				= "READWAIT"; //$NON-NLS-1$
	
	public static final String	PREF_ENCODING				= "ENCODING"; //$NON-NLS-1$
	
	public static final String	PREF_CURSORLINE_COLOR		= "CURSOR_LINE_COLOR"; //$NON-NLS-1$
	
	public static final String	PREF_EDITOR_FONT_STYLE		= "EDITOR_FONT_SYLE"; //$NON-NLS-1$
	
	public static final String 	PREF_COLORING_ITEMS			= "ITEM_COLORING"; //$NON-NLS-1$
	
	public static final String 	PREF_LAST_OPEN_FILES		= "LAST_OPEN_FILES"; //$NON-NLS-1$
	
	/* defines the maximum amount of files in the drop down history */
	public static final int		MAX_FILES_IN_HISTORY		= 10;
	public static final String 	PREF_HISTORY_FILES			= "HISTORY_FILES"; //$NON-NLS-1$
}
