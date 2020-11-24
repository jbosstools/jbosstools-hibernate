package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
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
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
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
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isOneToMany());
		OneToMany oneToMany = new OneToMany(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, oneToMany) {};
		assertTrue(valueFacade.isOneToMany());
	}

	@Test 
	public void testIsManyToOne() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isManyToOne());
		ManyToOne manyToOne = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, manyToOne) {};
		assertTrue(valueFacade.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isOneToOne());
		OneToOne oneToOne = new OneToOne(DummyMetadataBuildingContext.INSTANCE, null, new RootClass(null));
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, oneToOne) {};
		assertTrue(valueFacade.isOneToOne());
	}

	@Test
	public void testIsMap() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isMap());
		Map map = new Map(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, map) {};
		assertTrue(valueFacade.isMap());
	}

	@Test
	public void testIsComponent() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isComponent());
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, component) {};
		assertTrue(valueFacade.isComponent());
	}

	@Test
	public void testIsEmbedded() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.isEmbedded());
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, component) {};
		component.setEmbedded(true);
		assertTrue(valueFacade.isEmbedded());
		component.setEmbedded(false);
		assertFalse(valueFacade.isEmbedded());
	}

	@Test
	public void testIsToOne() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isToOne());
		ToOne toOne = new OneToOne(DummyMetadataBuildingContext.INSTANCE, null, new RootClass(null));
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, toOne) {};
		assertTrue(valueFacade.isToOne());
	}

	@Test 
	public void testGetTable() {
		Table tableTarget = new Table();
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE, tableTarget);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		ITable tableFacade = valueFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
	@Test
	public void testGetType() {
		SimpleValue valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueTarget.setTypeName("java.lang.Integer");
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		IType typeFacade = valueFacade.getType();
		assertEquals(
				"org.hibernate.type.IntegerType", 
				((IFacade)typeFacade).getTarget().getClass().getName());
	}
	
	@Test
	public void testSetElement() {
		SimpleValue elementTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		IValue elementFacade = new AbstractValueFacade(FACADE_FACTORY, elementTarget) {};
		Set valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueTarget.getElement());
		valueFacade.setElement(elementFacade);
		assertSame(elementTarget, valueTarget.getElement());
	}
	
	@Test
	public void testSetCollectionTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		Collection valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueTarget.getCollectionTable());
		valueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, valueTarget.getCollectionTable());
	}
	
	@Test
	public void testSetTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		SimpleValue valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueTarget.getTable());
		valueFacade.setTable(tableFacade);
		assertSame(tableTarget, valueTarget.getTable());
	}
	
}	

