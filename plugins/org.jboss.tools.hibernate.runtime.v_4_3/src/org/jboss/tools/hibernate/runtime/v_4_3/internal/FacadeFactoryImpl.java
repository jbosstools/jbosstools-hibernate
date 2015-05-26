package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Settings;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.jboss.tools.hibernate.proxy.HQLCodeAssistProxy;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterProxy;
import org.jboss.tools.hibernate.proxy.SettingsProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ISettings;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}
	
	@Override
	public IConfiguration createConfiguration(Object target) {
		return new ConfigurationFacadeImpl(this, (Configuration)target);
	}
	
	@Override
	public IEnvironment createEnvironment() {
		return new EnvironmentFacadeImpl(this);
	}
	
	@Override
	public IHibernateMappingExporter createHibernateMappingExporter(Object target) {
		return new HibernateMappingExporterProxy(this, (HibernateMappingExporterExtension)target);
	}
	
	@Override
	public IHQLCodeAssist createHQLCodeAssist(Object target) {
		return new HQLCodeAssistProxy(this, (HQLCodeAssist)target);
	}
	
	@Override
	public ISettings createSettings(Object target) {
		return new SettingsProxy(this, (Settings)target);
	}
	
	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return new SpecialRootClassFacadeImpl(this, property);
	}
	
}
