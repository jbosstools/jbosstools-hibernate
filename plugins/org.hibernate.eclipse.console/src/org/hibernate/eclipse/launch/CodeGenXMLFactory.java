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
package org.hibernate.eclipse.launch;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.console.ConfigurationXMLFactory;
import org.hibernate.console.ConnectionProfileUtil;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;

/**
 * XML document part creation factory,
 * responsible for creation whole Ant code generation script for
 * Hibernate Tools.
 * 
 * @author Vitali Yemialyanchyk
 */
public class CodeGenXMLFactory {
	protected ILaunchConfiguration lc = null;

	public CodeGenXMLFactory(ILaunchConfiguration lc) {
		this.lc = lc;
	}

	@SuppressWarnings("unchecked")
	public Element createRoot() {
		ExporterAttributes attributes = null;
		try {
			attributes = new ExporterAttributes(lc);
		} catch (CoreException e) {
			// ignore
		}
		if (attributes == null) {
			return null;
		}
		Properties props = new Properties();
		if (attributes.isReverseEngineer()) {
			props.setProperty("isRevEng", Boolean.toString(attributes.isReverseEngineer())); //$NON-NLS-1$
			props.setProperty("packageName", attributes.getPackageName()); //$NON-NLS-1$
			props.setProperty("detectManyToMany", Boolean.toString(attributes.detectManyToMany())); //$NON-NLS-1$
			props.setProperty("detectOneToOne", Boolean.toString(attributes.detectOneToOne())); //$NON-NLS-1$
			props.setProperty("detectOptimisticLock", Boolean.toString(attributes.detectOptimisticLock())); //$NON-NLS-1$
			props.setProperty("reverseStrategy", attributes.getRevengStrategy()); //$NON-NLS-1$
			String revEngFile = getResLocation(attributes.getRevengSettings());
			props.setProperty("revEngFile", revEngFile); //$NON-NLS-1$
		}
		String consoleConfigName = attributes.getConsoleConfigurationName();
		ConsoleConfigurationPreferences consoleConfigPrefs = 
			getConsoleConfigPreferences(consoleConfigName);
		ConfigurationXMLFactory csfXML = new ConfigurationXMLFactory(
			consoleConfigPrefs, props);
		Element rootConsoleConfig = csfXML.createRoot(false);
		//
		Element root = DocumentFactory.getInstance().createElement("project"); //$NON-NLS-1$
		root.addAttribute("name", "CodeGen"); //$NON-NLS-1$ //$NON-NLS-2$
		String defaultTargetName = "JdbcCodeGen"; //$NON-NLS-1$
		root.addAttribute("default", defaultTargetName); //$NON-NLS-1$
		Element el = root.addElement("property"); //$NON-NLS-1$
		el.addAttribute("name", "build.dir"); //$NON-NLS-1$ //$NON-NLS-2$
		String location = getResLocation(attributes.getOutputPath());
		el.addAttribute("location", location); //$NON-NLS-1$
		el = root.addElement("property"); //$NON-NLS-1$
		el.addAttribute("name", "jdbc.driver"); //$NON-NLS-1$ //$NON-NLS-2$
		String driverURL = getConnectionProfileDriverURL(consoleConfigPrefs.getConnectionProfileName());
		el.addAttribute("location", driverURL); //$NON-NLS-1$
		//
		Element target = root.addElement("target"); //$NON-NLS-1$
		target.addAttribute("name", defaultTargetName); //$NON-NLS-1$
		//
		Element taskdef = target.addElement("taskdef"); //$NON-NLS-1$
		taskdef.addAttribute("name", "hibernatetool"); //$NON-NLS-1$ //$NON-NLS-2$
		taskdef.addAttribute("classname", "org.hibernate.tool.ant.HibernateToolTask"); //$NON-NLS-1$ //$NON-NLS-2$
		//
		Element hibernatetool = target.addElement("hibernatetool"); //$NON-NLS-1$
		hibernatetool.addAttribute("destdir", "${build.dir}"); //$NON-NLS-1$ //$NON-NLS-2$
		hibernatetool.content().add(rootConsoleConfig);
		//
		Properties globalProps = new Properties();
		globalProps.put("ejb3", "" + attributes.isEJB3Enabled()); //$NON-NLS-1$ //$NON-NLS-2$
		globalProps.put("jdk5", "" + attributes.isJDK5Enabled()); //$NON-NLS-1$//$NON-NLS-2$
		List<ExporterFactory> exporterFactories = attributes.getExporterFactories();
		for (Iterator<ExporterFactory> iter = exporterFactories.iterator(); iter.hasNext();) {
			ExporterFactory ef = iter.next();
			if (!ef.isEnabled(lc)) {
				continue;
			}
			Map<String, ExporterProperty> defExpProps = ef.getDefaultExporterProperties();
			String expName = ef.getExporterTag();
			Element exporter = hibernatetool.addElement(expName);
			Properties expProps = new Properties();
			expProps.putAll(globalProps);
			expProps.putAll(ef.getProperties());
			for (Map.Entry<String, ExporterProperty> name2prop : defExpProps.entrySet()) {
				Object val = expProps.get(name2prop.getKey());
				if (val == null || 0 == val.toString().compareTo(name2prop.getValue().getDefaultValue())) {
					continue;
				}
				exporter.addAttribute(name2prop.getKey(), val.toString());
			}
			if ("hbmtemplate".compareToIgnoreCase(expName) == 0 ) { //$NON-NLS-1$
				Element property = null;
				if (attributes.isJDK5Enabled()) {
					property = exporter.addElement("property"); //$NON-NLS-1$
					property.addAttribute("key", "jdk5"); //$NON-NLS-1$ //$NON-NLS-2$
					property.addAttribute("value", "" + attributes.isJDK5Enabled()); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (attributes.isEJB3Enabled()) {
					property = exporter.addElement("property"); //$NON-NLS-1$
					property.addAttribute("key", "ejb3"); //$NON-NLS-1$ //$NON-NLS-2$
					property.addAttribute("value", "" + attributes.isEJB3Enabled()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		return root;
	}
	
	public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
		ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find(consoleConfigName);
		return consoleConfig.getPreferences();
	}
	
	public IResource findResource(String path) {
		final IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IResource res = PathHelper.findMember(wsRoot, path);
		return res;
	}
	
	public String getResLocation(String path) {
		final IResource outputPathRes = findResource(path);
		String location = ""; //$NON-NLS-1$
		if (outputPathRes != null) {
			location = outputPathRes.getLocation().toString();
		}
		return location;
	}
	
	public String getConnectionProfileDriverURL(String connectionProfile) {
		String driverURL = ConnectionProfileUtil.getConnectionProfileDriverURL(connectionProfile);
		return driverURL;
	}
}
