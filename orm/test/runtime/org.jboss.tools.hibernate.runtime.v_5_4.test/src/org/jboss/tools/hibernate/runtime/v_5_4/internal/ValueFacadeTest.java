package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
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
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.MetadataHelper;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.MockConnectionProvider;
import org.jboss.tools.hibernate.runtime.v_5_4.internal.util.MockDialect;
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
		Configuration configuration = new Configuration();
		configuration.setProperty(AvailableSettings.DIALECT, MockDialect.class.getName());
		configuration.setProperty(AvailableSettings.CONNECTION_PROVIDER, MockConnectionProvider.class.getName());
		SimpleValue valueTarget = new SimpleValue(
				(MetadataImplementor) MetadataHelper.getMetadata(
						configuration));
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
	
	@Test
	public void testGetIndex() {
		List valueTarget = new List(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getIndex());
		SimpleValue indexValue = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueTarget.setIndex(indexValue);
		assertSame(indexValue, ((IFacade)valueFacade.getIndex()).getTarget());
	}
	
	@Test
	public void testGetElementClassName() {
		Array valueTarget = new Array(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getElementClassName());
		valueTarget.setElementClassName("org.foo.Bar");
		assertEquals("org.foo.Bar", valueFacade.getElementClassName());;
	}
	
	@Test
	public void testGetTypeName() {
		SimpleValue valueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getTypeName());
		valueTarget.setTypeName("org.foo.Bar");
		assertEquals("org.foo.Bar", valueFacade.getTypeName());
	}
	
	@Test
	public void testIsDependantValue() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isDependantValue());
		DependantValue dependantValueTarget = new DependantValue(DummyMetadataBuildingContext.INSTANCE, null, null);
		valueFacade = FACADE_FACTORY.createValue(dependantValueTarget);
		assertTrue(valueFacade.isDependantValue());
	}
	
	@Test
	public void testIsAny() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isAny());
		Any anyTarget = new Any(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(anyTarget);
		assertTrue(valueFacade.isAny());
	}
	
	@Test
	public void testIsSet() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isSet());
		Set setTarget = new Set(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(setTarget);
		assertTrue(valueFacade.isSet());
	}
	
	@Test
	public void testIsPrimitiveArray() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isPrimitiveArray());
		PrimitiveArray primitiveArrayTarget = new PrimitiveArray(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(primitiveArrayTarget);
		assertTrue(valueFacade.isPrimitiveArray());
	}
	
	@Test
	public void testIsArray() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isArray());
		Array arrayTarget = new Array(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(arrayTarget);
		assertTrue(valueFacade.isArray());
	}
	
	@Test
	public void testIsIdentifierBag() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isIdentifierBag());
		IdentifierBag identifierBagTarget = new IdentifierBag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(identifierBagTarget);
		assertTrue(valueFacade.isIdentifierBag());
	}
	
	@Test
	public void testIsBag() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertFalse(valueFacade.isBag());
		Bag bagTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(bagTarget);
		assertTrue(valueFacade.isBag());
	}
	
	@Test
	public void testGetReferencedEntityName() {
		ManyToOne valueTarget = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueFacade.getReferencedEntityName());
		valueTarget.setReferencedEntityName("Foo");
		assertEquals("Foo", valueFacade.getReferencedEntityName());
	}
	
	@Test
	public void testGetEntityName() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertNull(valueFacade.getEntityName());
		RootClass pc = new RootClass(null);
		pc.setEntityName("foobar");
		OneToOne oneToOneTarget = new OneToOne(DummyMetadataBuildingContext.INSTANCE, null, pc);
		valueFacade = FACADE_FACTORY.createValue(oneToOneTarget);
		assertEquals("foobar", valueFacade.getEntityName());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Component componentTarget = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(componentTarget);
		Iterator<IProperty> iter = valueFacade.getPropertyIterator();
		assertFalse(iter.hasNext());
		Property propertyTarget = new Property();
		componentTarget.addProperty(propertyTarget);
		valueFacade = FACADE_FACTORY.createValue(componentTarget);
		iter = valueFacade.getPropertyIterator();
		assertTrue(iter.hasNext());
		IProperty propertyFacade = iter.next();
		assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testAddColumn() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
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
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertNull(simpleValueTarget.getTypeParameters());
		Properties properties = new Properties();
		valueFacade.setTypeParameters(properties);
		assertSame(properties, simpleValueTarget.getTypeParameters());		
	}
	
	@Test
	public void testGetForeignKeyName() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		assertNull(valueFacade.getForeignKeyName());
		simpleValueTarget.setForeignKeyName("foobar");
		assertEquals("foobar", valueFacade.getForeignKeyName());
	}
	
	@Test
	public void testGetOwner() {
		RootClass rc = new RootClass(null);
		Component componentTarget = new Component(DummyMetadataBuildingContext.INSTANCE, rc);
		valueFacade = FACADE_FACTORY.createValue(componentTarget);
		assertSame(rc, ((IFacade)valueFacade.getOwner()).getTarget());
	}
	
	@Test
	public void testGetElement() {
		Bag bagValueTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		IValue bagValueFacade = FACADE_FACTORY.createValue(bagValueTarget);
		assertNull(bagValueFacade.getElement());
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		bagValueTarget.setElement(simpleValueTarget);
		assertSame(
				simpleValueTarget, 
				((IFacade)bagValueFacade.getElement()).getTarget());
	}
	
	@Test
	public void testGetParentProperty() {
		Component componentTarget = new Component(DummyMetadataBuildingContext.INSTANCE, new RootClass(null));
		IValue valueFacade = FACADE_FACTORY.createValue(componentTarget);
		assertNull(valueFacade.getParentProperty());
		componentTarget.setParentProperty("foobar");
		assertEquals("foobar", valueFacade.getParentProperty());
	}
	
	@Test
	public void testSetElementClassName() {
		Array arrayTarget = new Array(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(arrayTarget);
		assertNull(arrayTarget.getElementClassName());
		valueFacade.setElementClassName("foobar");
		assertEquals("foobar", arrayTarget.getElementClassName());
	}
	
	@Test
	public void testSetKey() {
		KeyValue keyValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		IValue keyValueFacade = FACADE_FACTORY.createValue(keyValueTarget);
		Collection collectionTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		IValue collectionFacade = FACADE_FACTORY.createValue(collectionTarget);
		assertNull(collectionTarget.getKey());
		collectionFacade.setKey(keyValueFacade);
		assertSame(keyValueTarget, collectionTarget.getKey());
	}
	
	@Test
	public void testSetFetchModeJoin() {
		SimpleValue simpleValueTarget = new SimpleValue(DummyMetadataBuildingContext.INSTANCE, null);
		assertNotEquals(FetchMode.JOIN, simpleValueTarget.getFetchMode());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		valueFacade.setFetchModeJoin();
		assertNotEquals(FetchMode.JOIN, simpleValueTarget.getFetchMode());
		Collection collectionTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		assertNotEquals(FetchMode.JOIN, collectionTarget.getFetchMode());
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		valueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, collectionTarget.getFetchMode());
		ManyToOne manyToOneTarget = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, null);
		assertNotEquals(FetchMode.JOIN, manyToOneTarget.getFetchMode());
		valueFacade = FACADE_FACTORY.createValue(manyToOneTarget);
		valueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, manyToOneTarget.getFetchMode());
	}
	
	@Test
	public void testIsInverse() {
		Collection collectionTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		assertFalse(valueFacade.isInverse());
		collectionTarget.setInverse(true);
		assertTrue(valueFacade.isInverse());
	}
	
	@Test
	public void testGetAssociatedClass() {
		RootClass rootClass = new RootClass(null);
		OneToMany oneToManyTarget = new OneToMany(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(oneToManyTarget);
		assertNull(valueFacade.getAssociatedClass());
		oneToManyTarget.setAssociatedClass(rootClass);
		assertSame(
				rootClass, 
				((IFacade)valueFacade.getAssociatedClass()).getTarget());
	}
	
	@Test
	public void testSetLazy() {
		Collection collectionTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		valueFacade.setLazy(true);
		assertTrue(collectionTarget.isLazy());
		valueFacade.setLazy(false);
		assertFalse(collectionTarget.isLazy());
	}
	
	@Test
	public void testSetRole() {
		Collection collectionTarget = new Bag(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		assertNull(collectionTarget.getRole());
		valueFacade.setRole("foobar");
		assertEquals("foobar", collectionTarget.getRole());
	}
	
	@Test
	public void testSetReferencedEntityName() {
		ManyToOne valueTarget = new ManyToOne(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		assertNull(valueTarget.getReferencedEntityName());
		valueFacade.setReferencedEntityName("Foo");
		assertEquals("Foo", valueTarget.getReferencedEntityName());
	}
	
	@Test
	public void testSetAssociatedClass() {
		RootClass rootClassTarget = new RootClass(null);
		IPersistentClass rootClassFacade = 
				FACADE_FACTORY.createPersistentClass(rootClassTarget);
		OneToMany oneToManyTarget = new OneToMany(DummyMetadataBuildingContext.INSTANCE, null);
		valueFacade = FACADE_FACTORY.createValue(oneToManyTarget);
		assertNull(oneToManyTarget.getAssociatedClass());
		valueFacade.setAssociatedClass(rootClassFacade);
		assertSame(
				rootClassTarget, 
				oneToManyTarget.getAssociatedClass());
	}
	
}
