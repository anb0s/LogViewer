package de.anbos.eclipse.logviewer.plugin.preferences.rule;

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

public class RulePreferenceData {
    
    // Attribute --------------------------------------------------------------------
    	
    private int position;
    private boolean enabled;
    
    // Rule
    private String rule;
    private String value;
    private String matchMode;
    private boolean caseInsensitive;

    // Action: coloring
    private boolean coloringEnabled; 
    private RGB background;
    private RGB foreground;
    
    // Constructor ------------------------------------------------------------------
    
    public RulePreferenceData() {
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
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
    
    /**
     * @param value The value to set.
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * @return Returns the case insensitive.
     */
    public boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    /**
     * @return Returns the value.
     */
    public String getMatchMode() {
        return matchMode;
    }

    /**
     * @param value The value to set.
     */
    public void setMatchMode(String matchMode) {
        this.matchMode = matchMode;
    }
    
    public boolean isColoringEnabled() {
		return coloringEnabled;
	}

	public void setColoringEnabled(boolean coloringEnabled) {
		this.coloringEnabled = coloringEnabled;
	}

	public boolean equals(Object object) {
    	if(!(object instanceof RulePreferenceData)) {
    		return false;
    	}
    	RulePreferenceData data = (RulePreferenceData)object;
    	if(data.getPosition() == this.getPosition() &
    			data.getBackground().equals(this.getBackground()) &
    			data.getForeground().equals(this.getForeground()) &
    			data.getRule().equals(this.getRule()) &
    			data.getValue().equals(this.getValue()) &
    			data.getCaseInsensitive() == this.getCaseInsensitive() &
    			data.getMatchMode().equals(this.getMatchMode())) {
    		return true;
    	}
    	return false;
    }
    
    // Private ----------------------------------------------------------------------
}
