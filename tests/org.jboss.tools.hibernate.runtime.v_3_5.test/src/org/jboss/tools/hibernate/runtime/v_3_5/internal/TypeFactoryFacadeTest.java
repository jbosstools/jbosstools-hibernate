package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.junit.Assert;
import org.junit.Test;

public class TypeFactoryFacadeTest {
	
	private ITypeFactory typeFactoryFacade = new FacadeFactoryImpl().createTypeFactory();
	
	@SuppressWarnings("deprecation")
	@Test
	public void testgetBooleanType() {
		Type typeTarget = Hibernate.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testgetByteType() {
		Type typeTarget = Hibernate.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testgetBigIntegerType() {
		Type typeTarget = Hibernate.BIG_INTEGER;
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testgetShortType() {
		Type typeTarget = Hibernate.SHORT;
		IType typeFacade = typeFactoryFacade.getShortType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
}
