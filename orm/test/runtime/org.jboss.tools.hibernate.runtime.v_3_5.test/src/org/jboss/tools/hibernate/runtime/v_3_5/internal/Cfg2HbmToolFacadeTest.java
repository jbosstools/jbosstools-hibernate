package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
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
	private Cfg2HbmTool cfg2HbmTool = null;
	
	@BeforeEach
	public void beforeEach() {
		cfg2HbmTool = new Cfg2HbmTool();
		cfg2HbmToolFacade = new AbstractCfg2HbmToolFacade(FACADE_FACTORY, cfg2HbmTool) {};
	}

	@Test
	public void testGetTagPersistentClass() {
		PersistentClass target = new RootClass();
		IPersistentClass persistentClass = FACADE_FACTORY.createPersistentClass(target);
		assertEquals("class", cfg2HbmToolFacade.getTag(persistentClass));
	}

	@Test
	public void testGetTagProperty() throws Exception {
		RootClass rc = new RootClass();
		Property p = new Property();
		Table t = new Table();
		SimpleValue sv = new SimpleValue(t);
		sv.setTypeName("foobar");
		p.setValue(sv);
		p.setPersistentClass(rc);
		rc.setVersion(p);
		IProperty property = new AbstractPropertyFacade(FACADE_FACTORY, p) {};
		assertEquals("version", cfg2HbmToolFacade.getTag(property));
	}
	
}
