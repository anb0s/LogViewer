package ch.mimo.eclipse.plugin.logfiletools.action.delegate;

import org.eclipse.swt.widgets.Shell;

import ch.mimo.eclipse.plugin.logfiletools.LogFileView;

public class StopTailOnAllFileActionDelegate implements ILogfileActionDelegate {

    // Public ------------------------------------------------------------------
    
    /* (non-Javadoc)
     * @see ch.mimo.eclipse.plugin.logfiletools.action.delegate.ILogfileActionDelegate#run(ch.mimo.eclipse.plugin.logfiletools.LogFileView, org.eclipse.swt.widgets.Shell)
     */
    public void run(LogFileView view, Shell shell) {
        view.stopTailOnAllDocuments();
    }
}
