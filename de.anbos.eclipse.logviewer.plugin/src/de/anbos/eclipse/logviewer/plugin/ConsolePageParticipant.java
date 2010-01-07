package de.anbos.eclipse.logviewer.plugin;

import java.lang.reflect.Method;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsolePage;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBookView;

import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;

public class ConsolePageParticipant implements IConsolePageParticipant {

	private IPageBookViewPage page;

	public void activated() {
		// no op
	}

	public void deactivated() {
		// no op
	}

	public void dispose() {
		page = null;
	}

	public void init(IPageBookViewPage myPage, IConsole console) {
        page = myPage;
        IToolBarManager toolBarManager = page.getSite().getActionBars()
        .getToolBarManager();
        toolBarManager.appendToGroup(IConsoleConstants.OUTPUT_GROUP, new Separator());
        toolBarManager.appendToGroup(IConsoleConstants.OUTPUT_GROUP, new Action(
        		LogViewerPlugin.getResourceString("logviewer.action.openwith.name"),
        		UIImages.getImageDescriptor(ILogViewerConstants.IMG_LOG_VIEWER)) {
            public void run() {
            	ConsolePageParticipant.this.run();
            }
        });
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	private void run() {

		IWorkbenchPart part = page.getSite().getWorkbenchWindow().getActivePage().getActivePart();

        if(!(part instanceof IViewPart)){
            return;
        }
		
        IViewPart vp =(IViewPart) part;
        if (vp instanceof PageBookView) {
            IPage page = ((PageBookView) vp).getCurrentPage();
            ITextViewer viewer = getViewer(page);
            if (viewer == null || viewer.getDocument() == null)
            	return;
        }

		LogViewer view = null;

		try {
			view = (LogViewer) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.anbos.eclipse.logviewer.plugin.LogViewer");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		IConsole con = ((IConsoleView)part).getConsole();
		String path = con.getClass().toString().replaceFirst("class ", "") + System.getProperty("file.separator") + con.getName();

		view.checkAndOpenFile(LogFileType.LOGFILE_ECLIPSE_CONSOLE, path, false);
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

}
