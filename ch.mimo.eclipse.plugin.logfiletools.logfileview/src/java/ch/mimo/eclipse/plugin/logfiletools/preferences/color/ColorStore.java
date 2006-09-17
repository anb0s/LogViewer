package ch.mimo.eclipse.plugin.logfiletools.preferences.color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.mimo.eclipse.plugin.logfiletools.ILogFileViewConstants;
import ch.mimo.eclipse.plugin.logfiletools.preferences.PreferenceValueConverter;

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

public class ColorStore {

    // Constant ---------------------------------------------------------------------
    
    // Attribute --------------------------------------------------------------------
    
    private IPreferenceStore store;
    private List items;
    private DataObjectComparator comparator;
    
    // Constructor ------------------------------------------------------------------
    
    public ColorStore(IPreferenceStore store) {
        items = new ArrayList();
        this.store = store;
    }
    
    // Public -----------------------------------------------------------------------
    
    public ColorPreferenceData[] getAllColorDetails() {
        ColorPreferenceData[] data = new ColorPreferenceData[items.size()];
        for(int i = 0 ; i < data.length ; i++) {
            data[i] = (ColorPreferenceData)items.get(i);
        }
        return data;
    }
    
    public ColorPreferenceData[] getAllCheckedColorDetails() {
        List checkedItems = new ArrayList();
        Iterator dataIterator = items.iterator();
        while(dataIterator.hasNext()) {
            ColorPreferenceData data = (ColorPreferenceData)dataIterator.next();
            if(data.isChecked()) {
                checkedItems.add(data);
            }
        }
        if(checkedItems.size() <= 0) {
        	return new ColorPreferenceData[0];
        }
        ColorPreferenceData[] checked = new ColorPreferenceData[checkedItems.size()];
        for(int i = 0 ; i < checked.length ; i++) {
            checked[i] = (ColorPreferenceData)checkedItems.get(i);
        }
        return checked;
    }
    
    public ColorPreferenceData getPreviousElement(ColorPreferenceData data) {
    	sort();
        for(int i = 0 ; i < items.size() ; i++) {
            ColorPreferenceData item = (ColorPreferenceData)items.get(i);
            if(item.equals(data)) {
            	try {
            		return (ColorPreferenceData)items.get(i - 1);
            	} catch(Throwable t) {
            		return null;
            	}
            }           
        }
        return null;
    }
    
    public ColorPreferenceData getNextElement(ColorPreferenceData data) {
    	sort();
        for(int i = 0 ; i < items.size() ; i++) {
            ColorPreferenceData item = (ColorPreferenceData)items.get(i);
            if(item.equals(data)) {
            	try {
            		return (ColorPreferenceData)items.get(i + 1);
            	} catch(Throwable t) {
            		return null;
            	}
            }           
        }
        return null;
    }
    
    public ColorPreferenceData getLastElement() {
    	sort();
    	int index = items.size() - 1;
    	if(index < 0) {
    		return null;
    	}
    	return (ColorPreferenceData)items.get(index);
    }
    
    /**
     * adds the prioprity automaticaly
     */
    public void add(ColorPreferenceData data) {
    	int position = 0;
    	ColorPreferenceData lastElement = getLastElement();
    	if(lastElement != null) {
    		position = lastElement.getPosition() + 1;
    	}
    	data.setPosition(position);
        items.add(data);
        sort();
    }
    
    public void delete(ColorPreferenceData data) {
        items.remove(data);
        sort();
    }
    
    public void save() {
        store.setValue(ILogFileViewConstants.PREF_COLORING_ITEMS,PreferenceValueConverter.asString(getAllColorDetails()));
    }
    
    public void loadDefault() { 
        ColorPreferenceData[] items = PreferenceValueConverter.asColorPreferenceDataArray(store.getDefaultString(ILogFileViewConstants.PREF_COLORING_ITEMS));
        this.items.clear();
        for(int i = 0 ; i < items.length ; i++) {
            this.items.add(items[i]);
        }
        sort();
    }
    
    public void load() {
        ColorPreferenceData[] items = PreferenceValueConverter.asColorPreferenceDataArray(store.getString(ILogFileViewConstants.PREF_COLORING_ITEMS));
        this.items.clear();
        for(int i = 0 ; i < items.length ; i++) {
            this.items.add(items[i]);
        }
        sort();
    }
    
    public void removeAll() {
    	items.clear();
    }
    
    // Private ----------------------------------------------------------------------
    
    private void sort() {
    	if(comparator == null) {
    		comparator = new DataObjectComparator();
    	}
    	Collections.sort(items,comparator);
    }
    
    // Inner Classe -----------------------------------------------------------------
    
    
    private class DataObjectComparator implements Comparator {

		public int compare(Object object1, Object object2) {
			ColorPreferenceData data1 = null;
			ColorPreferenceData data2 = null;
			if(object1 instanceof ColorPreferenceData) {
				data1 = (ColorPreferenceData)object1;
			}
			if(object2 instanceof ColorPreferenceData) {
				data2 = (ColorPreferenceData)object2;
			}
			if(data1 == null | data2 == null) {
				return -1;
			}
			if(data1.getPosition() > data2.getPosition()) {
				return 1;
			}
			if(data1.getPosition() == data2.getPosition()) {
				return 0;
			}
			if(data1.getPosition() < data2.getPosition()) {
				return -1;
			}
			return -1;
		}
    	
    }
}
