package org.jboss.tools.hibernate.runtime.v_4_3.internal;

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
	public void testGetBooleanType() {
		Type typeTarget = StandardBasicTypes.BOOLEAN;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetByteType() {
		Type typeTarget = StandardBasicTypes.BYTE;
		IType typeFacade = typeFactoryFacade.getByteType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigIntegerType() {
		Type typeTarget = StandardBasicTypes.BIG_INTEGER;
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetShortType() {
		Type typeTarget = StandardBasicTypes.SHORT;
		IType typeFacade = typeFactoryFacade.getShortType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarType() {
		Type typeTarget = StandardBasicTypes.CALENDAR;
		IType typeFacade = typeFactoryFacade.getCalendarType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarDateType() {
		Type typeTarget = StandardBasicTypes.CALENDAR_DATE;
		IType typeFacade = typeFactoryFacade.getCalendarDateType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetIntegerType() {
		Type typeTarget = StandardBasicTypes.INTEGER;
		IType typeFacade = typeFactoryFacade.getIntegerType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigDecimalType() {
		Type typeTarget = StandardBasicTypes.BIG_DECIMAL;
		IType typeFacade = typeFactoryFacade.getBigDecimalType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCharacterType() {
		Type typeTarget = StandardBasicTypes.CHARACTER;
		IType typeFacade = typeFactoryFacade.getCharacterType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetClassType() {
		Type typeTarget = StandardBasicTypes.CLASS;
		IType typeFacade = typeFactoryFacade.getClassType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCurrencyType() {
		Type typeTarget = StandardBasicTypes.CURRENCY;
		IType typeFacade = typeFactoryFacade.getCurrencyType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
}
