package ch.mimo.eclipse.plugin.logfiletools.viewer.rule;

import org.apache.regexp.RE;
import org.apache.regexp.REUtil;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

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

public class RegExpRule implements IPredicateRule, ILogFileToolColoringRule {

	// Attribute ---------------------------------------------------------------

	private RE regexp;
	private Token successToken;
	private int priority;
	
	// Constructor -------------------------------------------------------------
	
	public RegExpRule(int priority, String ruleValue, Color backgroundColor, Color foregroundColor) {
		regexp = REUtil.createRE(ruleValue);
		successToken = new Token(new TokenData(new TextAttribute(foregroundColor,backgroundColor,SWT.NORMAL),priority));
		this.priority = priority;
	}
	
	// Static ------------------------------------------------------------------
	
	// Public ------------------------------------------------------------------
	
	public IToken getSuccessToken() {
		return successToken;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		String line = returnNextCompleteLine(scanner);
		if(line == null) {
			return Token.UNDEFINED;
		}
		if(regexp.match(line)) {
			return successToken;
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
