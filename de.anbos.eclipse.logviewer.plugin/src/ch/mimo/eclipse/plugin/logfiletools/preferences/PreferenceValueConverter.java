package ch.mimo.eclipse.plugin.logfiletools.preferences;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import ch.mimo.eclipse.plugin.logfiletools.LogFile;
import ch.mimo.eclipse.plugin.logfiletools.LogFileViewPlugin;
import ch.mimo.eclipse.plugin.logfiletools.preferences.color.ColorPreferenceData;
import ch.mimo.eclipse.plugin.logfiletools.viewer.rule.ILogFileToolColoringRule;
import ch.mimo.eclipse.plugin.logfiletools.viewer.rule.RuleFactory;

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
	
    public static String asString(ColorPreferenceData[] items) {
        StringBuffer buffer = new StringBuffer();
        for(int i = 0 ; i < items.length ; i++) {
            buffer.append(asString(items[i]));
            buffer.append(ITEM_DELIMITER);
        }
        return buffer.toString();
    }
    
    public static final String asString(ColorPreferenceData data) {
        String position = Base64.encode(Integer.toString(data.getPosition()));
        String checked = Base64.encode(Boolean.toString(data.isChecked()));
        String rule = Base64.encode(LogFileViewPlugin.getResourceString(data.getRule()));
        String background = Base64.encode(StringConverter.asString(data.getBackground()));
        String foreground = Base64.encode(StringConverter.asString(data.getForeground()));
        String value = Base64.encode(data.getValue());
        return position + VALUE_DELIMITER + checked + VALUE_DELIMITER + rule + VALUE_DELIMITER + background + VALUE_DELIMITER + foreground + VALUE_DELIMITER + value;
    }
    
    public static ColorPreferenceData[] asColorPreferenceDataArray(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
        ColorPreferenceData[] items = new ColorPreferenceData[tokenizer.countTokens()];
        for(int i = 0 ; i < items.length ; i++) {
            items[i] = asColorPreferenceData(tokenizer.nextToken());
        }
        return items;
    }
    
    public static ColorPreferenceData asColorPreferenceData(String value) {
        ColorPreferenceData data = new ColorPreferenceData();
        if(value == null || value.length() <= 0) {
            return data;
        }
        StringTokenizer tokenizer = new StringTokenizer(value,VALUE_DELIMITER);
        data.setPosition(Integer.parseInt(Base64.decode(tokenizer.nextToken())));
        data.setChecked(Boolean.valueOf(Base64.decode(tokenizer.nextToken())).booleanValue());
        data.setRule(LogFileViewPlugin.getResourceString(Base64.decode(tokenizer.nextToken())));
        data.setBackground(StringConverter.asRGB(Base64.decode(tokenizer.nextToken())));
        data.setForeground(StringConverter.asRGB(Base64.decode(tokenizer.nextToken())));
        data.setValue(Base64.decode(tokenizer.nextToken()));
        return data;
    }
	
	public static ILogFileToolColoringRule asRule(String value) {
		if(value == null || value.length() <= 0) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(value,VALUE_DELIMITER);
        int priority = Integer.parseInt(Base64.decode(tokenizer.nextToken()));
		String checked = Base64.decode(tokenizer.nextToken());
		String rule = Base64.decode(tokenizer.nextToken());
		String background = Base64.decode(tokenizer.nextToken());
		String foreground = Base64.decode(tokenizer.nextToken());
		String ruleValue = Base64.decode(tokenizer.nextToken());	
		if(!Boolean.valueOf(checked).booleanValue()) {
			return null;
		}
		Color backgroundColor = new Color(Display.getDefault(),StringConverter.asRGB(background));
		Color foregroundColor = new Color(Display.getDefault(),StringConverter.asRGB(foreground));
		return RuleFactory.getRule(rule,priority,ruleValue,backgroundColor,foregroundColor);
	}
	
	public static List asRuleArray(String value) {
		List rules = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			ILogFileToolColoringRule rule = asRule(tokenizer.nextToken());
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
		return logFile.getFileName();
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
	
	public static LogFile asLogFile(String value) {
		LogFile logFile = new LogFile(value);
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
