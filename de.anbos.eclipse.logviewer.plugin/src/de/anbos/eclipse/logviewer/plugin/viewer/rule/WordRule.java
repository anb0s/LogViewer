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

/**
 * I have decided to use the JakartaRegExpRule instead of the eclipse internal word
 * matching magic due to the fact that the eclipse internal stuff rewindes the
 * cursor all the time and that is posion for my rulebased scanner!
 */

package de.anbos.eclipse.logviewer.plugin.viewer.rule;

public class WordRule extends JakartaRegExpRule {

	// Attribute ---------------------------------------------------------------
	
	// Constructor -------------------------------------------------------------
	
	public WordRule(LogToolRuleDesc ruleDesc) {
		super(ruleDesc);
	}

	// Static ------------------------------------------------------------------
	
	// Public ------------------------------------------------------------------
}
