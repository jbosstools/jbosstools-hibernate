package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.orm.jbt.type.TypeFactory;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ITypeFactoryTest {
	
	private ITypeFactory typeFactoryFacade = null;
	
	@BeforeEach
	public void beforeEach() {
		typeFactoryFacade = (ITypeFactory)GenericFacadeFactory.createFacade(
				ITypeFactory.class, 
				WrapperFactory.createTypeFactoryWrapper());
	}
	
	@Test
	public void testConstruction() {
		assertNotNull(typeFactoryFacade);
		assertSame(TypeFactory.INSTANCE, ((IFacade)typeFactoryFacade).getTarget());
	}
	
}
