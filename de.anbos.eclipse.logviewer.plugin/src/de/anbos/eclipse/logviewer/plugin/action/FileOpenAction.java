/*
 * Copyright 2009 by Andre Bossert
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

package de.anbos.eclipse.logviewer.plugin.action;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.anbos.eclipse.logviewer.plugin.LogFile;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.action.delegate.FileOpenActionDelegate;
import de.anbos.eclipse.logviewer.plugin.preferences.FileHistoryTracker;


public class FileOpenAction implements IObjectActionDelegate {

	private File[] resource = null;
	private IStructuredSelection currentSelection;

	/**
	 * Constructor for EasyExploreAction.
	 */
	public FileOpenAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		if (!isEnabled()) {
			MessageDialog.openInformation(
				new Shell(),
				"Logfile Viewer",
				"Wrong Selection");
			return;
		}

		for (int i=0;i<resource.length;i++) {

			if (resource[i] == null)
				continue;

			String full_path = null;

			full_path = resource[i].toString();
			LogViewer view = null;

			try {
				view = (LogViewer) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.anbos.eclipse.logviewer.plugin.LogViewer");
			} catch (PartInitException e) {
				e.printStackTrace();
			}

			if (resource[i].isDirectory()) {
				FileOpenActionDelegate action_delegate = new FileOpenActionDelegate();
				action_delegate.setParentPath(full_path);
				action_delegate.run(view, new Shell());
			}else {
	    	    LogFile file = new LogFile(full_path);
	    	    if(!view.hasLogFile(file)) {
	    	        view.openLogFile(file);
	                FileHistoryTracker.getInstance().storeFile(full_path);
	    	    }
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	    currentSelection = selection instanceof IStructuredSelection ? (IStructuredSelection)selection : null;
	}

	protected boolean isEnabled()
	{
		boolean enabled = false;
		if (currentSelection != null)
		{
			Object[] selectedObjects = currentSelection.toArray();
			if (selectedObjects.length >= 1)
			{
				resource = new File[selectedObjects.length];
				for (int i=0;i<selectedObjects.length;i++) {
					resource[i] = getResource(selectedObjects[i]);
					if (resource != null)
						enabled=true;
				}
			}
		}
		return enabled;
	}

	protected File getResource(Object object) {
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
