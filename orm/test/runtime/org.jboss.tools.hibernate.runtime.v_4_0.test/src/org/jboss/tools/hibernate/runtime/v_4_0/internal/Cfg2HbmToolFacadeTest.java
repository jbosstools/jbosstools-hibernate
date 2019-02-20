package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmToolFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractPersistentClassFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class Cfg2HbmToolFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();

	private ICfg2HbmTool cfg2HbmToolFacade = null; 
	private Cfg2HbmTool cfg2HbmTool = null;
	
	@Before
	public void setUp() {
		cfg2HbmTool = new Cfg2HbmTool();
		cfg2HbmToolFacade = new AbstractCfg2HbmToolFacade(FACADE_FACTORY, cfg2HbmTool) {};
	}

	@Test
	public void testGetPersistentClassTag() {
		PersistentClass target = new RootClass();
		IPersistentClass persistentClass = new AbstractPersistentClassFacade(FACADE_FACTORY, target) {};
		Assert.assertEquals("class", cfg2HbmToolFacade.getTag(persistentClass));
	}
	
	public void testGetPropertyTag() throws Exception {
		RootClass rc = new RootClass();
		Property p = new Property();
		Mappings m = (Mappings)Proxy.newProxyInstance(
				FACADE_FACTORY.getClassLoader(), 
				new Class[] { Mappings.class }, 
				new TestInvocationHandler());
		SimpleValue sv = new SimpleValue(m);
		sv.setTypeName("foobar");
		p.setValue(sv);
		p.setPersistentClass(rc);
		rc.setVersion(p);
		IProperty property = new AbstractPropertyFacade(FACADE_FACTORY, p) {};
		Assert.assertEquals("version", cfg2HbmToolFacade.getTag(property));
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}
	}

}
