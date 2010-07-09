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
import org.hibernate.console.CFS;
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
			props.setProperty(CFS.ISREVENG, Boolean.toString(attributes.isReverseEngineer()));
			props.setProperty(CFS.PACKAGENAME, attributes.getPackageName());
			props.setProperty(CFS.DETECTMANYTOMANY, Boolean.toString(attributes.detectManyToMany()));
			props.setProperty(CFS.DETECTONTTOONE, Boolean.toString(attributes.detectOneToOne()));
			props.setProperty(CFS.DETECTOPTIMISTICLOCK, Boolean.toString(attributes.detectOptimisticLock()));
			props.setProperty(CFS.REVERSESTRATEGY, attributes.getRevengStrategy());
			String revEngFile = getResLocation(attributes.getRevengSettings());
			props.setProperty(CFS.REVENGFILE, revEngFile);
		}
		String consoleConfigName = attributes.getConsoleConfigurationName();
		ConsoleConfigurationPreferences consoleConfigPrefs = 
			getConsoleConfigPreferences(consoleConfigName);
		ConfigurationXMLFactory csfXML = new ConfigurationXMLFactory(
			consoleConfigPrefs, props);
		Element rootConsoleConfig = csfXML.createRoot();
		//
		Element root = DocumentFactory.getInstance().createElement(CGS.PROJECT);
		root.addAttribute(CGS.NAME, "CodeGen"); //$NON-NLS-1$
		String defaultTargetName = "JdbcCodeGen"; //$NON-NLS-1$
		root.addAttribute(CGS.DEFAULT, defaultTargetName);
		Element el = root.addElement(CGS.PROPERTY);
		el.addAttribute(CGS.NAME, "build.dir"); //$NON-NLS-1$
		String location = getResLocation(attributes.getOutputPath());
		el.addAttribute(CGS.LOCATION, location);
		el = root.addElement(CGS.PROPERTY);
		el.addAttribute(CGS.NAME, "jdbc.driver"); //$NON-NLS-1$
		String driverURL = getConnectionProfileDriverURL(consoleConfigPrefs.getConnectionProfileName());
		el.addAttribute(CGS.LOCATION, driverURL);
		//
		Element target = root.addElement(CGS.TARGET);
		target.addAttribute(CGS.NAME, defaultTargetName);
		//
		Element taskdef = target.addElement(CGS.TASKDEF);
		taskdef.addAttribute(CGS.NAME, CGS.HIBERNATETOOL);
		taskdef.addAttribute(CGS.CLASSNAME, "org.hibernate.tool.ant.HibernateToolTask"); //$NON-NLS-1$
		//
		Element hibernatetool = target.addElement(CGS.HIBERNATETOOL);
		hibernatetool.addAttribute(CGS.DESTDIR, "${build.dir}"); //$NON-NLS-1$
		hibernatetool.content().add(rootConsoleConfig);
		//
		Properties globalProps = new Properties();
		globalProps.put(CGS.EJB3, "" + attributes.isEJB3Enabled()); //$NON-NLS-1$
		globalProps.put(CGS.JDK5, "" + attributes.isJDK5Enabled()); //$NON-NLS-1$
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
			Properties extract = new Properties();
			try {
				ExporterFactory.extractExporterProperties(ef.getId(), expProps, extract);
			} catch (CoreException e) {
				// ignore
			}
			expProps.putAll(extract);
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
					property = exporter.addElement(CGS.PROPERTY);
					property.addAttribute(CGS.KEY, CGS.JDK5);
					property.addAttribute(CGS.VALUE, "" + attributes.isJDK5Enabled()); //$NON-NLS-1$
				}
				if (attributes.isEJB3Enabled()) {
					property = exporter.addElement(CGS.PROPERTY);
					property.addAttribute(CGS.KEY, CGS.EJB3);
					property.addAttribute(CGS.VALUE, "" + attributes.isEJB3Enabled()); //$NON-NLS-1$
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
