/*******************************************************************************
 * Copyright (c) 2009 - 2018 by Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Artur Wozniak - clear file
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.UIImages;
import de.anbos.eclipse.logviewer.plugin.action.delegate.FileClearActionDelegate;

public class FileClearAction extends AbstractViewAction {

	// Constructor
	// ------------------------------------------------------------------

	public FileClearAction(LogViewer view, Shell shell) {
		super(view, shell, new FileClearActionDelegate());
	}

	// Public
	// -----------------------------------------------------------------------

	public void init() {
		this.setText(LogViewerPlugin.getResourceString("menu.file.clear.text")); //$NON-NLS-1$
		this.setToolTipText(LogViewerPlugin.getResourceString("menu.file.clear.tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_FILE_CLEAR_ACTIVE));
		this.setDisabledImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_FILE_CLEAR_PASSIVE));
	}
}
