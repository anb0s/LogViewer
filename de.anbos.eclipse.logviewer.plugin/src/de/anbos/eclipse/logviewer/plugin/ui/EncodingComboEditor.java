package de.anbos.eclipse.logviewer.plugin.ui;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.Logger;

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

public class EncodingComboEditor extends FieldEditor {

	// Constant ----------------------------------------------------------------
	
	/**
	 * Text limit constant (value <code>-1</code>) indicating unlimited
	 * text limit and width.
	 */
	public static int UNLIMITED = -1;
	
	// Attribute ---------------------------------------------------------------
	
    private Logger logger;
	private Combo combo;
	
	/**
	 * Width of text field in characters; initially unlimited.
	 */
	private int widthInChars = UNLIMITED;
	
	// Constructor -------------------------------------------------------------
	
	public EncodingComboEditor(String name, String labelText, int width, Composite parent) {
        logger = LogViewerPlugin.getDefault().getLogger();
		init(name, labelText);
		widthInChars = width;
		createControl(parent);
	}
	
	public EncodingComboEditor(String name, String labelText, Composite parent) {
		this(name,labelText,UNLIMITED,parent);
	}
	
	// Protected ---------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData)combo.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);
		
		combo = getComboControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		if (widthInChars != UNLIMITED) {
			GC gc = new GC(combo);
			try {
				Point extent = gc.textExtent("X");//$NON-NLS-1$
				gd.widthHint = widthInChars * extent.x;
			} finally {
				gc.dispose();
			}
		} else {
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
		}
		combo.setLayoutData(gd);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() {
		if(combo != null) {
			// fill the encoding data
			SortedMap charsets = Charset.availableCharsets();
			Set keys = charsets.keySet();
			// fill the combo with all available encoding types
			Iterator keyIterator = keys.iterator();
			while(keyIterator.hasNext()) {
				Object obj = charsets.get(keyIterator.next());
				try {				
					Method method = obj.getClass().getMethod("displayName",new Class[]{}); //$NON-NLS-1$
					String encoding = (String)method.invoke(obj,new Object[]{});
					combo.add(encoding);
				} catch(Exception e) {
                    logger.logError("unable to load all available character encondings for this platform",e); //$NON-NLS-1$
				}
			}
		}
		combo.setText(getPreferenceStore().getString(getPreferenceName()));
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		if(combo != null) {
			String value = getPreferenceStore().getDefaultString(getPreferenceName());
			combo.setText(value);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(),getComboControl().getText());
	}
	
	protected Combo getComboControl() {
		return combo;
	}
	
	protected Combo getComboControl(Composite parent) {
		if(combo != null) {
			return combo;
		}
		combo = new Combo(parent,SWT.RIGHT);
		combo.setFont(parent.getFont());
		return combo;
	}
	
	// Public ------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		return 2;
	}
}
