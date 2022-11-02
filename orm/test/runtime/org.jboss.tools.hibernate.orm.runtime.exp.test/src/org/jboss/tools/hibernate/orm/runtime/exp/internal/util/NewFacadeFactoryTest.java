package org.jboss.tools.hibernate.orm.runtime.exp.internal.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.tool.internal.export.common.DefaultArtifactCollector;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewFacadeFactoryTest {

	private NewFacadeFactory facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = new NewFacadeFactory();
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
	
}
