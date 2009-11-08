package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewer;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.UIImages;
import de.anbos.eclipse.logviewer.plugin.action.delegate.StopTailOnAllFileActionDelegate;

public class StopTailOnAllFileViewAction extends AbstractViewAction {

    // Constructor -------------------------------------------------------------
    
    public StopTailOnAllFileViewAction(LogViewer view, Shell shell) {
        super(view,shell,new StopTailOnAllFileActionDelegate());
    }
    
    /* (non-Javadoc)
     * @see de.anbos.eclipse.logviewer.plugin.action.AbstractViewAction#init()
     */
    public void init() {
		this.setText(LogViewerPlugin.getResourceString("menu.file.stopall.text")); //$NON-NLS-1$
		this.setToolTipText(LogViewerPlugin.getResourceString("menu.file.stopall.tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_STOP_ALL_TAIL_ACTIVE));
		this.setDisabledImageDescriptor(UIImages.getImageDescriptor(ILogViewerConstants.IMG_STOP_ALL_TAIL_PASSIVE));
    }
}
