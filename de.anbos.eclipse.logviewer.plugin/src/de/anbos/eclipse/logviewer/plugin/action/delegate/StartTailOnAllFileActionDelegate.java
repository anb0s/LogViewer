package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogFileView;

public class StartTailOnAllFileActionDelegate implements ILogfileActionDelegate {

    // Public ------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see de.anbos.eclipse.logviewer.plugin.action.delegate.ILogfileActionDelegate#run(de.anbos.eclipse.logviewer.plugin.LogFileView, org.eclipse.swt.widgets.Shell)
     */
    public void run(LogFileView view, Shell shell) {
        view.startTailOnAllDocuments();
    }
}
