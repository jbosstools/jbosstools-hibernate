package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.AbstractExporter;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.ConfigurationMetadataDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExporterFacadeTest {

	private static final FacadeFactoryImpl FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Exporter exporterTarget = null;
	private ExporterFacadeImpl exporterFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		exporterTarget = new TestExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(exporterFacade);
	}
	
	@Test
	public void testSetConfiguration() throws Exception {
		exporterTarget = new HibernateConfigurationExporter();
		exporterFacade = new ExporterFacadeImpl(FACADE_FACTORY, exporterTarget);
		Properties properties = new Properties();
		Configuration configurationTarget = new Configuration();
		configurationTarget.setProperties(properties);
		ConfigurationFacadeImpl configurationFacade1 = new ConfigurationFacadeImpl(FACADE_FACTORY, configurationTarget);
		exporterFacade.setConfiguration(configurationFacade1);	
		assertSame(properties, ((HibernateConfigurationExporter)exporterTarget).getCustomProperties());
		Field metadataDescriptorField = AbstractExporter.class.getDeclaredField("metadataDescriptor");
		metadataDescriptorField.setAccessible(true);
		ConfigurationMetadataDescriptor configurationMetadataDescriptor = (ConfigurationMetadataDescriptor)metadataDescriptorField.get(exporterTarget);
		assertNotNull(configurationMetadataDescriptor);
		Field configurationFacadeField = ConfigurationMetadataDescriptor.class.getDeclaredField("configurationFacade");
		configurationFacadeField.setAccessible(true);
		ConfigurationFacadeImpl configurationFacade2 = (ConfigurationFacadeImpl)configurationFacadeField.get(configurationMetadataDescriptor);
		assertNotNull(configurationFacade2);
		assertSame(configurationFacade1, configurationFacade2);
	}
	
	private static class TestExporter extends AbstractExporter {

		@Override
		protected void doStart() {
		}		

	}

}
