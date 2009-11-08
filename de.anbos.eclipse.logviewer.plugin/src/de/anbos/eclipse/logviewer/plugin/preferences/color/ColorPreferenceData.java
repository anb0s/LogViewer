package de.anbos.eclipse.logviewer.plugin.preferences.color;

import org.eclipse.swt.graphics.RGB;

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

public class ColorPreferenceData {
    
    // Attribute --------------------------------------------------------------------
    
    private int position;
    private boolean checked;
    private String rule;
    private RGB background;
    private RGB foreground;
    private String value;
    
    // Constructor ------------------------------------------------------------------
    
    public ColorPreferenceData() {
    }
    
    // Public -----------------------------------------------------------------------
    
    /**
     * @return Returns the background.
     */
    public RGB getBackground() {
        return background;
    }

    /**
     * @param background The background to set.
     */
    public void setBackground(RGB background) {
        this.background = background;
    }

    /**
     * @return Returns the checked.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked The checked to set.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return Returns the foreground.
     */
    public RGB getForeground() {
        return foreground;
    }

    /**
     * @param foreground The foreground to set.
     */
    public void setForeground(RGB foreground) {
        this.foreground = foreground;
    }

    /**
     * @return Returns the position.
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return Returns the rule.
     */
    public String getRule() {
        return rule;
    }

    /**
     * @param rule The rule to set.
     */
    public void setRule(String rule) {
        this.rule = rule;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    public boolean equals(Object object) {
    	if(!(object instanceof ColorPreferenceData)) {
    		return false;
    	}
    	ColorPreferenceData data = (ColorPreferenceData)object;
    	if(data.getPosition() == this.getPosition() &
    			data.getBackground().equals(this.getBackground()) &
    			data.getForeground().equals(this.getForeground()) &
    			data.getRule().equals(this.getRule()) &
    			data.getValue().equals(this.getValue())) {
    		return true;
    	}
    	return false;
    }
    
    // Private ----------------------------------------------------------------------
}
