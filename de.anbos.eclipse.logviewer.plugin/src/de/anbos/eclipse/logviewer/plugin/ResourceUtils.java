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

/*
 * function getViewer() copied from AnyEditTools (Andrei Loskutov):
 * de.loskutov.anyedit.ui.editor.EditorPropertyTester
 */

package de.anbos.eclipse.logviewer.plugin;

import java.io.File;
import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsolePage;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBookView;

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

    static public ISelection getConsoleSelection(IWorkbenchPart part) {
    	ISelection selection = null;
    	
    	IConsole con = getConsole(part);
    	
    	if (con != null)
    		selection = new StructuredSelection(con);

    	return selection;
    }

    public static IConsole getConsole(IWorkbenchPart part) {
        if(!(part instanceof IViewPart)){
            return null;
        }

        IViewPart vp =(IViewPart) part;
        if (vp instanceof PageBookView) {
            IPage page = ((PageBookView) vp).getCurrentPage();
            ITextViewer viewer = getViewer(page);
            if (viewer == null || viewer.getDocument() == null)
            	return null;
        }
		
        IConsole con = null;
    	try {
    		con = ((IConsoleView)part).getConsole();
    	} catch (Exception e) {
			
		}

		return con;
    }

    public static ITextViewer getViewer(IPage page) {
        if(page == null){
            return null;
        }
        if(page instanceof TextConsolePage) {
            return ((TextConsolePage)page).getViewer();
        }
        if(page.getClass().equals(MessagePage.class)){
            // empty page placeholder
            return null;
        }
        try {
            /*
             * org.eclipse.cdt.internal.ui.buildconsole.BuildConsolePage does not
             * extend TextConsolePage, so we get access to the viewer with dirty tricks
             */
            Method method = page.getClass().getDeclaredMethod("getViewer", null);
            method.setAccessible(true);
            return (ITextViewer) method.invoke(page, null);
        } catch (Exception e) {
            // AnyEditToolsPlugin.logError("Can't get page viewer from the console page", e);
        }
        return null;
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
			return toFile(((IFile)object).getLocation());
		}
		if (object instanceof File) {
			return (File) object;
		}
		if (object instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) object;
			IFile ifile = (IFile) adaptable.getAdapter(IFile.class);
			if (ifile != null) {
				return toFile(ifile.getLocation());
			}
			IResource ires = (IResource) adaptable.getAdapter(IResource.class);
			if (ires != null) {
				return toFile(ires.getLocation());
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
    
    static public File toFile(IPath iPath)
    {    
    	if (iPath != null)
    		return iPath.toFile();
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
