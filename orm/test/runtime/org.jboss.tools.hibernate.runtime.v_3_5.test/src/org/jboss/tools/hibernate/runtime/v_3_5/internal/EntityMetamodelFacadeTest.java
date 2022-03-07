package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.EntityMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class EntityMetamodelFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final Object OBJECT = new Object();
	private static final Integer INDEX = Integer.MAX_VALUE;
	
	private IEntityMetamodel entityMetamodelFacade = null; 
	private EntityMetamodel entityMetamodel = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		entityMetamodel = createFoobarModel();
		entityMetamodelFacade = new EntityMetamodelFacadeImpl(FACADE_FACTORY, entityMetamodel);
	}
	
	@Test
	public void testGetTuplizerPropertyValue() {
		assertSame(OBJECT, entityMetamodelFacade.getTuplizerPropertyValue(OBJECT, Integer.MAX_VALUE));
		assertEquals("getPropertyValue", methodName);
		assertArrayEquals(new Object[] { OBJECT,  Integer.MAX_VALUE }, arguments);
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		assertSame(INDEX, entityMetamodelFacade.getPropertyIndexOrNull("foobar"));
		assertEquals("getPropertyIndexOrNull", methodName);
		assertArrayEquals(arguments, new Object[] { "foobar" });
	}
	
	@SuppressWarnings("serial")
	private EntityMetamodel createFoobarModel() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", TestDialect.class.getName());
		SessionFactoryImplementor sfi = (SessionFactoryImplementor)configuration.buildSessionFactory();
		RootClass rc = new RootClass();
		Table t = new Table("foobar");
		rc.setTable(t);
		Column c = new Column("foo");
		t.addColumn(c);
		ArrayList<Column> keyList = new ArrayList<>();
		keyList.add(c);
		t.createUniqueKey(keyList);
		SimpleValue sv = new SimpleValue();
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		sv.addColumn(c);
		rc.setEntityName("foobar");
		rc.setIdentifier(sv);
		return entityMetamodel = new EntityMetamodel(rc, sfi) {
			@Override public EntityTuplizer getTuplizer(EntityMode entityMode) {
				return (EntityTuplizer)Proxy.newProxyInstance(
						FACADE_FACTORY.getClassLoader(), 
						new Class[] { EntityTuplizer.class }, 
						new TestInvocationHandler());
			}
			@Override public Integer getPropertyIndexOrNull(String id) {
				methodName = "getPropertyIndexOrNull";
				arguments = new Object[] { id };
				return INDEX;
			}
			
		};
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return OBJECT;
		}
	}
	
	public static class TestDialect extends Dialect {}
	
}
