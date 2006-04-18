package ch.mimo.eclipse.plugin.logfiletools.viewer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import ch.mimo.eclipse.plugin.logfiletools.ILogFileViewConstants;
import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;
import ch.mimo.eclipse.plugin.logfiletools.preferences.PreferenceValueConverter;

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

public class DynamicRuleBasedScanner implements ICharacterScanner, ITokenScanner {

    // Constant ---------------------------------------------------------------------
    
	/** Internal setting for the uninitialized column cache. */
	private static final int UNDEFINED= -1;
    
    // Attribute --------------------------------------------------------------------
    
    private IToken defaultToken;
    private IDocument document;
    private int offset;
    private int column;
    private int rangeEnd;
    private int tokenOffset;
    private char[][] delimiter;
    private List rules;
    
    // Constructor ------------------------------------------------------------------
    
    public DynamicRuleBasedScanner(String rulesPreferenceString) {
    	LogFileViewPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(new PropertyChangeListener());
        rules = new Vector();
        loadRules(rulesPreferenceString);        
    }
    
    // Public -----------------------------------------------------------------------
    
    public void setDefaultToken(IToken defaultToken) {
        this.defaultToken = defaultToken;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ITokenScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
     */
    public void setRange(IDocument document, int offset, int length) {
		this.document = document;
		this.offset = offset;
		this.column = UNDEFINED;
		this.rangeEnd = Math.min(this.document.getLength(), offset + length);
		
		String[] delimiters= this.document.getLegalLineDelimiters();
		this.delimiter = new char[delimiters.length][];
		for (int i= 0; i < delimiters.length; i++)
			this.delimiter[i]= delimiters[i].toCharArray();
			
		if (this.defaultToken == null) {
			this.defaultToken= new Token(null);
		}
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
     */
    public char[][] getLegalLineDelimiters() {
        return delimiter;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
     */
    public int getColumn() {
		if (column == UNDEFINED) {
			try {
				int line = document.getLineOfOffset(offset);
				int start = document.getLineOffset(line);
				
				column = offset - start;
				
			} catch (BadLocationException ex) {
			}
		}
		return column;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
     */
    public int read() {
		try {
			
			if (offset < rangeEnd) {
				try {
					return document.getChar(offset);
				} catch (BadLocationException e) {
				}
			}
			return EOF;
		
		} finally {
			++offset;
			column = UNDEFINED;
		}
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
     */
    public void unread() {
        --offset;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
     */
    public IToken nextToken() {
		IToken token;
		while (true) {
			tokenOffset = offset;
			column = UNDEFINED;
			Iterator ruleIterator = rules.iterator();
			while(ruleIterator.hasNext()) {
			    IRule rule = (IRule)ruleIterator.next();
			    token = rule.evaluate(this);
			    if(!token.isUndefined()) {
			        return token;
			    }
			}
			if (read() == EOF) {
				return Token.EOF;
			} else {
				return defaultToken;
			}
		}
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenOffset()
     */
    public int getTokenOffset() {
        return tokenOffset;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.ITokenScanner#getTokenLength()
     */
    public int getTokenLength() {
		if (offset < rangeEnd) {
			return offset - getTokenOffset();
		}
		return rangeEnd - getTokenOffset();
    }
    
    // Private -----------------------------------------------------------------
    
    private void loadRules(String newRules) {
		List newRulesList = PreferenceValueConverter.asRuleArray(newRules);
		rules.clear();
		rules.addAll(newRulesList);    	
    }
    
	// Inner classes ----------------------------------------------------------------
	
	private class PropertyChangeListener implements IPropertyChangeListener {
		
			/* (non-Javadoc)
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent event) {
			if(event.getProperty().equals(ILogFileViewConstants.PREF_COLORING_ITEMS)) {
				loadRules((String)event.getNewValue());
			}
		}
	}

}
