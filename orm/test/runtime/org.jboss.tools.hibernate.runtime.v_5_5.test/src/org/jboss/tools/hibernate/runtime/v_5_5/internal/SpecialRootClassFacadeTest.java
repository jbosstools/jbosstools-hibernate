package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.AbstractSpecialRootClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.DummyMetadataBuildingContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpecialRootClassFacadeTest {

	private static IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private SpecialRootClassFacadeImpl specialRootClassFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		PersistentClass persistentClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Property propertyTarget = new Property();
		propertyTarget.setPersistentClass(persistentClassTarget);
		specialRootClassFacade = new SpecialRootClassFacadeImpl(
				FACADE_FACTORY, 
				FACADE_FACTORY.createProperty(propertyTarget));
	}
	
	@Test
	public void testConstruction() throws Exception {
		PersistentClass persistentClassTarget = new RootClass(DummyMetadataBuildingContext.INSTANCE);
		Property propertyTarget = new Property();
		Component componentTarget = new Component(DummyMetadataBuildingContext.INSTANCE, persistentClassTarget);
		componentTarget.setOwner(persistentClassTarget);
		componentTarget.setParentProperty("fooBar");
		propertyTarget.setValue(componentTarget);
		propertyTarget.setPersistentClass(persistentClassTarget);
		IProperty propertyFacade = FACADE_FACTORY.createProperty(propertyTarget);
		specialRootClassFacade = new SpecialRootClassFacadeImpl(FACADE_FACTORY, propertyFacade);
		Object specialRootClassTarget = ((IFacade)specialRootClassFacade).getTarget();
		assertNotSame(propertyFacade, specialRootClassTarget);
		assertTrue(specialRootClassTarget instanceof RootClass);
		assertNotSame(specialRootClassTarget, persistentClassTarget);
		Field propertyField = AbstractSpecialRootClassFacade.class.getDeclaredField("property");
		propertyField.setAccessible(true);
		assertSame(propertyField.get(specialRootClassFacade), propertyFacade);
	}
	
}
