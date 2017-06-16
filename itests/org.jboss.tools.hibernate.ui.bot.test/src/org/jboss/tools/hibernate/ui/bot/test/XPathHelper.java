/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.test;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jboss.reddeer.common.logging.Logger;
import org.xml.sax.InputSource;

/**
 * Provides Xpath routines
 * @author Jiri Peterka
 *
 */
public class XPathHelper {

	
	private static final Logger log = Logger.getLogger(XPathHelper.class);
	private static XPathHelper instance;
	private XPath xpath;
	
	private XPathHelper() { 
		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
	}
	
	/**
	 * Return XpathHelperInstance
	 * @return
	 */
	public static XPathHelper getInstance() {
		if (instance == null) {
			instance = new XPathHelper();
		}
		return instance;
	}
	
	/**
	 * Return value for property with given name from persistence.xml
	 * @param property property name
	 * @return return found string or null
	 */
	public String getPersistencePropertyValue(String property,String text) {
		String ret = null;
		String form = "/*[local-name()='persistence']/*[local-name()='persistence-unit']/*[local-name()='properties']/*[local-name()='property'][@name='" + property + "']/@value";
		ret = evaluateXPath(form, text);
		return ret;
	}
	
	
	public String getMappingFileTable(String clazz, String text) {
		log.info("Getting mapping file from table. Source text: "+text);
		String ret = null;
		String form = "/hibernate-mapping/class[@name='" + clazz + "']/@table";
		ret = evaluateXPath(form, text);
		return ret;
	}	
	
	private String evaluateXPath(String eval, String text) {

		InputSource source = new InputSource(new StringReader(text));
		String status = null;
		try {
			status = xpath.evaluate(eval, source);
		} catch (XPathExpressionException e) {
			new RuntimeException("Cannot parse xpath" + e.getMessage());
		}
		return status;
	}
}
