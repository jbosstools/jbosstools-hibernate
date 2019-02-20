package org.jboss.tools.hibernate.runtime.v_5_4.internal;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.boot.spi.MetadataImplementor;
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
import org.junit.Assert;
import org.junit.Test;

public class ValueFacadeTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Value valueTarget = null;
	private IValue valueFacade = null;
	
	@Test
	public void testIsSimpleValue() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isSimpleValue());
		valueTarget = new Set(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isSimpleValue());
	}

	@Test
	public void testIsCollection() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isCollection());
		valueTarget = new Set(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isCollection());
	}

	@Test
	public void testGetCollectionElement() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IValue collectionElement = valueFacade.getCollectionElement();
		Assert.assertNull(collectionElement);
		Set set = new Set(new DummyMetadataBuildingContext(), null);
		set.setElement(valueTarget);
		valueFacade = FACADE_FACTORY.createValue(set);
		collectionElement = valueFacade.getCollectionElement();
		Assert.assertNotNull(collectionElement);
		Assert.assertSame(valueTarget, ((IFacade)collectionElement).getTarget());
	}

	@Test 
	public void testIsOneToMany() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isOneToMany());
		OneToMany oneToMany = new OneToMany(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(oneToMany);
		Assert.assertTrue(valueFacade.isOneToMany());
	}

	@Test 
	public void testIsManyToOne() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isManyToOne());
		ManyToOne manyToOne = new ManyToOne(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(manyToOne);
		Assert.assertTrue(valueFacade.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isOneToOne());
		OneToOne oneToOne = new OneToOne(new DummyMetadataBuildingContext(), null, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(oneToOne);
		Assert.assertTrue(valueFacade.isOneToOne());
	}

	@Test
	public void testIsMap() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isMap());
		Map map = new Map(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(map);
		Assert.assertTrue(valueFacade.isMap());
	}

	@Test
	public void testIsComponent() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isComponent());
		Component component = new Component(new DummyMetadataBuildingContext(), new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(component);
		Assert.assertTrue(valueFacade.isComponent());
	}

	@Test
	public void testIsEmbedded() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.isEmbedded());
		Component component = new Component(new DummyMetadataBuildingContext(), new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(component);
		component.setEmbedded(true);
		Assert.assertTrue(valueFacade.isEmbedded());
		component.setEmbedded(false);
		Assert.assertFalse(valueFacade.isEmbedded());
	}

	@Test
	public void testIsToOne() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isToOne());
		ToOne toOne = new OneToOne(new DummyMetadataBuildingContext(), null, new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(toOne);
		Assert.assertTrue(valueFacade.isToOne());
	}

	@Test 
	public void testGetTable() {
		Table tableTarget = new Table();
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext(), tableTarget);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		ITable tableFacade = valueFacade.getTable();
		Assert.assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
	@Test
	public void testGetType() {
		SimpleValue valueTarget = new SimpleValue(
				(MetadataImplementor) MetadataHelper.getMetadata(
						new Configuration()));
		valueTarget.setTypeName("java.lang.Integer");
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IType typeFacade = valueFacade.getType();
		Assert.assertEquals(
				"org.hibernate.type.IntegerType", 
				((IFacade)typeFacade).getTarget().getClass().getName());
	}
	
	@Test
	public void testSetElement() {
		SimpleValue elementTarget = new SimpleValue(new DummyMetadataBuildingContext());
		IValue elementFacade = FACADE_FACTORY.createValue(elementTarget);
		Set valueTarget = new Set(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getElement());
		valueFacade.setElement(elementFacade);
		Assert.assertSame(elementTarget, valueTarget.getElement());
	}
	
	@Test
	public void testSetCollectionTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		Collection valueTarget = new Set(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getCollectionTable());
		valueFacade.setCollectionTable(tableFacade);
		Assert.assertSame(tableTarget, valueTarget.getCollectionTable());
	}
	
	@Test
	public void testSetTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		SimpleValue valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getTable());
		valueFacade.setTable(tableFacade);
		Assert.assertSame(tableTarget, valueTarget.getTable());
	}
	
	@Test
	public void testIsList() {
		valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isList());
		valueTarget = new List(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isList());
	}
	
	@Test
	public void testSetIndex() {
		List valueTarget = new List(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getIndex());
		SimpleValue indexTarget = new SimpleValue(new DummyMetadataBuildingContext());
		IValue indexFacade = FACADE_FACTORY.createValue(indexTarget);
		valueFacade.setIndex(indexFacade);
		Assert.assertSame(indexTarget, valueTarget.getIndex());
	}
	
	@Test
	public void testSetTypeName() {
		SimpleValue valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getTypeName());
		valueFacade.setTypeName("java.lang.Integer");
		Assert.assertEquals("java.lang.Integer", valueTarget.getTypeName());
	}
	
	@Test
	public void testGetComponentClassName() {
		Component valueTarget = new Component(new DummyMetadataBuildingContext(), new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getComponentClassName());
		valueTarget.setComponentClassName("org.foo.Bar");
		Assert.assertEquals("org.foo.Bar", valueFacade.getComponentClassName());
	}
	
	@Test
	public void testGetColumnIterator() {
		SimpleValue valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Iterator<IColumn> columnIterator = valueFacade.getColumnIterator();
		Assert.assertFalse(columnIterator.hasNext());
		Column columnTarget = new Column();
		valueTarget.addColumn(columnTarget);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		columnIterator = valueFacade.getColumnIterator();
		Assert.assertTrue(columnIterator.hasNext());
		Assert.assertSame(columnTarget, ((IFacade)columnIterator.next()).getTarget());
	}
	
	@Test
	public void testIsTypeSpecified() {
		SimpleValue valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isTypeSpecified());
		valueTarget.setTypeName("org.foo.Bar");
		Assert.assertTrue(valueFacade.isTypeSpecified());
	}
	
	@Test
	public void testGetCollectionTable() {
		Table tableTarget = new Table();
		Collection valueTarget = new Set(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getCollectionTable());
		valueTarget.setCollectionTable(tableTarget);
		Assert.assertSame(
				tableTarget, 
				((IFacade)valueFacade.getCollectionTable()).getTarget());
	}
	
	@Test
	public void testGetKey() {
		Map valueTarget = new Map(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getKey());
		KeyValue keyValue = new SimpleValue(new DummyMetadataBuildingContext());
		valueTarget.setKey(keyValue);
		Assert.assertSame(keyValue, ((IFacade)valueFacade.getKey()).getTarget());
	}
	
	@Test
	public void testGetIndex() {
		List valueTarget = new List(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getIndex());
		SimpleValue indexValue = new SimpleValue(new DummyMetadataBuildingContext());
		valueTarget.setIndex(indexValue);
		Assert.assertSame(indexValue, ((IFacade)valueFacade.getIndex()).getTarget());
	}
	
	@Test
	public void testGetElementClassName() {
		Array valueTarget = new Array(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getElementClassName());
		valueTarget.setElementClassName("org.foo.Bar");
		Assert.assertEquals("org.foo.Bar", valueFacade.getElementClassName());;
	}
	
	@Test
	public void testGetTypeName() {
		SimpleValue valueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getTypeName());
		valueTarget.setTypeName("org.foo.Bar");
		Assert.assertEquals("org.foo.Bar", valueFacade.getTypeName());
	}
	
	@Test
	public void testIsDependantValue() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isDependantValue());
		DependantValue dependantValueTarget = new DependantValue(new DummyMetadataBuildingContext(), null, null);
		valueFacade = FACADE_FACTORY.createValue(dependantValueTarget);
		Assert.assertTrue(valueFacade.isDependantValue());
	}
	
	@Test
	public void testIsAny() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isAny());
		Any anyTarget = new Any(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(anyTarget);
		Assert.assertTrue(valueFacade.isAny());
	}
	
	@Test
	public void testIsSet() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isSet());
		Set setTarget = new Set(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(setTarget);
		Assert.assertTrue(valueFacade.isSet());
	}
	
	@Test
	public void testIsPrimitiveArray() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isPrimitiveArray());
		PrimitiveArray primitiveArrayTarget = new PrimitiveArray(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(primitiveArrayTarget);
		Assert.assertTrue(valueFacade.isPrimitiveArray());
	}
	
	@Test
	public void testIsArray() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isArray());
		Array arrayTarget = new Array(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(arrayTarget);
		Assert.assertTrue(valueFacade.isArray());
	}
	
	@Test
	public void testIsIdentifierBag() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isIdentifierBag());
		IdentifierBag identifierBagTarget = new IdentifierBag(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(identifierBagTarget);
		Assert.assertTrue(valueFacade.isIdentifierBag());
	}
	
	@Test
	public void testIsBag() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertFalse(valueFacade.isBag());
		Bag bagTarget = new Bag(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(bagTarget);
		Assert.assertTrue(valueFacade.isBag());
	}
	
	@Test
	public void testGetReferencedEntityName() {
		ManyToOne valueTarget = new ManyToOne(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getReferencedEntityName());
		valueTarget.setReferencedEntityName("Foo");
		Assert.assertEquals("Foo", valueFacade.getReferencedEntityName());
	}
	
	@Test
	public void testGetEntityName() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertNull(valueFacade.getEntityName());
		RootClass pc = new RootClass(null);
		pc.setEntityName("foobar");
		OneToOne oneToOneTarget = new OneToOne(new DummyMetadataBuildingContext(), null, pc);
		valueFacade = FACADE_FACTORY.createValue(oneToOneTarget);
		Assert.assertEquals("foobar", valueFacade.getEntityName());
	}
	
	@Test
	public void testGetPropertyIterator() {
		Component componentTarget = new Component(new DummyMetadataBuildingContext(), new RootClass(null));
		valueFacade = FACADE_FACTORY.createValue(componentTarget);
		Iterator<IProperty> iter = valueFacade.getPropertyIterator();
		Assert.assertFalse(iter.hasNext());
		Property propertyTarget = new Property();
		componentTarget.addProperty(propertyTarget);
		valueFacade = FACADE_FACTORY.createValue(componentTarget);
		iter = valueFacade.getPropertyIterator();
		Assert.assertTrue(iter.hasNext());
		IProperty propertyFacade = iter.next();
		Assert.assertSame(propertyTarget, ((IFacade)propertyFacade).getTarget());
	}
	
	@Test
	public void testAddColumn() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Iterator<?> columnIterator = simpleValueTarget.getColumnIterator();
		Assert.assertFalse(columnIterator.hasNext());
		Column columnTarget = new Column();
		IColumn columnFacade = FACADE_FACTORY.createColumn(columnTarget);
		valueFacade.addColumn(columnFacade);
		columnIterator = simpleValueTarget.getColumnIterator();
		Assert.assertTrue(columnIterator.hasNext());
		Assert.assertSame(columnTarget, columnIterator.next());
	}
	
	@Test
	public void testGetTypeParameters() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertNull(simpleValueTarget.getTypeParameters());
		Properties properties = new Properties();
		valueFacade.setTypeParameters(properties);
		Assert.assertSame(properties, simpleValueTarget.getTypeParameters());		
	}
	
	@Test
	public void testGetForeignKeyName() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		Assert.assertNull(valueFacade.getForeignKeyName());
		simpleValueTarget.setForeignKeyName("foobar");
		Assert.assertEquals("foobar", valueFacade.getForeignKeyName());
	}
	
	@Test
	public void testGetOwner() {
		RootClass rc = new RootClass(null);
		Component componentTarget = new Component(new DummyMetadataBuildingContext(), rc);
		valueFacade = FACADE_FACTORY.createValue(componentTarget);
		Assert.assertSame(rc, ((IFacade)valueFacade.getOwner()).getTarget());
	}
	
	@Test
	public void testGetElement() {
		Bag bagValueTarget = new Bag(new DummyMetadataBuildingContext(), null);
		IValue bagValueFacade = FACADE_FACTORY.createValue(bagValueTarget);
		Assert.assertNull(bagValueFacade.getElement());
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		bagValueTarget.setElement(simpleValueTarget);
		Assert.assertSame(
				simpleValueTarget, 
				((IFacade)bagValueFacade.getElement()).getTarget());
	}
	
	@Test
	public void testGetParentProperty() {
		Component componentTarget = new Component(new DummyMetadataBuildingContext(), new RootClass(null));
		IValue valueFacade = FACADE_FACTORY.createValue(componentTarget);
		Assert.assertNull(valueFacade.getParentProperty());
		componentTarget.setParentProperty("foobar");
		Assert.assertEquals("foobar", valueFacade.getParentProperty());
	}
	
	@Test
	public void testSetElementClassName() {
		Array arrayTarget = new Array(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(arrayTarget);
		Assert.assertNull(arrayTarget.getElementClassName());
		valueFacade.setElementClassName("foobar");
		Assert.assertEquals("foobar", arrayTarget.getElementClassName());
	}
	
	@Test
	public void testSetKey() {
		KeyValue keyValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		IValue keyValueFacade = FACADE_FACTORY.createValue(keyValueTarget);
		Collection collectionTarget = new Bag(new DummyMetadataBuildingContext(), null);
		IValue collectionFacade = FACADE_FACTORY.createValue(collectionTarget);
		Assert.assertNull(collectionTarget.getKey());
		collectionFacade.setKey(keyValueFacade);
		Assert.assertSame(keyValueTarget, collectionTarget.getKey());
	}
	
	@Test
	public void testSetFetchModeJoin() {
		SimpleValue simpleValueTarget = new SimpleValue(new DummyMetadataBuildingContext());
		Assert.assertNotEquals(FetchMode.JOIN, simpleValueTarget.getFetchMode());
		valueFacade = FACADE_FACTORY.createValue(simpleValueTarget);
		valueFacade.setFetchModeJoin();
		Assert.assertNotEquals(FetchMode.JOIN, simpleValueTarget.getFetchMode());
		Collection collectionTarget = new Bag(new DummyMetadataBuildingContext(), null);
		Assert.assertNotEquals(FetchMode.JOIN, collectionTarget.getFetchMode());
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		valueFacade.setFetchModeJoin();
		Assert.assertEquals(FetchMode.JOIN, collectionTarget.getFetchMode());
		ManyToOne manyToOneTarget = new ManyToOne(new DummyMetadataBuildingContext(), null);
		Assert.assertNotEquals(FetchMode.JOIN, manyToOneTarget.getFetchMode());
		valueFacade = FACADE_FACTORY.createValue(manyToOneTarget);
		valueFacade.setFetchModeJoin();
		Assert.assertEquals(FetchMode.JOIN, manyToOneTarget.getFetchMode());
	}
	
	@Test
	public void testIsInverse() {
		Collection collectionTarget = new Bag(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		Assert.assertFalse(valueFacade.isInverse());
		collectionTarget.setInverse(true);
		Assert.assertTrue(valueFacade.isInverse());
	}
	
	@Test
	public void testGetAssociatedClass() {
		RootClass rootClass = new RootClass(null);
		OneToMany oneToManyTarget = new OneToMany(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(oneToManyTarget);
		Assert.assertNull(valueFacade.getAssociatedClass());
		oneToManyTarget.setAssociatedClass(rootClass);
		Assert.assertSame(
				rootClass, 
				((IFacade)valueFacade.getAssociatedClass()).getTarget());
	}
	
	@Test
	public void testSetLazy() {
		Collection collectionTarget = new Bag(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		valueFacade.setLazy(true);
		Assert.assertTrue(collectionTarget.isLazy());
		valueFacade.setLazy(false);
		Assert.assertFalse(collectionTarget.isLazy());
	}
	
	@Test
	public void testSetRole() {
		Collection collectionTarget = new Bag(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(collectionTarget);
		Assert.assertNull(collectionTarget.getRole());
		valueFacade.setRole("foobar");
		Assert.assertEquals("foobar", collectionTarget.getRole());
	}
	
	@Test
	public void testSetReferencedEntityName() {
		ManyToOne valueTarget = new ManyToOne(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getReferencedEntityName());
		valueFacade.setReferencedEntityName("Foo");
		Assert.assertEquals("Foo", valueTarget.getReferencedEntityName());
	}
	
	@Test
	public void testSetAssociatedClass() {
		RootClass rootClassTarget = new RootClass(null);
		IPersistentClass rootClassFacade = 
				FACADE_FACTORY.createPersistentClass(rootClassTarget);
		OneToMany oneToManyTarget = new OneToMany(new DummyMetadataBuildingContext(), null);
		valueFacade = FACADE_FACTORY.createValue(oneToManyTarget);
		Assert.assertNull(oneToManyTarget.getAssociatedClass());
		valueFacade.setAssociatedClass(rootClassFacade);
		Assert.assertSame(
				rootClassTarget, 
				oneToManyTarget.getAssociatedClass());
	}
	
}
