package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.type.ShortType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimeZoneType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.Type;
import org.hibernate.type.YesNoType;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.BigDecimalType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.BigIntegerType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.BooleanType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.ByteType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CalendarDateType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CalendarType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CharacterType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.ClassType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.CurrencyType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.DateType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.DoubleType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.FloatType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.IntegerType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.LocaleType;
import org.jboss.tools.hibernate.runtime.v_6_0.internal.legacy.LongType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TypeFactoryFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private ITypeFactory typeFactoryFacade = new TypeFactoryFacadeImpl(FACADE_FACTORY, null);
	
	@Test
	public void testGetBooleanType() {
		Type typeTarget = BooleanType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getBooleanType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetByteType() {
		Type typeTarget = ByteType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getByteType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigIntegerType() {
		Type typeTarget = BigIntegerType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getBigIntegerType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetShortType() {
		Type typeTarget = ShortType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getShortType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarType() {
		Type typeTarget = CalendarType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getCalendarType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCalendarDateType() {
		Type typeTarget = CalendarDateType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getCalendarDateType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetIntegerType() {
		Type typeTarget = IntegerType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getIntegerType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetBigDecimalType() {
		Type typeTarget = BigDecimalType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getBigDecimalType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCharacterType() {
		Type typeTarget = CharacterType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getCharacterType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetClassType() {
		Type typeTarget = ClassType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getClassType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetCurrencyType() {
		Type typeTarget = CurrencyType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getCurrencyType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDateType() {
		Type typeTarget = DateType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getDateType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetDoubleType() {
		Type typeTarget = DoubleType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getDoubleType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetFloatType() {
		Type typeTarget = FloatType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getFloatType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLocaleType() {
		Type typeTarget = LocaleType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getLocaleType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetLongType() {
		Type typeTarget = LongType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getLongType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetStringType() {
		Type typeTarget = StringType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getStringType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTextType() {
		Type typeTarget = TextType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getTextType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimeType() {
		Type typeTarget = TimeType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getTimeType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimestampType() {
		Type typeTarget = TimestampType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getTimestampType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTimezoneType() {
		Type typeTarget = TimeZoneType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getTimezoneType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetTrueFalseType() {
		Type typeTarget = TrueFalseType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getTrueFalseType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	@Test
	public void testGetYesNoType() {
		Type typeTarget = YesNoType.INSTANCE;
		IType typeFacade = typeFactoryFacade.getYesNoType();
		assertSame(typeTarget, ((IFacade)typeFacade).getTarget());
	}
	
	// TODO JBIDE-28154: Investigate failure 
	@Disabled
	@Test
	public void testGetNamedType() {
		IType typeFacade = typeFactoryFacade.getNamedType(String.class.getName());
		assertSame(StandardBasicTypes.STRING, ((IFacade)typeFacade).getTarget());
	}
	
	// TODO JBIDE-28154: Investigate failure 
	@Disabled
	@Test
	public void testGetBasicType() {
		IType typeFacade = typeFactoryFacade.getBasicType(String.class.getName());
		assertSame(StandardBasicTypes.STRING, ((IFacade)typeFacade).getTarget());
	}
		
	// TODO: JBIDE-27557 Reenable the following test when the associated issue is solved
	@Disabled
	@Test
	public void testGetTypeFormats() {
		Map<IType, String> typeFormats = typeFactoryFacade.getTypeFormats();
		assertEquals(23, typeFormats.size());
		assertEquals("true", typeFormats.get(typeFactoryFacade.getBooleanType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getByteType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getBigIntegerType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getShortType()));
		assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getCalendarType()));
		assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getCalendarDateType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getIntegerType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getBigDecimalType()));
		assertEquals("h", typeFormats.get(typeFactoryFacade.getCharacterType()));
		assertEquals(
				ITable.class.getName(), 
				typeFormats.get(typeFactoryFacade.getClassType()));
		assertEquals(
				Currency.getInstance(Locale.getDefault()).toString(), 
				typeFormats.get(typeFactoryFacade.getCurrencyType()));
		assertEquals(
				new SimpleDateFormat("dd MMMM yyyy").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getDateType()));
		assertEquals("42.42", typeFormats.get(typeFactoryFacade.getDoubleType()));
		assertEquals("42.42", typeFormats.get(typeFactoryFacade.getFloatType()));
		assertEquals(
				Locale.getDefault().toString(), 
				typeFormats.get(typeFactoryFacade.getLocaleType()));
		assertEquals("42", typeFormats.get(typeFactoryFacade.getLongType()));
		assertEquals("a string", typeFormats.get(typeFactoryFacade.getStringType()));
		assertEquals("a text", typeFormats.get(typeFactoryFacade.getTextType()));
		assertEquals(12, typeFormats.get(typeFactoryFacade.getTimeType()).length());
		assertEquals(
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 
				typeFormats.get(typeFactoryFacade.getTimestampType()).substring(0, 10));
		assertEquals(
				TimeZone.getDefault().getID(), 
				typeFormats.get(typeFactoryFacade.getTimezoneType()));
		assertEquals("true", typeFormats.get(typeFactoryFacade.getTrueFalseType()));
		assertEquals("true", typeFormats.get(typeFactoryFacade.getYesNoType()));
	}
	
}
