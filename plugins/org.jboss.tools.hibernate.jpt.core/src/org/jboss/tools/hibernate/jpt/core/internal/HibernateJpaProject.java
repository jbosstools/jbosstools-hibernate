/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jpt.common.core.JptCommonCorePlugin;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackage;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackageInfoCompilationUnit;
import org.eclipse.jpt.common.utility.internal.iterables.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterables.TransformationIterable;
import org.eclipse.jpt.jpa.core.JpaFile;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXml;
import org.eclipse.jpt.jpa.core.internal.AbstractJpaProject;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.hibernate.eclipse.nature.HibernateNature;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;
import org.osgi.service.prefs.Preferences;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaProject extends AbstractJpaProject {

	private Boolean cachedNamingStrategyEnable;

	public HibernateJpaProject(JpaProject.Config config){
		super(config);
	}
	
	public ConsoleConfiguration getDefaultConsoleConfiguration(){
		HibernateNature nature = HibernateNature.getHibernateNature(getJavaProject());
		if (nature != null){
			return nature.getDefaultConsoleConfiguration();
		}
		return null;
	}

	public NamingStrategy getNamingStrategy(){
		ConsoleConfiguration cc = getDefaultConsoleConfiguration();
		if (cc != null){
			if (cc.getConfiguration() != null){
				Configuration config = cc.getConfiguration();
				return config.getNamingStrategy();
			}
		}
		return null;
	}
	
	public BasicHibernateProperties getBasicHibernateProperties(){
		if (getRootContextNode() != null){
			PersistenceXml persistenceXml = getRootContextNode().getPersistenceXml();
			Persistence persistence = persistenceXml.getPersistence();
			if (persistence.getPersistenceUnitsSize() > 0){
				PersistenceUnit persistenceUnit = persistence.getPersistenceUnit(0);
				if (persistenceUnit instanceof HibernatePersistenceUnit) {
					return ((HibernatePersistenceUnit) persistenceUnit).getHibernatePersistenceUnitProperties();
				}
			}
		}
		return null;
	}
	
	/*
	 * The sequence is(from biggest priority to lowest):
	 * 1) Configuration.getProperty() (if cc.hasConfiguration())
	 * 2) ConsoleConfiguration.getPreference().getProperty()-uses hibernate.properties
	 * 3) JpaProject user overrides
	 * 4) persistence.xml
	 * 5) logic from superclass
	 */
	@Override
	public String getDefaultSchema() {
		String schema = null;
		ConsoleConfiguration cc = getDefaultConsoleConfiguration();
		if (cc != null){
			if (cc.hasConfiguration()){//was not build yet
				Configuration configuration = cc.getConfiguration();
				if (configuration.getProperties().containsKey(Environment.DEFAULT_SCHEMA)){
					schema = configuration.getProperty(Environment.DEFAULT_SCHEMA);
				}
			}
			Properties properties = cc.getPreferences().getProperties();
			if (properties != null && properties.containsKey(Environment.DEFAULT_SCHEMA)){
				schema = properties.getProperty(Environment.DEFAULT_SCHEMA);
			}
		}
		if (schema == null){
			BasicHibernateProperties prop = getBasicHibernateProperties();
			if (getUserOverrideDefaultSchema() != null){
				schema = getUserOverrideDefaultSchema();
			} else if (prop != null && prop.getSchemaDefault() != null){
				schema = prop.getSchemaDefault(); 
			}
		}
		
		return schema != null ? schema : super.getDefaultSchema();
	}
	
	/*
	 * The sequence is(from biggest priority to lowest):
	 * 1) Configuration.getProperty() (if cc.hasConfiguration())
	 * 2) ConsoleConfiguration.getPreference().getProperty()-uses hibernate.properties
	 * 3) JpaProject user overrides
	 * 4) persistence.xml
	 * 5) logic from superclass
	 */
	@Override
	public String getDefaultCatalog() {
		String catalog = null;
		BasicHibernateProperties prop = getBasicHibernateProperties();
		ConsoleConfiguration cc = getDefaultConsoleConfiguration();
		if (cc != null){
			if (cc.hasConfiguration()){//was not build yet
				Configuration configuration = cc.getConfiguration();
				if (configuration.getProperties().containsKey(Environment.DEFAULT_CATALOG)){
					catalog = configuration.getProperty(Environment.DEFAULT_CATALOG);
				}
				
			}
			Properties properties = cc.getPreferences().getProperties();
			if (properties != null && properties.containsKey(Environment.DEFAULT_CATALOG)){
				catalog = properties.getProperty(Environment.DEFAULT_CATALOG);
			}
		}
		if (catalog == null){
			if (getUserOverrideDefaultCatalog() != null){
				catalog = getUserOverrideDefaultCatalog();
			} else if (prop != null && prop.getCatalogDefault() != null){
				catalog = prop.getCatalogDefault(); 
			}
		}
		
		return catalog != null ? catalog : super.getDefaultCatalog();
	}

	public String getDefaultConsoleConfigurationName(){
		HibernateNature nature = HibernateNature.getHibernateNature(getJavaProject());
		if (nature != null){
			return nature.getDefaultConsoleConfigurationName();
		}
		return null;
	}

	public boolean isNamingStrategyEnabled(){
		// as this flag cannot be changed without cleaning up and
		// rebuilding ( == creating new instance) of jpa project we cache it
		if (this.cachedNamingStrategyEnable == null){
			IScopeContext scope = new ProjectScope(getProject());
			Preferences node = scope.getNode(HibernatePropertiesConstants.HIBERNATE_CONSOLE_NODE);
			if(node!=null) {
				this.cachedNamingStrategyEnable = node.getBoolean(HibernatePropertiesConstants.NAMING_STRATEGY_ENABLED, true );
			} else {
				this.cachedNamingStrategyEnable = true;
			}
		}
		return this.cachedNamingStrategyEnable;
	}
	
	protected Iterable<JpaFile> getJavaPackageInfoSourceJpaFiles() {
		return this.getJpaFiles(JptCommonCorePlugin.JAVA_SOURCE_PACKAGE_INFO_CONTENT_TYPE);
	}

	/**
	 * Return the JPA project's resource compilation units.
	 */
	protected Iterable<JavaResourcePackageInfoCompilationUnit> getInternalJavaResourcePackageInfoCompilationUnits() {
		return new TransformationIterable<JpaFile, JavaResourcePackageInfoCompilationUnit>(this.getJavaPackageInfoSourceJpaFiles()) {
			@Override
			protected JavaResourcePackageInfoCompilationUnit transform(JpaFile jpaFile) {
				return (JavaResourcePackageInfoCompilationUnit) jpaFile.getResourceModel();
			}
		};
	}

	protected Iterable<JavaResourcePackage> getInternalSourceJavaResourcePackages() {
		return new TransformationIterable<JavaResourcePackageInfoCompilationUnit, JavaResourcePackage>(this.getInternalJavaResourcePackageInfoCompilationUnits()) {
			@Override
			protected JavaResourcePackage transform(final JavaResourcePackageInfoCompilationUnit compilationUnit) {
				return compilationUnit.getPackage();
			}
		};
	}
	
	protected Iterable<JavaResourcePackage> getInternalAnnotatedSourceJavaResourcePacakges() {
		return new FilteringIterable<JavaResourcePackage>(this.getInternalSourceJavaResourcePackages()) {
			@Override
			protected boolean accept(JavaResourcePackage jrpPackage) {
				return /*jrpPackage.isPersistable() && jrpPackage.isAnnotated()*/true; 
			}
		};
	}
	
	/**
	 * Return only those valid <em>mapped</em> (i.e. has any annotations) Java resource
	 * persistent packages that are directly part of the JPA project, ignoring
	 * those in JARs referenced in <code>persistence.xml</code>.
	 */
	protected Iterable<JavaResourcePackage> getInternalMappedSourceJavaResourcePackages() {
		return getInternalAnnotatedSourceJavaResourcePacakges();
	}
	
	public Iterable<String> getMappedJavaSourcePackagesNames() {
		return new TransformationIterable<JavaResourcePackage, String>(this.getInternalMappedSourceJavaResourcePackages()) {
			@Override
			protected String transform(JavaResourcePackage jrpPackage) {
				return jrpPackage == null ? null :  jrpPackage.getName();
			}
		};
	}

	@Override
	protected void validate(List<IMessage> messages, IReporter reporter) {
		super.validate(messages, reporter);
		this.validateConsoleConfiguration(messages);
	}

	/**
	 * @param messages
	 */
	protected void validateConsoleConfiguration(List<IMessage> messages) {
		if (KnownConfigurations.getInstance().find(getDefaultConsoleConfigurationName()) == null){
			IMessage message = HibernateJpaValidationMessage.buildMessage(IMessage.HIGH_SEVERITY,
					Messages.CC_NOT_EXISTS, new String[]{getDefaultConsoleConfigurationName()}, getResource());
			messages.add(message);
		}
	}
	
	//replace original method as it has NPE https://issues.jboss.org/browse/JBIDE-11378
	@Override
	public JavaResourcePackage getJavaResourcePackage(String packageName) {
		for (JavaResourcePackage jrp : this.getJavaResourcePackages()) {
			if (jrp.getName() != null && jrp.getName().equals(packageName)) {
				return jrp;
			}
		}
		return null;
	}

}
