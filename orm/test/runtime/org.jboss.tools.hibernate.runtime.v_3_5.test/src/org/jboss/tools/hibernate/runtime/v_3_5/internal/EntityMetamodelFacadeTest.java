package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.hibernate.EntityMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EntityMetamodelFacadeTest {

	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	private static final Object OBJECT = new Object();
	private static final Integer INDEX = Integer.MAX_VALUE;
	
	private IEntityMetamodel entityMetamodelFacade = null; 
	private EntityMetamodel entityMetamodel = null;
	
	private String methodName = null;
	private Object[] arguments = null;
	
	@SuppressWarnings("serial")
	@Before
	public void setUp() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		SessionFactoryImplementor sfi = new SessionFactoryImpl(configuration, null, configuration.buildSettings(), null, null);
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
		entityMetamodel = new EntityMetamodel(rc, sfi) {
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
		entityMetamodelFacade = new EntityMetamodelFacadeImpl(FACADE_FACTORY, entityMetamodel);
	}
	
	@Test
	public void testGetTuplizerPropertyValue() {
		Assert.assertSame(OBJECT, entityMetamodelFacade.getTuplizerPropertyValue(OBJECT, Integer.MAX_VALUE));
		Assert.assertEquals("getPropertyValue", methodName);
		Assert.assertArrayEquals(new Object[] { OBJECT,  Integer.MAX_VALUE }, arguments);
	}
	
	@Test
	public void testGetPropertyIndexOrNull() {
		Assert.assertSame(INDEX, entityMetamodelFacade.getPropertyIndexOrNull("foobar"));
		Assert.assertEquals("getPropertyIndexOrNull", methodName);
		Assert.assertArrayEquals(arguments, new Object[] { "foobar" });
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			methodName = method.getName();
			arguments = args;
			return OBJECT;
		}
	}
	
}
