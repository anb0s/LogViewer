package ch.mimo.eclipse.plugin.logfiletools.preferences.color;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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

public class ColorLableProvider extends LabelProvider implements ITableLabelProvider {

    // Public -----------------------------------------------------------------------
    
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if(!(element instanceof ColorPreferenceData)) {
            return ""; //$NON-NLS-1$
        }
        ColorPreferenceData data = (ColorPreferenceData)element;
        switch(columnIndex) {
            case 0:
                return data.getRule();
            case 1:
            	return data.getValue();
//                return StringConverter.asString(data.getBackground());
//            case 2:
//                return StringConverter.asString(data.getForeground());
//            case 3:
//                return data.getValue();
//            case 4:
//                return Integer.toString(data.getPosition());
            default:
                return ""; //$NON-NLS-1$
        }
    }

}
