package ch.mimo.eclipse.plugin.logfiletools.viewer.rule;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

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

public class WordRule extends WordPatternRule implements ILogFileToolColoringRule {

	// Attribute ---------------------------------------------------------------
	
	private int priority;
	
	// Constructor -------------------------------------------------------------
	
	public WordRule(int priority, String ruleValue, Color backgroundColor, Color foregroundColor) {
		super(new WordDetector(ruleValue),ruleValue.substring(0,1),ruleValue.substring(ruleValue.length() - 1,ruleValue.length()),new Token(new TokenData(new TextAttribute(foregroundColor,backgroundColor,SWT.NORMAL),priority)));
		this.priority = priority;
	}
	
	// Static ------------------------------------------------------------------
	
	// Public ------------------------------------------------------------------
	
	public int getPriority() {
		return priority;
	}
}
