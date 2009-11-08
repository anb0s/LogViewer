package de.anbos.eclipse.logviewer.plugin.viewer.rule;

import org.eclipse.swt.graphics.Color;

import de.anbos.eclipse.logviewer.plugin.LogFileViewPlugin;

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

public class RuleFactory {

	// Constant ----------------------------------------------------------------
	
	private static final String[] RULES = new String[] {
		LogFileViewPlugin.getResourceString(WordRule.class.getName()),
		LogFileViewPlugin.getResourceString(RegExpRule.class.getName())
	};
	
	// Public ------------------------------------------------------------------
	
	public static ILogFileToolColoringRule getRule(String rule, int priority, String value, Color backgroundColor, Color foregroundColor) {
		if(isWordRule(rule)) {
			return getWordRule(priority,value,backgroundColor,foregroundColor);
		}
		if(isRegExpRule(rule)) {
			return getRegExpRule(priority,value,backgroundColor,foregroundColor);
		}
		return null;
	}
	
	public static boolean isWordRule(String rule) {
		return rule.equals(WordRule.class.getName());
	}
	
	public static WordRule getWordRule(int priority, String value, Color backgroundColor, Color foregroundColor) {
		return new WordRule(priority,value,backgroundColor,foregroundColor);
	}
	
	public static boolean isRegExpRule(String rule) {
		return rule.equals(RegExpRule.class.getName());
	}
	
	public static RegExpRule getRegExpRule(int priority, String value, Color backgroundColor, Color foregroundColor) {
		return new RegExpRule(priority,value,backgroundColor,foregroundColor);
	}
	
	public static String[] getAllRulesAsComboNames() {
		return RULES;
	}
	
	public static String getDefaultRule() {
		return RULES[0];
	}
}
