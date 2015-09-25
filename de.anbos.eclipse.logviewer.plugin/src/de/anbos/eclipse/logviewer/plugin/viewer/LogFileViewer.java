package de.anbos.eclipse.logviewer.plugin.viewer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.CursorLinePainter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;

/*
 * Copyright (c) 2007 - 2011 by Michael Mimo Moratti
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */

public class LogFileViewer {

    // Attribute ---------------------------------------------------------------

    private TextViewer txtViewer;

    private IDocument document;

    private IPreferenceStore store;

    private CursorLinePainter cursorLinePainter;
    private PresentationReconciler presentationReconciler;

    private boolean showWhenUpdated;
    private boolean showTopOfFile;

    PropertyChangeListener propertyChangeListener = null;

    // Constructor -------------------------------------------------------------

    public LogFileViewer(Composite parent, int style) {
        store = LogViewerPlugin.getDefault().getPreferenceStore();
        if (store.getBoolean(ILogViewerConstants.PREF_WORD_WRAP))
            style |= SWT.WRAP;
        showWhenUpdated = store.getBoolean(ILogViewerConstants.PREF_SHOW_WHEN_UPDATED);
        showTopOfFile = store.getBoolean(ILogViewerConstants.PREF_SHOW_TOP_OF_FILE);
        txtViewer = new SourceViewer(parent,null,style);
        FontData[] fontData = PreferenceConverter.getFontDataArray(store,ILogViewerConstants.PREF_EDITOR_FONT_STYLE);
        if(fontData == null) {
            fontData = JFaceResources.getDefaultFont().getFontData();
        }
        txtViewer.getTextWidget().setFont(new Font(Display.getCurrent(),fontData));
        propertyChangeListener = new PropertyChangeListener();
        store.addPropertyChangeListener(propertyChangeListener);
        createCursorLinePainter();
        createAndInstallPresentationReconciler();
    }

    // Public ------------------------------------------------------------------

    public void setDocument(IDocument document) {
        this.document = document;
        txtViewer.setDocument(document);
    }

    public IDocument getDocument() {
        return document;
    }

    public TextViewer getActualViewer() {
        return txtViewer;
    }

    public Control getControl() {
        return txtViewer.getControl();
    }

    public ISelection getSelection() {
        return txtViewer.getSelection();
    }

    public int getTopIndex() {
        return txtViewer.getTopIndex();
    }

    public void refresh() {
        txtViewer.refresh();
    }

    public void setSelection(ISelection sel) {
        txtViewer.setSelection(sel);
    }

    public void showTopOfFile() {
        txtViewer.setTopIndex(0);
    }

    public void showBottomOfFile() {
        txtViewer.setTopIndex(document.getNumberOfLines());
    }

    public void showTopOrBottomOfFile() {
        if (isShowTopOfFile())
            showTopOfFile();
        else
            showBottomOfFile();
    }

    public void setTopIndex(int index) {
        txtViewer.setTopIndex(index);
    }

    public boolean isShowWhenUpdated() {
        return showWhenUpdated;
    }

    public boolean isShowTopOfFile() {
        return showTopOfFile;
    }

    public void removeListeners() {
       if (propertyChangeListener != null) {
          store.removePropertyChangeListener(propertyChangeListener);
       }
    }

    // Private -----------------------------------------------------------------

    private void createCursorLinePainter() {
        cursorLinePainter = new CursorLinePainter(txtViewer);
        Color color = new Color(Display.getCurrent(),PreferenceConverter.getColor(store,ILogViewerConstants.PREF_CURSORLINE_COLOR));
        cursorLinePainter.setHighlightColor(color);
        ITextViewerExtension2 extension = (ITextViewerExtension2)txtViewer;
        extension.addPainter(cursorLinePainter);
    }

    private void createAndInstallPresentationReconciler() {
        presentationReconciler = new PresentationReconciler();
        DamageRepairer dr = new DamageRepairer(new DynamicRuleBasedScanner(store.getString(ILogViewerConstants.PREF_COLORING_ITEMS)));
        presentationReconciler.setDamager(dr,IDocument.DEFAULT_CONTENT_TYPE);
        presentationReconciler.setRepairer(dr,IDocument.DEFAULT_CONTENT_TYPE);
        presentationReconciler.install(txtViewer);
    }

    // Inner classes ----------------------------------------------------------------

    private class PropertyChangeListener implements IPropertyChangeListener {

            /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event) {
            if(event.getProperty().equals(ILogViewerConstants.PREF_CURSORLINE_COLOR)) {
                Color color = new Color(Display.getCurrent(),PreferenceConverter.getColor(store,ILogViewerConstants.PREF_CURSORLINE_COLOR));
                cursorLinePainter.setHighlightColor(color);
            }
            if(event.getProperty().equals(ILogViewerConstants.PREF_EDITOR_FONT_STYLE)) {
                FontData[] fontData = PreferenceConverter.getFontDataArray(store,ILogViewerConstants.PREF_EDITOR_FONT_STYLE);
                txtViewer.getTextWidget().setFont(new Font(Display.getCurrent(),fontData));
            }
            if(event.getProperty().equals(ILogViewerConstants.PREF_WORD_WRAP)) {
                boolean wordWrap = store.getBoolean(ILogViewerConstants.PREF_WORD_WRAP);
                txtViewer.getTextWidget().setWordWrap(wordWrap);
            }
            if(event.getProperty().equals(ILogViewerConstants.PREF_SHOW_WHEN_UPDATED)) {
                showWhenUpdated = store.getBoolean(ILogViewerConstants.PREF_SHOW_WHEN_UPDATED);
            }
            if(event.getProperty().equals(ILogViewerConstants.PREF_SHOW_TOP_OF_FILE)) {
                showTopOfFile = store.getBoolean(ILogViewerConstants.PREF_SHOW_TOP_OF_FILE);
            }
        }
    }
}
