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

import java.net.URISyntaxException;
import java.net.URL;
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
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.console.CFS;
import org.hibernate.console.ConfigurationXMLFactory;
import org.hibernate.console.ConnectionProfileUtil;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.PreferencesClassPathUtils;
import org.hibernate.eclipse.console.model.impl.ExporterFactory;
import org.hibernate.eclipse.console.model.impl.ExporterProperty;
import org.hibernate.util.StringHelper;

/**
 * XML document part creation factory,
 * responsible for creation whole Ant code generation script for
 * Hibernate Tools.
 * 
 * @author Vitali Yemialyanchyk
 */
public class CodeGenXMLFactory {
	
	public static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final long versionUID4PropFile = 1841714864553304000L;
	
	protected String propFileContentPreSave = null;
	
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
			props.setProperty(CFS.PREFERBASICCOMPOSITEIDS, Boolean.toString(attributes.isPreferBasicCompositeIds()));
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
		String defaultTargetName = "JdbcCodeGen"; //$NON-NLS-1$
		Element root = DocumentFactory.getInstance().createElement(CGS.PROJECT);
		root.addAttribute(CGS.NAME, "CodeGen"); //$NON-NLS-1$
		root.addAttribute(CGS.DEFAULT, defaultTargetName);
		//
		String location = getResLocation(attributes.getOutputPath());
		Element el = root.addElement(CGS.PROPERTY);
		el.addAttribute(CGS.NAME, "build.dir"); //$NON-NLS-1$
		el.addAttribute(CGS.LOCATION, location);
		//
		String hibernatePropFile = null;
		String generateHibernatePropeties = null;
		String connProfileName = consoleConfigPrefs.getConnectionProfileName();
		if (!isEmpty(connProfileName)) {
			IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(connProfileName);
			if (profile != null) {
				StringBuilder propFileContent = new StringBuilder();
				DriverInstance driverInstance = ConnectionProfileUtil.getDriverDefinition(connProfileName);
				final Properties cpProperties = profile.getProperties(profile.getProviderId());
				//
				/** /
				String driverURL = getConnectionProfileDriverURL(connProfileName);
				el = root.addElement(CGS.PROPERTY);
				el.addAttribute(CGS.NAME, "jdbc.driver"); //$NON-NLS-1$
				el.addAttribute(CGS.LOCATION, driverURL);
				/**/
				//
				String driverClass = driverInstance.getProperty("org.eclipse.datatools.connectivity.db.driverClass"); //$NON-NLS-1$
				el = root.addElement(CGS.PROPERTY);
				el.addAttribute(CGS.NAME, Environment.DRIVER);
				el.addAttribute(CGS.VALUE, driverClass);
				addIntoPropFileContent(propFileContent, Environment.DRIVER);
				//
				String url = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.URL"); //$NON-NLS-1$
				el = root.addElement(CGS.PROPERTY);
				el.addAttribute(CGS.NAME, Environment.URL);
				el.addAttribute(CGS.VALUE, url);
				addIntoPropFileContent(propFileContent, Environment.URL);
				//
				String user = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.username"); //$NON-NLS-1$
				el = root.addElement(CGS.PROPERTY);
				el.addAttribute(CGS.NAME, Environment.USER);
				el.addAttribute(CGS.VALUE, user);
				addIntoPropFileContent(propFileContent, Environment.USER);
				//
				String pass = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.password"); //$NON-NLS-1$
				el = root.addElement(CGS.PROPERTY);
				el.addAttribute(CGS.NAME, Environment.PASS);
				el.addAttribute(CGS.VALUE, pass);
				addIntoPropFileContent(propFileContent, Environment.PASS);
				//
				String dialectName = consoleConfigPrefs.getDialectName();
				if (StringHelper.isNotEmpty(dialectName)) {
					el = root.addElement(CGS.PROPERTY);
					el.addAttribute(CGS.NAME, Environment.DIALECT);
					el.addAttribute(CGS.VALUE, dialectName);
					addIntoPropFileContent(propFileContent, Environment.DIALECT);
				}
				//
				hibernatePropFile = "hibernatePropFile"; //$NON-NLS-1$
				el = root.addElement(CGS.PROPERTY);
				el.addAttribute(CGS.NAME, hibernatePropFile);
				el.addAttribute(CGS.VALUE, "${java.io.tmpdir}${ant.project.name}-hibernate.properties"); //$NON-NLS-1$
				//
				generateHibernatePropeties = "generateHibernatePropeties"; //$NON-NLS-1$
				Element target = root.addElement(CGS.TARGET);
				target.addAttribute(CGS.NAME, generateHibernatePropeties);
				//
				hibernatePropFile = "${" + hibernatePropFile + "}"; //$NON-NLS-1$ //$NON-NLS-2$
				Element echo = target.addElement(CGS.ECHO);
				echo.addAttribute(CGS.FILE, hibernatePropFile);
				echo.addText(getPropFileContentStubUID());
				//echo.addText(propFileContent.toString());
				propFileContentPreSave = propFileContent.toString();
			}
		}
		// all jars from libraries should be here
		String toolslibID = "toolslib"; //$NON-NLS-1$
		Element toolslib = root.addElement(CGS.PATH);
		toolslib.addAttribute(CGS.ID, toolslibID);
		final URL[] customClassPathURLs = PreferencesClassPathUtils.getCustomClassPathURLs(consoleConfigPrefs);
		for (int i = 0; i < customClassPathURLs.length; i++) {
			if (customClassPathURLs[i] == null) {
				continue;
			}
			// what is right here: CGS.PATH or CGS.PATHELEMENT?
			// http://www.redhat.com/docs/en-US/JBoss_Developer_Studio/en/hibernatetools/html/ant.html
			// use CGS.PATH - so may be error in documentation?
			Element pathItem = toolslib.addElement(CGS.PATH);
			//Element pathItem = toolslib.addElement(CGS.PATHELEMENT);
			String strPathItem = customClassPathURLs[i].getPath();
			try {
				strPathItem = (new java.io.File(customClassPathURLs[i].toURI())).getPath();
			} catch (URISyntaxException e) {
				// ignore
			}
			pathItem.addAttribute(CGS.LOCATION, strPathItem);
		}
		//
		Element target = root.addElement(CGS.TARGET);
		target.addAttribute(CGS.NAME, defaultTargetName);
		if (!isEmpty(generateHibernatePropeties)) {
			target.addAttribute(CGS.DEPENDS, generateHibernatePropeties);
		}
		//
		Element taskdef = target.addElement(CGS.TASKDEF);
		taskdef.addAttribute(CGS.NAME, CGS.HIBERNATETOOL);
		taskdef.addAttribute(CGS.CLASSNAME, "org.hibernate.tool.ant.HibernateToolTask"); //$NON-NLS-1$
		taskdef.addAttribute(CGS.CLASSPATHREF, toolslibID);
		//
		Element hibernatetool = target.addElement(CGS.HIBERNATETOOL);
		hibernatetool.addAttribute(CGS.DESTDIR, "${build.dir}"); //$NON-NLS-1$
		String templatePath = getResLocation(attributes.getTemplatePath());
		if (attributes.isUseOwnTemplates()) {
			hibernatetool.addAttribute(CGS.TEMPLATEPATH, templatePath);
		}
		//
		if (StringHelper.isNotEmpty(hibernatePropFile)) {
			rootConsoleConfig.addAttribute(CFS.PROPERTYFILE, hibernatePropFile);
		}
		// add hibernate console configuration
		hibernatetool.content().add(rootConsoleConfig);
		//
		// the path there are user classes
		Element classpath = hibernatetool.addElement(CGS.CLASSPATH);
		Element path = classpath.addElement(CGS.PATH);
		path.addAttribute(CGS.LOCATION, "${build.dir}"); //$NON-NLS-1$
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
	
	public void addIntoPropFileContent(StringBuilder pfc, String str) {
		pfc.append(NL + str + "=${" + str + "}"); //$NON-NLS-1$ //$NON-NLS-2$
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
			location = outputPathRes.getLocation().toOSString();
		}
		return location;
	}
	
	public String getConnectionProfileDriverURL(String connectionProfile) {
		String driverURL = ConnectionProfileUtil.getConnectionProfileDriverURL(connectionProfile);
		if (driverURL == null) {
			driverURL = ""; //$NON-NLS-1$
		}
		return driverURL;
	}
	
	public boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}
	
	public String getPropFileContentStubUID() {
		return Long.toHexString(versionUID4PropFile);
	}
	
	public String getPropFileContentPreSave() {
		return propFileContentPreSave;
	}
	
	public static String replaceString(String input, String repl, String with) {
		final StringBuffer tmp = new StringBuffer();
		int startIdx = 0;
		int idxOld = 0;
		while ((idxOld = input.indexOf(repl, startIdx)) >= 0) {
			tmp.append(input.substring(startIdx, idxOld));
			tmp.append(with);
			startIdx = idxOld + repl.length();
		}
		tmp.append(input.substring(startIdx));
		return tmp.toString();
	}
}
