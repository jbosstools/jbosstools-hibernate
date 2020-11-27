package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.jboss.tools.hibernate.runtime.common.AbstractValueFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
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
	
	@Test
	public void testIsList() {
		valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isList());
		valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertTrue(valueFacade.isList());
	}
	
	@Test
	public void testSetIndex() {
		List valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueTarget.getIndex());
		SimpleValue indexTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		IValue indexFacade = new AbstractValueFacade(FACADE_FACTORY, indexTarget) {};
		valueFacade.setIndex(indexFacade);
		assertSame(indexTarget, valueTarget.getIndex());
	}
	
	@Test
	public void testSetTypeName() {
		SimpleValue valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueTarget.getTypeName());
		valueFacade.setTypeName("java.lang.Integer");
		assertEquals("java.lang.Integer", valueTarget.getTypeName());
	}
	
	@Test
	public void testGetComponentClassName() {
		Component valueTarget = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getComponentClassName());
		valueTarget.setComponentClassName("org.foo.Bar");
		assertEquals("org.foo.Bar", valueFacade.getComponentClassName());
	}
	
	@Test
	public void testGetColumnIterator() {
		SimpleValue valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueTarget.setTable(new Table());
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		Iterator<IColumn> columnIterator = valueFacade.getColumnIterator();
		assertFalse(columnIterator.hasNext());
		Column columnTarget = new Column();
		valueTarget.addColumn(columnTarget);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		columnIterator = valueFacade.getColumnIterator();
		assertTrue(columnIterator.hasNext());
		assertSame(columnTarget, ((IFacade)columnIterator.next()).getTarget());
	}
	
	@Test
	public void testIsTypeSpecified() {
		SimpleValue valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertFalse(valueFacade.isTypeSpecified());
		valueTarget.setTypeName("org.foo.Bar");
		assertTrue(valueFacade.isTypeSpecified());
	}
	
	@Test
	public void testGetCollectionTable() {
		Table tableTarget = new Table();
		Collection valueTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getCollectionTable());
		valueTarget.setCollectionTable(tableTarget);
		assertSame(
				tableTarget, 
				((IFacade)valueFacade.getCollectionTable()).getTarget());
	}
	
	@Test
	public void testGetKey() {
		Map valueTarget = new Map(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getKey());
		KeyValue keyValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueTarget.setKey(keyValue);
		assertSame(keyValue, ((IFacade)valueFacade.getKey()).getTarget());
	}
	
	@Test
	public void testGetIndex() {
		List valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getIndex());
		SimpleValue indexValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueTarget.setIndex(indexValue);
		assertSame(indexValue, ((IFacade)valueFacade.getIndex()).getTarget());
	}
	
	@Test
	public void testGetElementClassName() {
		Array valueTarget = new Array(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getElementClassName());
		valueTarget.setElementClassName("org.foo.Bar");
		assertEquals("org.foo.Bar", valueFacade.getElementClassName());;
	}
	
	@Test
	public void testGetTypeName() {
		SimpleValue valueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getTypeName());
		valueTarget.setTypeName("org.foo.Bar");
		assertEquals("org.foo.Bar", valueFacade.getTypeName());
	}
	
	@Test
	public void testIsDependantValue() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isDependantValue());
		DependantValue dependantValueTarget = new DependantValue(DummyMetadataBuildingContext.INSTANCE, null, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, dependantValueTarget) {};
		assertTrue(valueFacade.isDependantValue());
	}
	
	@Test
	public void testIsAny() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isAny());
		Any anyTarget = new Any(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, anyTarget) {};
		assertTrue(valueFacade.isAny());
	}
	
	@Test
	public void testIsSet() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isSet());
		Set setTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, setTarget) {};
		assertTrue(valueFacade.isSet());
	}
	
	@Test
	public void testIsPrimitiveArray() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isPrimitiveArray());
		PrimitiveArray primitiveArrayTarget = new PrimitiveArray(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, primitiveArrayTarget) {};
		assertTrue(valueFacade.isPrimitiveArray());
	}
	
	@Test
	public void testIsArray() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isArray());
		Array arrayTarget = new Array(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, arrayTarget) {};
		assertTrue(valueFacade.isArray());
	}
	
	@Test
	public void testIsIdentifierBag() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isIdentifierBag());
		IdentifierBag identifierBagTarget = new IdentifierBag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, identifierBagTarget) {};
		assertTrue(valueFacade.isIdentifierBag());
	}
	
	@Test
	public void testIsBag() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertFalse(valueFacade.isBag());
		Bag bagTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, bagTarget) {};
		assertTrue(valueFacade.isBag());
	}
	
	@Test
	public void testGetReferencedEntityName() {
		ManyToOne valueTarget = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, valueTarget) {};
		assertNull(valueFacade.getReferencedEntityName());
		valueTarget.setReferencedEntityName("Foo");
		assertEquals("Foo", valueFacade.getReferencedEntityName());
	}
	
	@Test
	public void testGetEntityName() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertNull(valueFacade.getEntityName());
		RootClass pc = new RootClass(null);
		pc.setEntityName("foobar");
		OneToOne oneToOneTarget = new OneToOne(DummyMetadataBuildingContext.INSTANCE, null, pc);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, oneToOneTarget) {};
		assertEquals("foobar", valueFacade.getEntityName());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Component componentTarget = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, componentTarget) {};
		Iterator<IProperty> iter = valueFacade.getPropertyIterator();
		assertFalse(iter.hasNext());
		Property propertyTarget = new Property();
		componentTarget.addProperty(propertyTarget);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, componentTarget) {};
		iter = valueFacade.getPropertyIterator();
		assertTrue(iter.hasNext());
		IProperty propertyFacade = iter.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testAddColumn() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		simpleValueTarget.setTable(new Table());
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		Iterator<?> columnIterator = simpleValueTarget.getColumnIterator();
		assertFalse(columnIterator.hasNext());
		Column columnTarget = new Column();
		IColumn columnFacade = FACADE_FACTORY.createColumn(columnTarget);
		valueFacade.addColumn(columnFacade);
		columnIterator = simpleValueTarget.getColumnIterator();
		assertTrue(columnIterator.hasNext());
		assertSame(columnTarget, columnIterator.next());
	}
	
	@Test
	public void testGetTypeParameters() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertNull(simpleValueTarget.getTypeParameters());
		Properties properties = new Properties();
		valueFacade.setTypeParameters(properties);
		assertSame(properties, simpleValueTarget.getTypeParameters());		
	}
	
	@Test
	public void testGetForeignKeyName() {
		SimpleValue simpleValueTarget = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, simpleValueTarget) {};
		assertNull(valueFacade.getForeignKeyName());
		simpleValueTarget.setForeignKeyName("foobar");
		assertEquals("foobar", valueFacade.getForeignKeyName());
	}
	
	@Test
	public void testGetOwner() {
		RootClass rc = new RootClass(null);
		Component componentTarget = new Component(DummyMetadataBuildingContext.INSTANCE, rc);
		valueFacade = new AbstractValueFacade(FACADE_FACTORY, componentTarget) {};
		assertSame(rc, ((IFacade)valueFacade.getOwner()).getTarget());
	}
	
}	

