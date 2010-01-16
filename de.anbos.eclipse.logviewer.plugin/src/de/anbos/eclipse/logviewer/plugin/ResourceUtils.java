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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

public class ResourceUtils {

    static public ISelection getResourceSelection(IWorkbenchPart part) {
		ISelection selection = null;
		if (part != null) {
			if (part instanceof IEditorPart) {
				File file = getResource((IEditorPart)part);
		        if (file != null) {
		        	selection = new StructuredSelection(file);
		        }
			} else {
		    	try {
		    		selection = part.getSite().getSelectionProvider().getSelection();
		    	} catch(Exception e) {
		    		// no op
		    	}
			}			
		}
    	return selection;
    }

    static public File getResource(Object myObj) {
    	Object object = null;
    	
    	if (myObj instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart)myObj;
			IEditorInput input = editorPart.getEditorInput();
	        Object adapter = input.getAdapter(IFile.class);
	        if(adapter instanceof IFile){
	        	object = (IFile) adapter;
	        } else {
	        	adapter = editorPart.getAdapter(IFile.class);
	        	if(adapter instanceof IFile){
	        		object = (IFile) adapter;
	        	} else {
	        		object = input;
	        	}
	        }    		
    	} else {
    		object = myObj;
    	}

		if (object instanceof IFile) {
			return ((IFile) object).getLocation().toFile();
		}
		if (object instanceof File) {
			return (File) object;
		}
		if (object instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) object;
			IFile ifile = (IFile) adaptable.getAdapter(IFile.class);
			if (ifile != null) {
				return ifile.getLocation().toFile();
			}
			IResource ires = (IResource) adaptable.getAdapter(IResource.class);
			if (ires != null) {
				return ires.getLocation().toFile();
			}
			/*
			if (adaptable instanceof PackageFragment
					&& ((PackageFragment) adaptable).getPackageFragmentRoot() instanceof JarPackageFragmentRoot) {
				return getJarFile(((PackageFragment) adaptable)
						.getPackageFragmentRoot());
			} else if (adaptable instanceof JarPackageFragmentRoot) {
				return getJarFile(adaptable);
			} else if (adaptable instanceof FileStoreEditorInput) {
				URI fileuri = ((FileStoreEditorInput) adaptable).getURI();
				return new File(fileuri.getPath());
			}
			*/
			File file = (File) adaptable.getAdapter(File.class);
			if (file != null) {
				return file;
			}
		}
		return null;
	}
	/*
	protected File getJarFile(IAdaptable adaptable) {
		JarPackageFragmentRoot jpfr = (JarPackageFragmentRoot) adaptable;
		File resource = (File) jpfr.getPath().makeAbsolute().toFile();
		if (!((File) resource).exists()) {
			File projectFile =
				new File(
					jpfr
						.getJavaProject()
						.getProject()
						.getLocation()
						.toOSString());
			resource = new File(projectFile.getParent() + resource.toString());
		}
		return resource;
	}
	*/
}
