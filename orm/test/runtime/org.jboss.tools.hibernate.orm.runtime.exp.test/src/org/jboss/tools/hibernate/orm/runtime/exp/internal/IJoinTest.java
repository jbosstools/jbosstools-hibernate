package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IJoinTest {
	
	private IJoin joinFacade = null;
	private Join joinTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		IPersistentClass persistentClassFacade = NewFacadeFactory.INSTANCE.createRootClass();
		PersistentClass persistentClassTarget = 
				(PersistentClass)((Wrapper)((IFacade)persistentClassFacade).getTarget()).getWrappedObject();
		joinTarget = new Join();
		persistentClassTarget.addJoin(joinTarget);
		joinFacade = persistentClassFacade.getJoinIterator().next();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(joinFacade);
		assertNotNull(joinTarget);
		assertSame(((IFacade)joinFacade).getTarget(), joinTarget);
	}

}
