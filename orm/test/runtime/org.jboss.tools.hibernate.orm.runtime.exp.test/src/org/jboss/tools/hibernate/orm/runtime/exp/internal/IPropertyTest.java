package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.mapping.Property;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IPropertyTest {
	
	private IProperty propertyFacade = null;
	private Property propertyTarget = null;
	
	@BeforeEach
	public void beforeEach() {
		propertyFacade = NewFacadeFactory.INSTANCE.createProperty();
		propertyTarget = (Property)((IFacade)propertyFacade).getTarget();
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(propertyFacade);
		assertNotNull(propertyTarget);
	}

}
