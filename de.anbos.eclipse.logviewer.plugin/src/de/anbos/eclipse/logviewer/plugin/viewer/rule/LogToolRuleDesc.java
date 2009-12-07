/*
 * Copyright 2009 by Andre Bossert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package de.anbos.eclipse.logviewer.plugin.viewer.rule;

import org.eclipse.swt.graphics.Color;

public class LogToolRuleDesc {

	// Attribute ---------------------------------------------------------------

	private	int priority;
	private String ruleValue;
	private Color backgroundColor;
	private Color foregroundColor;
	private String matchMode;
	private boolean caseInsensitive;

	// Constructor -------------------------------------------------------------
	
	public LogToolRuleDesc(int priority, String ruleValue, Color backgroundColor, Color foregroundColor, String matchMode, boolean caseInsensitive) {
		this.priority = priority;
		this.ruleValue = ruleValue;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.matchMode = matchMode;
		this.caseInsensitive = caseInsensitive;
	}

	// Public ------------------------------------------------------------------

	public int getPriority() {
		return priority;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public String getMatchMode() {
		return matchMode;
	}

	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public void setMatchMode(String matchMode) {
		this.matchMode = matchMode;
	}

	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}	
	
}
