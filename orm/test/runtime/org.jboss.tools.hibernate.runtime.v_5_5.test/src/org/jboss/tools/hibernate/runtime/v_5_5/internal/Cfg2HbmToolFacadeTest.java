package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmToolFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Cfg2HbmToolFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private ICfg2HbmTool cfg2HbmToolFacade = null; 
	private Cfg2HbmTool cfg2HbmTool = null;
	
	@BeforeEach
	public void beforeEach() {
		cfg2HbmTool = new Cfg2HbmTool();
		cfg2HbmToolFacade = new AbstractCfg2HbmToolFacade(FACADE_FACTORY, cfg2HbmTool) {};
	}

	@Test
	public void testGetTagPersistentClass() {
		PersistentClass target = new RootClass(null);
		IPersistentClass persistentClass = FACADE_FACTORY.createPersistentClass(target);
		assertEquals("class", cfg2HbmToolFacade.getTag(persistentClass));
	}

}
