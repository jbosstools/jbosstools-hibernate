package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReverseEngineeringSettingsFacadeTest {

	private static NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private RevengSettings revengSettingsTarget = null;
	private IReverseEngineeringSettings revengSettingsFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		IReverseEngineeringStrategy revengStrategyFacade = 
				FACADE_FACTORY.createReverseEngineeringStrategy();
		revengSettingsFacade = FACADE_FACTORY.createReverseEngineeringSettings(
				((IFacade)revengStrategyFacade).getTarget());
		revengSettingsTarget = (RevengSettings)((IFacade)revengSettingsFacade).getTarget();	
	}
	
	@Test
	public void testSetDefaultPackageName() {
		assertEquals("", revengSettingsTarget.getDefaultPackageName());
		revengSettingsFacade.setDefaultPackageName("foo");
		assertEquals("foo", revengSettingsTarget.getDefaultPackageName());
	}
	
	@Test
	public void testSetDetectManyToMany() {
		assertTrue(revengSettingsTarget.getDetectManyToMany());
		revengSettingsFacade.setDetectManyToMany(false);
		assertFalse(revengSettingsTarget.getDetectManyToMany());
	}
	
	@Test
	public void testSetDetectOneToOne() {
		assertTrue(revengSettingsTarget.getDetectOneToOne());
		revengSettingsFacade.setDetectOneToOne(false);
		assertFalse(revengSettingsTarget.getDetectOneToOne());
	}
	
	@Test
	public void testSetDetectOptimisticLock() {
		assertTrue(revengSettingsTarget.getDetectOptimsticLock());
		revengSettingsFacade.setDetectOptimisticLock(false);
		assertFalse(revengSettingsTarget.getDetectOptimsticLock());
	}
	
}
