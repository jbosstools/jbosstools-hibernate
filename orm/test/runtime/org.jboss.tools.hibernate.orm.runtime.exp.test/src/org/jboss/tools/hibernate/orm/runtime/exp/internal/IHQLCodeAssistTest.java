package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.orm.jbt.util.MockDialect;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IHQLCodeAssistTest {
	
	private IHQLCodeAssist hqlCodeAssistFacade = null;
	private HQLCodeAssist hqlCodeAssistTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		NativeConfiguration configuration = new NativeConfiguration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		hqlCodeAssistFacade = (IHQLCodeAssist)GenericFacadeFactory.createFacade(
				IHQLCodeAssist.class, 
				WrapperFactory.createHqlCodeAssistWrapper(configuration));
		hqlCodeAssistTarget = (HQLCodeAssist)((IFacade)hqlCodeAssistFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(hqlCodeAssistFacade);
		assertNotNull(hqlCodeAssistTarget);
	}

}
