package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

	@BeforeEach
	public void beforeEach() {
		nativeConfigurationFacade = NEW_FACADE_FACTORY.createNativeConfiguration();
		Configuration configuration = (Configuration)((IFacade)nativeConfigurationFacade).getTarget();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
	}	
	
	@Test
	public void testInstance() {
		assertNotNull(nativeConfigurationFacade);
	}

}
