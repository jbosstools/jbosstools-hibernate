package org.jboss.tools.hibernate.orm.runtime.v_7_0;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.tool.orm.jbt.api.factory.WrapperFactory;
import org.hibernate.tool.orm.jbt.api.wrp.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.PropertyWrapper;
import org.hibernate.tool.orm.jbt.api.wrp.ValueWrapper;
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
		PropertyWrapper propertyWrapper = (PropertyWrapper)((IFacade)propertyFacade).getTarget();
		PersistentClassWrapper rootClassWrapper = (PersistentClassWrapper)WrapperFactory.createRootClassWrapper();
		RootClass rc = (RootClass)rootClassWrapper.getWrappedObject();
		Property propertyTarget = (Property)propertyWrapper.getWrappedObject();
		ValueWrapper valueWrapper = (ValueWrapper)WrapperFactory.createSimpleValueWrapper();
		valueWrapper.setTypeName("foobar");
		propertyWrapper.setValue(valueWrapper);
		propertyWrapper.setPersistentClass(rootClassWrapper);
		rc.setVersion(propertyTarget);
		assertEquals("version", facade.getTag(propertyFacade));
	}
	
}
