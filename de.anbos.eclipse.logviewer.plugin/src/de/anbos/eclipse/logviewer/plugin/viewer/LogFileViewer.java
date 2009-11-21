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
	
	private TextViewer viewer;
	
	private IDocument document;
	
	private IPreferenceStore store;
	
	private CursorLinePainter cursorLinePainter;
	private PresentationReconciler presentationReconciler;
	
	// Constructor -------------------------------------------------------------
	
	public LogFileViewer(Composite parent,int style) {
		store = LogViewerPlugin.getDefault().getPreferenceStore();
		viewer = new SourceViewer(parent,null,style);
		FontData[] fontData = PreferenceConverter.getFontDataArray(store,ILogViewerConstants.PREF_EDITOR_FONT_STYLE);
		if(fontData == null) {
			fontData = JFaceResources.getDefaultFont().getFontData();
		}
		viewer.getTextWidget().setFont(new Font(Display.getCurrent(),fontData));
		store.addPropertyChangeListener(new PropertyChangeListener());
		createCursorLinePainter();
		createAndInstallPresentationReconciler();
	}
	
	// Public ------------------------------------------------------------------
	
	public void setDocument(IDocument document) {
		this.document = document;
		viewer.setDocument(document);
	}
	
	public IDocument getDocument() {
		return document;
	}
	
	public TextViewer getActualViewer() {
		return viewer;
	}
	
	public Control getControl() {
		return viewer.getControl();
	}
	
	// Private -----------------------------------------------------------------
	
	private void createCursorLinePainter() {
		cursorLinePainter = new CursorLinePainter(viewer);
		Color color = new Color(Display.getCurrent(),PreferenceConverter.getColor(store,ILogViewerConstants.PREF_CURSORLINE_COLOR));
		cursorLinePainter.setHighlightColor(color);
		ITextViewerExtension2 extension = (ITextViewerExtension2)viewer;
		extension.addPainter(cursorLinePainter);
	}
	
	private void createAndInstallPresentationReconciler() {
		presentationReconciler = new PresentationReconciler();
		DamageRepairer dr = new DamageRepairer(new DynamicRuleBasedScanner(LogViewerPlugin.getDefault().getPreferenceStore().getString(ILogViewerConstants.PREF_COLORING_ITEMS)));
		presentationReconciler.setDamager(dr,IDocument.DEFAULT_CONTENT_TYPE);
		presentationReconciler.setRepairer(dr,IDocument.DEFAULT_CONTENT_TYPE);
		presentationReconciler.install(viewer);
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
				viewer.getTextWidget().setFont(new Font(Display.getCurrent(),fontData));
			}
		}
	}
}
