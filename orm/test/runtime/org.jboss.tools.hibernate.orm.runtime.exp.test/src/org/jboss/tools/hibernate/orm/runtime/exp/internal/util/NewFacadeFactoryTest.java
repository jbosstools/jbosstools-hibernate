package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.hibernate.tool.internal.reveng.strategy.DefaultStrategy;
import org.hibernate.tool.internal.reveng.strategy.DelegatingStrategy;
import org.hibernate.tool.internal.reveng.strategy.OverrideRepository;
import org.hibernate.tool.orm.jbt.util.JpaConfiguration;
import org.hibernate.tool.orm.jbt.util.NativeConfiguration;
import org.hibernate.tool.orm.jbt.util.RevengConfiguration;
import org.hibernate.tool.orm.jbt.wrp.ColumnWrapper;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewFacadeFactoryTest {

	private NewFacadeFactory facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = NewFacadeFactory.INSTANCE;
	}
	
	@Test
	public void testCreateArtifactCollector() {
		IArtifactCollector facade = facadeFactory.createArtifactCollector();
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof DefaultArtifactCollector);
	}
	
	@Test
	public void testCreateCfg2HbmTool() {
		ICfg2HbmTool facade = facadeFactory.createCfg2HbmTool();
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof Cfg2HbmTool);
	}
	
	@Test
	public void testCreateNamingStrategy() {
		INamingStrategy facade = facadeFactory.createNamingStrategy(DefaultNamingStrategy.class.getName());
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(NamingStrategy.class.isAssignableFrom(target.getClass()));
	}
	
	@Test
	public void testCreateOverrideRepository() {
		IOverrideRepository facade = facadeFactory.createOverrideRepository();
		Object target = ((IFacade)facade).getTarget();
		assertNotNull(target);
		assertTrue(target instanceof OverrideRepository);
	}
	
	@Test
	public void testCreateRevengStrategy() throws Exception {
		IReverseEngineeringStrategy facade = facadeFactory.createReverseEngineeringStrategy();
		Object firstTarget = ((IFacade)facade).getTarget();
		assertNotNull(firstTarget);
		assertTrue(firstTarget instanceof DefaultStrategy);
		facade = facadeFactory.createReverseEngineeringStrategy(TestRevengStrategy.class.getName(), firstTarget);
		Object secondTarget = ((IFacade)facade).getTarget();
		assertNotNull(secondTarget);
		assertTrue(secondTarget instanceof DelegatingStrategy);
		Field delegateField = DelegatingStrategy.class.getDeclaredField("delegate");
		delegateField.setAccessible(true);
		assertSame(delegateField.get(secondTarget), firstTarget);
		facade = facadeFactory.createReverseEngineeringStrategy(DefaultStrategy.class.getName(), secondTarget);
		Object thirdTarget = ((IFacade)facade).getTarget();
		assertNotNull(thirdTarget);
		assertTrue(thirdTarget instanceof DefaultStrategy);
	}
	
	@Test 
	public void testCreateRevengSettings() {
		IReverseEngineeringStrategy strategyFacade = facadeFactory.createReverseEngineeringStrategy();
		Object strategyTarget = ((IFacade)strategyFacade).getTarget();
		IReverseEngineeringSettings settingsFacade = facadeFactory.createReverseEngineeringSettings(strategyTarget);
		assertNotNull(settingsFacade);
		Object settingsTarget = ((IFacade)settingsFacade).getTarget();
		assertNotNull(settingsTarget);
		assertTrue(settingsTarget instanceof RevengSettings);
		assertSame(strategyTarget, ((RevengSettings)settingsTarget).getRootStrategy());
	}
	
	@Test
	public void testCreateNativeConfiguration() {
		IConfiguration nativeConfigurationFacade = facadeFactory.createNativeConfiguration();
		assertNotNull(nativeConfigurationFacade);
		Object nativeConfigurationTarget = ((IFacade)nativeConfigurationFacade).getTarget();
		assertNotNull(nativeConfigurationTarget);
		assertTrue(nativeConfigurationTarget instanceof NativeConfiguration);
	}
	
	@Test
	public void testCreateRevengConfiguration() {
		IConfiguration revengConfigurationFacade = facadeFactory.createRevengConfiguration();
		assertNotNull(revengConfigurationFacade);
		Object revengConfigurationTarget = ((IFacade)revengConfigurationFacade).getTarget();
		assertNotNull(revengConfigurationTarget);
		assertTrue(revengConfigurationTarget instanceof RevengConfiguration);
	}
	
	@Test
	public void testCreateJpaConfiguration() {
		IConfiguration jpaConfigurationFacade = facadeFactory.createJpaConfiguration(null, null);
		assertNotNull(jpaConfigurationFacade);
		Object jpaConfigurationTarget = ((IFacade)jpaConfigurationFacade).getTarget();
		assertNotNull(jpaConfigurationTarget);
		assertTrue(jpaConfigurationTarget instanceof JpaConfiguration);
	}
	
	@Test
	public void testCreateColumn() {
		IColumn columnFacade = facadeFactory.createColumn(null);
		assertNotNull(columnFacade);
		Object columnTarget = ((IFacade)columnFacade).getTarget();
		assertNotNull(columnTarget);
		assertTrue(columnTarget instanceof ColumnWrapper);
	}
	
	public static class TestRevengStrategy extends DelegatingStrategy {
		public TestRevengStrategy(RevengStrategy delegate) {
			super(delegate);
		}
	}
	
}
