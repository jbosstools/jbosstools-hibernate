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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

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
import org.hibernate.eclipse.launch.ExportersXMLAttributeDescription.AttributeDescription;
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
	 */
	public static final String propFileNameSuffix = "hibernate.properties"; //$NON-NLS-1$
	/**
	 * presave generated Hibernate Properties file content,
	 * this is necessary to proper content formating
	 */
	protected String propFileContentPreSave = ""; //$NON-NLS-1$
	/**
	 * generate Ant script from this launch configuration
	 */
	protected ILaunchConfiguration lc = null;
	/**
	 * generate external Hibernate Properties file or
	 * put generated properties into Ant script directly
	 */
	protected boolean externalPropFile = true;
	/**
	 * file name for generated properties file
	 */
	protected String externalPropFileName = propFileNameSuffix;

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
		String connProfileName = consoleConfigPrefs == null ? null : 
			consoleConfigPrefs.getConnectionProfileName();
		IConnectionProfile profile = getConnectionProfile(connProfileName);
		if (profile != null) {
			StringBuilder propFileContent = new StringBuilder();
			String driverClass = getDriverClass(connProfileName); 
			final Properties cpProperties = profile.getProperties(profile.getProviderId());
			//
			String url = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.URL"); //$NON-NLS-1$
			//
			String user = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.username"); //$NON-NLS-1$
			//
			String pass = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.password"); //$NON-NLS-1$
			//
			String dialectName = consoleConfigPrefs.getDialectName();
			if (consoleConfigPrefs.getPropertyFile() != null) {
				props = consoleConfigPrefs.getProperties();
				props.setProperty(Environment.DRIVER, driverClass);
				props.setProperty(Environment.URL, url);
				props.setProperty(Environment.USER, user);
				props.setProperty(Environment.PASS, pass);
				if (StringHelper.isNotEmpty(dialectName)) {
					props.setProperty(Environment.DIALECT, dialectName);
				}
				// output keys in sort order
				Object[] keys = props.keySet().toArray();
				Arrays.sort(keys);
				for (Object obj : keys) {
					addIntoPropFileContent(propFileContent, obj.toString(), props.getProperty(obj.toString()));
				}
			} else {
				//
				/** /
				String driverURL = getConnectionProfileDriverURL(connProfileName);
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, "jdbc.driver"); //$NON-NLS-1$
				el.addAttribute(CodeGenerationStrings.LOCATION, driverURL);
				/**/
				//
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.DRIVER);
				el.addAttribute(CodeGenerationStrings.VALUE, driverClass);
				addIntoPropFileContent(propFileContent, Environment.DRIVER);
				//
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.URL);
				el.addAttribute(CodeGenerationStrings.VALUE, url);
				addIntoPropFileContent(propFileContent, Environment.URL);
				//
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.USER);
				el.addAttribute(CodeGenerationStrings.VALUE, user);
				addIntoPropFileContent(propFileContent, Environment.USER);
				//
				el = root.addElement(CodeGenerationStrings.PROPERTY);
				el.addAttribute(CodeGenerationStrings.NAME, Environment.PASS);
				el.addAttribute(CodeGenerationStrings.VALUE, pass);
				addIntoPropFileContent(propFileContent, Environment.PASS);
				//
				if (StringHelper.isNotEmpty(dialectName)) {
					el = root.addElement(CodeGenerationStrings.PROPERTY);
					el.addAttribute(CodeGenerationStrings.NAME, Environment.DIALECT);
					el.addAttribute(CodeGenerationStrings.VALUE, dialectName);
					addIntoPropFileContent(propFileContent, Environment.DIALECT);
				}
			}
			if (externalPropFile) {
				hibernatePropFile = externalPropFileName;
			} else {
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
			}
			propFileContentPreSave = propFileContent.toString().trim();
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
			// what is right here: CodeGenerationStrings.PATH or CodeGenerationStrings.PATHELEMENT?
			// http://www.redhat.com/docs/en-US/JBoss_Developer_Studio/en/hibernatetools/html/ant.html
			// use CodeGenerationStrings.PATH - so may be error in documentation?
			Element pathItem = toolslib.addElement(CodeGenerationStrings.PATH);
			//Element pathItem = toolslib.addElement(CodeGenerationStrings.PATHELEMENT);
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
		if (rootConsoleConfig != null) {
			if (StringHelper.isNotEmpty(hibernatePropFile)) {
				rootConsoleConfig.addAttribute(ConfigurationXMLStrings.PROPERTYFILE, hibernatePropFile);
			}
			// add hibernate console configuration
			hibernatetool.content().add(rootConsoleConfig);
		}
		//
		// the path there are user classes
		Element classpath = hibernatetool.addElement(CodeGenerationStrings.CLASSPATH);
		Element path = classpath.addElement(CodeGenerationStrings.PATH);
		path.addAttribute(CodeGenerationStrings.LOCATION, "${build.dir}"); //$NON-NLS-1$
		//
		Map<String, Map<String, AttributeDescription>> exportersDescr = 
			ExportersXMLAttributeDescription.getExportersDescription();
		//
		Properties globalProps = new Properties();
		// obligatory global properties
		globalProps.put(CodeGenerationStrings.EJB3, "" + attributes.isEJB3Enabled()); //$NON-NLS-1$
		globalProps.put(CodeGenerationStrings.JDK5, "" + attributes.isJDK5Enabled()); //$NON-NLS-1$
		List<ExporterFactory> exporterFactories = attributes.getExporterFactories();
		for (Iterator<ExporterFactory> iter = exporterFactories.iterator(); iter.hasNext();) {
			ExporterFactory ef = iter.next();
			if (!ef.isEnabled(lc)) {
				continue;
			}
			//Map<String, ExporterProperty> defExpProps = ef.getDefaultExporterProperties();
			//String expId = ef.getId();
			String expName = ef.getExporterTag();
			// mapping: guiName -> AttributeDescription
			Map<String, AttributeDescription> attributesDescrGui = exportersDescr.get(expName);
			// construct new mapping: name -> AttributeDescription
			Map<String, AttributeDescription> attributesDescrAnt = new TreeMap<String, AttributeDescription>();
			for (AttributeDescription ad : attributesDescrGui.values()) {
				attributesDescrAnt.put(ad.name, ad);
			}
			//
			Element exporter = hibernatetool.addElement(expName);
			Properties expProps = new Properties();
			expProps.putAll(globalProps);
			expProps.putAll(ef.getProperties());
			//
			Properties extractGUISpecial = new Properties();
			try {
				ExporterFactory.extractExporterProperties(ef.getId(), expProps, extractGUISpecial);
			} catch (CoreException e) {
				// ignore
			}
			// convert gui special properties names into Ant names
			for (Map.Entry<Object, Object> propEntry : extractGUISpecial.entrySet()) {
				Object key = propEntry.getKey();
				Object val = propEntry.getValue();
				AttributeDescription ad = attributesDescrGui.get(key);
				if (ad == null) {
					expProps.put(key, val);
					continue;
				}
				expProps.put(ad.name, val);
			}
			// list2Remove - list to collect properties which put into attributes,
			// all other properties be ordinal property definition
			List<Object> list2Remove = new ArrayList<Object>();
			for (Map.Entry<Object, Object> propEntry : expProps.entrySet()) {
				Object key = propEntry.getKey();
				Object val = propEntry.getValue();
				AttributeDescription ad = attributesDescrAnt.get(key);
				if (ad == null) {
					continue;
				}
				list2Remove.add(key);
				if (val == null || 0 == val.toString().compareTo(ad.defaultValue)) {
					continue;
				}
				exporter.addAttribute(ad.name, val.toString());
			}
			for (Object obj : list2Remove) {
				expProps.remove(obj);
			}
			for (Map.Entry<Object, Object> propEntry : expProps.entrySet()) {
				Object key = propEntry.getKey();
				Object val = propEntry.getValue();
				Element property = exporter.addElement(CodeGenerationStrings.PROPERTY);
				property.addAttribute(CodeGenerationStrings.KEY, key.toString());
				property.addAttribute(CodeGenerationStrings.VALUE, "" + val); //$NON-NLS-1$
			}
		}
		return root;
	}
	
	public IConnectionProfile getConnectionProfile(String connProfileName) {
		IConnectionProfile profile = null;
		if (!isEmpty(connProfileName)) {
			profile = ProfileManager.getInstance().getProfileByName(connProfileName);
		}
		return profile;
	}
	
	public DriverInstance getDriverInstance(String connProfileName) {
		DriverInstance driverInstance = null;
		if (!isEmpty(connProfileName)) {
			driverInstance = ConnectionProfileUtil.getDriverDefinition(connProfileName);
		}
		return driverInstance;
	}

	public String getDriverClass(String connProfileName) {
		DriverInstance driverInstance = getDriverInstance(connProfileName);
		String driverClass = driverInstance != null ? 
			driverInstance.getProperty("org.eclipse.datatools.connectivity.db.driverClass") : ""; //$NON-NLS-1$ //$NON-NLS-2$
		return driverClass;
	}
	
	public void addIntoPropFileContent(StringBuilder pfc, String str) {
		pfc.append(NL + str + "=${" + str + "}"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void addIntoPropFileContent(StringBuilder pfc, String name, String value) {
		pfc.append(NL + name + "=" + value); //$NON-NLS-1$
	}
	
	public ConsoleConfigurationPreferences getConsoleConfigPreferences(String consoleConfigName) {
		ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find(consoleConfigName);
		if (consoleConfig == null) {
			return null;
		}
		return consoleConfig.getPreferences();
	}
	
	public IResource findResource(String path) {
		final IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IResource res = PathHelper.findMember(wsRoot, path);
		return res;
	}
	
	public String getResLocation(String path) {
		final IResource outputPathRes = findResource(path);
		String location = path == null ? "" : path; //$NON-NLS-1$
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
	
	public static boolean isEmpty(String str) {
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
	
	public void setExternalPropFile(boolean externalPropFile) {
		this.externalPropFile = externalPropFile;
	}
	
	public void setExternalPropFileName(String externalPropFileName) {
		this.externalPropFileName = externalPropFileName;
	}
	
	public String getExternalPropFileName() {
		return externalPropFileName;
	}
	
}
