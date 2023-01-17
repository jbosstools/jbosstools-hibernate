package org.jboss.tools.hibernate.runtime.v_6_2.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringSettingsFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReverseEngineeringSettingsFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private RevengSettings revengSettingsTarget = null;
	private IReverseEngineeringSettings revengSettingsFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		revengSettingsTarget = new RevengSettings(new DefaultStrategy());
		revengSettingsFacade = 
				new AbstractReverseEngineeringSettingsFacade(FACADE_FACTORY, revengSettingsTarget) {};		
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
