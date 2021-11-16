package org.jboss.tools.hibernate.runtime.v_5_2.internal;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringStrategyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.Test;

public class ReverseEngineeringStrategyFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testSetSettings() throws Exception {
		ReverseEngineeringStrategy revengStrategyTarget = new DefaultReverseEngineeringStrategy();
		ReverseEngineeringSettings revengSettingsTarget = new ReverseEngineeringSettings(revengStrategyTarget);
		IReverseEngineeringSettings revengSettingsFacade = 
				FACADE_FACTORY.createReverseEngineeringSettings(revengSettingsTarget);
		IReverseEngineeringStrategy revengStrategyFacade = 
				new AbstractReverseEngineeringStrategyFacade(FACADE_FACTORY, revengStrategyTarget) {};
		Field field = DefaultReverseEngineeringStrategy.class.getDeclaredField("settings");
		field.setAccessible(true);
		assertNotSame(field.get(revengStrategyTarget), revengSettingsTarget);
		revengStrategyFacade.setSettings(revengSettingsFacade);
		assertSame(field.get(revengStrategyTarget), revengSettingsTarget);
	}
	
}
