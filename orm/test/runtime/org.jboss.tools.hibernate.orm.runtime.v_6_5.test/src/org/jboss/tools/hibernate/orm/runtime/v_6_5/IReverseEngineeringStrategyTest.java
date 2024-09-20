package org.jboss.tools.hibernate.orm.runtime.v_6_5;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;

import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.Test;

public class IReverseEngineeringStrategyTest {

	@Test
	public void testSetSettings() throws Exception {
		IReverseEngineeringStrategy revengStrategyFacade = (IReverseEngineeringStrategy)GenericFacadeFactory.createFacade(
				IReverseEngineeringStrategy.class, 
				WrapperFactory.createRevengStrategyWrapper());
		Wrapper revengStrategyWrapper = (Wrapper)((IFacade)revengStrategyFacade).getTarget();
		Object revengStrategyTarget = revengStrategyWrapper.getWrappedObject();
		IReverseEngineeringSettings revengSettingsFacade = 
				(IReverseEngineeringSettings)GenericFacadeFactory.createFacade(
						IReverseEngineeringSettings.class, 
						WrapperFactory.createRevengSettingsWrapper(revengStrategyWrapper));
		Wrapper revengSettingsWrapper = (Wrapper)((IFacade)revengSettingsFacade).getTarget();
		Object revengSettingsTarget = revengSettingsWrapper.getWrappedObject();
		Field field = AbstractStrategy.class.getDeclaredField("settings");
		field.setAccessible(true);
		assertNotSame(field.get(revengStrategyTarget), revengSettingsTarget);
		revengStrategyFacade.setSettings(revengSettingsFacade);
		assertSame(field.get(revengStrategyTarget), revengSettingsTarget);
	}
	
}
