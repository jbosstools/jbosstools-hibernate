package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.Test;

public class ReverseEngineeringStrategyFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	@Test
	public void testSetSettings() throws Exception {
		RevengStrategy revengStrategyTarget = new DefaultStrategy();
		RevengSettings revengSettingsTarget = new RevengSettings(revengStrategyTarget);
		IReverseEngineeringSettings revengSettingsFacade = 
				FACADE_FACTORY.createReverseEngineeringSettings(revengSettingsTarget);
		IReverseEngineeringStrategy revengStrategyFacade = 
				new ReverseEngineeringStrategyFacadeImpl(FACADE_FACTORY, revengStrategyTarget);
		Field field = AbstractStrategy.class.getDeclaredField("settings");
		field.setAccessible(true);
		assertNotSame(field.get(revengStrategyTarget), revengSettingsTarget);
		revengStrategyFacade.setSettings(revengSettingsFacade);
		assertSame(field.get(revengStrategyTarget), revengSettingsTarget);
	}
	
}
