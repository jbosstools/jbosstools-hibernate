package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.hibernate.tool.orm.jbt.type.TypeFactory;
import org.hibernate.tool.orm.jbt.wrp.WrapperFactory;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IType;
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
	
	@Test
	public void testGetBooleanType() {
		IType typeFacade = typeFactoryFacade.getBooleanType();
		assertSame(TypeFactory.BOOLEAN_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetByteType() {
		IType typeFacade = typeFactoryFacade.getByteType();
		assertSame(TypeFactory.BYTE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigIntegerType() {
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		assertSame(TypeFactory.BIG_INTEGER_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetShortType() {
		IType typeFacade = typeFactoryFacade.getShortType();
		assertSame(TypeFactory.SHORT_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarType() {
		IType typeFacade = typeFactoryFacade.getCalendarType();
		assertSame(TypeFactory.CALENDAR_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarDateType() {
		IType typeFacade = typeFactoryFacade.getCalendarDateType();
		assertSame(TypeFactory.CALENDAR_DATE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetIntegerType() {
		IType typeFacade = typeFactoryFacade.getIntegerType();
		assertSame(TypeFactory.INTEGER_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigDecimalType() {
		IType typeFacade = typeFactoryFacade.getBigDecimalType();
		assertSame(TypeFactory.BIG_DECIMAL_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCharacterType() {
		IType typeFacade = typeFactoryFacade.getCharacterType();
		assertSame(TypeFactory.CHARACTER_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetClassType() {
		IType typeFacade = typeFactoryFacade.getClassType();
		assertSame(TypeFactory.CLASS_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCurrencyType() {
		IType typeFacade = typeFactoryFacade.getCurrencyType();
		assertSame(TypeFactory.CURRENCY_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDateType() {
		IType typeFacade = typeFactoryFacade.getDateType();
		assertSame(TypeFactory.DATE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDoubleType() {
		IType typeFacade = typeFactoryFacade.getDoubleType();
		assertSame(TypeFactory.DOUBLE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetFloatType() {
		IType typeFacade = typeFactoryFacade.getFloatType();
		assertSame(TypeFactory.FLOAT_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLocaleType() {
		IType typeFacade = typeFactoryFacade.getLocaleType();
		assertSame(TypeFactory.LOCALE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLongType() {
		IType typeFacade = typeFactoryFacade.getLongType();
		assertSame(TypeFactory.LONG_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetStringType() {
		IType typeFacade = typeFactoryFacade.getStringType();
		assertSame(TypeFactory.STRING_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTextType() {
		IType typeFacade = typeFactoryFacade.getTextType();
		assertSame(TypeFactory.TEXT_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimeType() {
		IType typeFacade = typeFactoryFacade.getTimeType();
		assertSame(TypeFactory.TIME_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimestampType() {
		IType typeFacade = typeFactoryFacade.getTimestampType();
		assertSame(TypeFactory.TIMESTAMP_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimezoneType() {
		IType typeFacade = typeFactoryFacade.getTimezoneType();
		assertSame(TypeFactory.TIMEZONE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTrueFalseType() {
		IType typeFacade = typeFactoryFacade.getTrueFalseType();
		assertSame(TypeFactory.TRUE_FALSE_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetYesNoType() {
		IType typeFacade = typeFactoryFacade.getYesNoType();
		assertSame(TypeFactory.YES_NO_TYPE, ((IFacade)typeFacade).getTarget());
	}
	
}
