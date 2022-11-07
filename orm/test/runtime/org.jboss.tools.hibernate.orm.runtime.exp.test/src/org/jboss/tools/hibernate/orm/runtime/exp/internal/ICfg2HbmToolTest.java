package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractPropertyFacade;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ICfg2HbmToolTest {

	private static final NewFacadeFactory FACADE_FACTORY = new NewFacadeFactory();

	private ICfg2HbmTool facade = null;
	
	@BeforeEach
	public void beforeEach() {
		facade = FACADE_FACTORY.createCfg2HbmTool();
	}

	@Test
	public void testGetTagPersistentClass() {
		PersistentClass target = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		IPersistentClass persistentClass = FACADE_FACTORY.createPersistentClass(target);
		assertEquals("class", facade.getTag(persistentClass));
	}

	@Test
	public void testGetTagProperty() throws Exception {
		RootClass rc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Property p = new Property();
		BasicValue basicValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		basicValue.setTypeName("foobar");
		p.setValue(basicValue);
		p.setPersistentClass(rc);
		rc.setVersion(p);
		IProperty property = new AbstractPropertyFacade(FACADE_FACTORY, p) {};
		assertEquals("version", facade.getTag(property));
	}
	
}
