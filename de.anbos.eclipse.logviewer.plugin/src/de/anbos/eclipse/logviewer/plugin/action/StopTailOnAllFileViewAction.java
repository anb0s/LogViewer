package de.anbos.eclipse.logviewer.plugin.action;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.ILogFileViewConstants;
import de.anbos.eclipse.logviewer.plugin.LogFileView;
import de.anbos.eclipse.logviewer.plugin.LogFileViewPlugin;
import de.anbos.eclipse.logviewer.plugin.UIImages;
import de.anbos.eclipse.logviewer.plugin.action.delegate.StopTailOnAllFileActionDelegate;

public class StopTailOnAllFileViewAction extends AbstractViewAction {

    // Constructor -------------------------------------------------------------
    
    public StopTailOnAllFileViewAction(LogFileView view, Shell shell) {
        super(view,shell,new StopTailOnAllFileActionDelegate());
    }
    
    /* (non-Javadoc)
     * @see de.anbos.eclipse.logviewer.plugin.action.AbstractViewAction#init()
     */
    public void init() {
		this.setText(LogFileViewPlugin.getResourceString("menu.file.stopall.text")); //$NON-NLS-1$
		this.setToolTipText(LogFileViewPlugin.getResourceString("menu.file.stopall.tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(UIImages.getImageDescriptor(ILogFileViewConstants.IMG_STOP_ALL_TAIL_ACTIVE));
		this.setDisabledImageDescriptor(UIImages.getImageDescriptor(ILogFileViewConstants.IMG_STOP_ALL_TAIL_PASSIVE));
    }
}
