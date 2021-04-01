package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.internal.export.hbm.Cfg2HbmTool;
import org.jboss.tools.hibernate.runtime.common.AbstractCfg2HbmToolFacade;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Cfg2HbmToolFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ICfg2HbmTool cfg2HbmToolFacade = null;
	private Cfg2HbmTool cfg2HbmToolTarget = null;
	
	@BeforeEach
	public void before() {
		cfg2HbmToolTarget = new Cfg2HbmTool();
		cfg2HbmToolFacade = new AbstractCfg2HbmToolFacade(FACADE_FACTORY, cfg2HbmToolTarget) {};
	}
	
	@Test
	public void testGetTagPersistentClass() {
		PersistentClass target = new RootClass(null);
		IPersistentClass persistentClass = FACADE_FACTORY.createPersistentClass(target);
		assertEquals("class", cfg2HbmToolFacade.getTag(persistentClass));
	}

	public void testGetTagProperty() throws Exception {
		RootClass rc = new RootClass(null);
		Property p = new Property();
		BasicValue basicValue = new BasicValue(createMetadaBuildingContext());
		basicValue.setTypeName("foobar");
		p.setValue(basicValue);
		p.setPersistentClass(rc);
		rc.setVersion(p);
		IProperty property = new AbstractPropertyFacade(FACADE_FACTORY, p) {};
		assertEquals("version", cfg2HbmToolFacade.getTag(property));
	}
	
	private MetadataBuildingContext createMetadaBuildingContext() {
		return (MetadataBuildingContext)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { MetadataBuildingContext.class }, 
				new InvocationHandler() {			
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
		});
	}
	
}
