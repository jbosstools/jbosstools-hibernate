/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console;

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

	public ConsoleConfigurationPreferences getPreferences() {
		return prefs;
	}

	public Document createXML(boolean includeMappings) {
		Document res = DocumentFactory.getInstance().createDocument();
		Element root = createRoot(includeMappings);
		res.setRootElement(root);
		return res;
	}

	public Element createRoot(boolean includeMappings) {
		Properties properties = prefs.getProperties();
		Element root = createRoot(properties, includeMappings);
		return root;
	}

	protected Element createRoot(Properties properties, boolean includeMappings) {
		String rootName = null;
		Boolean jdbcConfig = Boolean.valueOf(additional.getProperty("isRevEng", "false")); //$NON-NLS-1$ //$NON-NLS-2$
		if (jdbcConfig) {
			rootName = "jdbcconfiguration"; //$NON-NLS-1$
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.ANNOTATIONS)) {
			rootName = "annotationconfiguration"; //$NON-NLS-1$
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.JPA)) {
			rootName = "jpaconfiguration"; //$NON-NLS-1$
		} else if (prefs.getConfigurationMode().equals(ConfigurationMode.CORE)) {
			rootName = "configuration"; //$NON-NLS-1$
		} else {
			rootName = "undef"; //$NON-NLS-1$
		}
		Element root = DocumentFactory.getInstance().createElement(rootName);
		final String configurationFile = getPreferences().getConfigXMLFile() == null ? null : 
				getPreferences().getConfigXMLFile().toString();
		if (!StringHelper.isEmpty(configurationFile)) {
			root.addAttribute("configurationFile", configurationFile); //$NON-NLS-1$
		}
		final String propertyFile = getPreferences().getPropertyFile() == null ? null :
			getPreferences().getPropertyFile().toString();
		if (!StringHelper.isEmpty(propertyFile)) {
			root.addAttribute("propertyFile", propertyFile); //$NON-NLS-1$
		}
		final String entityResolver = getPreferences().getEntityResolverName();
		if (!StringHelper.isEmpty(entityResolver)) {
			root.addAttribute("entityResolver", entityResolver); //$NON-NLS-1$
		}
		final String persistenceUnit = getPreferences().getPersistenceUnitName();
		if (!StringHelper.isEmpty(persistenceUnit)) {
			root.addAttribute("persistenceUnit", persistenceUnit); //$NON-NLS-1$
		}
		final String detectManyToMany = additional.getProperty("detectManyToMany", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringHelper.isEmpty(detectManyToMany)) {
			root.addAttribute("detectManyToMany", detectManyToMany); //$NON-NLS-1$
		}
		final String detectOneToOne = additional.getProperty("detectOneToOne", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringHelper.isEmpty(detectOneToOne)) {
			root.addAttribute("detectOneToOne", detectOneToOne); //$NON-NLS-1$
		}
		final String detectOptimisticLock = additional.getProperty("detectOptimisticLock", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringHelper.isEmpty(detectOptimisticLock)) {
			root.addAttribute("detectOptimisticLock", detectOptimisticLock); //$NON-NLS-1$
		}
		final String packageName = additional.getProperty("packageName", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringHelper.isEmpty(packageName)) {
			root.addAttribute("packageName", packageName); //$NON-NLS-1$
		}
		final String revEngFile = additional.getProperty("revEngFile", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringHelper.isEmpty(revEngFile)) {
			root.addAttribute("revEngFile", revEngFile); //$NON-NLS-1$
		}
		final String reverseStrategy = additional.getProperty("reverseStrategy", ""); //$NON-NLS-1$ //$NON-NLS-2$
		if (!StringHelper.isEmpty(reverseStrategy)) {
			root.addAttribute("reverseStrategy", reverseStrategy); //$NON-NLS-1$
		}
		if (includeMappings) {
			Element fileset = root.addElement("fileset"); //$NON-NLS-1$
			fileset.addAttribute("dir", "./src"); //$NON-NLS-1$ //$NON-NLS-2$
			fileset.addAttribute("id", "id"); //$NON-NLS-1$ //$NON-NLS-2$
			Element include = fileset.addElement("include"); //$NON-NLS-1$
			include.addAttribute("name", "**/*"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return root;
	}
	
	public static void dump(OutputStream os, Element element) {
		try {
			// try to "pretty print" it
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(os, outformat);
			writer.write(element);
			writer.flush();
		} catch (Throwable t) {
			// otherwise, just dump it
			try {
				os.write(element.asXML().getBytes());
			} catch (IOException e) {
				// ignore
			}
		}

	}
}
