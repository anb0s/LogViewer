package de.anbos.eclipse.logviewer.plugin.preferences.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import de.anbos.eclipse.logviewer.plugin.ILogViewerConstants;
import de.anbos.eclipse.logviewer.plugin.preferences.PreferenceValueConverter;

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

public class RuleStore {

    // Constant ---------------------------------------------------------------------
    
    // Attribute --------------------------------------------------------------------
    
    private IPreferenceStore store;
    private List items;
    private DataObjectComparator comparator;
    
    // Constructor ------------------------------------------------------------------
    
    public RuleStore(IPreferenceStore store) {
        items = new ArrayList();
        this.store = store;
    }
    
    // Public -----------------------------------------------------------------------
    
    public RulePreferenceData[] getAllRuleDetails() {
        RulePreferenceData[] data = new RulePreferenceData[items.size()];
        for(int i = 0 ; i < data.length ; i++) {
            data[i] = (RulePreferenceData)items.get(i);
        }
        return data;
    }
    
    public RulePreferenceData[] getAllCheckedRuleDetails() {
        List checkedItems = new ArrayList();
        Iterator dataIterator = items.iterator();
        while(dataIterator.hasNext()) {
            RulePreferenceData data = (RulePreferenceData)dataIterator.next();
            if(data.isEnabled()) {
                checkedItems.add(data);
            }
        }
        if(checkedItems.size() <= 0) {
        	return new RulePreferenceData[0];
        }
        RulePreferenceData[] checked = new RulePreferenceData[checkedItems.size()];
        for(int i = 0 ; i < checked.length ; i++) {
            checked[i] = (RulePreferenceData)checkedItems.get(i);
        }
        return checked;
    }
    
    public RulePreferenceData getPreviousElement(RulePreferenceData data) {
    	sort();
        for(int i = 0 ; i < items.size() ; i++) {
            RulePreferenceData item = (RulePreferenceData)items.get(i);
            if(item.equals(data)) {
            	try {
            		return (RulePreferenceData)items.get(i - 1);
            	} catch(Throwable t) {
            		return null;
            	}
            }           
        }
        return null;
    }
    
    public RulePreferenceData getNextElement(RulePreferenceData data) {
    	sort();
        for(int i = 0 ; i < items.size() ; i++) {
            RulePreferenceData item = (RulePreferenceData)items.get(i);
            if(item.equals(data)) {
            	try {
            		return (RulePreferenceData)items.get(i + 1);
            	} catch(Throwable t) {
            		return null;
            	}
            }           
        }
        return null;
    }
    
    public RulePreferenceData getLastElement() {
    	sort();
    	int index = items.size() - 1;
    	if(index < 0) {
    		return null;
    	}
    	return (RulePreferenceData)items.get(index);
    }
    
    /**
     * adds the prioprity automaticaly
     */
    public void add(RulePreferenceData data) {
    	int position = 0;
    	RulePreferenceData lastElement = getLastElement();
    	if(lastElement != null) {
    		position = lastElement.getPosition() + 1;
    	}
    	data.setPosition(position);
        items.add(data);
        sort();
    }
    
    public void delete(RulePreferenceData data) {
        items.remove(data);
        sort();
    }
    
    public void save() {
        store.setValue(ILogViewerConstants.PREF_COLORING_ITEMS,PreferenceValueConverter.asString(getAllRuleDetails()));
    }
    
    public void loadDefault() { 
        RulePreferenceData[] items = PreferenceValueConverter.asRulePreferenceDataArray(store.getDefaultString(ILogViewerConstants.PREF_COLORING_ITEMS));
        this.items.clear();
        for(int i = 0 ; i < items.length ; i++) {
            this.items.add(items[i]);
        }
        sort();
    }
    
    public void load() {
        RulePreferenceData[] items = PreferenceValueConverter.asRulePreferenceDataArray(store.getString(ILogViewerConstants.PREF_COLORING_ITEMS));
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
			RulePreferenceData data1 = null;
			RulePreferenceData data2 = null;
			if(object1 instanceof RulePreferenceData) {
				data1 = (RulePreferenceData)object1;
			}
			if(object2 instanceof RulePreferenceData) {
				data2 = (RulePreferenceData)object2;
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
