package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.util.Iterator;

import org.hibernate.cfg.Configuration;
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
import org.jboss.tools.hibernate.runtime.spi.IValueVisitor;
import org.junit.Assert;
import org.junit.Test;

public class ValueFacadeTest {
	
	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private Value valueTarget = null;
	private IValue valueFacade = null;
	
	@Test
	public void testIsSimpleValue() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isSimpleValue());
		valueTarget = new Set(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isSimpleValue());
	}

	@Test
	public void testIsCollection() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isCollection());
		valueTarget = new Set(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isCollection());
	}

	@Test
	public void testGetCollectionElement() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IValue collectionElement = valueFacade.getCollectionElement();
		Assert.assertNull(collectionElement);
		Set set = new Set(null, null);
		set.setElement(valueTarget);
		valueFacade = FACADE_FACTORY.createValue(set);
		collectionElement = valueFacade.getCollectionElement();
		Assert.assertNotNull(collectionElement);
		Assert.assertSame(valueTarget, ((IFacade)collectionElement).getTarget());
	}

	@Test 
	public void testIsOneToMany() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isOneToMany());
		OneToMany oneToMany = new OneToMany(null, null);
		valueFacade = FACADE_FACTORY.createValue(oneToMany);
		Assert.assertTrue(valueFacade.isOneToMany());
	}

	@Test 
	public void testIsManyToOne() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isManyToOne());
		ManyToOne manyToOne = new ManyToOne(null, null);
		valueFacade = FACADE_FACTORY.createValue(manyToOne);
		Assert.assertTrue(valueFacade.isManyToOne());
	}

	@Test 
	public void testIsOneToOne() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isOneToOne());
		OneToOne oneToOne = new OneToOne(null, null, new RootClass());
		valueFacade = FACADE_FACTORY.createValue(oneToOne);
		Assert.assertTrue(valueFacade.isOneToOne());
	}

	@Test
	public void testIsMap() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isMap());
		Map map = new Map(null, null);
		valueFacade = FACADE_FACTORY.createValue(map);
		Assert.assertTrue(valueFacade.isMap());
	}

	@Test
	public void testIsComponent() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isComponent());
		Component component = new Component(null, new RootClass());
		valueFacade = FACADE_FACTORY.createValue(component);
		Assert.assertTrue(valueFacade.isComponent());
	}

	@Test
	public void testIsEmbedded() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.isEmbedded());
		Component component = new Component(null, new RootClass());
		valueFacade = FACADE_FACTORY.createValue(component);
		component.setEmbedded(true);
		Assert.assertTrue(valueFacade.isEmbedded());
		component.setEmbedded(false);
		Assert.assertFalse(valueFacade.isEmbedded());
	}

	@Test
	public void testIsToOne() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isToOne());
		ToOne toOne = new OneToOne(null, null, new RootClass());
		valueFacade = FACADE_FACTORY.createValue(toOne);
		Assert.assertTrue(valueFacade.isToOne());
	}

	@Test
	public void testAccept() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		TestValueVisitor visitor = new TestValueVisitor();
		Assert.assertFalse(visitor.visited);
		Object object = valueFacade.accept(visitor);
		Assert.assertSame(visitor, object);
		Assert.assertTrue(visitor.visited);
	}
	
	@Test 
	public void testGetTable() {
		Table tableTarget = new Table();
		valueTarget = new SimpleValue(null, tableTarget);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		ITable tableFacade = valueFacade.getTable();
		Assert.assertSame(tableTarget, ((IFacade)tableFacade).getTarget());
	}
	
	@Test
	public void testGetType() {
		SimpleValue valueTarget = new SimpleValue(
				new Configuration().createMappings());
		valueTarget.setTypeName("java.lang.Integer");
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		IType typeFacade = valueFacade.getType();
		Assert.assertEquals(
				"org.hibernate.type.IntegerType", 
				((IFacade)typeFacade).getTarget().getClass().getName());
	}
	
	@Test
	public void testSetElement() {
		SimpleValue elementTarget = new SimpleValue(null);
		IValue elementFacade = FACADE_FACTORY.createValue(elementTarget);
		Set valueTarget = new Set(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getElement());
		valueFacade.setElement(elementFacade);
		Assert.assertSame(elementTarget, valueTarget.getElement());
	}
	
	@Test
	public void testSetCollectionTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		Collection valueTarget = new Set(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getCollectionTable());
		valueFacade.setCollectionTable(tableFacade);
		Assert.assertSame(tableTarget, valueTarget.getCollectionTable());
	}
	
	@Test
	public void testSetTable() {
		Table tableTarget = new Table();
		ITable tableFacade = FACADE_FACTORY.createTable(tableTarget);
		SimpleValue valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getTable());
		valueFacade.setTable(tableFacade);
		Assert.assertSame(tableTarget, valueTarget.getTable());
	}
	
	@Test
	public void testIsList() {
		valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isList());
		valueTarget = new List(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertTrue(valueFacade.isList());
	}
	
	@Test
	public void testSetIndex() {
		List valueTarget = new List(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getIndex());
		SimpleValue indexTarget = new SimpleValue(null);
		IValue indexFacade = FACADE_FACTORY.createValue(indexTarget);
		valueFacade.setIndex(indexFacade);
		Assert.assertSame(indexTarget, valueTarget.getIndex());
	}
	
	@Test
	public void testSetTypeName() {
		SimpleValue valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueTarget.getTypeName());
		valueFacade.setTypeName("java.lang.Integer");
		Assert.assertEquals("java.lang.Integer", valueTarget.getTypeName());
	}
	
	@Test
	public void testGetComponentClassName() {
		Component valueTarget = new Component(null, new RootClass());
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getComponentClassName());
		valueTarget.setComponentClassName("org.foo.Bar");
		Assert.assertEquals("org.foo.Bar", valueFacade.getComponentClassName());
	}
	
	@Test
	public void testGetColumnIterator() {
		SimpleValue valueTarget = new SimpleValue(null);
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
		SimpleValue valueTarget = new SimpleValue(null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertFalse(valueFacade.isTypeSpecified());
		valueTarget.setTypeName("org.foo.Bar");
		Assert.assertTrue(valueFacade.isTypeSpecified());
	}
	
	@Test
	public void testGetCollectionTable() {
		Table tableTarget = new Table();
		Collection valueTarget = new Set(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getCollectionTable());
		valueTarget.setCollectionTable(tableTarget);
		Assert.assertSame(
				tableTarget, 
				((IFacade)valueFacade.getCollectionTable()).getTarget());
	}
	
	@Test
	public void testGetKey() {
		Map valueTarget = new Map(null, null);
		valueFacade = FACADE_FACTORY.createValue(valueTarget);
		Assert.assertNull(valueFacade.getKey());
		KeyValue keyValue = new SimpleValue(null);
		valueTarget.setKey(keyValue);
		Assert.assertSame(keyValue, ((IFacade)valueFacade.getKey()).getTarget());
	}
	
	private class TestValueVisitor implements IValueVisitor {
		boolean visited = false;
		@Override
		public Object accept(IValue value) {
			visited = true;
			return this;
		}		
	}

}
