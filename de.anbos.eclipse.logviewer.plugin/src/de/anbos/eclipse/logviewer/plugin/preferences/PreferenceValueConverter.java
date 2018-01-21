/*******************************************************************************
 * Copyright (c) 2007 - 2011 by Michael Mimo Moratti
 * Copyright (c) 2012 - 2018 by Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Mimo Moratti - initial API and implementation and/or initial documentation
 *    Andre Bossert - extensions
 *******************************************************************************/

package de.anbos.eclipse.logviewer.plugin.preferences;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.jface.resource.StringConverter;
import de.anbos.eclipse.logviewer.plugin.LogFile;
import de.anbos.eclipse.logviewer.plugin.LogFile.LogFileType;
import de.anbos.eclipse.logviewer.plugin.preferences.rule.RulePreferenceData;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.ILogFileToolRule;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.LogToolRuleDesc;
import de.anbos.eclipse.logviewer.plugin.viewer.rule.RuleFactory;

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
        String rule = Base64.encode(data.getRuleName());
        String background = Base64.encode(StringConverter.asString(data.getBackgroundColor()));
        String foreground = Base64.encode(StringConverter.asString(data.getForegroundColor()));
        String value = Base64.encode(data.getRuleValue());
		String matchMode = Base64.encode(data.getMatchMode());
		String caseInsensitive = Base64.encode(Boolean.toString(data.isCaseInsensitive()));
        return position + VALUE_DELIMITER + checked + VALUE_DELIMITER + rule + VALUE_DELIMITER + background + VALUE_DELIMITER + foreground + VALUE_DELIMITER + value + VALUE_DELIMITER + matchMode + VALUE_DELIMITER + caseInsensitive;
    }

    public static RulePreferenceData[] asRulePreferenceDataArray(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
        RulePreferenceData[] items = new RulePreferenceData[tokenizer.countTokens()];
        for(int i = 0 ; i < items.length ; i++) {
            items[i] = asRulePreferenceData(tokenizer.nextToken());
        }
        return items;
    }

    public static RulePreferenceData asRulePreferenceData(String value) {
        RulePreferenceData data = new RulePreferenceData();
        data.fillTokens(value, VALUE_DELIMITER);
        return data;
    }

	public static ILogFileToolRule asRule(String value) {
		if(value == null || value.length() <= 0) {
			return null;
		}
		// get tokens
		LogToolRuleDesc ruleDesc = new LogToolRuleDesc();
		ruleDesc.fillTokens(value, VALUE_DELIMITER);
		return RuleFactory.getRule(ruleDesc.getRuleName(), ruleDesc);
	}

	public static List<ILogFileToolRule> asRuleArray(String value) {
		List<ILogFileToolRule> rules = new Vector<ILogFileToolRule>();
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
		return historyFile.getPath() + VALUE_DELIMITER + historyFile.getCount() + VALUE_DELIMITER + historyFile.getType()+ VALUE_DELIMITER + historyFile.getNamePattern();
	}

	public static HistoryFile asHistoryFile(String value) {
	    final int numStr = 4;
		String str[] = new String[numStr];
		StringTokenizer tokenizer = new StringTokenizer(value,VALUE_DELIMITER);
		for (int i=0;i<numStr;i++) {
			if(tokenizer.hasMoreTokens()) {
				str[i] = tokenizer.nextToken();
			} else {
				str[i] = null;
			}
		}
		return new HistoryFile(str[0], str[3], asType(str[2]), Integer.parseInt(str[1]));
	}

	public static String asString(List<?> historyFiles) {
		Iterator<?> it = historyFiles.iterator();
		StringBuffer buffer = new StringBuffer();
		while(it.hasNext()) {
			HistoryFile file = (HistoryFile)it.next();
			buffer.append(asString(file));
			buffer.append(ITEM_DELIMITER);
		}
		return buffer.toString();
	}

	public static List<HistoryFile> asUnsortedHistoryFileList(String value) {
		List<HistoryFile> files = new Vector<HistoryFile>();
		StringTokenizer tokenizer = new StringTokenizer(value,ITEM_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			files.add(asHistoryFile(token));
		}
		return files;
	}

	public static String asString(LogFile logFile) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(logFile.getPath());
		buffer.append(VALUE_DELIMITER);
		buffer.append(logFile.getNamePattern());
		buffer.append(VALUE_DELIMITER);
		buffer.append(logFile.getEncoding());
		buffer.append(VALUE_DELIMITER);
		buffer.append(logFile.getMonitor());
		buffer.append(VALUE_DELIMITER);
		buffer.append(logFile.getType().toString());
		return buffer.toString();
	}

	public static String asLogFileListString(List<?> logFiles) {
		Iterator<?> it = logFiles.iterator();
		StringBuffer buffer = new StringBuffer();
		while(it.hasNext()) {
			LogFile logFile = (LogFile)it.next();
			buffer.append(asString(logFile));
			buffer.append(ITEM_DELIMITER);
		}
		return buffer.toString();
	}

	public static LogFile asLogFile(String logFileStr) {
		String str[] = new String[5];
		StringTokenizer tokenizer = new StringTokenizer(logFileStr,VALUE_DELIMITER);
		for (int i=0;i<5;i++) {
			if(tokenizer.hasMoreTokens()) {
				str[i] = tokenizer.nextToken();
			} else {
				str[i] = null;
			}
		}
		LogFileType type = asType(str[4]);
		LogFile logFile = new LogFile(type, str[0], str[1], str[2], !("false".equals(str[3])));
		return logFile;
	}

	public static LogFileType asType(String logTypeStr) {
		LogFileType type = LogFileType.LOGFILE_SYSTEM_FILE;
		if (logTypeStr != null && !logTypeStr.isEmpty()) {
			LogFileType[] types = LogFileType.values();
			for (int i=0;i<types.length;i++) {
				if (types[i].toString().equals(logTypeStr)) {
					type = types[i];
					break;
				}
			}
		}
		return type;
	}

	public static List<LogFile> asLogFileList(String logFileList) {
		List<LogFile> files = new Vector<LogFile>();
		StringTokenizer tokenizer = new StringTokenizer(logFileList,ITEM_DELIMITER);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			files.add(asLogFile(token));
		}
		return files;
	}
}
