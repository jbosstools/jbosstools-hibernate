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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jpt.common.core.resource.java.JavaResourceCompilationUnit;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackage;
import org.eclipse.jpt.common.core.resource.java.JavaResourcePackageInfoCompilationUnit;
import org.eclipse.jpt.common.core.utility.command.JobCommand;
import org.eclipse.jpt.common.utility.internal.iterable.FilteringIterable;
import org.eclipse.jpt.common.utility.internal.iterable.TransformationIterable;
import org.eclipse.jpt.common.utility.internal.predicate.PredicateTools;
import org.eclipse.jpt.common.utility.transformer.Transformer;
import org.eclipse.jpt.jpa.core.JpaFile;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.jpa.core.context.persistence.PersistenceXml;
import org.eclipse.jpt.jpa.core.internal.AbstractJpaProject;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.properties.HibernatePropertiesConstants;
import org.hibernate.eclipse.nature.HibernateNature;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.core.internal.validation.HibernateJpaValidationMessage;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IEnvironment;
import org.jboss.tools.hibernate.spi.INamingStrategy;
import org.jboss.tools.hibernate.util.HibernateHelper;
import org.osgi.service.prefs.Preferences;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaProject extends AbstractJpaProject {

	private Boolean cachedNamingStrategyEnable;
	
	private final JobCommand revalidateCommand;
	
	private IEnvironment environment;

	public HibernateJpaProject(JpaProject.Config config){
		super(config);
		revalidateCommand = new RevalidateProjectCommand();
	}
	
	private IEnvironment getEnvironment() {
		if (environment == null) {
			environment = HibernateHelper.INSTANCE.getHibernateService().getEnvironment();
		}
		return environment;
	}
	
	public ConsoleConfiguration getDefaultConsoleConfiguration(){
		HibernateNature nature = HibernateNature.getHibernateNature(getJavaProject());
		if (nature != null){
			return nature.getDefaultConsoleConfiguration();
		}
		return null;
	}

	public INamingStrategy getNamingStrategy(){
		ConsoleConfiguration cc = getDefaultConsoleConfiguration();
		if (cc != null){
			if (cc.getConfiguration() != null){
				IConfiguration config = cc.getConfiguration();
				return config.getNamingStrategy();
			}
		}
		return null;
	}
	
	public BasicHibernateProperties getBasicHibernateProperties(){
		if (getContextRoot() != null){
			PersistenceXml persistenceXml = getContextRoot().getPersistenceXml();
			Persistence persistence = persistenceXml.getRoot();
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
				IConfiguration configuration = cc.getConfiguration();
				if (configuration.getProperties().containsKey(getEnvironment().getDefaultSchema())){
					schema = configuration.getProperty(getEnvironment().getDefaultSchema());
				}
			}
			Properties properties = cc.getPreferences().getProperties();
			if (properties != null && properties.containsKey(getEnvironment().getDefaultSchema())){
				schema = properties.getProperty(getEnvironment().getDefaultSchema());
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
				IConfiguration configuration = cc.getConfiguration();
				if (configuration.getProperties().containsKey(getEnvironment().getDefaultCatalog())){
					catalog = configuration.getProperty(getEnvironment().getDefaultCatalog());
				}
				
			}
			Properties properties = cc.getPreferences().getProperties();
			if (properties != null && properties.containsKey(getEnvironment().getDefaultCatalog())){
				catalog = properties.getProperty(getEnvironment().getDefaultCatalog());
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
		return this.getJpaFiles(JavaResourceCompilationUnit.PACKAGE_INFO_CONTENT_TYPE);
	}

	/**
	 * Return the JPA project's resource compilation units.
	 */
	protected Iterable<JavaResourcePackageInfoCompilationUnit> getInternalJavaResourcePackageInfoCompilationUnits() {
		return new TransformationIterable<JpaFile, JavaResourcePackageInfoCompilationUnit>(
				this.getJavaPackageInfoSourceJpaFiles(),
				new Transformer<JpaFile, JavaResourcePackageInfoCompilationUnit> () {
					@Override
					public JavaResourcePackageInfoCompilationUnit transform(JpaFile jpaFile) {
						return (JavaResourcePackageInfoCompilationUnit) jpaFile.getResourceModel();
					}
				}
			);
	}

	protected Iterable<JavaResourcePackage> getInternalSourceJavaResourcePackages() {
		return new TransformationIterable<JavaResourcePackageInfoCompilationUnit, JavaResourcePackage>(
				this.getInternalJavaResourcePackageInfoCompilationUnits(),
				new Transformer<JavaResourcePackageInfoCompilationUnit, JavaResourcePackage>() {
					@Override
					public JavaResourcePackage transform(final JavaResourcePackageInfoCompilationUnit compilationUnit) {
						return compilationUnit.getPackage();
					}
				}
			);
	}
	
	protected Iterable<JavaResourcePackage> getInternalAnnotatedSourceJavaResourcePacakges() {
		return new FilteringIterable<JavaResourcePackage>(this.getInternalSourceJavaResourcePackages(),PredicateTools.true_());
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
		return new TransformationIterable<JavaResourcePackage, String>(
				this.getInternalMappedSourceJavaResourcePackages(),
				new Transformer<JavaResourcePackage, String>() {
					@Override
					public String transform(JavaResourcePackage jrpPackage) {
						return jrpPackage == null ? null :  jrpPackage.getName();
					}
				}
			);
	}
	
	@Override
	protected boolean synchronizeJpaFiles(IFile file, int deltaKind) {
		boolean result = super.synchronizeJpaFiles(file, deltaKind);
		ConsoleConfiguration cc = getDefaultConsoleConfiguration();
		if (cc != null){
			ConsoleConfigurationPreferences preferences = cc.getPreferences();
			if (file.getLocation().toFile().equals(preferences.getPropertyFile())){
				switch (deltaKind) {
					case IResourceDelta.ADDED :
					case IResourceDelta.REMOVED :
					case IResourceDelta.CHANGED :
						stateChanged();
					revalidate();
				}
			}
		}

		return result;
	}

	/**
	 * I don't know what I do incorrectly but JpaProject isn't validated on the stateChanged().
	 * This command is a temporary solution.
	 *
	 */
	protected void revalidate() {
		getManager().execute(revalidateCommand, org.jboss.tools.hibernate.jpt.core.internal.Messages.HibernateJpaProject_Update_Hibernate_properties, this);
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
	
	class RevalidateProjectCommand implements JobCommand {

		public IStatus execute(IProgressMonitor monitor) {
			try {
				ValidationFramework.getDefault().validate(new IProject[]{HibernateJpaProject.this.getProject()},
						false, true,  new NullProgressMonitor());
				return Status.OK_STATUS;
			} catch (CoreException e) {
				return new Status(Status.ERROR, HibernateJptPlugin.ID, e.getMessage(), e);
			}
		}
	}

}
