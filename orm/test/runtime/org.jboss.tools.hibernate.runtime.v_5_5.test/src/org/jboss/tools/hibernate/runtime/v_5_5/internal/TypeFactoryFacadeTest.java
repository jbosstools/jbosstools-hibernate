package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.junit.jupiter.api.Test;

public class TypeFactoryFacadeTest {

	private ITypeFactory typeFactoryFacade = new FacadeFactoryImpl().createTypeFactory();
	
	@Test
	public void testGetBooleanType() {
		Type typeTarget = StandardBasicTypes.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetByteType() {
		Type typeTarget = StandardBasicTypes.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
}
