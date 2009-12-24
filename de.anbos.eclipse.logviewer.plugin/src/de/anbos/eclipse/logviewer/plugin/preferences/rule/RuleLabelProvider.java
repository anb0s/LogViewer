package de.anbos.eclipse.logviewer.plugin.preferences.rule;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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

public class RuleLabelProvider extends LabelProvider implements ITableLabelProvider {

    // Public -----------------------------------------------------------------------
    
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if(!(element instanceof RulePreferenceData)) {
            return ""; //$NON-NLS-1$
        }
        RulePreferenceData data = (RulePreferenceData)element;
        switch(columnIndex) {
            case 0:
                return data.getRuleNameShort();
            case 1:
            	return data.getRuleValue();
            default:
                return ""; //$NON-NLS-1$
        }
    }

}
