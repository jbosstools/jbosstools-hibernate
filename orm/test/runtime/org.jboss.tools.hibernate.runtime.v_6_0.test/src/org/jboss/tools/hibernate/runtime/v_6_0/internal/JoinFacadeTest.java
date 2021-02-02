package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.runtime.common.AbstractJoinFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.Before;
import org.junit.Test;

public class JoinFacadeTest {

	private final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private IJoin joinFacade = null;
	private Join joinTarget = null;
	
	@Before
	public void before() {
		joinTarget = new Join();
	}
	
	@Test
	public void testGetPropertyIterator() {
		joinFacade = new AbstractJoinFacade(FACADE_FACTORY, joinTarget) {};
		Iterator<IProperty> propertyIterator = joinFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property property = new Property();
		joinTarget.addProperty(property);
		joinFacade = new AbstractJoinFacade(FACADE_FACTORY, joinTarget) {};
		propertyIterator = joinFacade.getPropertyIterator();
		assertTrue(propertyIterator.hasNext());
		assertSame(property, (((IFacade)propertyIterator.next()).getTarget()));
	}
	
}
