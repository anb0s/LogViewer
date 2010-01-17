/*
 * Copyright 2009 - 2010 by Andre Bossert
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

import java.util.regex.Pattern;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class JavaRegExpRule implements IPredicateRule, ILogFileToolRule {

	// Attribute ---------------------------------------------------------------

	private Pattern regexp;
	private boolean find;
	private Token successToken;
	private int priority;
	
	// Constructor -------------------------------------------------------------
	
	public JavaRegExpRule(LogToolRuleDesc ruleDesc) {
		int flags = 0;
		find = false;
		if (ruleDesc.isCaseInsensitive())
			flags = java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.UNICODE_CASE;
		if (ruleDesc.getMatchMode().startsWith("find"))
			find = true;
		regexp = Pattern.compile(ruleDesc.getRuleValue(),flags);
		priority = ruleDesc.getPriority();
		successToken = new Token(new TokenData(new TextAttribute(new Color(Display.getDefault(),ruleDesc.getForegroundColor()),new Color(Display.getDefault(),ruleDesc.getBackgroundColor()),SWT.NORMAL),priority));
	}
	
	// Static ------------------------------------------------------------------
	
	// Public ------------------------------------------------------------------
	
	public IToken getSuccessToken() {
		return successToken;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		String line = returnNextCompleteLine(scanner);
		if(line != null) {
			if (find) {
				if(regexp.matcher(line).find()) {
					return successToken;
				}
			} else {
				if(regexp.matcher(line).matches()) {
					return successToken;
				}			
			}
		}
		return Token.UNDEFINED;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner,false);
	}
	
	public int getPriority() {
		return priority;
	}
	
	// Private -----------------------------------------------------------------
	
	private String returnNextCompleteLine(ICharacterScanner scanner) {

		char[][] lineDelimiters= scanner.getLegalLineDelimiters();
		
		int c;
		StringBuffer buffer = new StringBuffer();
		while((c = scanner.read()) != ICharacterScanner.EOF) {
			if(isEOLCharacter(c,lineDelimiters)) {
				return buffer.toString();
			}
			buffer.append((char)c);
		}
		return null;
	}
	
	private boolean isEOLCharacter(int c, char[][] eolChars) {
		for (int i= 0; i < eolChars.length; i++) {
			if (c == eolChars[i][0])
				return true;
		}
		return false;
	}
}
