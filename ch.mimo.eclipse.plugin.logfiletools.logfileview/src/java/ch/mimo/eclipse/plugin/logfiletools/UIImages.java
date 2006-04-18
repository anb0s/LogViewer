package ch.mimo.eclipse.plugin.logfiletools;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;

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
            ICON_BASE_URL = new URL(LogFileViewPlugin.getDefault().getBundle().getEntry("/"), pathSuffix); //$NON-NLS-1$
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
        
        declareRegistryImage(ILogFileViewConstants.IMG_OPEN_FILE_ACTIVE,		"active/open_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_OPEN_FILE_PASSIVE,		"passive/open_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_CLOSE_FILE_ACTIVE,		"active/remove_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_CLOSE_FILE_PASSIVE,		"passive/remove_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_CLOSEALL_ACTIVE,			"active/removeall_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_CLOSEALL_PASSIVE,		"passive/removeall_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_REFRESH_FILE_ACTIVE,		"active/refresh_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_REFRESH_FILE_PASSIVE,	"passive/refresh_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_START_TAIL_ACTIVE,		"active/start_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_START_TAIL_PASSIVE,		"passive/start_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_STOP_TAIL_ACTIVE,		"active/stop_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_STOP_TAIL_PASSIVE,		"passive/stop_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_FILTER_ACTIVE,			"active/filter_active.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_FILTER_PASSIVE,			"passive/filter_passive.gif"); //$NON-NLS-1$
        declareRegistryImage(ILogFileViewConstants.IMG_ENCODING_ACTIVE,			"active/encoding_active.gif"); //$NON-NLS-1$
        return imageRegistry;
    }
    
    private static void declareRegistryImage(String key, String path) {
        ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
        try {
            desc = ImageDescriptor.createFromURL(makeIconFileURL(path));
        } catch(MalformedURLException me) {
            LogFileViewPlugin.getDefault().showErrorMessage(me);
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
