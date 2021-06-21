package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
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
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.Test;

public class ValueFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Value valueTarget = null;
	private IValue valueFacade = null;
	
	@Test
	public void testIsSimpleValue() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertTrue(valueFacade.isSimpleValue());
		valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isSimpleValue());
	}

	@Test
	public void testIsCollection() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isCollection());
		valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertTrue(valueFacade.isCollection());
	}

	@Test
	public void testGetCollectionElement() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IValue collectionElement = valueFacade.getCollectionElement();
		assertNull(collectionElement);
		Set set = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		set.setElement(valueTarget);
		valueFacade = FACADE_FACTORY.createValue(set);
		collectionElement = valueFacade.getCollectionElement();
		assertNotNull(collectionElement);
		assertSame(valueTarget, ((IFacade)collectionElement).getTarget());
	}

	@Test 
	public void testIsOneToMany() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isOneToMany());
		OneToMany oneToMany = new OneToMany(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(oneToMany);
		assertTrue(valueFacade.isOneToMany());
	}

	@Test 
	public void testIsManyToOne() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isManyToOne());
		ManyToOne manyToOne = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(manyToOne);
		assertTrue(valueFacade.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isOneToOne());
		OneToOne oneToOne = new OneToOne(DummyMetadataBuildingContext.INSTANCE, null, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(oneToOne);
		assertTrue(valueFacade.isOneToOne());
	}

	@Test
	public void testIsMap() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isMap());
		Map map = new Map(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(map);
		assertTrue(valueFacade.isMap());
	}

	@Test
	public void testIsComponent() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isComponent());
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(component);
		assertTrue(valueFacade.isComponent());
	}

	@Test
	public void testIsEmbedded() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.isEmbedded());
		Component component = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(component);
		component.setEmbedded(true);
		assertTrue(valueFacade.isEmbedded());
		component.setEmbedded(false);
		assertFalse(valueFacade.isEmbedded());
	}

	@Test
	public void testIsToOne() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isToOne());
		ToOne toOne = new OneToOne(DummyMetadataBuildingContext.INSTANCE, null, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(toOne);
		assertTrue(valueFacade.isToOne());
	}

	@Test 
	public void testGetTable() {
		Table tableTarget = new Table();
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, tableTarget);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		ITable tableFacade = valueFacade.getTable();
		assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
	@Test
	public void testGetType() {
		SimpleValue valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, new Table());
		valueTarget.setTypeName("java.lang.Integer");
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IType typeFacade = valueFacade.getType();
		assertEquals(
				"org.hibernate.type.IntegerType", 
				((IFacade)typeFacade).getTarget().getClass().getName());
	}
	
	@Test
	public void testSetElement() {
		SimpleValue elementTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		IValue elementFacade = FACADE_FACTORY.createValue(elementTarget);
		Set valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueTarget.getElement());
		valueFacade.setElement(elementFacade);
		assertSame(elementTarget, valueTarget.getElement());
	}
	
	@Test
	public void testSetCollectionTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		Collection valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueTarget.getCollectionTable());
		valueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, valueTarget.getCollectionTable());
	}
	
	@Test
	public void testSetTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		SimpleValue valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueTarget.getTable());
		valueFacade.setTable(tableFacade);
		assertSame(tableTarget, valueTarget.getTable());
	}
	
	@Test
	public void testIsList() {
		valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isList());
		valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertTrue(valueFacade.isList());
	}
	
	@Test
	public void testSetIndex() {
		List valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueTarget.getIndex());
		SimpleValue indexTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		IValue indexFacade = FACADE_FACTORY.createValue(indexTarget);
		valueFacade.setIndex(indexFacade);
		assertSame(indexTarget, valueTarget.getIndex());
	}
	
	@Test
	public void testSetTypeName() {
		SimpleValue valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueTarget.getTypeName());
		valueFacade.setTypeName("java.lang.Integer");
		assertEquals("java.lang.Integer", valueTarget.getTypeName());
	}
	
	@Test
	public void testGetComponentClassName() {
		Component valueTarget = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getComponentClassName());
		valueTarget.setComponentClassName("org.foo.Bar");
		assertEquals("org.foo.Bar", valueFacade.getComponentClassName());
	}
	
	@Test
	public void testGetColumnIterator() {
		SimpleValue valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Iterator<IColumn> columnIterator = valueFacade.getColumnIterator();
		assertFalse(columnIterator.hasNext());
		Column columnTarget = new Column();
		valueTarget.addColumn(columnTarget);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		columnIterator = valueFacade.getColumnIterator();
		assertTrue(columnIterator.hasNext());
		assertSame(columnTarget, ((IFacade)columnIterator.next()).getTarget());
	}
	
	@Test
	public void testIsTypeSpecified() {
		SimpleValue valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertFalse(valueFacade.isTypeSpecified());
		valueTarget.setTypeName("org.foo.Bar");
		assertTrue(valueFacade.isTypeSpecified());
	}
	
	@Test
	public void testGetCollectionTable() {
		Table tableTarget = new Table();
		Collection valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getCollectionTable());
		valueTarget.setCollectionTable(tableTarget);
		assertSame(
				tableTarget, 
				((IFacade)valueFacade.getCollectionTable()).getTarget());
	}
	
	@Test
	public void testGetKey() {
		Map valueTarget = new Map(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getKey());
		KeyValue keyValue = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueTarget.setKey(keyValue);
		assertSame(keyValue, ((IFacade)valueFacade.getKey()).getTarget());
	}
	
}
