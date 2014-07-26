package org.jboss.tools.hibernate.spi;


public interface ITypeFactory {

	IType getBooleanType();
	IType getByteType();
	IType getBigIntegerType();
	IType getShortType();
	IType getCalendarType();
	IType getCalendarDateType();
	IType getIntegerType();
	IType getBigDecimalType();
	IType getCharacterType();
	IType getClassType();
	IType getCurrencyType();
	IType getDateType();
	IType getDoubleType();
	IType getFloatType();
	IType getLocaleType();
	IType getLongType();
	IType getStringType();
	IType getTextType();
	IType getTimeType();
	IType getTimestampType();
	IType getTimezoneType();
	IType getTrueFalseType();
	IType getYesNoType();
	IType getNamedType(String typeName);
	IType getBasicType(String type);

}
