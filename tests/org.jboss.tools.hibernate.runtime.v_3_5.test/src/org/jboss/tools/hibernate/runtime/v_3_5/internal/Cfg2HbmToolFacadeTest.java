package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
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

	private ICfg2HbmTool cfg2HbmTool = null; 
	
	@Before
	public void setUp() {
		cfg2HbmTool = new AbstractCfg2HbmToolFacade(FACADE_FACTORY) {};
	}

	@Test
	public void testGetPersistentClassTag() {
		PersistentClass target = new RootClass();
		IPersistentClass persistentClass = new AbstractPersistentClassFacade(FACADE_FACTORY, target) {};
		Assert.assertEquals("class", cfg2HbmTool.getTag(persistentClass));
	}
	
	public void testGetPropertyTag() {
		RootClass rc = new RootClass();
		Property p = new Property();
		SimpleValue sv = new SimpleValue();
		sv.setTypeName("foobar");
		p.setValue(sv);
		p.setPersistentClass(rc);
		rc.setVersion(p);
		IProperty property = new AbstractPropertyFacade(FACADE_FACTORY, p) {};
		Assert.assertEquals("version", cfg2HbmTool.getTag(property));
	}

}
