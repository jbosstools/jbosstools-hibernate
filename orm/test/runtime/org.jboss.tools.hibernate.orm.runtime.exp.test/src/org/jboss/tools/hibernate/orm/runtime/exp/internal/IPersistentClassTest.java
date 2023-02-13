package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.PersistentClass;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPersistentClassTest {

	private static final NewFacadeFactory FACADE_FACTORY = NewFacadeFactory.INSTANCE;
	
	private IPersistentClass persistentClassFacade = null;
	private PersistentClass persistentClassTarget = null;
	
	@BeforeEach
	public void setUp() {
		persistentClassFacade = FACADE_FACTORY.createRootClass();
		persistentClassTarget = (PersistentClass)((IFacade)persistentClassFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(persistentClassTarget);
	}
	
}
