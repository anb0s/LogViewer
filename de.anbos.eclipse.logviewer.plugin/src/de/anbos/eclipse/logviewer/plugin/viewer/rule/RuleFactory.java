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

package de.anbos.eclipse.logviewer.plugin.viewer.rule;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;

public class RuleFactory {

	// Constant ----------------------------------------------------------------
	
	private static final String[] RULES = new String[] {
		LogViewerPlugin.getResourceString(WordRule.class.getName()),
		LogViewerPlugin.getResourceString(JakartaRegExpRule.class.getName()),
		LogViewerPlugin.getResourceString(JavaRegExpRule.class.getName())
	};
	
	// Public ------------------------------------------------------------------
	
	public static ILogFileToolRule getRule(String ruleName, LogToolRuleDesc ruleDesc) {
		if (ruleDesc.isEnabled())
		{
			if(isWordRule(ruleName)) {
				return getWordRule(ruleDesc);
			}
			if(isJakartaRegExpRule(ruleName)) {
				return getJakartaRegExpRule(ruleDesc);
			}
			if(isJavaRegExpRule(ruleName)) {
				return getJavaRegExpRule(ruleDesc);
			}
		}
		return null;
	}
	
	public static boolean isWordRule(String ruleName) {
		return ruleName.equals(WordRule.class.getName());
	}
	
	public static WordRule getWordRule(LogToolRuleDesc ruleDesc) {
		return new WordRule(ruleDesc);
	}
	
	public static boolean isJakartaRegExpRule(String ruleName) {
		return ruleName.equals(JakartaRegExpRule.class.getName());
	}
	
	public static JakartaRegExpRule getJakartaRegExpRule(LogToolRuleDesc ruleDesc) {
		return new JakartaRegExpRule(ruleDesc);
	}

	public static boolean isJavaRegExpRule(String ruleName) {
		return ruleName.equals(JavaRegExpRule.class.getName());
	}
	
	public static JavaRegExpRule getJavaRegExpRule(LogToolRuleDesc ruleDesc) {
		return new JavaRegExpRule(ruleDesc);
	}
	
	public static String[] getAllRulesAsComboNames() {
		return RULES;
	}
	
	public static String getDefaultRule() {
		return RULES[0];
	}
}
