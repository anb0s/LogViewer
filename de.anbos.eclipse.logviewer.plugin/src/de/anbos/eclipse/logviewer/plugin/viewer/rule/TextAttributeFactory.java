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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class TextAttributeFactory {

	// Public ------------------------------------------------------------------
	
	public static TextAttribute getTextAttribute(LogToolRuleDesc ruleDesc) {		
		if (ruleDesc != null) {
			int style = SWT.NORMAL;
			if (ruleDesc.isItalic())
				style |= SWT.ITALIC;
			if (ruleDesc.isBold())
				style |= SWT.BOLD;
			if (ruleDesc.isStrikethrough())
				style |= TextAttribute.STRIKETHROUGH;
			if (ruleDesc.isUnderline())
				style |= TextAttribute.UNDERLINE;			
			return new TextAttribute(new Color(Display.getDefault(),ruleDesc.getForegroundColor()),new Color(Display.getDefault(),ruleDesc.getBackgroundColor()), style);	
		}
		return null;
	}	
}
