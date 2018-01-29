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
 *    Artur Wozniak - clear file
 *
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;

public class UIImages {

    // Attribute ---------------------------------------------------------------

    private static URL ICON_BASE_URL = null;

    private static ImageRegistry imageRegistry = null;

    // Constructor -------------------------------------------------------------

    public UIImages() {
    }

    // Static ------------------------------------------------------------------

    static  {
        String pathSuffix = "icons/"; //$NON-NLS-1$
        try {
            ICON_BASE_URL = new URL(LogViewerPlugin.getDefault().getBundle().getEntry("/"), pathSuffix); //$NON-NLS-1$
        } catch(MalformedURLException ex) { }
	}

    public static ImageDescriptor getImageDescriptor(String key) {
        return getImageRegistry().getDescriptor(key);
    }

    // Private -----------------------------------------------------------------

    private static ImageRegistry getImageRegistry() {
        if(imageRegistry == null) {
            initializeImageRegistry();
        }
        return imageRegistry;
    }

    private static ImageRegistry initializeImageRegistry() {
        Display display = Display.getCurrent();
        if(display == null) {
            display = Display.getDefault();
        }
        imageRegistry = new ImageRegistry(display);
        // register images

        declareRegistryImage(ILogViewerConstants.IMG_OPEN_FILE_ACTIVE,		"active/open_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_OPEN_FILE_PASSIVE,		"passive/open_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_CLEAR_HISTORY_ACTIVE,	"active/removeall_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_CLEAR_HISTORY_PASSIVE,	"passive/removeall_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_PREFERENCES_ACTIVE,	"active/prop_ps.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_PREFERENCES_PASSIVE,	"passive/prop_ps.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_CLOSE_FILE_ACTIVE,		"active/remove_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_CLOSE_FILE_PASSIVE,	"passive/remove_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_CLOSEALL_ACTIVE,		"active/removeall_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_CLOSEALL_PASSIVE,		"passive/removeall_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_REFRESH_FILE_ACTIVE,	"active/refresh_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_REFRESH_FILE_PASSIVE,	"passive/refresh_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_START_TAIL_ACTIVE,		"active/start_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_START_TAIL_PASSIVE,	"passive/start_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_START_ALL_TAIL_ACTIVE,	"active/start_all_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_START_ALL_TAIL_PASSIVE,"passive/start_all_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_STOP_TAIL_ACTIVE,		"active/stop_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_STOP_TAIL_PASSIVE,		"passive/stop_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_STOP_ALL_TAIL_ACTIVE,	"active/stop_all_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_STOP_ALL_TAIL_PASSIVE,	"passive/stop_all_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_FILTER_ACTIVE,			"active/filter_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_FILTER_PASSIVE,		"passive/filter_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_ENCODING_ACTIVE,		"active/encoding_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_FIND_ACTIVE,			"active/find_obj_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_FIND_PASSIVE,			"passive/find_obj_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_RENAME_ACTIVE,			"active/rename_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_RENAME_PASSIVE,		"passive/rename_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_COPY_ACTIVE,			"active/copy_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_COPY_PASSIVE,			"passive/copy_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_FILE_CLEAR_ACTIVE,		"active/clear_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_FILE_CLEAR_PASSIVE,	"passive/clear_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogViewerConstants.IMG_LOG_VIEWER,			"logviewer.gif");

        return imageRegistry;
    }

    private static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        try {
            desc = ImageDescriptor.createFromURL(makeIconFileURL(path));
        } catch(MalformedURLException me) {
            LogViewerPlugin.getDefault().showErrorMessage(me);
        }
        imageRegistry.put(key, desc);
	}

    private static URL makeIconFileURL(String iconPath) throws MalformedURLException {
        if(ICON_BASE_URL == null) {
            	throw new MalformedURLException();
        } else {
            return new URL(ICON_BASE_URL, iconPath);
        }
	}
}
