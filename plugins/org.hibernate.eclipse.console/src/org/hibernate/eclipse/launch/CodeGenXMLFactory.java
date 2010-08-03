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
package org.hibernate.eclipse.launch;

import java.io.ByteArrayOutputStream;
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
import org.hibernate.console.ConfigurationXMLStrings;
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
	/**
	 * UUID to make a stub for propFileContentPreSave,
	 * before formatting
	 */
	public static final long versionUID4PropFile = 1841714864553304000L;
	/**
	 * presave generated Hibernate Properties file content,
	 * this is necessary to proper content formating
	 */
	protected String propFileContentPreSave = ""; //$NON-NLS-1$
	
	protected ILaunchConfiguration lc = null;

	public CodeGenXMLFactory(ILaunchConfiguration lc) {
		this.lc = lc;
	}

	@SuppressWarnings("unchecked")
	protected Element createRoot() {
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
			props.setProperty(ConfigurationXMLStrings.ISREVENG, Boolean.toString(attributes.isReverseEngineer()));
			props.setProperty(ConfigurationXMLStrings.PACKAGENAME, attributes.getPackageName());
			props.setProperty(ConfigurationXMLStrings.PREFERBASICCOMPOSITEIDS, Boolean.toString(attributes.isPreferBasicCompositeIds()));
			props.setProperty(ConfigurationXMLStrings.DETECTMANYTOMANY, Boolean.toString(attributes.detectManyToMany()));
			props.setProperty(ConfigurationXMLStrings.DETECTONTTOONE, Boolean.toString(attributes.detectOneToOne()));
			props.setProperty(ConfigurationXMLStrings.DETECTOPTIMISTICLOCK, Boolean.toString(attributes.detectOptimisticLock()));
			props.setProperty(ConfigurationXMLStrings.REVERSESTRATEGY, attributes.getRevengStrategy());
			String revEngFile = getResLocation(attributes.getRevengSettings());
			props.setProperty(ConfigurationXMLStrings.REVENGFILE, revEngFile);
		}
		String consoleConfigName = attributes.getConsoleConfigurationName();
		ConsoleConfigurationPreferences consoleConfigPrefs = 
			getConsoleConfigPreferences(consoleConfigName);
		final ConfigurationXMLFactory configurationXMLFactory = new ConfigurationXMLFactory(
			consoleConfigPrefs, props);
		Element rootConsoleConfig = configurationXMLFactory.createRoot();
		//
		String defaultTargetName = "hibernateAntCodeGeneration"; //$NON-NLS-1$
		Element root = DocumentFactory.getInstance().createElement(CodeGenerationStrings.PROJECT);
		root.addAttribute(CodeGenerationStrings.NAME, "CodeGen"); //$NON-NLS-1$
		root.addAttribute(CodeGenerationStrings.DEFAULT, defaultTargetName);
		//
		String location = getResLocation(attributes.getOutputPath());
		Element el = root.addElement(CodeGenerationStrings.PROPERTY);
		el.addAttribute(CodeGenerationStrings.NAME, "build.dir"); //$NON-NLS-1$
		el.addAttribute(CodeGenerationStrings.LOCATION, location);
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
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.DRIVER);
				el.addAttribute(CodeGenerationStrings.VALUE, driverClass);
				addIntoPropFileContent(propFileContent, Environment.DRIVER);
				//
				String url = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.URL"); //$NON-NLS-1$
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.URL);
				el.addAttribute(CodeGenerationStrings.VALUE, url);
				addIntoPropFileContent(propFileContent, Environment.URL);
				//
				String user = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.username"); //$NON-NLS-1$
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.USER);
				el.addAttribute(CodeGenerationStrings.VALUE, user);
				addIntoPropFileContent(propFileContent, Environment.USER);
				//
				String pass = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.password"); //$NON-NLS-1$
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.PASS);
				el.addAttribute(CodeGenerationStrings.VALUE, pass);
				addIntoPropFileContent(propFileContent, Environment.PASS);
				//
				String dialectName = consoleConfigPrefs.getDialectName();
				if (StringHelper.isNotEmpty(dialectName)) {
					el = root.addElement(CodeGenerationStrings.PROPERTY);
					el.addAttribute(CodeGenerationStrings.NAME, Environment.DIALECT);
					el.addAttribute(CodeGenerationStrings.VALUE, dialectName);
					addIntoPropFileContent(propFileContent, Environment.DIALECT);
				}
				//
				hibernatePropFile = "hibernatePropFile"; //$NON-NLS-1$
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, hibernatePropFile);
				el.addAttribute(CodeGenerationStrings.VALUE, "${java.io.tmpdir}${ant.project.name}-hibernate.properties"); //$NON-NLS-1$
				//
				generateHibernatePropeties = "generateHibernatePropeties"; //$NON-NLS-1$
				Element target = root.addElement(CodeGenerationStrings.TARGET);
				target.addAttribute(CodeGenerationStrings.NAME, generateHibernatePropeties);
				//
				hibernatePropFile = "${" + hibernatePropFile + "}"; //$NON-NLS-1$ //$NON-NLS-2$
				Element echo = target.addElement(CodeGenerationStrings.ECHO);
				echo.addAttribute(CodeGenerationStrings.FILE, hibernatePropFile);
				echo.addText(getPropFileContentStubUID());
				//echo.addText(propFileContent.toString());
				propFileContentPreSave = propFileContent.toString();
			}
		}
		// all jars from libraries should be here
		String toolslibID = "toolslib"; //$NON-NLS-1$
		Element toolslib = root.addElement(CodeGenerationStrings.PATH);
		toolslib.addAttribute(CodeGenerationStrings.ID, toolslibID);
		final URL[] customClassPathURLs = PreferencesClassPathUtils.getCustomClassPathURLs(consoleConfigPrefs);
		for (int i = 0; i < customClassPathURLs.length; i++) {
			if (customClassPathURLs[i] == null) {
				continue;
			}
			// what is right here: CGS.PATH or CGS.PATHELEMENT?
			// http://www.redhat.com/docs/en-US/JBoss_Developer_Studio/en/hibernatetools/html/ant.html
			// use CGS.PATH - so may be error in documentation?
			Element pathItem = toolslib.addElement(CodeGenerationStrings.PATH);
			//Element pathItem = toolslib.addElement(CGS.PATHELEMENT);
			String strPathItem = customClassPathURLs[i].getPath();
			try {
				strPathItem = (new java.io.File(customClassPathURLs[i].toURI())).getPath();
			} catch (URISyntaxException e) {
				// ignore
			}
			pathItem.addAttribute(CodeGenerationStrings.LOCATION, strPathItem);
		}
		//
		Element target = root.addElement(CodeGenerationStrings.TARGET);
		target.addAttribute(CodeGenerationStrings.NAME, defaultTargetName);
		if (!isEmpty(generateHibernatePropeties)) {
			target.addAttribute(CodeGenerationStrings.DEPENDS, generateHibernatePropeties);
		}
		//
		Element taskdef = target.addElement(CodeGenerationStrings.TASKDEF);
		taskdef.addAttribute(CodeGenerationStrings.NAME, CodeGenerationStrings.HIBERNATETOOL);
		taskdef.addAttribute(CodeGenerationStrings.CLASSNAME, "org.hibernate.tool.ant.HibernateToolTask"); //$NON-NLS-1$
		taskdef.addAttribute(CodeGenerationStrings.CLASSPATHREF, toolslibID);
		//
		Element hibernatetool = target.addElement(CodeGenerationStrings.HIBERNATETOOL);
		hibernatetool.addAttribute(CodeGenerationStrings.DESTDIR, "${build.dir}"); //$NON-NLS-1$
		String templatePath = getResLocation(attributes.getTemplatePath());
		if (attributes.isUseOwnTemplates()) {
			hibernatetool.addAttribute(CodeGenerationStrings.TEMPLATEPATH, templatePath);
		}
		//
		if (StringHelper.isNotEmpty(hibernatePropFile)) {
			rootConsoleConfig.addAttribute(ConfigurationXMLStrings.PROPERTYFILE, hibernatePropFile);
		}
		// add hibernate console configuration
		hibernatetool.content().add(rootConsoleConfig);
		//
		// the path there are user classes
		Element classpath = hibernatetool.addElement(CodeGenerationStrings.CLASSPATH);
		Element path = classpath.addElement(CodeGenerationStrings.PATH);
		path.addAttribute(CodeGenerationStrings.LOCATION, "${build.dir}"); //$NON-NLS-1$
		//
		Properties globalProps = new Properties();
		globalProps.put(CodeGenerationStrings.EJB3, "" + attributes.isEJB3Enabled()); //$NON-NLS-1$
		globalProps.put(CodeGenerationStrings.JDK5, "" + attributes.isJDK5Enabled()); //$NON-NLS-1$
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
					property = exporter.addElement(CodeGenerationStrings.PROPERTY);
					property.addAttribute(CodeGenerationStrings.KEY, CodeGenerationStrings.JDK5);
					property.addAttribute(CodeGenerationStrings.VALUE, "" + attributes.isJDK5Enabled()); //$NON-NLS-1$
				}
				if (attributes.isEJB3Enabled()) {
					property = exporter.addElement(CodeGenerationStrings.PROPERTY);
					property.addAttribute(CodeGenerationStrings.KEY, CodeGenerationStrings.EJB3);
					property.addAttribute(CodeGenerationStrings.VALUE, "" + attributes.isEJB3Enabled()); //$NON-NLS-1$
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
		return propFileContentPreSave == null ? "" : propFileContentPreSave; //$NON-NLS-1$
	}

	public String createCodeGenXML() {
		Element rootBuildXml = createRoot();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ConfigurationXMLFactory.dump(baos, rootBuildXml);
		String res = baos.toString().replace(
			getPropFileContentStubUID(), getPropFileContentPreSave()).trim();
		return res;
	}
}
