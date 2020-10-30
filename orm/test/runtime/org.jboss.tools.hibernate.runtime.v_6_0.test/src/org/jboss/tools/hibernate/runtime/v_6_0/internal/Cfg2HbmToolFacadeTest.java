package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmToolFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Cfg2HbmToolFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ICfg2HbmTool cfg2HbmToolFacade = null;
	private Cfg2HbmTool cfg2HbmToolTarget = null;
	
	@Before
	public void before() {
		cfg2HbmToolTarget = new Cfg2HbmTool();
		cfg2HbmToolFacade = new AbstractCfg2HbmToolFacade(FACADE_FACTORY, cfg2HbmToolTarget) {};
	}
	
	@Test
	public void testGetTagPersistentClass() {
		PersistentClass target = new RootClass(null);
		IPersistentClass persistentClass = FACADE_FACTORY.createPersistentClass(target);
		Assert.assertEquals("class", cfg2HbmToolFacade.getTag(persistentClass));
	}

}
