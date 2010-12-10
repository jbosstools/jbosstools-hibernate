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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCDriverDefinitionConstants;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.console.ConfigurationFactory;
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
	
	public static final String varBuildDir = "build.dir"; //$NON-NLS-1$
	public static final String varCurrentDir = "current.dir"; //$NON-NLS-1$
	public static final String varWorkspaceDir = "workspace.dir"; //$NON-NLS-1$
	
	public static final String NL = System.getProperty("line.separator"); //$NON-NLS-1$
	/**
	 * UUID to make a stub for propFileContentPreSave,
	 * before formatting
	 */
	public static final long versionUID4PropFile = 1841714864553304000L;
	public static final long place2GenerateUID = 3855319363698081943L;
	public static final long workspacePathUID = 2720818065195124531L;
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
	/**
	 * place to generate Ant script file (all paths in script should be
	 * relative to this place)
	 */
	protected String place2Generate = ""; //$NON-NLS-1$
	/**
	 * workspace path
	 */
	protected String workspacePath = ""; //$NON-NLS-1$

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
		//
		final IPath pathPlace2Generate = isEmpty(place2Generate) ? null : new Path(getResLocation(place2Generate));
		final IPath pathWorkspacePath = isEmpty(workspacePath) ? null : new Path(getResLocation(workspacePath));
		//
		String consoleConfigName = attributes.getConsoleConfigurationName();
		ConsoleConfigurationPreferences consoleConfigPrefs = 
			getConsoleConfigPreferences(consoleConfigName);
		final ConfigurationXMLFactory configurationXMLFactory = new ConfigurationXMLFactory(
			consoleConfigPrefs, props);
		configurationXMLFactory.setPlace2Generate(pathPlace2Generate);
		configurationXMLFactory.setWorkspacePath(pathWorkspacePath);
		Element rootConsoleConfig = configurationXMLFactory.createRoot();
		//
		String defaultTargetName = "hibernateAntCodeGeneration"; //$NON-NLS-1$
		Element el, root = DocumentFactory.getInstance().createElement(CodeGenerationStrings.PROJECT);
		root.addAttribute(CodeGenerationStrings.NAME, "CodeGen"); //$NON-NLS-1$
		root.addAttribute(CodeGenerationStrings.DEFAULT, defaultTargetName);
		//
		if (!isEmpty(place2Generate)) {
			el = root.addElement(CodeGenerationStrings.PROPERTY);
			el.addAttribute(CodeGenerationStrings.NAME, varCurrentDir);
			el.addAttribute(CodeGenerationStrings.LOCATION, getPlace2GenerateUID());
		}
		if (!isEmpty(workspacePath)) {
			el = root.addElement(CodeGenerationStrings.PROPERTY);
			el.addAttribute(CodeGenerationStrings.NAME, varWorkspaceDir);
			el.addAttribute(CodeGenerationStrings.LOCATION, getWorkspacePathUID());
		}
		//
		String location = getResLocation(attributes.getOutputPath());
		location = ConfigurationXMLFactory.makePathRelative(location, pathPlace2Generate, pathWorkspacePath);
		el = root.addElement(CodeGenerationStrings.PROPERTY);
		el.addAttribute(CodeGenerationStrings.NAME, varBuildDir);
		el.addAttribute(CodeGenerationStrings.LOCATION, location);
		//
		String hibernatePropFile = null;
		String generateHibernatePropeties = null;
		String connProfileName = consoleConfigPrefs == null ? null : 
			consoleConfigPrefs.getConnectionProfileName();
		IConnectionProfile profile = getConnectionProfile(connProfileName);
		boolean bPropFile = profile != null;
		// update property with fake tm
		Properties propsTmp = null;
		if (consoleConfigPrefs != null && consoleConfigPrefs.getPropertyFile() != null) {
			propsTmp = consoleConfigPrefs.getProperties();
			String tmStrategy = propsTmp.getProperty(Environment.TRANSACTION_MANAGER_STRATEGY);
			if (tmStrategy != null && StringHelper.isEmpty(tmStrategy)) {
				propsTmp.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY,
					ConfigurationFactory.FAKE_TM_LOOKUP);
				bPropFile = true;
			}
		}
		if (bPropFile) {
			Set<String> specialProps = new TreeSet<String>();
			specialProps.add(Environment.DRIVER);
			specialProps.add(Environment.URL);
			specialProps.add(Environment.USER);
			specialProps.add(Environment.PASS);
			specialProps.add(Environment.DIALECT);
			//
			if (propsTmp == null) {
				propsTmp = new Properties();
			}
			StringBuilder propFileContent = new StringBuilder();
			String driverClass = getDriverClass(connProfileName); 
			if (profile != null) {
				final Properties cpProperties = profile.getProperties(profile.getProviderId());
				//
				String url = cpProperties.getProperty(IJDBCDriverDefinitionConstants.URL_PROP_ID);
				//
				String user = cpProperties.getProperty(IJDBCDriverDefinitionConstants.USERNAME_PROP_ID);
				//
				String pass = cpProperties.getProperty(IJDBCDriverDefinitionConstants.PASSWORD_PROP_ID);
				//
				String dialectName = consoleConfigPrefs.getDialectName();
				//
				propsTmp.setProperty(Environment.DRIVER, driverClass);
				propsTmp.setProperty(Environment.URL, url);
				propsTmp.setProperty(Environment.USER, user);
				propsTmp.setProperty(Environment.PASS, pass);
				if (StringHelper.isNotEmpty(dialectName)) {
					propsTmp.setProperty(Environment.DIALECT, dialectName);
				}
			}
			// output keys in sort order
			Object[] keys = propsTmp.keySet().toArray();
			Arrays.sort(keys);
			//
			if (externalPropFile) {
				for (Object obj : keys) {
					addIntoPropFileContent(propFileContent, obj.toString(), propsTmp.getProperty(obj.toString()));
				}
			} else {
				for (Object obj : keys) {
					if (specialProps.contains(obj)) {
						el = root.addElement(CodeGenerationStrings.PROPERTY);
						el.addAttribute(CodeGenerationStrings.NAME, obj.toString());
						el.addAttribute(CodeGenerationStrings.VALUE, propsTmp.getProperty(obj.toString()));
						addIntoPropFileContent(propFileContent, obj.toString());
					} else {
						addIntoPropFileContent(propFileContent, obj.toString(), propsTmp.getProperty(obj.toString()));
					}
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
				hibernatePropFile = getVar(hibernatePropFile);
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
			strPathItem = new Path(strPathItem).toString();
			strPathItem = ConfigurationXMLFactory.makePathRelative(strPathItem, pathPlace2Generate, pathWorkspacePath);
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
		hibernatetool.addAttribute(CodeGenerationStrings.DESTDIR, getVar(varBuildDir));
		if (attributes.isUseOwnTemplates()) {
			String templatePath = getResLocation(attributes.getTemplatePath());
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
		path.addAttribute(CodeGenerationStrings.LOCATION, getVar(varBuildDir));
		//
		Map<String, Map<String, AttributeDescription>> exportersDescr = 
			ExportersXMLAttributeDescription.getExportersDescription();
		Map<String, Set<String>> exportersSetSubTags = 
			ExportersXMLAttributeDescription.getExportersSetSubTags();
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
			String expDefId = ef.getExporterDefinitionId();
			String expName = ef.getExporterTag();
			// mapping: guiName -> AttributeDescription
			Map<String, AttributeDescription> attributesDescrGui = exportersDescr.get(expName);
			if (attributesDescrGui == null) {
				attributesDescrGui = new TreeMap<String, AttributeDescription>();
			}
			// mapping: guiName -> set of sub tags
			Set<String> setSubTags = exportersSetSubTags.get(expName);
			if (setSubTags == null) {
				setSubTags = new TreeSet<String>();
			}
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
				ExporterFactory.extractExporterProperties(expDefId, expProps, extractGUISpecial);
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
			// to add attributes and properties in alphabetic order
			Map<String, Object> expPropsSorted = new TreeMap<String, Object>();
			for (Map.Entry<Object, Object> propEntry : expProps.entrySet()) {
				Object key = propEntry.getKey();
				Object val = propEntry.getValue();
				expPropsSorted.put(key.toString(), val);
			}
			// list2Remove - list to collect properties which put into attributes,
			// all other properties be ordinal property definition
			List<Object> list2Remove = new ArrayList<Object>();
			for (Map.Entry<String, Object> propEntry : expPropsSorted.entrySet()) {
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
				String processedVal = processPropertyValue(val);
				if (setSubTags.contains(ad.guiName)) {
					Element subTag = exporter.addElement(ad.name);
					subTag.addText(processedVal);
				} else {
					exporter.addAttribute(ad.name, processedVal);
				}
			}
			for (Object obj : list2Remove) {
				expProps.remove(obj);
				expPropsSorted.remove(obj);
			}
			for (Map.Entry<String, Object> propEntry : expPropsSorted.entrySet()) {
				Object key = propEntry.getKey();
				Object val = propEntry.getValue();
				String processedVal = processPropertyValue(val);
				Element property = exporter.addElement(CodeGenerationStrings.PROPERTY);
				property.addAttribute(CodeGenerationStrings.KEY, key.toString());
				property.addAttribute(CodeGenerationStrings.VALUE, processedVal);
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

	public String getDriverClass(String connProfileName) {
		return ConnectionProfileUtil.getDriverClass(connProfileName);
	}
	
	public String getVar(String str) {
		return "${" + str + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void addIntoPropFileContent(StringBuilder pfc, String str) {
		pfc.append(NL + str + "=" + getVar(str)); //$NON-NLS-1$
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

	/**
	 * Check is the value a path in current file system,
	 * if true - update result value (workspace related path is 
	 * converted to absolute path).
	 * 
	 * @param val
	 * @return
	 */
	public String processPropertyValue(Object val) {
		if (val == null) {
			return ""; //$NON-NLS-1$
		}
		String res = val.toString();
		String processedVal = getResLocation(res);
		if ((new java.io.File(processedVal)).exists()) {
			res = processedVal;
		}
		return res;
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
			location = outputPathRes.getLocation().toString();
		} else {
			location = new Path(location).toString();
		}
		return location;
	}
	
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}
	
	public static String getPropFileContentStubUID() {
		return Long.toHexString(versionUID4PropFile);
	}
	
	public static String getPlace2GenerateUID() {
		return Long.toHexString(place2GenerateUID);
	}
	
	public static String getWorkspacePathUID() {
		return Long.toHexString(workspacePathUID);
	}
	
	public String getPropFileContentPreSave() {
		return propFileContentPreSave == null ? "" : propFileContentPreSave; //$NON-NLS-1$
	}

	public String createCodeGenXML() {
		Element rootBuildXml = createRoot();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ConfigurationXMLFactory.dump(baos, rootBuildXml);
		String res = baos.toString().trim();
		//place2Generate, workspacePath
		if (!isEmpty(place2Generate)) {
			String location = getResLocation(place2Generate);
			res = res.replace(location, getVar(varCurrentDir));
			res = res.replace(getPlace2GenerateUID(), location);
		}
		if (!isEmpty(workspacePath)) {
			String location = getResLocation(workspacePath);
			res = res.replace(getWorkspacePathUID(), location);
		}
		res = res.replace(getPropFileContentStubUID(), getPropFileContentPreSave());
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
	
	public void setPlace2Generate(String place2Generate) {
		this.place2Generate = place2Generate;
	}
	
	public String getPlace2Generate() {
		return place2Generate;
	}
	
	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}
	
	public String getWorkspacePath() {
		return workspacePath;
	}
	
	public static String getExternalPropFileNameStandard(String fileName) {
		String externalPropFileName = CodeGenXMLFactory.propFileNameSuffix;
		externalPropFileName = fileName + "." + externalPropFileName; //$NON-NLS-1$
		return externalPropFileName;
	}
}
