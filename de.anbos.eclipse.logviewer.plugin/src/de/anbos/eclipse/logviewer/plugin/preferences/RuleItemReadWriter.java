package de.anbos.eclipse.logviewer.plugin.preferences;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.util.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.anbos.eclipse.logviewer.plugin.preferences.rule.RulePreferenceData;

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

public class RuleItemReadWriter {

	// Constant ----------------------------------------------------------------
	
	private static final String NODE_ROOT		= "rule-items"; //$NON-NLS-1$
	private static final String NODE_ITEM		= "item"; //$NON-NLS-1$
	private static final String NODE_RULE		= "rule"; //$NON-NLS-1$
	private static final String NODE_BACKGROUND = "background"; //$NON-NLS-1$
	private static final String NODE_FOREGROUND	= "foreground"; //$NON-NLS-1$
	private static final String NODE_VALUE		= "value"; //$NON-NLS-1$
	private static final String NODE_MATCHMODE  = "matchmode"; //$NON-NLS-1$
	private static final String NODE_CASEINSENSITIVE  = "case-insensitive"; //$NON-NLS-1$

	private static final String ATTR_POSITION	= "position"; //$NON-NLS-1$
	private static final String ATTR_CHECKED	= "checked"; //$NON-NLS-1$
	
	// Attribute ---------------------------------------------------------------
	
	// Constructor -------------------------------------------------------------
	
	public RuleItemReadWriter() {
	}
	
	// Public ------------------------------------------------------------------
	
	public RulePreferenceData[] read(InputStream stream) throws IOException {
		return read(new InputSource(stream));
	}
	
	public void write(RulePreferenceData[] data, OutputStream stream) throws IOException {
		write(data,new StreamResult(stream));
	}
	
	// Private -----------------------------------------------------------------
	
	private RulePreferenceData[] read(InputSource input) throws IOException {
		boolean errorInParsing = false;
		Collection itemArray= new ArrayList();
		try {
			DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
			DocumentBuilder parser= factory.newDocumentBuilder();
			Document document= parser.parse(input);
			
			NodeList items= document.getElementsByTagName(NODE_ITEM);
			
			for (int i= 0; i != items.getLength(); i++) {
				int fieldCounter = 0;
				RulePreferenceData data = new RulePreferenceData();
				Node item= items.item(i);
				NamedNodeMap attributes= item.getAttributes();
				
				// position
				Node positionAttr = attributes.getNamedItem(ATTR_POSITION);
				if(positionAttr == null) {
					errorInParsing = true;
					break;
				}
				try {
					data.setPosition(Integer.parseInt(positionAttr.getNodeValue()));
					fieldCounter++;
				} catch (NumberFormatException e) {
					throw new IOException(e.getMessage());
				}

				// checked
				Node checkedAttr = attributes.getNamedItem(ATTR_CHECKED);
				if(checkedAttr == null) {
					errorInParsing = true;
					break;					
				}
				if(checkedAttr.getNodeValue().equals(Boolean.toString(true))) {
					data.setEnabled(true);
					fieldCounter++;
				} else if(checkedAttr.getNodeValue().equals(Boolean.toString(false))) {
					data.setEnabled(false);
					fieldCounter++;
				} else {
					errorInParsing = true;
					break;
				}
				
				NodeList nodes = item.getChildNodes();
				for(int n = 0 ; n < nodes.getLength() ; n++) {
					Node node = nodes.item(n);
					// rule
					if(node.getNodeName().equals(NODE_RULE)) {
						data.setRuleName(extractStringValueFromNode(node));
						fieldCounter++;
						continue;
					}
					// background
					if(node.getNodeName().equals(NODE_BACKGROUND)) {
						data.setBackgroundColor(StringConverter.asRGB(extractStringValueFromNode(node)));
						fieldCounter++;
						continue;
					}
					// foreground
					if(node.getNodeName().equals(NODE_FOREGROUND)) {
						data.setForegroundColor(StringConverter.asRGB(extractStringValueFromNode(node)));
						fieldCounter++;
						continue;
					}
					// value
					if(node.getNodeName().equals(NODE_VALUE)) {
						data.setRuleValue(extractStringValueFromNode(node));
						fieldCounter++;
						continue;
					}
					// match mode
					if(node.getNodeName().equals(NODE_MATCHMODE)) {
						data.setMatchMode(extractStringValueFromNode(node));
						fieldCounter++;
						continue;
					}					
					// case insensitive
					if(node.getNodeName().equals(NODE_CASEINSENSITIVE)) {
						if(extractStringValueFromNode(node).equals(Boolean.toString(true))) {
							data.setCaseInsensitive(true);
						} else {
							data.setCaseInsensitive(false);
						}
						fieldCounter++;
						continue;
					}					
				}
				if(fieldCounter != 8) {
					if (fieldCounter < 8 && fieldCounter >= 6) {
						data.setMatchMode("match");
						if (fieldCounter == 6)
							data.setCaseInsensitive(false);
					} else {						
						errorInParsing = true;
						break;
					}
				}
				itemArray.add(data);
			}
		} catch (ParserConfigurationException e) {
			Assert.isTrue(false);
		} catch (SAXException e) {
			Throwable t= e.getCause();
			if (t instanceof IOException)
				throw (IOException) t;
			else if (t != null)
				throw new IOException(t.getMessage());
			else
				throw new IOException(e.getMessage());
		}
		if(!errorInParsing) {
			return (RulePreferenceData[])itemArray.toArray(new RulePreferenceData[itemArray.size()]);
		}
		throw new IOException("unable to parse the xml"); //$NON-NLS-1$
	}
	
	private void write(RulePreferenceData[] data, StreamResult streamResult) throws IOException {
		try {
			DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= factory.newDocumentBuilder();
			Document document= builder.newDocument();
			
			// create root node of document
			Node rootNode = document.createElement(NODE_ROOT);
			document.appendChild(rootNode);
			
			// add children
			for(int i = 0 ; i < data.length ; i++) {
				RulePreferenceData item = data[i];
				
				Node itemNode = document.createElement(NODE_ITEM);
				rootNode.appendChild(itemNode);
				
				NamedNodeMap attributes= itemNode.getAttributes();
				// position
				Attr positionAttr= document.createAttribute(ATTR_POSITION);
				positionAttr.setValue(Integer.toString(item.getPosition()));
				attributes.setNamedItem(positionAttr);
				// checked
				Attr checkedAttr = document.createAttribute(ATTR_CHECKED);
				checkedAttr.setValue(Boolean.toString(item.isEnabled()));
				attributes.setNamedItem(checkedAttr);
				
				// rule
				Node ruleNode = document.createElement(NODE_RULE);
				itemNode.appendChild(ruleNode);
				Text ruleValue = document.createTextNode(item.getRuleName());
				ruleNode.appendChild(ruleValue);
				// background
				Node backgroundNode = document.createElement(NODE_BACKGROUND);
				itemNode.appendChild(backgroundNode);
				Text backgroundValue = document.createTextNode(StringConverter.asString(item.getBackgroundColor()));
				backgroundNode.appendChild(backgroundValue);
				// foreground
				Node foregroundNode = document.createElement(NODE_FOREGROUND);
				itemNode.appendChild(foregroundNode);
				Text foregroundValue = document.createTextNode(StringConverter.asString(item.getForegroundColor()));
				foregroundNode.appendChild(foregroundValue);			
				// value
				Node valueNode = document.createElement(NODE_VALUE);
				itemNode.appendChild(valueNode);
				CDATASection valueCDataSection = document.createCDATASection(item.getRuleValue());
				valueNode.appendChild(valueCDataSection);
				// match mode
				Node matchModeNode = document.createElement(NODE_MATCHMODE);
				itemNode.appendChild(matchModeNode);
				Text matchModeValue = document.createTextNode(item.getMatchMode());
				matchModeNode.appendChild(matchModeValue);
				// case insensitive
				Node caseInsensitiveNode = document.createElement(NODE_CASEINSENSITIVE);
				itemNode.appendChild(caseInsensitiveNode);
				Text caseInsensitiveValue = document.createTextNode(Boolean.toString(item.isCaseInsensitive()));
				caseInsensitiveNode.appendChild(caseInsensitiveValue);
			}
			
			Transformer transformer=TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			transformer.transform(source,streamResult);
		} catch (ParserConfigurationException e) {
			Assert.isTrue(false);
		} catch (TransformerException e) {
			if (e.getException() instanceof IOException)
				throw (IOException) e.getException();
			Assert.isTrue(false);
		}
	}
	
	private String extractStringValueFromNode(Node node) {
		StringBuffer buffer = new StringBuffer();
		NodeList children= node.getChildNodes();
		for (int i= 0; i != children.getLength(); i++) {
			String value= children.item(i).getNodeValue();
			if (value != null)
				buffer.append(value);
		}
		return buffer.toString();
	}
}
