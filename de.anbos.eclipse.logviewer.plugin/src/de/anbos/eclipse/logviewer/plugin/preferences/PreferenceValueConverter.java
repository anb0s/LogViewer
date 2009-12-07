package de.anbos.eclipse.logviewer.plugin.preferences;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.anbos.eclipse.logviewer.plugin.LogFile;
import de.anbos.eclipse.logviewer.plugin.LogViewerPlugin;
import de.anbos.eclipse.logviewer.plugin.preferences.rule.RulePreferenceData;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.ILogFileToolRule;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.LogToolRuleDesc;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.RuleFactory;

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

public class PreferenceValueConverter {

	// Constant ----------------------------------------------------------------
	
	public static String VALUE_DELIMITER	= "|"; //$NON-NLS-1$
	public static String ITEM_DELIMITER		= "#"; //$NON-NLS-1$
	
	// Static ------------------------------------------------------------------
	
    public static String asString(RulePreferenceData[] items) {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0 ; i < items.length ; i++) {
            buffer.append(asString(items[i]));
            buffer.append(ITEM_DELIMITER);
        }
        return buffer.toString();
    }
    
    public static final String asString(RulePreferenceData data) {
        String position = Base64.encode(Integer.toString(data.getPosition()));
        String checked = Base64.encode(Boolean.toString(data.isEnabled()));
        String rule = Base64.encode(LogViewerPlugin.getResourceString(data.getRule()));
        String background = Base64.encode(StringConverter.asString(data.getBackground()));
        String foreground = Base64.encode(StringConverter.asString(data.getForeground()));
        String value = Base64.encode(data.getValue());
		String matchMode = Base64.encode(data.getMatchMode());
		String caseInsensitive = Base64.encode(Boolean.toString(data.getCaseInsensitive()));        
        return position + VALUE_DELIMITER + checked + VALUE_DELIMITER + rule + VALUE_DELIMITER + background + VALUE_DELIMITER + foreground + VALUE_DELIMITER + value + VALUE_DELIMITER + matchMode + VALUE_DELIMITER + caseInsensitive;
    }
    
    public static RulePreferenceData[] asColorPreferenceDataArray(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
        RulePreferenceData[] items = new RulePreferenceData[tokenizer.countTokens()];
        for(int i = 0 ; i < items.length ; i++) {
            items[i] = asColorPreferenceData(tokenizer.nextToken());
        }
        return items;
    }
    
    public static RulePreferenceData asColorPreferenceData(String value) {
        RulePreferenceData data = new RulePreferenceData();
        if(value == null || value.length() <= 0) {
            return data;
        }
        StringTokenizer tokenizer = new StringTokenizer(value,VALUE_DELIMITER);
        data.setPosition(Integer.parseInt(Base64.decode(tokenizer.nextToken())));
        data.setEnabled(Boolean.valueOf(Base64.decode(tokenizer.nextToken())).booleanValue());
        data.setRule(LogViewerPlugin.getResourceString(Base64.decode(tokenizer.nextToken())));
        data.setBackground(StringConverter.asRGB(Base64.decode(tokenizer.nextToken())));
        data.setForeground(StringConverter.asRGB(Base64.decode(tokenizer.nextToken())));
        data.setValue(Base64.decode(tokenizer.nextToken()));        
        String matchMode = null;
		try {
			matchMode = tokenizer.nextToken();
			matchMode = Base64.decode(matchMode);
		} catch (NoSuchElementException e) {
			matchMode = "match ";
		}
		data.setMatchMode(matchMode);
		String caseInsensitive = null;
		try {
			caseInsensitive = tokenizer.nextToken();
			caseInsensitive = Base64.decode(caseInsensitive);
		} catch (NoSuchElementException e) {
			caseInsensitive = "false";
		}		
		data.setCaseInsensitive(Boolean.valueOf(caseInsensitive));
        return data;
    }
	
	public static ILogFileToolRule asRule(String value) {
		if(value == null || value.length() <= 0) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(value,VALUE_DELIMITER);
        int priority = Integer.parseInt(Base64.decode(tokenizer.nextToken()));
		String checked = Base64.decode(tokenizer.nextToken());
		String ruleName = Base64.decode(tokenizer.nextToken());
		String background = Base64.decode(tokenizer.nextToken());
		String foreground = Base64.decode(tokenizer.nextToken());
		String ruleValue = Base64.decode(tokenizer.nextToken());
		String matchMode = null;
		try {
			matchMode = tokenizer.nextToken();
			matchMode = Base64.decode(matchMode);
		} catch (NoSuchElementException e) {
			matchMode = "match ";
		}
		String caseInsensitive = null;
		try {
			caseInsensitive = tokenizer.nextToken();
			caseInsensitive = Base64.decode(caseInsensitive);
		} catch (NoSuchElementException e) {
			caseInsensitive = "false";
		}		
		if(!Boolean.valueOf(checked).booleanValue()) {
			return null;
		}		
		Color backgroundColor = new Color(Display.getDefault(),StringConverter.asRGB(background));
		Color foregroundColor = new Color(Display.getDefault(),StringConverter.asRGB(foreground));
		matchMode = matchMode.toLowerCase().substring(0, matchMode.indexOf(" "));
		LogToolRuleDesc ruleDesc = new LogToolRuleDesc(priority,ruleValue,backgroundColor,foregroundColor,matchMode,Boolean.valueOf(caseInsensitive).booleanValue());
		return RuleFactory.getRule(ruleName,ruleDesc);
	}

	public static List asRuleArray(String value) {
		List rules = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			ILogFileToolRule rule = asRule(tokenizer.nextToken());
			if(rule != null) {
				rules.add(rule);
			}
		}		
		return rules;
	}
	
	public static String asString(HistoryFile historyFile) {
		return historyFile.getPath() + VALUE_DELIMITER + historyFile.getCount();
	}
	
	public static HistoryFile asHistoryFile(String value) {
		StringTokenizer tokenizer = new StringTokenizer(value,VALUE_DELIMITER);
		return new HistoryFile(tokenizer.nextToken(),Integer.parseInt(tokenizer.nextToken()));
	}
	
	public static String asString(List historyFiles) {
		Iterator it = historyFiles.iterator();
		StringBuffer buffer = new StringBuffer();
		while(it.hasNext()) {
			HistoryFile file = (HistoryFile)it.next();
			buffer.append(asString(file));
			buffer.append(ITEM_DELIMITER);
		}
		return buffer.toString();
	}
	
	public static List asUnsortedHistoryFileList(String value) {
		List files = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			files.add(asHistoryFile(token));
		}
		return files;
	}

	public static String asString(LogFile logFile) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(logFile.getFileName());
		buffer.append(VALUE_DELIMITER);
		buffer.append(logFile.getTabName());
		buffer.append(VALUE_DELIMITER);
		buffer.append(logFile.getMonitor());
		return buffer.toString();
	}
	
	public static String asLogFileListString(List logFiles) {
		Iterator it = logFiles.iterator();
		StringBuffer buffer = new StringBuffer();
		while(it.hasNext()) {
			LogFile logFile = (LogFile)it.next();
			buffer.append(asString(logFile));
			buffer.append(ITEM_DELIMITER);
		}
		return buffer.toString();
	}
	
	public static LogFile asLogFile(String logFileStr) {
		String str[] = new String[3];
		StringTokenizer tokenizer = new StringTokenizer(logFileStr,VALUE_DELIMITER);
		for (int i=0;i<3;i++) {			
			if(tokenizer.hasMoreTokens()) {
				str[i] = tokenizer.nextToken();
			} else {
				str[i] = null;
			}
		}
		LogFile logFile = new LogFile(str[0], str[1], !("false".equals(str[2])));
		return logFile;
	}
	
	public static List asLogFileList(String logFileList) {
		List files = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(logFileList,ITEM_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			files.add(asLogFile(token));
		}
		return files;
	}
}
