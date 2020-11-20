package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
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
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isCollection());
		valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertTrue(valueFacade.isCollection());
	}

}
