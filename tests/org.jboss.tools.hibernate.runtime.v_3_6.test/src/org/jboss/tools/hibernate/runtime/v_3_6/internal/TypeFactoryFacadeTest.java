package org.jboss.tools.hibernate.runtime.v_3_6.internal;

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
	public void testGetBooleanType() {
		Type typeTarget = Hibernate.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetByteType() {
		Type typeTarget = Hibernate.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetBigIntegerType() {
		Type typeTarget = Hibernate.BIG_INTEGER;
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetShortType() {
		Type typeTarget = Hibernate.SHORT;
		IType typeFacade = typeFactoryFacade.getShortType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCalendarType() {
		Type typeTarget = Hibernate.CALENDAR;
		IType typeFacade = typeFactoryFacade.getCalendarType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCalendarDateType() {
		Type typeTarget = Hibernate.CALENDAR_DATE;
		IType typeFacade = typeFactoryFacade.getCalendarDateType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetIntegerType() {
		Type typeTarget = Hibernate.INTEGER;
		IType typeFacade = typeFactoryFacade.getIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetBigDecimalType() {
		Type typeTarget = Hibernate.BIG_DECIMAL;
		IType typeFacade = typeFactoryFacade.getBigDecimalType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCharacterType() {
		Type typeTarget = Hibernate.CHARACTER;
		IType typeFacade = typeFactoryFacade.getCharacterType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetClassType() {
		Type typeTarget = Hibernate.CLASS;
		IType typeFacade = typeFactoryFacade.getClassType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
}
