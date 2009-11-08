package ch.mimo.eclipse.plugin.logfiletools.action;

import org.eclipse.swt.widgets.Shell;

import ch.mimo.eclipse.plugin.logfiletools.ILogFileViewConstants;
import ch.mimo.eclipse.plugin.logfiletools.LogFileView;
import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;
import ch.mimo.eclipse.plugin.logfiletools.UIImages;
import ch.mimo.eclipse.plugin.logfiletools.action.delegate.StartTailOnAllFileActionDelegate;

public class StartTailOnAllFileViewAction extends AbstractViewAction {

    // Constructor -------------------------------------------------------------
    
    public StartTailOnAllFileViewAction(LogFileView view, Shell shell) {
        super(view,shell,new StartTailOnAllFileActionDelegate());
    }
    
    /* (non-Javadoc)
     * @see ch.mimo.eclipse.plugin.logfiletools.action.AbstractViewAction#init()
     */
    public void init() {
		this.setText(LogFileViewPlugin.getResourceString("menu.file.startall.text")); //$NON-NLS-1$
		this.setToolTipText(LogFileViewPlugin.getResourceString("menu.file.startall.tooltip")); //$NON-NLS-1$
		this.setImageDescriptor(UIImages.getImageDescriptor(ILogFileViewConstants.IMG_START_ALL_TAIL_ACTIVE));
		this.setDisabledImageDescriptor(UIImages.getImageDescriptor(ILogFileViewConstants.IMG_START_ALL_TAIL_PASSIVE));
    }
}
