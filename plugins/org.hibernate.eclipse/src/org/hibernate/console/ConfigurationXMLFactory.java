/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.util.StringHelper;

/**
 * XML document part creation factory,
 * responsible for creation Hibernate Configuration part for
 * Hibernate Tools core Ant code generation.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ConfigurationXMLFactory {
	protected ConsoleConfigurationPreferences prefs;
	protected Properties additional;

	public ConfigurationXMLFactory(ConsoleConfigurationPreferences prefs, Properties additional) {
		this.prefs = prefs;
		this.additional = additional;
	}

	public Document createXML() {
		Document res = DocumentFactory.getInstance().createDocument();
		Element root = createRoot();
		res.setRootElement(root);
		return res;
	}

	public Element createRoot() {
		if (prefs == null) {
			return null;
		}
		@SuppressWarnings("unused")
		Properties properties = prefs.getProperties();
		String rootName = "undef"; //$NON-NLS-1$
		Boolean jdbcConfig = Boolean.valueOf(additional.getProperty(ConfigurationXMLStrings.ISREVENG, "false")); //$NON-NLS-1$
		if (jdbcConfig) {
			rootName = ConfigurationXMLStrings.JDBCCONFIGURATION;
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.ANNOTATIONS)) {
			rootName = ConfigurationXMLStrings.ANNOTATIONCONFIGURATION;
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.JPA)) {
			rootName = ConfigurationXMLStrings.JPACONFIGURATION;
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.CORE)) {
			rootName = ConfigurationXMLStrings.CONFIGURATION;
		}
		Element root = DocumentFactory.getInstance().createElement(rootName);
		updateAttr(root, file2Str(prefs.getConfigXMLFile()), ConfigurationXMLStrings.CONFIGURATIONFILE);
		updateAttr(root, file2Str(prefs.getPropertyFile()), ConfigurationXMLStrings.PROPERTYFILE);
		updateAttr(root, prefs.getEntityResolverName(), ConfigurationXMLStrings.ENTITYRESOLVER);
		updateAttr(root, prefs.getNamingStrategy(), ConfigurationXMLStrings.NAMINGSTRATEGY);
		updateAttr(root, prefs.getPersistenceUnitName(), ConfigurationXMLStrings.PERSISTENCEUNIT);
		updateAttr(root, additional, ConfigurationXMLStrings.DETECTMANYTOMANY);
		updateAttr(root, additional, ConfigurationXMLStrings.DETECTONTTOONE);
		updateAttr(root, additional, ConfigurationXMLStrings.DETECTOPTIMISTICLOCK);
		updateAttr(root, additional, ConfigurationXMLStrings.PACKAGENAME);
		updateAttr(root, additional, ConfigurationXMLStrings.REVENGFILE);
		updateAttr(root, additional, ConfigurationXMLStrings.REVERSESTRATEGY);
		// includeMappings
		File[] mappingFiles = prefs.getMappingFiles();
		if (mappingFiles.length > 0) {
			Element fileset = root.addElement("fileset"); //$NON-NLS-1$
			fileset.addAttribute("dir", "."); //$NON-NLS-1$ //$NON-NLS-2$
			fileset.addAttribute("id", "id"); //$NON-NLS-1$ //$NON-NLS-2$
			for (int i = 0; i < mappingFiles.length; i++) {
				Element include = fileset.addElement("include"); //$NON-NLS-1$
				include.addAttribute("name", mappingFiles[i].getAbsolutePath()); //$NON-NLS-1$
			}
		}
		return root;
	}

	public static String file2Str(File file) {
		return file == null ? null : file.getPath();
	}

	public static void updateAttr(Element el, String val, String prName) {
		if (!StringHelper.isEmpty(val)) {
			el.addAttribute(prName, val);
		}
	}

	public static void updateAttr(Element el, Properties prs, String prName) {
		final String val = prs.getProperty(prName, ""); //$NON-NLS-1$
		if (!StringHelper.isEmpty(val)) {
			el.addAttribute(prName, val);
		}
	}

	public String createConfigurationXML() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Element element = createRoot();
		dump(baos, element);
		return baos.toString();
	}
	
	public static void dump(OutputStream os, Element element) {
		// try to "pretty print" it
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(os, outformat);
			writer.write(element);
			writer.flush();
		} catch (IOException e1) {
			// otherwise, just dump it
			try {
				os.write(element.asXML().getBytes());
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
