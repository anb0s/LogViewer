package ch.mimo.eclipse.plugin.logfiletools.preferences.color;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/*
 * Copyright (c) 2006 by Michael Mimo Moratti
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

public class ItemMover implements SelectionListener {

	// Attribute ---------------------------------------------------------------
	
	private Table table;
	private ColorStore store;
	
	private ColorPreferenceData currentSelection;
	
	// Constructor -------------------------------------------------------------
	
	public ItemMover(Table table, ColorStore store) {
		this.table = table;
		this.store = store;
		table.addSelectionListener(this);
	}
	
	// Public ------------------------------------------------------------------
	
	public void moveCurrentSelectionUp() {
		if(currentSelection == null) {
			return;
		}
		ColorPreferenceData previousElement = store.getPreviousElement(currentSelection);
		if(previousElement == null) {
			return;
		}
		int newPosition = previousElement.getPosition();
		int oldPosition = currentSelection.getPosition();
		previousElement.setPosition(oldPosition);
		currentSelection.setPosition(newPosition);		
	}
	
	public void moveCurrentSelectionDown() {
		if(currentSelection == null) {
			return;
		}
		ColorPreferenceData nextElement = store.getNextElement(currentSelection);
		if(nextElement == null) {
			return;
		}
		int newPosition = nextElement.getPosition();
		int oldPosition = currentSelection.getPosition();
		nextElement.setPosition(oldPosition);
		currentSelection.setPosition(newPosition);
	}

	public void widgetDefaultSelected(SelectionEvent e) {	
	}

	public void widgetSelected(SelectionEvent e) {
		TableItem item = null;
		try {
			item = table.getSelection()[0];
		} catch(Throwable t) {
			currentSelection = null;
			return;
		}
		if(item == null || !(item.getData() instanceof ColorPreferenceData)) {
			currentSelection = null;
			return;
		}
		currentSelection = (ColorPreferenceData)item.getData();
	}

	
	
	// Private -----------------------------------------------------------------
}
