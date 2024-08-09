package org.jboss.tools.hibernate.orm.runtime.v_6_6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.Wrapper;
import org.hibernate.tool.orm.jbt.internal.util.DummyMetadataBuildingContext;
import org.jboss.tools.hibernate.orm.runtime.common.GenericFacadeFactory;
import org.jboss.tools.hibernate.orm.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ICfg2HbmToolTest {

	private ICfg2HbmTool facade = null;
	
	@BeforeEach
	public void beforeEach() {
		facade = (ICfg2HbmTool)GenericFacadeFactory.createFacade(
				ICfg2HbmTool.class,
				WrapperFactory.createCfg2HbmWrapper());;
	}

	@Test
	public void testGetTagPersistentClass() {
		IPersistentClass persistentClassFacade = (IPersistentClass)GenericFacadeFactory.createFacade(
				IPersistentClass.class, 
				WrapperFactory.createRootClassWrapper());
		assertEquals("class", facade.getTag(persistentClassFacade));
	}

	@Test
	public void testGetTagProperty() throws Exception {
		IProperty propertyFacade = (IProperty)GenericFacadeFactory.createFacade(
				IProperty.class, 
				WrapperFactory.createPropertyWrapper());
		Wrapper wrapper = (Wrapper)((IFacade)propertyFacade).getTarget();
		Property propertyTarget = (Property)wrapper.getWrappedObject();
		RootClass rc = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		BasicValue basicValue = new BasicValue(DummyMetadataBuildingContext.INSTANCE);
		basicValue.setTypeName("foobar");
		propertyTarget.setValue(basicValue);
		propertyTarget.setPersistentClass(rc);
		rc.setVersion(propertyTarget);
		assertEquals("version", facade.getTag(propertyFacade));
	}
	
}
