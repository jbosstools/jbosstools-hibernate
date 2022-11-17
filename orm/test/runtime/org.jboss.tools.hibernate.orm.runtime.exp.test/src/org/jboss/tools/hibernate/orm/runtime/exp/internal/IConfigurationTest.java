package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.orm.jbt.util.MockConnectionProvider;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IConfigurationTest {

	private static final NewFacadeFactory NEW_FACADE_FACTORY = NewFacadeFactory.INSTANCE;

	private IConfiguration nativeConfigurationFacade = null;
	private Configuration nativeConfigurationTarget = null;

	@BeforeEach
	public void beforeEach() {
		nativeConfigurationFacade = NEW_FACADE_FACTORY.createNativeConfiguration();
		nativeConfigurationTarget = (Configuration)((IFacade)nativeConfigurationFacade).getTarget();
		nativeConfigurationTarget.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		nativeConfigurationTarget.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
	}	
	
	@Test
	public void testInstance() {
		assertNotNull(nativeConfigurationFacade);
	}

	@Test
	public void testGetProperty() {
		assertNull(nativeConfigurationFacade.getProperty("foo"));
		nativeConfigurationTarget.setProperty("foo", "bar");
		assertEquals("bar", nativeConfigurationFacade.getProperty("foo"));
	}

}
