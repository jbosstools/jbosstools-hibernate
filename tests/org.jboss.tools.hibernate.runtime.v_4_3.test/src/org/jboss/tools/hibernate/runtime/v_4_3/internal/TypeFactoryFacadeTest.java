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
	
	@Test
	public void testGetDateType() {
		Type typeTarget = StandardBasicTypes.DATE;
		IType typeFacade = typeFactoryFacade.getDateType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDoubleType() {
		Type typeTarget = StandardBasicTypes.DOUBLE;
		IType typeFacade = typeFactoryFacade.getDoubleType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetFloatType() {
		Type typeTarget = StandardBasicTypes.FLOAT;
		IType typeFacade = typeFactoryFacade.getFloatType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLocaleType() {
		Type typeTarget = StandardBasicTypes.LOCALE;
		IType typeFacade = typeFactoryFacade.getLocaleType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLongType() {
		Type typeTarget = StandardBasicTypes.LONG;
		IType typeFacade = typeFactoryFacade.getLongType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetStringType() {
		Type typeTarget = StandardBasicTypes.STRING;
		IType typeFacade = typeFactoryFacade.getStringType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTextType() {
		Type typeTarget = StandardBasicTypes.TEXT;
		IType typeFacade = typeFactoryFacade.getTextType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimeType() {
		Type typeTarget = StandardBasicTypes.TIME;
		IType typeFacade = typeFactoryFacade.getTimeType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimestampType() {
		Type typeTarget = StandardBasicTypes.TIMESTAMP;
		IType typeFacade = typeFactoryFacade.getTimestampType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimezoneType() {
		Type typeTarget = StandardBasicTypes.TIMEZONE;
		IType typeFacade = typeFactoryFacade.getTimezoneType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTrueFalseType() {
		Type typeTarget = StandardBasicTypes.TRUE_FALSE;
		IType typeFacade = typeFactoryFacade.getTrueFalseType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetYesNoType() {
		Type typeTarget = StandardBasicTypes.YES_NO;
		IType typeFacade = typeFactoryFacade.getYesNoType();
		Assert.assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
}
