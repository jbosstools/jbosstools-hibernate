package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.util.DummyMetadataBuildingContext;
import org.junit.Test;

public class ValueFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Value valueTarget = null;
	private IValue valueFacade = null;
	
	@Test
	public void testIsSimpleValue() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertTrue(valueFacade.isSimpleValue());
		valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isSimpleValue());
	}

	@Test
	public void testIsCollection() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isCollection());
		valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertTrue(valueFacade.isCollection());
	}

	@Test
	public void testGetCollectionElement() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		IValue collectionElement = valueFacade.getCollectionElement();
		assertNull(collectionElement);
		Set set = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		set.setElement(valueTarget);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, set) {};
		collectionElement = valueFacade.getCollectionElement();
		assertNotNull(collectionElement);
		assertSame(valueTarget, ((IFacade)collectionElement).getTarget());
	}

	@Test 
	public void testIsOneToMany() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isOneToMany());
		OneToMany oneToMany = new OneToMany(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, oneToMany) {};
		assertTrue(valueFacade.isOneToMany());
	}

}
