package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.internal.export.java.Cfg2JavaTool;
import org.hibernate.tool.internal.export.java.EntityPOJOClass;
import org.hibernate.tool.internal.export.java.POJOClass;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPOJOClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPojoClassTest {
	
	private POJOClass pojoClassTarget = null;
	private IPOJOClass pojoClassFacade = null;
	
	@BeforeEach 
	public void beforeEach() {
		PersistentClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		pc.setClassName("bar");
		pojoClassTarget = new EntityPOJOClass(pc, new Cfg2JavaTool());
		pojoClassFacade = (IPOJOClass)GenericFacadeFactory.createFacade(IPOJOClass.class, pojoClassTarget);
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(pojoClassTarget);
		assertNotNull(pojoClassFacade);
	}
	
	@Test
	public void testGetQualifiedDeclarationName() {
		assertEquals("bar", pojoClassFacade.getQualifiedDeclarationName());
	}

}
