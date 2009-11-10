package de.anbos.eclipse.logviewer.plugin.action.delegate;

import org.eclipse.swt.widgets.Shell;

import de.anbos.eclipse.logviewer.plugin.LogViewer;

public class StopTailOnAllFileActionDelegate implements ILogViewerActionDelegate {

    // Public ------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see de.anbos.eclipse.logviewer.plugin.action.delegate.ILogViewerActionDelegate#run(de.anbos.eclipse.logviewer.plugin.LogViewer, org.eclipse.swt.widgets.Shell)
     */
    public void run(LogViewer view, Shell shell) {
        view.stopTailOnAllDocuments();
    }
}
