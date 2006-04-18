package ch.mimo.eclipse.plugin.logfiletools.viewer.rule;

import org.eclipse.jface.text.rules.IWordDetector;

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

public class WordDetector implements IWordDetector {

	private String word;
	
	public WordDetector(String word) {
		this.word = word;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c) {
		if(word.indexOf(c) > 0) {
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c) {
		if(word.charAt(0) == c) {
			return true;
		}
		return false;
	}
}
