package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import java.util.Properties;

import org.hibernate.FetchMode;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Fetchable;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.hibernate.type.AnyType;
import org.hibernate.type.ArrayType;
import org.hibernate.type.BagType;
import org.hibernate.type.BasicType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.IdentifierBagType;
import org.hibernate.type.ListType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.MapType;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.SetType;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IValueTest {
	
	private IValue arrayValueFacade = null;
	private Value arrayValueTarget = null;
	private IValue bagValueFacade = null;
	private Value bagValueTarget = null;
	private IValue listValueFacade = null;
	private Value listValueTarget = null;
	private IValue manyToOneValueFacade = null;
	private Value manyToOneValueTarget = null;
	private IValue mapValueFacade = null;
	private Value mapValueTarget = null;
	private IValue oneToManyValueFacade = null;
	private Value oneToManyValueTarget = null;
	private IValue oneToOneValueFacade = null;
	private Value oneToOneValueTarget = null;
	private IValue primitiveArrayValueFacade = null;
	private Value primitiveArrayValueTarget = null;
	private IValue setValueFacade = null;
	private Value setValueTarget = null;
	private IValue simpleValueFacade = null;
	private Value simpleValueTarget = null;
	private IValue componentValueFacade = null;
	private Value componentValueTarget = null;
	private IValue dependantValueFacade = null;
	private Value dependantValueTarget = null;
	private IValue anyValueFacade = null;
	private Value anyValueTarget = null;
	private IValue identifierBagValueFacade = null;
	private Value identifierBagValueTarget = null;
	
	private IPersistentClass persistentClassFacade = null;
	private PersistentClass persistentClassTarget = null;
	private ITable tableFacade = null;
	private Table tableTarget = null;
	
	@BeforeEach 
	public void beforeEach() {
		
		persistentClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.
				class, WrapperFactory.createRootClassWrapper());
		Object persistentClassWrapper = ((IFacade)persistentClassFacade).getTarget();
		persistentClassTarget = ((PersistentClassWrapper)persistentClassWrapper).getWrappedObject();
		
		tableFacade = (ITable)GenericFacadeFactory.createFacade(
				ITable.class, 
				WrapperFactory.createTableWrapper("foo"));
		tableTarget = (Table)((IFacade)tableFacade).getTarget();
		
		arrayValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createArrayWrapper(persistentClassWrapper));
		arrayValueTarget = (Value)((Wrapper)((IFacade)arrayValueFacade).getTarget()).getWrappedObject();
		
		bagValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createBagWrapper(persistentClassWrapper));
		bagValueTarget = (Value)((Wrapper)((IFacade)bagValueFacade).getTarget()).getWrappedObject();

		listValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createListWrapper(persistentClassWrapper));
		listValueTarget = (Value)((Wrapper)((IFacade)listValueFacade).getTarget()).getWrappedObject();

		manyToOneValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createManyToOneWrapper(tableTarget));
		manyToOneValueTarget = (Value)((Wrapper)((IFacade)manyToOneValueFacade).getTarget()).getWrappedObject();

		mapValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createMapWrapper(persistentClassWrapper));
		mapValueTarget = (Value)((Wrapper)((IFacade)mapValueFacade).getTarget()).getWrappedObject();

		oneToManyValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToManyWrapper(persistentClassWrapper));
		oneToManyValueTarget = (Value)((Wrapper)((IFacade)oneToManyValueFacade).getTarget()).getWrappedObject();

		oneToOneValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToOneWrapper(persistentClassWrapper));
		oneToOneValueTarget = (Value)((Wrapper)((IFacade)oneToOneValueFacade).getTarget()).getWrappedObject();

		primitiveArrayValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createPrimitiveArrayWrapper(persistentClassWrapper));
		primitiveArrayValueTarget = (Value)((Wrapper)((IFacade)primitiveArrayValueFacade).getTarget()).getWrappedObject();

		setValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSetWrapper(persistentClassWrapper));
		setValueTarget = (Value)((Wrapper)((IFacade)setValueFacade).getTarget()).getWrappedObject();

		simpleValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createSimpleValueWrapper());
		simpleValueTarget = (Value)((Wrapper)((IFacade)simpleValueFacade).getTarget()).getWrappedObject();

		componentValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createComponentWrapper(persistentClassWrapper));
		componentValueTarget = (Value)((Wrapper)((IFacade)componentValueFacade).getTarget()).getWrappedObject();

		dependantValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createDependantValueWrapper(tableTarget, ((IFacade)simpleValueFacade).getTarget()));
		dependantValueTarget = (Value)((Wrapper)((IFacade)dependantValueFacade).getTarget()).getWrappedObject();
		
		anyValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createAnyValueWrapper(tableTarget));
		anyValueTarget = (Value)((Wrapper)((IFacade)anyValueFacade).getTarget()).getWrappedObject();
		
		identifierBagValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createIdentifierBagValueWrapper(persistentClassWrapper));
		identifierBagValueTarget = (Value)((Wrapper)((IFacade)identifierBagValueFacade).getTarget()).getWrappedObject();
		
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(arrayValueFacade);
		assertNotNull(arrayValueTarget);
		assertNotNull(bagValueFacade);
		assertNotNull(bagValueTarget);
		assertNotNull(listValueFacade);
		assertNotNull(listValueTarget);
		assertNotNull(manyToOneValueFacade);
		assertNotNull(manyToOneValueTarget);
		assertNotNull(mapValueFacade);
		assertNotNull(mapValueTarget);
		assertNotNull(oneToManyValueFacade);
		assertNotNull(oneToManyValueTarget);
		assertNotNull(oneToOneValueFacade);
		assertNotNull(oneToOneValueTarget);
		assertNotNull(primitiveArrayValueFacade);
		assertNotNull(primitiveArrayValueTarget);
		assertNotNull(setValueFacade);
		assertNotNull(setValueTarget);
		assertNotNull(simpleValueFacade);
		assertNotNull(simpleValueTarget);
		assertNotNull(componentValueFacade);
		assertNotNull(componentValueTarget);
		assertNotNull(dependantValueFacade);
		assertNotNull(dependantValueTarget);
		assertNotNull(anyValueFacade);
		assertNotNull(anyValueTarget);
		assertNotNull(identifierBagValueFacade);
		assertNotNull(identifierBagValueTarget);
	}

	@Test
	public void testIsSimpleValue() {
		assertFalse(arrayValueFacade.isSimpleValue());
		assertFalse(bagValueFacade.isSimpleValue());
		assertFalse(listValueFacade.isSimpleValue());
		assertTrue(manyToOneValueFacade.isSimpleValue());
		assertFalse(mapValueFacade.isSimpleValue());
		assertFalse(oneToManyValueFacade.isSimpleValue());
		assertTrue(oneToOneValueFacade.isSimpleValue());
		assertFalse(primitiveArrayValueFacade.isSimpleValue());
		assertFalse(setValueFacade.isSimpleValue());
		assertTrue(simpleValueFacade.isSimpleValue());
		assertTrue(componentValueFacade.isSimpleValue());
		assertTrue(dependantValueFacade.isSimpleValue());
		assertTrue(anyValueFacade.isSimpleValue());
		assertFalse(identifierBagValueFacade.isSimpleValue());
	}

	@Test
	public void testIsCollection() {
		assertTrue(arrayValueFacade.isCollection());
		assertTrue(bagValueFacade.isCollection());
		assertTrue(listValueFacade.isCollection());
		assertFalse(manyToOneValueFacade.isCollection());
		assertTrue(mapValueFacade.isCollection());
		assertFalse(oneToManyValueFacade.isCollection());
		assertFalse(oneToOneValueFacade.isCollection());
		assertTrue(primitiveArrayValueFacade.isCollection());
		assertTrue(setValueFacade.isCollection());
		assertFalse(simpleValueFacade.isCollection());
		assertFalse(componentValueFacade.isCollection());
		assertFalse(dependantValueFacade.isCollection());
		assertFalse(anyValueFacade.isCollection());
		assertTrue(identifierBagValueFacade.isCollection());
	}
	
	@Test
	public void testGetCollectionElement() {
		assertNull(arrayValueFacade.getCollectionElement());
		((Collection)arrayValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)arrayValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
		assertNull(bagValueFacade.getCollectionElement());
		((Collection)bagValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)bagValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
		assertNull(listValueFacade.getCollectionElement());
		((Collection)listValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)listValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
		assertNull(manyToOneValueFacade.getCollectionElement());
		assertNull(mapValueFacade.getCollectionElement());
		((Collection)mapValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)mapValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
		assertNull(oneToManyValueFacade.getCollectionElement());
		assertNull(oneToOneValueFacade.getCollectionElement());
		assertNull(primitiveArrayValueFacade.getCollectionElement());
		((Collection)primitiveArrayValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)primitiveArrayValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
		assertNull(setValueFacade.getCollectionElement());
		((Collection)setValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)setValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
		assertNull(simpleValueFacade.getCollectionElement());
		assertNull(componentValueFacade.getCollectionElement());
		assertNull(dependantValueFacade.getCollectionElement());
		assertNull(anyValueFacade.getCollectionElement());
		assertNull(identifierBagValueFacade.getCollectionElement());
		((Collection)identifierBagValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)identifierBagValueFacade.getCollectionElement()).getTarget()).getWrappedObject());
	}
	
	@Test 
	public void testIsOneToMany() {
		assertFalse(arrayValueFacade.isOneToMany());
		assertFalse(bagValueFacade.isOneToMany());
		assertFalse(listValueFacade.isOneToMany());
		assertFalse(manyToOneValueFacade.isOneToMany());
		assertFalse(mapValueFacade.isOneToMany());
		assertTrue(oneToManyValueFacade.isOneToMany());
		assertFalse(oneToOneValueFacade.isOneToMany());
		assertFalse(primitiveArrayValueFacade.isOneToMany());
		assertFalse(setValueFacade.isOneToMany());
		assertFalse(simpleValueFacade.isOneToMany());
		assertFalse(componentValueFacade.isOneToMany());
		assertFalse(dependantValueFacade.isOneToMany());
		assertFalse(anyValueFacade.isOneToMany());
		assertFalse(identifierBagValueFacade.isOneToMany());
	}
	
	@Test 
	public void testIsManyToOne() {
		assertFalse(arrayValueFacade.isManyToOne());
		assertFalse(bagValueFacade.isManyToOne());
		assertFalse(listValueFacade.isManyToOne());
		assertTrue(manyToOneValueFacade.isManyToOne());
		assertFalse(mapValueFacade.isManyToOne());
		assertFalse(oneToManyValueFacade.isManyToOne());
		assertFalse(oneToOneValueFacade.isManyToOne());
		assertFalse(primitiveArrayValueFacade.isManyToOne());
		assertFalse(setValueFacade.isManyToOne());
		assertFalse(simpleValueFacade.isManyToOne());
		assertFalse(componentValueFacade.isManyToOne());
		assertFalse(dependantValueFacade.isManyToOne());
		assertFalse(anyValueFacade.isManyToOne());
		assertFalse(identifierBagValueFacade.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		assertFalse(arrayValueFacade.isOneToOne());
		assertFalse(bagValueFacade.isOneToOne());
		assertFalse(listValueFacade.isOneToOne());
		assertFalse(manyToOneValueFacade.isOneToOne());
		assertFalse(mapValueFacade.isOneToOne());
		assertFalse(oneToManyValueFacade.isOneToOne());
		assertTrue(oneToOneValueFacade.isOneToOne());
		assertFalse(primitiveArrayValueFacade.isOneToOne());
		assertFalse(setValueFacade.isOneToOne());
		assertFalse(simpleValueFacade.isOneToOne());
		assertFalse(componentValueFacade.isOneToOne());
		assertFalse(dependantValueFacade.isOneToOne());
		assertFalse(anyValueFacade.isOneToOne());
		assertFalse(identifierBagValueFacade.isOneToOne());
	}

	@Test 
	public void testIsMap() {
		assertFalse(arrayValueFacade.isMap());
		assertFalse(bagValueFacade.isMap());
		assertFalse(listValueFacade.isMap());
		assertFalse(manyToOneValueFacade.isMap());
		assertTrue(mapValueFacade.isMap());
		assertFalse(oneToManyValueFacade.isMap());
		assertFalse(oneToOneValueFacade.isMap());
		assertFalse(primitiveArrayValueFacade.isMap());
		assertFalse(setValueFacade.isMap());
		assertFalse(simpleValueFacade.isMap());
		assertFalse(componentValueFacade.isMap());
		assertFalse(dependantValueFacade.isMap());
		assertFalse(anyValueFacade.isMap());
		assertFalse(identifierBagValueFacade.isMap());
	}
	
	@Test
	public void testIsComponent() {
		assertFalse(arrayValueFacade.isComponent());
		assertFalse(bagValueFacade.isComponent());
		assertFalse(listValueFacade.isComponent());
		assertFalse(manyToOneValueFacade.isComponent());
		assertFalse(mapValueFacade.isComponent());
		assertFalse(oneToManyValueFacade.isComponent());
		assertFalse(oneToOneValueFacade.isComponent());
		assertFalse(primitiveArrayValueFacade.isComponent());
		assertFalse(setValueFacade.isComponent());
		assertFalse(simpleValueFacade.isComponent());
		assertTrue(componentValueFacade.isComponent());
		assertFalse(dependantValueFacade.isComponent());
		assertFalse(anyValueFacade.isComponent());
		assertFalse(identifierBagValueFacade.isComponent());
	}

	@Test 
	public void testIsEmbedded() {
		assertFalse(arrayValueFacade.isEmbedded());
		assertFalse(bagValueFacade.isEmbedded());
		assertFalse(listValueFacade.isEmbedded());
		assertFalse(manyToOneValueFacade.isEmbedded());
		assertFalse(mapValueFacade.isEmbedded());
		assertFalse(oneToManyValueFacade.isEmbedded());
		assertFalse(oneToOneValueFacade.isEmbedded());
		assertFalse(primitiveArrayValueFacade.isEmbedded());
		assertFalse(setValueFacade.isEmbedded());
		assertFalse(simpleValueFacade.isEmbedded());
		assertFalse(componentValueFacade.isEmbedded());
		((Component)componentValueTarget).setEmbedded(true);
		assertTrue(componentValueFacade.isEmbedded());
		assertFalse(dependantValueFacade.isEmbedded());
		assertFalse(anyValueFacade.isEmbedded());
		assertFalse(identifierBagValueFacade.isEmbedded());
	}

	@Test
	public void testIsToOne() {
		assertFalse(arrayValueFacade.isToOne());
		assertFalse(bagValueFacade.isToOne());
		assertFalse(listValueFacade.isToOne());
		assertTrue(manyToOneValueFacade.isToOne());
		assertFalse(mapValueFacade.isToOne());
		assertFalse(oneToManyValueFacade.isToOne());
		assertTrue(oneToOneValueFacade.isToOne());
		assertFalse(primitiveArrayValueFacade.isToOne());
		assertFalse(setValueFacade.isToOne());
		assertFalse(simpleValueFacade.isToOne());
		assertFalse(componentValueFacade.isToOne());
		assertFalse(dependantValueFacade.isToOne());
		assertFalse(anyValueFacade.isToOne());
		assertFalse(identifierBagValueFacade.isToOne());
	}
	
	@Test
	public void testGetTable() {
		persistentClassFacade.setTable(null);
		assertNull(arrayValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)arrayValueFacade.getTable()).getTarget());
		persistentClassFacade.setTable(null);
		assertNull(bagValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)bagValueFacade.getTable()).getTarget());
		persistentClassFacade.setTable(null);
		assertNull(listValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)listValueFacade.getTable()).getTarget());
		((ManyToOne)manyToOneValueTarget).setTable(null);
		assertNull(manyToOneValueFacade.getTable());
		((ManyToOne)manyToOneValueTarget).setTable(tableTarget);
		assertSame(tableTarget, ((IFacade)manyToOneValueFacade.getTable()).getTarget());
		persistentClassFacade.setTable(null);
		assertNull(mapValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)mapValueFacade.getTable()).getTarget());
		assertNull(oneToManyValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		oneToManyValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToManyWrapper(((IFacade)persistentClassFacade).getTarget()));
		assertSame(tableTarget, ((IFacade)oneToManyValueFacade.getTable()).getTarget());
		assertNull(oneToOneValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		oneToOneValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToOneWrapper(((IFacade)persistentClassFacade).getTarget()));
		assertSame(tableTarget, ((IFacade)oneToOneValueFacade.getTable()).getTarget());
		persistentClassFacade.setTable(null);
		assertNull(primitiveArrayValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)primitiveArrayValueFacade.getTable()).getTarget());
		persistentClassFacade.setTable(null);
		assertNull(setValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)setValueFacade.getTable()).getTarget());
		((SimpleValue)simpleValueTarget).setTable(null);
		assertNull(simpleValueFacade.getTable());
		((SimpleValue)simpleValueTarget).setTable(tableTarget);
		assertSame(tableTarget, ((IFacade)simpleValueFacade.getTable()).getTarget());
		((Component)componentValueTarget).setTable(null);
		assertNull(componentValueFacade.getTable());
		((Component)componentValueTarget).setTable(tableTarget);
		assertSame(tableTarget, ((IFacade)componentValueFacade.getTable()).getTarget());
		((SimpleValue)dependantValueTarget).setTable(null);
		assertNull(dependantValueFacade.getTable());
		((SimpleValue)dependantValueTarget).setTable(tableTarget);
		assertSame(tableTarget, ((IFacade)dependantValueFacade.getTable()).getTarget());
		assertSame(tableTarget, ((IFacade)anyValueFacade.getTable()).getTarget());
		((Any)anyValueTarget).setTable(null);
		assertNull(anyValueFacade.getTable());
		persistentClassFacade.setTable(null);
		assertNull(identifierBagValueFacade.getTable());
		persistentClassFacade.setTable(tableFacade);
		assertSame(tableTarget, ((IFacade)identifierBagValueFacade.getTable()).getTarget());
	}

	@Test
	public void testGetType() {
		((SimpleValue)simpleValueTarget).setTypeName("java.lang.Integer");
		IType typeFacade = simpleValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof BasicType);
		((Collection)arrayValueTarget).setElement(simpleValueTarget);
		typeFacade = arrayValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof ArrayType);
		((Collection)bagValueTarget).setElement(simpleValueTarget);
		typeFacade = bagValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof BagType);
		((Collection)listValueTarget).setElement(simpleValueTarget);
		typeFacade = listValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof ListType);
		typeFacade = manyToOneValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof ManyToOneType);
		((Collection)mapValueTarget).setElement(simpleValueTarget);
		typeFacade = mapValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof MapType);
		typeFacade = oneToManyValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof ManyToOneType);
		typeFacade = oneToOneValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof OneToOneType);
		((Collection)primitiveArrayValueTarget).setElement(simpleValueTarget);
		typeFacade = primitiveArrayValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof ArrayType);
		((Collection)setValueTarget).setElement(simpleValueTarget);
		typeFacade = setValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof SetType);
		((Component)componentValueTarget).setComponentClassName("java.lang.String");
		typeFacade = componentValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof ComponentType);
		typeFacade = dependantValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof BasicType);
		((Any)anyValueTarget).setIdentifierType("java.lang.Integer");
		typeFacade = anyValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof AnyType);
		((Collection)identifierBagValueTarget).setElement(simpleValueTarget);
		typeFacade = identifierBagValueFacade.getType();
		assertTrue((Type)((IFacade)typeFacade).getTarget() instanceof IdentifierBagType);
	}
	
	@Test
	public void testSetElement() {
		assertNull(((Collection)arrayValueTarget).getElement());
		arrayValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)arrayValueTarget).getElement());
		assertNull(((Collection)bagValueTarget).getElement());
		bagValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)bagValueTarget).getElement());
		assertNull(((Collection)listValueTarget).getElement());
		listValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)listValueTarget).getElement());
		assertNull(manyToOneValueFacade.getElement());
		manyToOneValueFacade.setElement(simpleValueFacade);
		assertNull(manyToOneValueFacade.getElement());
		assertNull(((Collection)mapValueTarget).getElement());
		mapValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)mapValueTarget).getElement());
		assertNull(oneToManyValueFacade.getElement());
		oneToManyValueFacade.setElement(simpleValueFacade);
		assertNull(oneToManyValueFacade.getElement());
		assertNull(oneToOneValueFacade.getElement());
		oneToOneValueFacade.setElement(simpleValueFacade);
		assertNull(oneToOneValueFacade.getElement());
		assertNull(((Collection)primitiveArrayValueTarget).getElement());
		primitiveArrayValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)primitiveArrayValueTarget).getElement());
		assertNull(((Collection)setValueTarget).getElement());
		setValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)setValueTarget).getElement());
		assertNull(simpleValueFacade.getElement());
		simpleValueFacade.setElement(arrayValueFacade);
		assertNull(simpleValueFacade.getElement());
		assertNull(componentValueFacade.getElement());
		componentValueFacade.setElement(arrayValueFacade);
		assertNull(componentValueFacade.getElement());
		assertNull(dependantValueFacade.getElement());
		dependantValueFacade.setElement(arrayValueFacade);
		assertNull(dependantValueFacade.getElement());
		assertNull(anyValueFacade.getElement());
		anyValueFacade.setElement(simpleValueFacade);
		assertNull(anyValueFacade.getElement());
		assertNull(((Collection)identifierBagValueTarget).getElement());
		identifierBagValueFacade.setElement(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)identifierBagValueTarget).getElement());
	}
	
	@Test
	public void testSetCollectionTable() {
		assertNull(((Collection)arrayValueTarget).getCollectionTable());
		arrayValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)arrayValueTarget).getCollectionTable());
		assertNull(((Collection)bagValueTarget).getCollectionTable());
		bagValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)bagValueTarget).getCollectionTable());
		assertNull(((Collection)listValueTarget).getCollectionTable());
		listValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)listValueTarget).getCollectionTable());
		assertNull(manyToOneValueFacade.getCollectionTable());
		manyToOneValueFacade.setCollectionTable(tableFacade);
		assertNull(manyToOneValueFacade.getElement());
		assertNull(((Collection)mapValueTarget).getCollectionTable());
		mapValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)mapValueTarget).getCollectionTable());
		assertNull(oneToManyValueFacade.getCollectionTable());
		oneToManyValueFacade.setCollectionTable(tableFacade);
		assertNull(oneToManyValueFacade.getCollectionTable());
		assertNull(oneToOneValueFacade.getCollectionTable());
		oneToOneValueFacade.setCollectionTable(tableFacade);
		assertNull(oneToOneValueFacade.getCollectionTable());
		assertNull(((Collection)primitiveArrayValueTarget).getCollectionTable());
		primitiveArrayValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)primitiveArrayValueTarget).getCollectionTable());
		assertNull(((Collection)setValueTarget).getCollectionTable());
		setValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)setValueTarget).getCollectionTable());
		assertNull(simpleValueFacade.getCollectionTable());
		simpleValueFacade.setCollectionTable(tableFacade);
		assertNull(simpleValueFacade.getCollectionTable());
		assertNull(componentValueFacade.getCollectionTable());
		componentValueFacade.setCollectionTable(tableFacade);
		assertNull(componentValueFacade.getCollectionTable());
		assertNull(dependantValueFacade.getCollectionTable());
		dependantValueFacade.setCollectionTable(tableFacade);
		assertNull(dependantValueFacade.getCollectionTable());
		assertNull(anyValueFacade.getCollectionTable());
		anyValueFacade.setCollectionTable(tableFacade);
		assertNull(anyValueFacade.getCollectionTable());
		assertNull(((Collection)identifierBagValueTarget).getCollectionTable());
		identifierBagValueFacade.setCollectionTable(tableFacade);
		assertSame(tableTarget, ((Collection)identifierBagValueTarget).getCollectionTable());
	}
	
	@Test
	public void testSetTable() {
		assertNull(arrayValueTarget.getTable());
		arrayValueFacade.setTable(tableFacade);
		assertNull(arrayValueTarget.getTable());
		assertNull(bagValueTarget.getTable());
		bagValueFacade.setTable(tableFacade);
		assertNull(bagValueTarget.getTable());
		assertNull(listValueTarget.getTable());
		listValueFacade.setTable(tableFacade);
		assertNull(listValueTarget.getTable());
		assertSame(tableTarget, manyToOneValueTarget.getTable());
		manyToOneValueFacade.setTable(null);
		assertNull(manyToOneValueTarget.getTable());
		assertNull(mapValueTarget.getTable());
		mapValueFacade.setTable(tableFacade);
		assertNull(mapValueTarget.getTable());
		assertNull(oneToManyValueTarget.getTable());
		oneToManyValueFacade.setTable(tableFacade);
		assertNull(oneToManyValueTarget.getTable());
		assertNull(oneToOneValueTarget.getTable());
		oneToOneValueFacade.setTable(tableFacade);
		assertSame(tableTarget, oneToOneValueTarget.getTable());
		assertNull(primitiveArrayValueTarget.getTable());
		primitiveArrayValueFacade.setTable(tableFacade);
		assertNull(primitiveArrayValueTarget.getTable());
		assertNull(setValueTarget.getTable());
		setValueFacade.setTable(tableFacade);
		assertNull(setValueTarget.getTable());
		assertNull(simpleValueTarget.getTable());
		simpleValueFacade.setTable(tableFacade);
		assertSame(tableTarget, simpleValueTarget.getTable());
		assertNull(componentValueTarget.getTable());
		componentValueFacade.setTable(tableFacade);
		assertSame(tableTarget, componentValueTarget.getTable());
		assertSame(tableTarget, dependantValueTarget.getTable());
		dependantValueFacade.setTable(null);
		assertNull(dependantValueTarget.getTable());
		assertSame(tableTarget, anyValueTarget.getTable());
		anyValueFacade.setTable(null);
		assertNull(anyValueTarget.getTable());
		assertNull(identifierBagValueTarget.getTable());
		identifierBagValueFacade.setTable(tableFacade);
		assertNull(identifierBagValueTarget.getTable());
	}
	
	@Test 
	public void testIsList() {
		assertTrue(arrayValueFacade.isList());
		assertFalse(bagValueFacade.isList());
		assertTrue(listValueFacade.isList());
		assertFalse(manyToOneValueFacade.isList());
		assertFalse(mapValueFacade.isList());
		assertFalse(oneToManyValueFacade.isList());
		assertFalse(oneToOneValueFacade.isList());
		assertTrue(primitiveArrayValueFacade.isList());
		assertFalse(setValueFacade.isList());
		assertFalse(simpleValueFacade.isList());
		assertFalse(componentValueFacade.isList());
		assertFalse(dependantValueFacade.isList());
		assertFalse(anyValueFacade.isList());
		assertFalse(identifierBagValueFacade.isList());
	}
	
	@Test
	public void testSetIndex() {
		assertNull(((IndexedCollection)arrayValueTarget).getIndex());
		arrayValueFacade.setIndex(simpleValueFacade);
		assertSame(simpleValueTarget, ((IndexedCollection)arrayValueTarget).getIndex());
		assertNull(bagValueFacade.getIndex());
		bagValueFacade.setIndex(simpleValueFacade);
		assertNull(bagValueFacade.getIndex());
		assertNull(((IndexedCollection)listValueTarget).getIndex());
		listValueFacade.setIndex(simpleValueFacade);
		assertSame(simpleValueTarget, ((IndexedCollection)listValueTarget).getIndex());
		assertNull(manyToOneValueFacade.getIndex());
		manyToOneValueFacade.setIndex(simpleValueFacade);
		assertNull(manyToOneValueFacade.getIndex());
		assertNull(((IndexedCollection)mapValueTarget).getIndex());
		mapValueFacade.setIndex(simpleValueFacade);
		assertSame(simpleValueTarget, ((IndexedCollection)mapValueTarget).getIndex());
		assertNull(oneToManyValueFacade.getIndex());
		oneToManyValueFacade.setIndex(simpleValueFacade);
		assertNull(oneToManyValueFacade.getIndex());
		assertNull(oneToOneValueFacade.getIndex());
		oneToOneValueFacade.setIndex(simpleValueFacade);
		assertNull(oneToOneValueFacade.getIndex());
		assertNull(((IndexedCollection)primitiveArrayValueTarget).getIndex());
		primitiveArrayValueFacade.setIndex(simpleValueFacade);
		assertSame(simpleValueTarget, ((IndexedCollection)primitiveArrayValueTarget).getIndex());
		assertNull(setValueFacade.getIndex());
		setValueFacade.setIndex(simpleValueFacade);
		assertNull(setValueFacade.getIndex());
		assertNull(simpleValueFacade.getIndex());
		simpleValueFacade.setIndex(simpleValueFacade);
		assertNull(simpleValueFacade.getIndex());
		assertNull(componentValueFacade.getIndex());
		componentValueFacade.setIndex(simpleValueFacade);
		assertNull(componentValueFacade.getIndex());
		assertNull(dependantValueFacade.getIndex());
		dependantValueFacade.setIndex(simpleValueFacade);
		assertNull(dependantValueFacade.getIndex());
		assertNull(anyValueFacade.getIndex());
		anyValueFacade.setIndex(simpleValueFacade);
		assertNull(anyValueFacade.getIndex());
		assertNull(identifierBagValueFacade.getIndex());
		identifierBagValueFacade.setIndex(simpleValueFacade);
		assertNull(identifierBagValueFacade.getIndex());
	}
	
	@Test
	public void testSetTypeName() {
		assertNull(((Collection)arrayValueTarget).getTypeName());
		arrayValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)arrayValueTarget).getTypeName());
		assertNull(((Collection)bagValueTarget).getTypeName());
		bagValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)bagValueTarget).getTypeName());
		assertNull(((Collection)listValueTarget).getTypeName());
		listValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)listValueTarget).getTypeName());
		assertNull(((SimpleValue)manyToOneValueTarget).getTypeName());
		manyToOneValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)manyToOneValueTarget).getTypeName());
		assertNull(((Collection)mapValueTarget).getTypeName());
		mapValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)mapValueTarget).getTypeName());
		assertNull(oneToManyValueFacade.getTypeName());
		oneToManyValueFacade.setTypeName("foobar");
		assertNull(oneToManyValueFacade.getTypeName());
		assertNull(((SimpleValue)oneToOneValueTarget).getTypeName());
		oneToOneValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)oneToOneValueTarget).getTypeName());
		assertNull(((Collection)primitiveArrayValueTarget).getTypeName());
		primitiveArrayValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)primitiveArrayValueTarget).getTypeName());
		assertNull(((Collection)setValueTarget).getTypeName());
		setValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)setValueTarget).getTypeName());
		assertNull(((SimpleValue)simpleValueTarget).getTypeName());
		simpleValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)simpleValueTarget).getTypeName());
		assertNull(((SimpleValue)componentValueTarget).getTypeName());
		componentValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)componentValueTarget).getTypeName());
		assertNull(((SimpleValue)dependantValueTarget).getTypeName());
		dependantValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)dependantValueTarget).getTypeName());
		assertNull(((SimpleValue)anyValueTarget).getTypeName());
		anyValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((SimpleValue)anyValueTarget).getTypeName());
		assertNull(((Collection)identifierBagValueTarget).getTypeName());
		identifierBagValueFacade.setTypeName("foobar");
		assertEquals("foobar", ((Collection)identifierBagValueTarget).getTypeName());
	}
	
	@Test
	public void testGetComponentClassName() {
		assertNull(arrayValueFacade.getComponentClassName());
		assertNull(bagValueFacade.getComponentClassName());
		assertNull(listValueFacade.getComponentClassName());
		assertNull(manyToOneValueFacade.getComponentClassName());
		assertNull(mapValueFacade.getComponentClassName());
		assertNull(oneToManyValueFacade.getComponentClassName());
		assertNull(oneToOneValueFacade.getComponentClassName());
		assertNull(primitiveArrayValueFacade.getComponentClassName());
		assertNull(setValueFacade.getComponentClassName());
		assertNull(simpleValueFacade.getComponentClassName());
		assertNull(componentValueFacade.getComponentClassName());
		((Component)componentValueTarget).setComponentClassName("foobar");
		assertEquals("foobar", componentValueFacade.getComponentClassName());
		assertNull(dependantValueFacade.getComponentClassName());
		assertNull(anyValueFacade.getComponentClassName());
		assertNull(identifierBagValueFacade.getComponentClassName());
	}
	
	@Test
	public void testGetColumnIterator() {
		Iterator<IColumn> columnIterator = null;
		IColumn columnFacade = null;
		Column columnTarget = new Column("foo");
		// collection values have no columns
		assertFalse(arrayValueFacade.getColumnIterator().hasNext());
		assertFalse(bagValueFacade.getColumnIterator().hasNext());
		assertFalse(listValueFacade.getColumnIterator().hasNext());
		assertFalse(mapValueFacade.getColumnIterator().hasNext());
		assertFalse(primitiveArrayValueFacade.getColumnIterator().hasNext());
		assertFalse(setValueFacade.getColumnIterator().hasNext());
		assertFalse(identifierBagValueFacade.getColumnIterator().hasNext());
		// one to many value columns are the ones of the associated class
		RootClass pc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		BasicValue kv = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		kv.setTable(new Table(""));
		pc.setIdentifier(kv);
		((OneToMany)oneToManyValueTarget).setAssociatedClass(pc);
		assertFalse(oneToManyValueFacade.getColumnIterator().hasNext());
		kv.addColumn(columnTarget);
		columnIterator = oneToManyValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
		// simple value case
		((SimpleValue)simpleValueTarget).setTable(new Table(""));
		assertFalse(simpleValueFacade.getColumnIterator().hasNext());
		((SimpleValue)simpleValueTarget).addColumn(columnTarget);
		columnIterator = simpleValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
		// component value case
		assertFalse(componentValueFacade.getColumnIterator().hasNext());
		Property p = new Property();
		p.setValue(kv);
		((Component)componentValueTarget).addProperty(p);
		columnIterator = componentValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
		// many to one value
		assertFalse(manyToOneValueFacade.getColumnIterator().hasNext());
		((ManyToOne)manyToOneValueTarget).addColumn(columnTarget);
		columnIterator = manyToOneValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
		// one to one value
		assertFalse(oneToOneValueFacade.getColumnIterator().hasNext());
		((OneToOne)oneToOneValueTarget).addColumn(columnTarget);
		columnIterator = oneToOneValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
		// dependant value case
		((DependantValue)dependantValueTarget).setTable(new Table(""));
		assertFalse(dependantValueFacade.getColumnIterator().hasNext());
		((DependantValue)dependantValueTarget).addColumn(columnTarget);
		columnIterator = dependantValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
		// any value case
		((Any)anyValueTarget).setTable(new Table(""));
		assertFalse(anyValueFacade.getColumnIterator().hasNext());
		((Any)anyValueTarget).addColumn(columnTarget);
		columnIterator = anyValueFacade.getColumnIterator();
		columnFacade = columnIterator.next();
		assertFalse(columnIterator.hasNext());
		assertSame(((IFacade)columnFacade).getTarget(), columnTarget);
	}
	
	@Test
	public void testIsTypeSpecified() {
		try {
			arrayValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			bagValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			listValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			mapValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			oneToManyValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			primitiveArrayValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			setValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		try {
			identifierBagValueFacade.isTypeSpecified();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isTypeSpecified()'"));
		}
		assertFalse(manyToOneValueFacade.isTypeSpecified());
		((ManyToOne)manyToOneValueTarget).setTypeName("foo");
		assertFalse(manyToOneValueFacade.isTypeSpecified());
		((ManyToOne)manyToOneValueTarget).setReferencedEntityName("foo");
		assertTrue(manyToOneValueFacade.isTypeSpecified());
		assertFalse(oneToOneValueFacade.isTypeSpecified());
		((OneToOne)oneToOneValueTarget).setTypeName("foo");
		assertFalse(oneToOneValueFacade.isTypeSpecified());
		((OneToOne)oneToOneValueTarget).setReferencedEntityName("foo");
		assertTrue(manyToOneValueFacade.isTypeSpecified());
		assertFalse(simpleValueFacade.isTypeSpecified());
		((SimpleValue)simpleValueTarget).setTypeName("foo");
		assertTrue(simpleValueFacade.isTypeSpecified());
		assertFalse(componentValueFacade.isTypeSpecified());
		((Component)componentValueTarget).setTypeName("foo");
		assertTrue(componentValueFacade.isTypeSpecified());
		assertFalse(dependantValueFacade.isTypeSpecified());
		((DependantValue)dependantValueTarget).setTypeName("foo");
		assertTrue(dependantValueFacade.isTypeSpecified());
		assertFalse(anyValueFacade.isTypeSpecified());
		((Any)anyValueTarget).setTypeName("foo");
		assertTrue(anyValueFacade.isTypeSpecified());
	}
	
	@Test
	public void testGetCollectionTable() {
		assertNull(arrayValueFacade.getCollectionTable());
		((Collection)arrayValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)arrayValueFacade.getCollectionTable()).getTarget());
		assertNull(bagValueFacade.getCollectionTable());
		((Collection)bagValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)bagValueFacade.getCollectionTable()).getTarget());
		assertNull(listValueFacade.getCollectionTable());
		((Collection)listValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)listValueFacade.getCollectionTable()).getTarget());
		assertNull(manyToOneValueFacade.getCollectionTable());
		assertNull(mapValueFacade.getCollectionTable());
		((Collection)mapValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)mapValueFacade.getCollectionTable()).getTarget());
		assertNull(oneToManyValueFacade.getCollectionTable());
		assertNull(oneToOneValueFacade.getCollectionTable());
		assertNull(primitiveArrayValueFacade.getCollectionTable());
		((Collection)primitiveArrayValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)primitiveArrayValueFacade.getCollectionTable()).getTarget());
		assertNull(setValueFacade.getCollectionTable());
		((Collection)setValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)setValueFacade.getCollectionTable()).getTarget());
		assertNull(simpleValueFacade.getCollectionTable());
		assertNull(componentValueFacade.getCollectionTable());
		assertNull(dependantValueFacade.getCollectionTable());
		assertNull(anyValueFacade.getCollectionTable());
		assertNull(identifierBagValueFacade.getCollectionTable());
		((Collection)identifierBagValueTarget).setCollectionTable(tableTarget);
		assertSame(tableTarget, ((IFacade)identifierBagValueFacade.getCollectionTable()).getTarget());
	}
	
	@Test
	public void testGetKey() {
		assertNull(arrayValueFacade.getKey());
		((Collection)arrayValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)arrayValueFacade.getKey()).getTarget()).getWrappedObject());
		assertNull(bagValueFacade.getKey());
		((Collection)bagValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)bagValueFacade.getKey()).getTarget()).getWrappedObject());
		assertNull(listValueFacade.getKey());
		((Collection)listValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)listValueFacade.getKey()).getTarget()).getWrappedObject());
		assertNull(mapValueFacade.getKey());
		((Collection)mapValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)mapValueFacade.getKey()).getTarget()).getWrappedObject());
		assertNull(primitiveArrayValueFacade.getKey());
		((Collection)primitiveArrayValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)primitiveArrayValueFacade.getKey()).getTarget()).getWrappedObject());
		assertNull(setValueFacade.getKey());
		((Collection)setValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)setValueFacade.getKey()).getTarget()).getWrappedObject());
		assertNull(identifierBagValueFacade.getKey());
		((Collection)identifierBagValueTarget).setKey((KeyValue)simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)identifierBagValueFacade.getKey()).getTarget()).getWrappedObject());
		try {
			simpleValueFacade.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			manyToOneValueFacade.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			oneToOneValueFacade.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			componentValueFacade.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			dependantValueFacade.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
		try {
			anyValueFacade.getKey();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getKey()'"));
		}
	}
	
	@Test
	public void testGetIndex() {
		assertNull(arrayValueFacade.getIndex());
		((IndexedCollection)arrayValueTarget).setIndex(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)arrayValueFacade.getIndex()).getTarget()).getWrappedObject());
		assertNull(bagValueFacade.getIndex());
		assertNull(listValueFacade.getIndex());
		((IndexedCollection)listValueTarget).setIndex(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)listValueFacade.getIndex()).getTarget()).getWrappedObject());
		assertNull(manyToOneValueFacade.getIndex());
		assertNull(mapValueFacade.getIndex());
		((IndexedCollection)mapValueTarget).setIndex(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)mapValueFacade.getIndex()).getTarget()).getWrappedObject());
		assertNull(oneToManyValueFacade.getIndex());
		assertNull(oneToOneValueFacade.getIndex());
		assertNull(primitiveArrayValueFacade.getIndex());
		((IndexedCollection)primitiveArrayValueTarget).setIndex(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)primitiveArrayValueFacade.getIndex()).getTarget()).getWrappedObject());
		assertNull(setValueFacade.getIndex());
		assertNull(simpleValueFacade.getIndex());
		assertNull(componentValueFacade.getIndex());
		assertNull(dependantValueFacade.getIndex());
		assertNull(anyValueFacade.getIndex());
		assertNull(identifierBagValueFacade.getIndex());
	}	
	
	@Test
	public void testGetElementClassName() {
		// only supported by array values
		assertNull(arrayValueFacade.getElementClassName());
		((Array)arrayValueTarget).setElementClassName("foo");
		assertEquals("foo", arrayValueFacade.getElementClassName());
		assertNull(primitiveArrayValueFacade.getElementClassName());
		((PrimitiveArray)primitiveArrayValueTarget).setElementClassName("foo");
		assertEquals("foo", primitiveArrayValueFacade.getElementClassName());
		try {
			bagValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			listValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			setValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			mapValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			simpleValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			manyToOneValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			oneToOneValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			oneToManyValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			componentValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			dependantValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			anyValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
		try {
			identifierBagValueFacade.getElementClassName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getElementClassName()'"));
		}
	}
		
	@Test
	public void testGetTypeName() {
		assertNull(arrayValueFacade.getTypeName());
		((Collection)arrayValueTarget).setTypeName("foobar");
		assertEquals("foobar", arrayValueFacade.getTypeName());
		assertNull(bagValueFacade.getTypeName());
		((Collection)bagValueTarget).setTypeName("foobar");
		assertEquals("foobar", bagValueFacade.getTypeName());
		assertNull(listValueFacade.getTypeName());
		((Collection)listValueTarget).setTypeName("foobar");
		assertEquals("foobar", listValueFacade.getTypeName());
		assertNull(manyToOneValueFacade.getTypeName());
		((SimpleValue)manyToOneValueTarget).setTypeName("foobar");
		assertEquals("foobar", manyToOneValueFacade.getTypeName());
		assertNull(mapValueFacade.getTypeName());
		((Collection)mapValueTarget).setTypeName("foobar");
		assertEquals("foobar", mapValueFacade.getTypeName());
		assertNull(oneToManyValueFacade.getTypeName());
		assertNull(oneToOneValueFacade.getTypeName());
		((SimpleValue)oneToOneValueTarget).setTypeName("foobar");
		assertEquals("foobar", oneToOneValueFacade.getTypeName());
		assertNull(primitiveArrayValueFacade.getTypeName());
		((Collection)primitiveArrayValueTarget).setTypeName("foobar");
		assertEquals("foobar", primitiveArrayValueFacade.getTypeName());
		assertNull(setValueFacade.getTypeName());
		((Collection)setValueTarget).setTypeName("foobar");
		assertEquals("foobar", setValueFacade.getTypeName());
		assertNull(simpleValueFacade.getTypeName());
		((SimpleValue)simpleValueTarget).setTypeName("foobar");
		assertEquals("foobar", simpleValueFacade.getTypeName());
		assertNull(componentValueFacade.getTypeName());
		((SimpleValue)componentValueTarget).setTypeName("foobar");
		assertEquals("foobar", componentValueFacade.getTypeName());
		assertNull(dependantValueFacade.getTypeName());
		((SimpleValue)dependantValueTarget).setTypeName("foobar");
		assertEquals("foobar", dependantValueFacade.getTypeName());
		assertNull(anyValueFacade.getTypeName());
		((SimpleValue)anyValueTarget).setTypeName("foobar");
		assertEquals("foobar", dependantValueFacade.getTypeName());
		assertNull(identifierBagValueFacade.getTypeName());
		((Collection)identifierBagValueTarget).setTypeName("foobar");
		assertEquals("foobar", identifierBagValueFacade.getTypeName());
	}
	
	@Test
	public void testIsDependantValue() {
		assertFalse(arrayValueFacade.isDependantValue());
		assertFalse(bagValueFacade.isDependantValue());
		assertFalse(listValueFacade.isDependantValue());
		assertFalse(manyToOneValueFacade.isDependantValue());
		assertFalse(mapValueFacade.isDependantValue());
		assertFalse(oneToManyValueFacade.isDependantValue());
		assertFalse(oneToOneValueFacade.isDependantValue());
		assertFalse(primitiveArrayValueFacade.isDependantValue());
		assertFalse(setValueFacade.isDependantValue());
		assertFalse(simpleValueFacade.isDependantValue());
		assertFalse(componentValueFacade.isDependantValue());
		assertTrue(dependantValueFacade.isDependantValue());
		assertFalse(anyValueFacade.isDependantValue());
		assertFalse(identifierBagValueFacade.isDependantValue());
	}
	
	@Test
	public void testIsAny() {
		assertFalse(arrayValueFacade.isAny());
		assertFalse(bagValueFacade.isAny());
		assertFalse(listValueFacade.isAny());
		assertFalse(manyToOneValueFacade.isAny());
		assertFalse(mapValueFacade.isAny());
		assertFalse(oneToManyValueFacade.isAny());
		assertFalse(oneToOneValueFacade.isAny());
		assertFalse(primitiveArrayValueFacade.isAny());
		assertFalse(setValueFacade.isAny());
		assertFalse(simpleValueFacade.isAny());
		assertFalse(componentValueFacade.isAny());
		assertFalse(dependantValueFacade.isAny());
		assertTrue(anyValueFacade.isAny());
		assertFalse(identifierBagValueFacade.isAny());
	}
	
	@Test
	public void testIsSet() {
		assertFalse(arrayValueFacade.isSet());
		assertFalse(bagValueFacade.isSet());
		assertFalse(listValueFacade.isSet());
		assertFalse(manyToOneValueFacade.isSet());
		assertFalse(mapValueFacade.isSet());
		assertFalse(oneToManyValueFacade.isSet());
		assertFalse(oneToOneValueFacade.isSet());
		assertFalse(primitiveArrayValueFacade.isSet());
		assertTrue(setValueFacade.isSet());
		assertFalse(simpleValueFacade.isSet());
		assertFalse(componentValueFacade.isSet());
		assertFalse(dependantValueFacade.isSet());
		assertFalse(anyValueFacade.isSet());
		assertFalse(identifierBagValueFacade.isSet());
	}
	
	@Test
	public void testIsPrimitiveArray() {
		assertFalse(arrayValueFacade.isPrimitiveArray());
		assertFalse(bagValueFacade.isPrimitiveArray());
		assertFalse(listValueFacade.isPrimitiveArray());
		assertFalse(manyToOneValueFacade.isPrimitiveArray());
		assertFalse(mapValueFacade.isPrimitiveArray());
		assertFalse(oneToManyValueFacade.isPrimitiveArray());
		assertFalse(oneToOneValueFacade.isPrimitiveArray());
		assertTrue(primitiveArrayValueFacade.isPrimitiveArray());
		assertFalse(setValueFacade.isPrimitiveArray());
		assertFalse(simpleValueFacade.isPrimitiveArray());
		assertFalse(componentValueFacade.isPrimitiveArray());
		assertFalse(dependantValueFacade.isPrimitiveArray());
		assertFalse(anyValueFacade.isPrimitiveArray());
		assertFalse(identifierBagValueFacade.isPrimitiveArray());
	}
		
	@Test
	public void testIsArray() {
		assertTrue(arrayValueFacade.isArray());
		assertFalse(bagValueFacade.isArray());
		assertFalse(listValueFacade.isArray());
		assertFalse(manyToOneValueFacade.isArray());
		assertFalse(mapValueFacade.isArray());
		assertFalse(oneToManyValueFacade.isArray());
		assertFalse(oneToOneValueFacade.isArray());
		assertTrue(primitiveArrayValueFacade.isArray());
		assertFalse(setValueFacade.isArray());
		assertFalse(simpleValueFacade.isArray());
		assertFalse(componentValueFacade.isArray());
		assertFalse(dependantValueFacade.isArray());
		assertFalse(anyValueFacade.isArray());
		assertFalse(identifierBagValueFacade.isArray());
	}
		
	@Test
	public void testIsIdentifierBag() {
		assertFalse(arrayValueFacade.isIdentifierBag());
		assertFalse(bagValueFacade.isIdentifierBag());
		assertFalse(listValueFacade.isIdentifierBag());
		assertFalse(manyToOneValueFacade.isIdentifierBag());
		assertFalse(mapValueFacade.isIdentifierBag());
		assertFalse(oneToManyValueFacade.isIdentifierBag());
		assertFalse(oneToOneValueFacade.isIdentifierBag());
		assertFalse(primitiveArrayValueFacade.isIdentifierBag());
		assertFalse(setValueFacade.isIdentifierBag());
		assertFalse(simpleValueFacade.isIdentifierBag());
		assertFalse(componentValueFacade.isIdentifierBag());
		assertFalse(dependantValueFacade.isIdentifierBag());
		assertFalse(anyValueFacade.isIdentifierBag());
		assertTrue(identifierBagValueFacade.isIdentifierBag());
	}
	
	@Test
	public void testIsBag() {
		assertFalse(arrayValueFacade.isBag());
		assertTrue(bagValueFacade.isBag());
		assertFalse(listValueFacade.isBag());
		assertFalse(manyToOneValueFacade.isBag());
		assertFalse(mapValueFacade.isBag());
		assertFalse(oneToManyValueFacade.isBag());
		assertFalse(oneToOneValueFacade.isBag());
		assertFalse(primitiveArrayValueFacade.isBag());
		assertFalse(setValueFacade.isBag());
		assertFalse(simpleValueFacade.isBag());
		assertFalse(componentValueFacade.isBag());
		assertFalse(dependantValueFacade.isBag());
		assertFalse(anyValueFacade.isBag());
		assertFalse(identifierBagValueFacade.isBag());
	}

	@Test
	public void testGetReferencedEntityName() {
		assertNull(manyToOneValueFacade.getReferencedEntityName());
		((ManyToOne)manyToOneValueTarget).setReferencedEntityName("foobar");
		assertEquals("foobar", manyToOneValueFacade.getReferencedEntityName());
		assertNull(oneToManyValueFacade.getReferencedEntityName());
		((OneToMany)oneToManyValueTarget).setReferencedEntityName("foobar");
		assertEquals("foobar", oneToManyValueFacade.getReferencedEntityName());
		assertNull(oneToOneValueFacade.getReferencedEntityName());
		((OneToOne)oneToOneValueTarget).setReferencedEntityName("foobar");
		assertEquals("foobar", oneToOneValueFacade.getReferencedEntityName());
		try {
			arrayValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			bagValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			listValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			mapValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			primitiveArrayValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			setValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			simpleValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			componentValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			dependantValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			anyValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
		try {
			identifierBagValueFacade.getReferencedEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getReferencedEntityName()'"));
		}
	}

	@Test
	public void testGetEntityName() {
		try {
			arrayValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			bagValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			listValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			manyToOneValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			mapValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			oneToManyValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		assertNull(oneToOneValueFacade.getEntityName());
		persistentClassFacade.setEntityName("foobar");
		oneToOneValueFacade = (IValue)GenericFacadeFactory.createFacade(
				IValue.class, 
				WrapperFactory.createOneToOneWrapper(((IFacade)persistentClassFacade).getTarget()));
		assertEquals("foobar", oneToOneValueFacade.getEntityName());
		try {
			primitiveArrayValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			setValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			simpleValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			componentValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			dependantValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			anyValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
		try {
			identifierBagValueFacade.getEntityName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getEntityName()'"));
		}
	}
	
	@Test
	public void testGetPropertyIterator() {
		// only the component values have properties
		Iterator<IProperty> propertyIterator = componentValueFacade.getPropertyIterator();
		assertFalse(propertyIterator.hasNext());
		Property propertyTarget = new Property();
		((Component)componentValueTarget).addProperty(propertyTarget);
		propertyIterator = componentValueFacade.getPropertyIterator();
		IProperty propertyFacade = propertyIterator.next();
		assertFalse(propertyIterator.hasNext());
		assertSame(propertyTarget, ((Wrapper)((IFacade)propertyFacade).getTarget()).getWrappedObject());
		// other values do not support 'getPropertyIterator()'
		try {
			arrayValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			bagValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			listValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			manyToOneValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			mapValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			oneToManyValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			oneToOneValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			primitiveArrayValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			setValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			simpleValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			dependantValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			anyValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
		try {
			identifierBagValueFacade.getPropertyIterator();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getPropertyIterator()'"));
		}
	}
	
	@Test
	public void testAddColumn() {
		IColumn columnFacade = (IColumn)GenericFacadeFactory.createFacade(
				IColumn.class, 
				WrapperFactory.createColumnWrapper("foo"));
		Column columnTarget = (Column)((IFacade)columnFacade).getTarget();
		assertFalse(manyToOneValueTarget.getColumns().contains(columnTarget));
		manyToOneValueFacade.addColumn(columnFacade);
		assertTrue(manyToOneValueTarget.getColumns().contains(columnTarget));
		assertFalse(oneToOneValueTarget.getColumns().contains(columnTarget));
		oneToOneValueFacade.addColumn(columnFacade);
		assertTrue(oneToOneValueTarget.getColumns().contains(columnTarget));
		((BasicValue)simpleValueTarget).setTable(tableTarget);
		assertFalse(simpleValueTarget.getColumns().contains(columnTarget));
		simpleValueFacade.addColumn(columnFacade);
		assertTrue(simpleValueTarget.getColumns().contains(columnTarget));
		assertFalse(dependantValueTarget.getColumns().contains(columnTarget));
		dependantValueFacade.addColumn(columnFacade);
		assertTrue(dependantValueTarget.getColumns().contains(columnTarget));
		assertFalse(anyValueTarget.getColumns().contains(columnTarget));
		anyValueFacade.addColumn(columnFacade);
		assertTrue(anyValueTarget.getColumns().contains(columnTarget));
		try {
			arrayValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			bagValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			listValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			mapValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			oneToManyValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			primitiveArrayValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			setValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
		try {
			componentValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("Cant add a column to a component"));
		}
		try {
			identifierBagValueFacade.addColumn(columnFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'addColumn(Column)'"));
		}
	}
	
	@Test
	public void testSetTypeParameters() {
		Properties properties = new Properties();
		assertNull(((Collection)arrayValueTarget).getTypeParameters());
		arrayValueFacade.setTypeParameters(properties);
		assertSame(((Collection)arrayValueTarget).getTypeParameters(), properties);
		assertNull(((Collection)bagValueTarget).getTypeParameters());
		bagValueFacade.setTypeParameters(properties);
		assertSame(((Collection)bagValueTarget).getTypeParameters(), properties);
		assertNull(((Collection)listValueTarget).getTypeParameters());
		listValueFacade.setTypeParameters(properties);
		assertSame(((Collection)listValueTarget).getTypeParameters(), properties);
		assertNull(((SimpleValue)manyToOneValueTarget).getTypeParameters());
		manyToOneValueFacade.setTypeParameters(properties);
		assertSame(((SimpleValue)manyToOneValueTarget).getTypeParameters(), properties);
		assertNull(((Collection)mapValueTarget).getTypeParameters());
		mapValueFacade.setTypeParameters(properties);
		assertSame(((Collection)mapValueTarget).getTypeParameters(), properties);
		assertNull(((SimpleValue)oneToOneValueTarget).getTypeParameters());
		oneToOneValueFacade.setTypeParameters(properties);
		assertSame(((SimpleValue)oneToOneValueTarget).getTypeParameters(), properties);
		assertNull(((Collection)primitiveArrayValueTarget).getTypeParameters());
		primitiveArrayValueFacade.setTypeParameters(properties);
		assertSame(((Collection)primitiveArrayValueTarget).getTypeParameters(), properties);
		assertNull(((Collection)setValueTarget).getTypeParameters());
		setValueFacade.setTypeParameters(properties);
		assertSame(((Collection)setValueTarget).getTypeParameters(), properties);
		assertNull(((SimpleValue)simpleValueTarget).getTypeParameters());
		simpleValueFacade.setTypeParameters(properties);
		assertSame(((SimpleValue)simpleValueTarget).getTypeParameters(), properties);
		assertNull(((SimpleValue)componentValueTarget).getTypeParameters());
		componentValueFacade.setTypeParameters(properties);
		assertSame(((SimpleValue)componentValueTarget).getTypeParameters(), properties);
		assertNull(((SimpleValue)dependantValueTarget).getTypeParameters());
		dependantValueFacade.setTypeParameters(properties);
		assertSame(((SimpleValue)dependantValueTarget).getTypeParameters(), properties);
		assertNull(((SimpleValue)anyValueTarget).getTypeParameters());
		anyValueFacade.setTypeParameters(properties);
		assertSame(((SimpleValue)anyValueTarget).getTypeParameters(), properties);
		assertNull(((Collection)identifierBagValueTarget).getTypeParameters());
		identifierBagValueFacade.setTypeParameters(properties);
		assertSame(((Collection)identifierBagValueTarget).getTypeParameters(), properties);
		try {
			oneToManyValueFacade.setTypeParameters(properties);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setTypeParameters(Properties)'"));
		}
	}
	
	@Test
	public void testGetForeignKeyName() {
		assertNull(manyToOneValueFacade.getForeignKeyName());
		((SimpleValue)manyToOneValueTarget).setForeignKeyName("foo");
		assertEquals("foo", manyToOneValueFacade.getForeignKeyName());
		assertNull(oneToOneValueFacade.getForeignKeyName());
		((SimpleValue)oneToOneValueTarget).setForeignKeyName("foo");
		assertEquals("foo", oneToOneValueFacade.getForeignKeyName());
		assertNull(simpleValueFacade.getForeignKeyName());
		((SimpleValue)simpleValueTarget).setForeignKeyName("foo");
		assertEquals("foo", simpleValueFacade.getForeignKeyName());
		assertNull(componentValueFacade.getForeignKeyName());
		((SimpleValue)componentValueTarget).setForeignKeyName("foo");
		assertEquals("foo", componentValueFacade.getForeignKeyName());
		assertNull(dependantValueFacade.getForeignKeyName());
		((SimpleValue)dependantValueTarget).setForeignKeyName("foo");
		assertEquals("foo", dependantValueFacade.getForeignKeyName());
		assertNull(anyValueFacade.getForeignKeyName());
		((SimpleValue)anyValueTarget).setForeignKeyName("foo");
		assertEquals("foo", anyValueFacade.getForeignKeyName());
		try {
			arrayValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			bagValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			listValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			mapValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			oneToManyValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			primitiveArrayValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			setValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
		try {
			identifierBagValueFacade.getForeignKeyName();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getForeignKeyName()'"));
		}
	}
	
	@Test
	public void testGetOwner() {
		assertSame(persistentClassTarget, ((IFacade)arrayValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)bagValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)listValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)mapValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)primitiveArrayValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)setValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)identifierBagValueFacade.getOwner()).getTarget());
		assertSame(persistentClassTarget, ((IFacade)componentValueFacade.getOwner()).getTarget());
		try {
			manyToOneValueFacade.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			oneToManyValueFacade.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			oneToOneValueFacade.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			simpleValueFacade.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			dependantValueFacade.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
		try {
			anyValueFacade.getOwner();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getOwner()'"));
		}
	}
	
	@Test
	public void testGetElement() {
		assertNull(arrayValueFacade.getElement());
		((Collection)arrayValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)arrayValueFacade.getElement()).getTarget()).getWrappedObject());
		assertNull(bagValueFacade.getElement());
		((Collection)bagValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)bagValueFacade.getElement()).getTarget()).getWrappedObject());
		assertNull(listValueFacade.getElement());
		((Collection)listValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)listValueFacade.getElement()).getTarget()).getWrappedObject());
		assertNull(manyToOneValueFacade.getElement());
		assertNull(mapValueFacade.getElement());
		((Collection)mapValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)mapValueFacade.getElement()).getTarget()).getWrappedObject());
		assertNull(oneToManyValueFacade.getElement());
		assertNull(oneToOneValueFacade.getElement());
		assertNull(primitiveArrayValueFacade.getElement());
		((Collection)primitiveArrayValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)primitiveArrayValueFacade.getElement()).getTarget()).getWrappedObject());
		assertNull(setValueFacade.getElement());
		((Collection)setValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)setValueFacade.getElement()).getTarget()).getWrappedObject());
		assertNull(simpleValueFacade.getElement());
		assertNull(componentValueFacade.getElement());
		assertNull(dependantValueFacade.getElement());
		assertNull(anyValueFacade.getElement());
		assertNull(identifierBagValueFacade.getElement());
		((Collection)identifierBagValueTarget).setElement(simpleValueTarget);
		assertSame(simpleValueTarget, ((Wrapper)((IFacade)identifierBagValueFacade.getElement()).getTarget()).getWrappedObject());
	}
	
	@Test
	public void testGetParentProperty() {
		assertNull(componentValueFacade.getParentProperty());
		((Component)componentValueTarget).setParentProperty("foo");
		assertEquals("foo", componentValueFacade.getParentProperty());
		try {
			arrayValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			bagValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			listValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			manyToOneValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			oneToOneValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			mapValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			oneToManyValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			primitiveArrayValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			setValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			simpleValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			dependantValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			anyValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
		try {
			identifierBagValueFacade.getParentProperty();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getParentProperty()'"));
		}
	}
	
	@Test
	public void testSetElementClassName() {
		assertNull(((Array)arrayValueTarget).getElementClassName());
		arrayValueFacade.setElementClassName("foo");
		assertEquals("foo", ((Array)arrayValueTarget).getElementClassName());
		assertNull(((Array)primitiveArrayValueTarget).getElementClassName());
		primitiveArrayValueFacade.setElementClassName("foo");
		assertEquals("foo", ((Array)primitiveArrayValueTarget).getElementClassName());
		try {
			bagValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			listValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			manyToOneValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			mapValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			oneToManyValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			oneToOneValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			setValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			simpleValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			componentValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			dependantValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			anyValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
		try {
			identifierBagValueFacade.setElementClassName("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setElementClassName(String)'"));
		}
	}
	
	@Test
	public void testSetKey() {
		assertNull(((Collection)arrayValueTarget).getKey());
		arrayValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)arrayValueTarget).getKey());
		assertNull(((Collection)bagValueTarget).getKey());
		bagValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)bagValueTarget).getKey());
		assertNull(((Collection)listValueTarget).getKey());
		listValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)listValueTarget).getKey());
		assertNull(((Collection)mapValueTarget).getKey());
		mapValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)mapValueTarget).getKey());
		assertNull(((Collection)primitiveArrayValueTarget).getKey());
		primitiveArrayValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)primitiveArrayValueTarget).getKey());
		assertNull(((Collection)setValueTarget).getKey());
		setValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)setValueTarget).getKey());
		assertNull(((Collection)identifierBagValueTarget).getKey());
		identifierBagValueFacade.setKey(simpleValueFacade);
		assertSame(simpleValueTarget, ((Collection)identifierBagValueTarget).getKey());
		try {
			manyToOneValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			oneToManyValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			oneToOneValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			simpleValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			componentValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			dependantValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
		try {
			anyValueFacade.setKey(simpleValueFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setKey(KeyValue)'"));
		}
	}
	
	@Test
	public void testSetFetchModeJoin() {
		assertNotEquals(FetchMode.JOIN, arrayValueTarget.getFetchMode());
		arrayValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, arrayValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, bagValueTarget.getFetchMode());
		bagValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, bagValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, listValueTarget.getFetchMode());
		listValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, listValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, manyToOneValueTarget.getFetchMode());
		manyToOneValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, manyToOneValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, oneToOneValueTarget.getFetchMode());
		oneToOneValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, oneToOneValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, mapValueTarget.getFetchMode());
		mapValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, mapValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, primitiveArrayValueTarget.getFetchMode());
		primitiveArrayValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, primitiveArrayValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, setValueTarget.getFetchMode());
		setValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, setValueTarget.getFetchMode());
		assertNotEquals(FetchMode.JOIN, identifierBagValueTarget.getFetchMode());
		identifierBagValueFacade.setFetchModeJoin();
		assertEquals(FetchMode.JOIN, identifierBagValueTarget.getFetchMode());
		try {
			oneToManyValueFacade.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			simpleValueFacade.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			componentValueFacade.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			dependantValueFacade.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
		try {
			anyValueFacade.setFetchModeJoin();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setFetchModeJoin()'"));
		}
	}
	
	@Test
	public void testIsInverse() {
		assertFalse(arrayValueFacade.isInverse());
		((Collection)arrayValueTarget).setInverse(true);
		assertTrue(arrayValueFacade.isInverse());
		assertFalse(bagValueFacade.isInverse());
		((Collection)bagValueTarget).setInverse(true);
		assertTrue(bagValueFacade.isInverse());
		assertFalse(listValueFacade.isInverse());
		((Collection)listValueTarget).setInverse(true);
		assertTrue(listValueFacade.isInverse());
		assertFalse(mapValueFacade.isInverse());
		((Collection)mapValueTarget).setInverse(true);
		assertTrue(mapValueFacade.isInverse());
		assertFalse(setValueFacade.isInverse());
		((Collection)setValueTarget).setInverse(true);
		assertTrue(setValueFacade.isInverse());
		assertFalse(identifierBagValueFacade.isInverse());
		((Collection)identifierBagValueTarget).setInverse(true);
		assertTrue(identifierBagValueFacade.isInverse());
		assertFalse(primitiveArrayValueFacade.isInverse());
		((Collection)primitiveArrayValueTarget).setInverse(true);
		assertTrue(primitiveArrayValueFacade.isInverse());
		try {
			manyToOneValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			oneToManyValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			oneToOneValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			simpleValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			componentValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			anyValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
		try {
			dependantValueFacade.isInverse();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'isInverse()'"));
		}
	}
	
	@Test
	public void testGetAssociatedClass() {
		try {
			arrayValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			bagValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			listValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			setValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			primitiveArrayValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			mapValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			identifierBagValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			manyToOneValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			oneToOneValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			simpleValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			componentValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			anyValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		try {
			dependantValueFacade.getAssociatedClass();
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'getAssociatedClass()'"));
		}
		assertNull(oneToManyValueFacade.getAssociatedClass());
		((OneToMany)oneToManyValueTarget).setAssociatedClass(persistentClassTarget);
		assertSame(persistentClassTarget, ((IFacade)oneToManyValueFacade.getAssociatedClass()).getTarget());
	}
	
	@Test
	public void testSetLazy() {
		arrayValueFacade.setLazy(true);
		assertTrue(((Fetchable)arrayValueTarget).isLazy());
		arrayValueFacade.setLazy(false);
		assertFalse(((Fetchable)arrayValueTarget).isLazy());
		bagValueFacade.setLazy(true);
		assertTrue(((Fetchable)bagValueTarget).isLazy());
		bagValueFacade.setLazy(false);
		assertFalse(((Fetchable)bagValueTarget).isLazy());
		listValueFacade.setLazy(true);
		assertTrue(((Fetchable)listValueTarget).isLazy());
		listValueFacade.setLazy(false);
		assertFalse(((Fetchable)listValueTarget).isLazy());
		primitiveArrayValueFacade.setLazy(true);
		assertTrue(((Fetchable)primitiveArrayValueTarget).isLazy());
		primitiveArrayValueFacade.setLazy(false);
		assertFalse(((Fetchable)primitiveArrayValueTarget).isLazy());
		setValueFacade.setLazy(true);
		assertTrue(((Fetchable)setValueTarget).isLazy());
		setValueFacade.setLazy(false);
		assertFalse(((Fetchable)setValueTarget).isLazy());
		mapValueFacade.setLazy(true);
		assertTrue(((Fetchable)mapValueTarget).isLazy());
		mapValueFacade.setLazy(false);
		assertFalse(((Fetchable)mapValueTarget).isLazy());
		identifierBagValueFacade.setLazy(true);
		assertTrue(((Fetchable)identifierBagValueTarget).isLazy());
		identifierBagValueFacade.setLazy(false);
		assertFalse(((Fetchable)identifierBagValueTarget).isLazy());
		anyValueFacade.setLazy(true);
		assertTrue(((Any)anyValueTarget).isLazy());
		anyValueFacade.setLazy(false);
		assertFalse(((Any)anyValueTarget).isLazy());
		manyToOneValueFacade.setLazy(true);
		assertTrue(((Fetchable)manyToOneValueTarget).isLazy());
		manyToOneValueFacade.setLazy(false);
		assertFalse(((Fetchable)manyToOneValueTarget).isLazy());
		oneToOneValueFacade.setLazy(true);
		assertTrue(((Fetchable)oneToOneValueTarget).isLazy());
		oneToOneValueFacade.setLazy(false);
		assertFalse(((Fetchable)oneToOneValueTarget).isLazy());
		try {
			simpleValueFacade.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
		try {
			componentValueFacade.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
		try {
			dependantValueFacade.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
		try {
			oneToManyValueFacade.setLazy(true);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setLazy(boolean)'"));
		}
	}
	
	@Test
	public void testSetRole() {
		assertNull(((Collection)arrayValueTarget).getRole());
		arrayValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)arrayValueTarget).getRole());
		assertNull(((Collection)bagValueTarget).getRole());
		bagValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)bagValueTarget).getRole());
		assertNull(((Collection)listValueTarget).getRole());
		listValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)listValueTarget).getRole());
		assertNull(((Collection)mapValueTarget).getRole());
		mapValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)mapValueTarget).getRole());
		assertNull(((Collection)primitiveArrayValueTarget).getRole());
		primitiveArrayValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)primitiveArrayValueTarget).getRole());
		assertNull(((Collection)setValueTarget).getRole());
		setValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)setValueTarget).getRole());
		assertNull(((Collection)identifierBagValueTarget).getRole());
		identifierBagValueFacade.setRole("foo");
		assertEquals("foo", ((Collection)identifierBagValueTarget).getRole());
		try {
			manyToOneValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			oneToManyValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			oneToOneValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			simpleValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			componentValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			dependantValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
		try {
			anyValueFacade.setRole("foo");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setRole(String)'"));
		}
	}
	
	@Test
	public void testSetReferencedEntityName() {
		assertNull(((ManyToOne)manyToOneValueTarget).getReferencedEntityName());
		manyToOneValueFacade.setReferencedEntityName("foobar");
		assertEquals("foobar", ((ManyToOne)manyToOneValueTarget).getReferencedEntityName());
		assertNull(((OneToMany)oneToManyValueTarget).getReferencedEntityName());
		oneToManyValueFacade.setReferencedEntityName("foobar");
		assertEquals("foobar", ((OneToMany)oneToManyValueTarget).getReferencedEntityName());
		assertNull(((OneToOne)oneToOneValueTarget).getReferencedEntityName());
		oneToOneValueFacade.setReferencedEntityName("foobar");
		assertEquals("foobar", ((OneToOne)oneToOneValueTarget).getReferencedEntityName());
		try {
			arrayValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			bagValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			listValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			mapValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			primitiveArrayValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			setValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			simpleValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			componentValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			dependantValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			anyValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
		try {
			identifierBagValueFacade.setReferencedEntityName("foobar");
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setReferencedEntityName(String)'"));
		}
	}
	
	@Test
	public void testSetAssociatedClass() {
		try {
			arrayValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			bagValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			listValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			setValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			primitiveArrayValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			mapValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			identifierBagValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			manyToOneValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			oneToOneValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			simpleValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			componentValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			anyValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		try {
			dependantValueFacade.setAssociatedClass(persistentClassFacade);
			fail();
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().contains("does not support 'setAssociatedClass(PersistentClass)'"));
		}
		assertNull(((OneToMany)oneToManyValueTarget).getAssociatedClass());
		oneToManyValueFacade.setAssociatedClass(persistentClassFacade);
		assertSame(persistentClassTarget, ((OneToMany)oneToManyValueTarget).getAssociatedClass());
	}
	
}
