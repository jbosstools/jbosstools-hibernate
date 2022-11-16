package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.Test;

public class IReverseEngineeringStrategyTest {

	private static NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	@Test
	public void testSetSettings() throws Exception {
		IReverseEngineeringStrategy revengStrategyFacade = FACADE_FACTORY.createReverseEngineeringStrategy();
		Object revengStrategyTarget = ((IFacade)revengStrategyFacade).getTarget();
		IReverseEngineeringSettings revengSettingsFacade = 
				FACADE_FACTORY.createReverseEngineeringSettings(revengStrategyTarget);
		Object revengSettingsTarget = ((IFacade)revengSettingsFacade).getTarget();
		Field field = AbstractStrategy.class.getDeclaredField("settings");
		field.setAccessible(true);
		assertNotSame(field.get(revengStrategyTarget), revengSettingsTarget);
		revengStrategyFacade.setSettings(revengSettingsFacade);
		assertSame(field.get(revengStrategyTarget), revengSettingsTarget);
	}
	
}
