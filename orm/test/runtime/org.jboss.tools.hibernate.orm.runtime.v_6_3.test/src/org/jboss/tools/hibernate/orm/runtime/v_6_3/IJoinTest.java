package org.jboss.tools.hibernate.orm.runtime.v_6_3;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IJoinTest {
	
	private IJoin joinFacade = null;
	private Join joinTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		IPersistentClass persistentClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
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
	
	@Test
	public void testGetPropertyIterator() {
		Iterator<IProperty> propertyFacadeIterator = joinFacade.getPropertyIterator();
		assertFalse(propertyFacadeIterator.hasNext());
		Property propertyTarget = new Property();
		joinTarget.addProperty(propertyTarget);
		propertyFacadeIterator = joinFacade.getPropertyIterator();
		assertTrue(propertyFacadeIterator.hasNext());
		IProperty propertyFacade = propertyFacadeIterator.next();
		assertSame(((IFacade)propertyFacade).getTarget(), propertyTarget);
	}

}
