package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringSettingsFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.junit.Before;
import org.junit.Test;

public class ReverseEngineeringSettingsFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private RevengSettings revengSettingsTarget = null;
	private IReverseEngineeringSettings revengSettingsFacade = null;
	
	@Before
	public void before() {
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
