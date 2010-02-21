package de.anbos.eclipse.logviewer.plugin;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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

/**
 * The main plugin class to be used in the desktop.
 */
public class LogViewerPlugin extends AbstractUIPlugin {
	
	// Constant ---------------------------------------------------------------------
	
	//The shared instance.
	private static LogViewerPlugin plugin;
	
	// Attribute --------------------------------------------------------------------
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	// Logger
	private Logger logger;
	
	// Constructor ------------------------------------------------------------------
	
	/**
	 * The constructor.
	 */
	public LogViewerPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("de.anbos.eclipse.logviewer.plugin.LogfileUIMessages"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	// Static -----------------------------------------------------------------------
	
	/**
	 * Returns the shared instance.
	 */
	public static LogViewerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = LogViewerPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	/**
	 * returns a formated resouces bundle string
	 */
	public static String getResourceString(String key, Object[] args) {
		return MessageFormat.format(getResourceString(key),args);
	}
	
	// Public -----------------------------------------------------------------------
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		loadDefaultPluginPreferences(getPreferenceStore());
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}
	
	/**
	 * returns the instance of @see Logger
	 * @return Logger from @see LoggerImpl
	 */
	public Logger getLogger() {
		if(logger == null) {
			logger = new LoggerImpl(this.getLog(),this.getBundle().getSymbolicName());
		}
		return logger;
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
    public void showErrorMessage(String message) {
        IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
        if(window != null) {
            MessageDialog.openError(window.getShell().getShell(),getResourceString("error.fatal.title"),message); //$NON-NLS-1$
    	    }
  	}

	public void showErrorMessage(Throwable throwable) {
	    showErrorMessage(throwable.getClass().getName() + " " + throwable.getMessage()); //$NON-NLS-1$
	}

	public void showInfoMessage(String message) {
	    IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
     	if(window != null) {
     	    MessageDialog.openInformation(window.getShell().getShell(),getResourceString("error.info.title"),message); //$NON-NLS-1$
     	}
	}

	public void showWarningMessage(String message) {
	    IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
      	if(window != null) {
      	    MessageDialog.openWarning(window.getShell().getShell(),getResourceString("error.warning.title"),message); //$NON-NLS-1$
      	}
	}
	
	// Protected --------------------------------------------------------------------
	
	// Private ----------------------------------------------------------------------

    private void loadDefaultPluginPreferences(IPreferenceStore store) {
		store.setDefault(ILogViewerConstants.PREF_BACKLOG,ILogViewerConstants.DEFAULT_BACKLOG);
		store.setDefault(ILogViewerConstants.PREF_BUFFER,ILogViewerConstants.DEFAULT_BUFFER_CAPACITY);
		store.setDefault(ILogViewerConstants.PREF_READWAIT,ILogViewerConstants.DEFAULT_READWAIT_SIZE);
		store.setDefault(ILogViewerConstants.PREF_ENCODING,System.getProperty("file.encoding"));
		store.setDefault(ILogViewerConstants.PREF_WORD_WRAP,ILogViewerConstants.DEAFULT_WORD_WRAP);
		PreferenceConverter.setDefault(store,ILogViewerConstants.PREF_CURSORLINE_COLOR,new RGB(192,192,192));
		PreferenceConverter.setDefault(store,ILogViewerConstants.PREF_EDITOR_FONT_STYLE,JFaceResources.getDefaultFont().getFontData());
	}
}
