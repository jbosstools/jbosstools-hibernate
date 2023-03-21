package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IValueTest {
	
	private IValue arrayValueFacade = null;
	private Object arrayValueTarget = null;
	private IValue bagValueFacade = null;
	private Object bagValueTarget = null;
	private IValue listValueFacade = null;
	private Object listValueTarget = null;
	private IValue manyToOneValueFacade = null;
	private Object manyToOneValueTarget = null;
	private IValue mapValueFacade = null;
	private Object mapValueTarget = null;
	private IValue oneToManyValueFacade = null;
	private Object oneToManyValueTarget = null;
	private IValue oneToOneValueFacade = null;
	private Object oneToOneValueTarget = null;
	private IValue primitiveArrayValueFacade = null;
	private Object primitiveArrayValueTarget = null;
	private IValue setValueFacade = null;
	private Object setValueTarget = null;
	private IValue simpleValueFacade = null;
	private Object simpleValueTarget = null;
	
	private IPersistentClass persistentClassFacade = null;
	private ITable tableFacade = null;
	
	@BeforeEach 
	public void beforeEach() {
		persistentClassFacade = NewFacadeFactory.INSTANCE.createRootClass();
		tableFacade = NewFacadeFactory.INSTANCE.createTable("foo");
		arrayValueFacade = NewFacadeFactory.INSTANCE.createArray(persistentClassFacade);
		arrayValueTarget = ((IFacade)arrayValueFacade).getTarget();
		bagValueFacade = NewFacadeFactory.INSTANCE.createBag(persistentClassFacade);
		bagValueTarget = ((IFacade)bagValueFacade).getTarget();
		listValueFacade = NewFacadeFactory.INSTANCE.createList(persistentClassFacade);
		listValueTarget = ((IFacade)listValueFacade).getTarget();
		manyToOneValueFacade = NewFacadeFactory.INSTANCE.createManyToOne(tableFacade);
		manyToOneValueTarget = ((IFacade)manyToOneValueFacade).getTarget();
		mapValueFacade = NewFacadeFactory.INSTANCE.createMap(persistentClassFacade);
		mapValueTarget = ((IFacade)mapValueFacade).getTarget();
		oneToManyValueFacade = NewFacadeFactory.INSTANCE.createOneToMany(persistentClassFacade);
		oneToManyValueTarget = ((IFacade)oneToManyValueFacade).getTarget();
		oneToOneValueFacade = NewFacadeFactory.INSTANCE.createOneToOne(persistentClassFacade);
		oneToOneValueTarget = ((IFacade)oneToOneValueFacade).getTarget();
		primitiveArrayValueFacade = NewFacadeFactory.INSTANCE.createPrimitiveArray(persistentClassFacade);
		primitiveArrayValueTarget = ((IFacade)primitiveArrayValueFacade).getTarget();
		setValueFacade = NewFacadeFactory.INSTANCE.createSet(persistentClassFacade);
		setValueTarget = ((IFacade)setValueFacade).getTarget();
		simpleValueFacade = NewFacadeFactory.INSTANCE.createSimpleValue();
		simpleValueTarget = ((IFacade)simpleValueFacade).getTarget();
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
	}

}
