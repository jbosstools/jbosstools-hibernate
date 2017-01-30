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
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCurrencyType() {
		Type typeTarget = Hibernate.CURRENCY;
		IType typeFacade = typeFactoryFacade.getCurrencyType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetDateType() {
		Type typeTarget = Hibernate.DATE;
		IType typeFacade = typeFactoryFacade.getDateType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetDoubleType() {
		Type typeTarget = Hibernate.DOUBLE;
		IType typeFacade = typeFactoryFacade.getDoubleType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetFloatType() {
		Type typeTarget = Hibernate.FLOAT;
		IType typeFacade = typeFactoryFacade.getFloatType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetLocaleType() {
		Type typeTarget = Hibernate.LOCALE;
		IType typeFacade = typeFactoryFacade.getLocaleType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetLongType() {
		Type typeTarget = Hibernate.LONG;
		IType typeFacade = typeFactoryFacade.getLongType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetStringType() {
		Type typeTarget = Hibernate.STRING;
		IType typeFacade = typeFactoryFacade.getStringType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTextType() {
		Type typeTarget = Hibernate.TEXT;
		IType typeFacade = typeFactoryFacade.getTextType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTimeType() {
		Type typeTarget = Hibernate.TIME;
		IType typeFacade = typeFactoryFacade.getTimeType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTimestampType() {
		Type typeTarget = Hibernate.TIMESTAMP;
		IType typeFacade = typeFactoryFacade.getTimestampType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTimezoneType() {
		Type typeTarget = Hibernate.TIMEZONE;
		IType typeFacade = typeFactoryFacade.getTimezoneType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetTrueFalseType() {
		Type typeTarget = Hibernate.TRUE_FALSE;
		IType typeFacade = typeFactoryFacade.getTrueFalseType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetYesNoType() {
		Type typeTarget = Hibernate.YES_NO;
		IType typeFacade = typeFactoryFacade.getYesNoType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testGetNamedType() {
		IType typeFacade = typeFactoryFacade.getNamedType(String.class.getName());
		Assert.assertSame(Hibernate.STRING, ((IFacade)typeFacade).getTarget());
	}
	
}
