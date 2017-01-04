package org.jboss.tools.hibernate.runtime.v_4_0.internal;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.junit.Assert;
import org.junit.Test;

public class TypeFactoryFacadeTest {
	
	private ITypeFactory typeFactoryFacade = new FacadeFactoryImpl().createTypeFactory();
	
	@Test
	public void testgetBooleanType() {
		Type typeTarget = StandardBasicTypes.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testgetByteType() {
		Type typeTarget = StandardBasicTypes.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
}
