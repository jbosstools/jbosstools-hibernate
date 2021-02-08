package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.Before;
import org.junit.Test;

public class PersistentClassFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IPersistentClass persistentClassFacade = null; 
	private PersistentClass persistentClassTarget = null;
	
	@Before
	public void before() {
		persistentClassTarget = new RootClass(null);
		persistentClassFacade = new AbstractPersistentClassFacade(FACADE_FACTORY, persistentClassTarget) {};
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(persistentClassFacade);
		assertSame(persistentClassTarget, ((IFacade)persistentClassFacade).getTarget());
	}
	
}
