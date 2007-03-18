package ch.mimo.eclipse.plugin.logfiletools.preferences.color;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

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

public class TableItemColorController implements PaintListener {

	// Attribute ---------------------------------------------------------------
	
	private Table table;
	
	// Constructor -------------------------------------------------------------
	
	public TableItemColorController(Table table) {
		this.table = table;
		init();
	}
	
	// Public ------------------------------------------------------------------
	
	public void paintControl(PaintEvent e) {
		colorItems();
	}
	
	// Private -----------------------------------------------------------------
	
	private void init() {
		table.addPaintListener(this);
	}
	
	private void colorItems() {
		TableItem[] items = table.getItems();
		for(int i = 0 ; i < items.length ; i++) {
			TableItem item = items[i];
			Object object = item.getData();
			if(!(object instanceof ColorPreferenceData)) {
				return;
			}
			ColorPreferenceData data = (ColorPreferenceData)item.getData();
			item.setBackground(new Color(Display.getDefault(),data.getBackground()));
			item.setForeground(new Color(Display.getDefault(),data.getForeground()));
		}
	}
}
