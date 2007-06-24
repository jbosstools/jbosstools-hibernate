/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.util;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.PropertyInfoStructure;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;


/**
 * @author Nick
 *
 */
public class TypeUtils {
	
	//18.03.2005 method refactored not to depend on elements order in map
	public static Type columnTypeToHibType(IDatabaseColumn column, boolean convertNumerics, Type treatNum1_0_asType)
	{
		if (column == null)
			return null;
		
		switch (column.getSqlTypeCode())
		{
		case (Types.BIGINT):
			return Type.LONG;
		
		case (Types.SMALLINT):
			return Type.SHORT;
		
		case (Types.INTEGER):
			return Type.INTEGER;
		
		case (Types.TINYINT):
			return Type.BYTE;
		
		case (Types.REAL):
		case (Types.FLOAT):
			return Type.FLOAT;
		
		case (Types.DOUBLE):
			return Type.DOUBLE;
		
		case (Types.CHAR):
		{
			if (column.getLength() > 1)
				return Type.STRING;
			else
				return Type.CHARACTER;
		}
		
		case (Types.VARCHAR):
		case (Types.LONGVARCHAR):
			return Type.STRING;
		
		case (Types.TIME):
			return Type.TIME;
		
		case (Types.DATE):
			return Type.DATE;
		
		case (Types.TIMESTAMP):
			return Type.TIMESTAMP;
		
		case (Types.BIT):
		case (Types.BOOLEAN):
			return Type.BOOLEAN;
		
		case (Types.DECIMAL): 
		case(Types.NUMERIC):
		{
			int precision = column.getPrecision();
			int scale = column.getScale();
			
			Type type = Type.BIG_DECIMAL;
			
			if (scale == 0)
				type = Type.BIG_INTEGER;
			
			if (convertNumerics)
			{
				if (precision == 1 && scale == 0)
				{
					if (treatNum1_0_asType == null)
					{
						type = Type.BOOLEAN;
					}
					else
					{
						type = treatNum1_0_asType;
					}
				}
				else
				{
					int length = column.getLength();
					
					//length not set
					if (length <= Column.DEFAULT_LENGTH)
					{
						length = 0xFFFFFF;
					}
					
					if (scale == 0)
					{
						//maybe equiv to integer
						if (precision <= 3 || length <= 1)
							type = Type.BYTE;
						else
							if (precision <= 5 || length <= 2)
								type = Type.SHORT;
							else 
								if (precision <= 10 || length <= 4)
									type = Type.INTEGER;
								else
									if (precision <= 19 || length <= 8)
										type = Type.LONG;
					}
					else
					{
						//maybe equiv to doble
						if (precision <= 24 || length <= 4)
							type = Type.FLOAT;
						else 
							if (precision <= 53 || length <= 8)
								type = Type.DOUBLE;
					}
				}
			}
			return type;
		}
		
		case (Types.BLOB):
			return Type.BLOB;
		
		case (Types.CLOB):
			return Type.STRING;
		
		case (Types.VARBINARY):
		case (Types.BINARY):
		case (Types.LONGVARBINARY):
			return Type.BINARY;
		
		}
		return null;
	}
	
	public static Type columnTypeToHibTypeDefault(IDatabaseColumn column, ConfigurationReader reader)
	{
		if (column == null)
			return null;
		
		Type result = columnTypeToHibType(column, reader.numericShouldConvert(column), reader.getNumeric1_0_Type());
		if (result != null)
			return result;
		else
			return Type.SERIALIZABLE;
		
	}
	
	public static final Map<Integer,String> jdbcConstantsMap = new HashMap<Integer,String>();
	
	static
	{
		// Use reflection to populate a map of int values to names
		// Get all field in java.sql.Types
		Field[] fields = java.sql.Types.class.getFields();
		for (int i=0; i<fields.length; i++) {
			try {
				// Get field name
				String name = fields[i].getName();
				
				// Get field value
				Integer value = (Integer)fields[i].get(null);
				
				// Add to map
				jdbcConstantsMap.put(value, name);
			} catch (IllegalAccessException e) {
            	//TODO (tau-tau) for Exception				
			}
		}
	}
//	akuzmin 31.08.2005
	public static final Map<Integer,String> sqlConstantsMap = new HashMap<Integer,String>();
	
	static
	{
		Field[] fields = java.sql.Types.class.getFields();
		for (int i=0; i<fields.length; i++) {
			try {
				// Get field name
				String name = fields[i].getName();
				if (!name.equals("ARRAY")&&
						!name.equals("DATALINK")&&
						!name.equals("DISTINCT")&&
						!name.equals("JAVA_OBJECT")&&
						!name.equals("NULL")&&
						!name.equals("REF")&&
						!name.equals("STRUCT"))
				{
					// Get field value
					Integer value = (Integer)fields[i].get(null);
					
					// Add to map
					sqlConstantsMap.put(value, name);
				}
			} catch (IllegalAccessException e) {
			}
		}
	}
	
	public static String SQLTypeToName(int jdbcType) {
		// Return the JDBC type name
		return (String)jdbcConstantsMap.get(new Integer(jdbcType));
	}
//	akuzmin 09.06.2005
	public static int getSQLTypeCodeToName(String fQTypeName)
	{
		if (jdbcConstantsMap.containsValue(fQTypeName))
		{
			Iterator typeiter=jdbcConstantsMap.keySet().iterator();
			while(typeiter.hasNext())
			{
				Integer value=(Integer)typeiter.next();
				if (fQTypeName.equals((String)jdbcConstantsMap.get(value)))
				{
					return value.intValue();
				}
			}
		}
		return 0;
	}
	
	
	private final static Type[] BLANK_TYPES = new Type[0];
	
	private static final Set<Type> INTEGER_TYPES = new HashSet<Type>();
	private static final Set<Type> FLOAT_TYPES = new HashSet<Type>();
	private static final Set<Type> STRING_TYPES = new HashSet<Type>();
	private static final Set<Type> DATE_TYPES = new HashSet<Type>();
	private static final Set<Type> BOOLEAN_TYPES = new HashSet<Type>();
	static
	{
		INTEGER_TYPES.add(Type.BIG_DECIMAL);
		INTEGER_TYPES.add(Type.BIG_INTEGER);
		INTEGER_TYPES.add(Type.BYTE);
		INTEGER_TYPES.add(Type.INTEGER);
		INTEGER_TYPES.add(Type.LONG);
		INTEGER_TYPES.add(Type.SHORT);
		
		FLOAT_TYPES.add(Type.DOUBLE);
		FLOAT_TYPES.add(Type.FLOAT);
		FLOAT_TYPES.add(Type.BIG_DECIMAL);
		
		STRING_TYPES.add(Type.CHARACTER);
		STRING_TYPES.add(Type.CLASS);
//		STRING_TYPES.add(Type.CURRENCY);
//		STRING_TYPES.add(Type.LOCALE);
//		STRING_TYPES.add(Type.TIMEZONE);
		STRING_TYPES.add(Type.STRING);
		STRING_TYPES.add(Type.TEXT);
		
		DATE_TYPES.add(Type.CALENDAR);
		DATE_TYPES.add(Type.CALENDAR_DATE);
		DATE_TYPES.add(Type.DATE);
		DATE_TYPES.add(Type.TIME);
		DATE_TYPES.add(Type.TIMESTAMP);
		
		BOOLEAN_TYPES.add(Type.BOOLEAN);
		BOOLEAN_TYPES.add(Type.YES_NO);
		BOOLEAN_TYPES.add(Type.TRUE_FALSE);
	}
	
	public static boolean isFieldCompatibleValidator(String fQTypeName, Type theType)
	{
		Type[] types = javaTypeToCompatibleHibTypes(fQTypeName);
		if (types == null || types.length == 0)
			return false;
		
		Type type = types[0];
		
		if (INTEGER_TYPES.contains(type) && INTEGER_TYPES.contains(theType))
			return true;
		if (FLOAT_TYPES.contains(type) && FLOAT_TYPES.contains(theType))
			return true;
		if (STRING_TYPES.contains(type) && STRING_TYPES.contains(theType))
			return true;
		if (DATE_TYPES.contains(type) && DATE_TYPES.contains(theType))
			return true;
		if (BOOLEAN_TYPES.contains(type) && BOOLEAN_TYPES.contains(theType))
			return true;
		
		if (type == theType)
			return true;
		
		return false;
	}
	
	public static boolean canFieldStoreHibType(String fQTypeName, Type theType)
	{
		Type[] types = javaTypeToCompatibleHibTypes(fQTypeName);
		boolean result = false;
		
		int i = 0;
		while (i < types.length && !result)
		{
			if (types[i] == theType)
				result = true;
			i++;
		}
		
		return result;
	}
	
	public static Type[] javaTypeToCompatibleHibTypes(String fQTypeName)
	{
		if (fQTypeName == null)
			return BLANK_TYPES;
		
		if (fQTypeName.equals("java.lang.Byte") || fQTypeName.equals("byte"))
			return new Type[]{Type.BYTE};
		if (fQTypeName.equals("java.lang.Double") || fQTypeName.equals("double"))
			return new Type[]{Type.DOUBLE, Type.FLOAT};
		if (fQTypeName.equals("java.lang.Float") || fQTypeName.equals("float"))
			return new Type[]{Type.FLOAT};
		if (fQTypeName.equals("java.lang.Integer") || fQTypeName.equals("int"))
			return new Type[]{Type.INTEGER, Type.SHORT};
		if (fQTypeName.equals("java.lang.Long") || fQTypeName.equals("long"))
			return new Type[]{Type.LONG, Type.INTEGER, Type.SHORT};
		if (fQTypeName.equals("java.lang.Short") || fQTypeName.equals("short"))
			return new Type[]{Type.SHORT};
		if (fQTypeName.equals("java.lang.Character") || fQTypeName.equals("char"))
			return new Type[]{Type.CHARACTER, Type.STRING};
		if (fQTypeName.equals("java.lang.Boolean") || fQTypeName.equals("boolean"))
			return new Type[]{Type.BOOLEAN, Type.TRUE_FALSE, Type.YES_NO};
		if (fQTypeName.equals("java.lang.String"))
			return new Type[]{Type.STRING, Type.TEXT};
		if (fQTypeName.equals("java.util.Date"))
			return new Type[]{Type.DATE, Type.TIME, Type.TIMESTAMP};
		if (fQTypeName.equals("java.sql.Date"))
			return new Type[]{Type.DATE};
		if (fQTypeName.equals("java.sql.Time"))
			return new Type[]{Type.TIME};
		if (fQTypeName.equals("java.sql.Timestamp"))
			return new Type[]{Type.TIMESTAMP};
		if (fQTypeName.equals("java.util.Calendar"))
			return new Type[]{Type.CALENDAR, Type.CALENDAR_DATE, Type.DATE, Type.TIMESTAMP};
		if (fQTypeName.equals("java.math.BigDecimal"))
			return new Type[]{Type.BIG_DECIMAL, Type.BIG_INTEGER, Type.BYTE, Type.DOUBLE, Type.FLOAT, Type.LONG, Type.INTEGER, Type.SHORT};
		if (fQTypeName.equals("java.math.BigInteger"))
			return new Type[]{Type.BIG_INTEGER, Type.BIG_DECIMAL, Type.BYTE, Type.DOUBLE, Type.FLOAT, Type.LONG, Type.INTEGER, Type.SHORT};
		if (fQTypeName.equals("java.util.Locale"))
			return new Type[]{Type.LOCALE};
		if (fQTypeName.equals("java.util.TimeZone"))
			return new Type[]{Type.TIMEZONE};
		if (fQTypeName.equals("java.util.Currency"))
			return new Type[]{Type.CURRENCY};
		if (fQTypeName.equals("java.lang.Class"))
			return new Type[]{Type.CLASS};
		
		// edit tau 28.01.2006 / fix ESORM-499 for [][]...
		//if (fQTypeName.equals("[byte"))
		//	return new Type[]{Type.BINARY};
		if (Signature.getArrayCount(fQTypeName) != 0 && ClassUtils.isPrimitiveType(Signature.getElementType(fQTypeName))){
			return new Type[]{Type.BINARY};			
		}
		
		
		if (fQTypeName.equals("java.sql.Clob"))
			return new Type[]{Type.CLOB};
		if (fQTypeName.equals("java.sql.Blob"))
			return new Type[]{Type.BLOB};
//		add by yk 03.06.2005.
		if (fQTypeName.equals("java.io.Serializable"))
			return new Type[]{Type.SERIALIZABLE};
//		add by yk 03.06.2005 stop.
		return BLANK_TYPES;
	}
	
	public static boolean isColumnTypeCompatible(IDatabaseColumn column, Type type)
	{
		if (column == null || type == null)
			return true;
		
		// added by Nick 01.09.2005
		if (column.isNativeType())
			return true;
		// by Nick
		
		int SQLType = column.getSqlTypeCode();
		
		if (SQLType == Types.OTHER)
			return true;
		
		boolean binaryHibType = (type == Type.BINARY || type == Type.BLOB || type == Type.CLOB || type == Type.SERIALIZABLE);
		
		boolean binarySQLType = (SQLType == Types.BINARY || SQLType == Types.BLOB || SQLType == Types.CLOB ||
				SQLType == Types.LONGVARBINARY || SQLType == Types.OTHER || SQLType == Types.VARBINARY);
		
		//****************************************************
		// Do not say types are incompatible until final check
		//****************************************************
		if (binaryHibType && binarySQLType)
			return true;
		
		boolean stringHibType = (type == Type.CLASS || type == Type.LOCALE || type == Type.TIMEZONE || 
				type == Type.CURRENCY || type == Type.STRING);
		
		boolean charSQLType = (SQLType == Types.CHAR || SQLType == Types.LONGVARCHAR || SQLType == Types.VARCHAR);
		boolean stringSQLType = /*(column.getLength() > 1) && */charSQLType;
		
		if (stringHibType && stringSQLType)
			return true;
		
		if ((type == Type.BIG_DECIMAL || type == Type.BIG_INTEGER) && (SQLType == Types.NUMERIC || SQLType == Types.DECIMAL))
			return true;
		
		if (type == Type.CHARACTER && charSQLType)
			return true;
		
		//XXX Nick add more Hibernate - SQL types
		
		boolean timeStampCompatible = (type == Type.DATE || type == Type.TIME || type == Type.CALENDAR ||
				type == Type.CALENDAR_DATE);
		
		if (timeStampCompatible && SQLType == Types.TIMESTAMP)
			return true;
		
		boolean booleanType = (type == Type.BOOLEAN || type == Type.TRUE_FALSE || type == Type.YES_NO);
		boolean booleanSQLType = (SQLType == Types.BIT || SQLType == Types.BOOLEAN);
		boolean booleanNumericType = ((SQLType == Types.NUMERIC || SQLType == Types.DECIMAL) && column.getPrecision() == 1 && column.getScale() == 0);
		
		//boolean can be represented as NUMERIC(1,0)
		booleanSQLType |= booleanNumericType;
		
		if (booleanType && booleanSQLType)
			return true;
		
		boolean numericSQLType = (SQLType == Types.NUMERIC || SQLType == Types.DECIMAL);
		boolean intType = (type == Type.BYTE || type == Type.INTEGER || type == Type.LONG || type == Type.SHORT);
		boolean realType = (type == Type.DOUBLE || type == Type.FLOAT);
		
		if (numericSQLType)
		{
			if (realType)
				return true;
			boolean intSQLType = (column.getScale() == 0);
			
			if (intType && intSQLType)
				return true;
		}
		
		boolean floatSQLType = (SQLType == Types.FLOAT || SQLType == Types.DOUBLE || SQLType == Types.REAL);
		boolean floatType = (type == Type.DOUBLE || type == Type.FLOAT);
		
		if (floatSQLType && floatType)
			return true;
		
		boolean intSqlType = (SQLType == Types.INTEGER || SQLType == Types.SMALLINT || SQLType == Types.TINYINT || SQLType == Types.BIGINT);
		
		if (intSqlType && intType)
			return true;
		
		return (Type.isHibernateType(type) && type.getSqlType() == SQLType);
	}
	
	public static Type javaTypeToHibType(String fQTypeName)
	{
		Type[] types = javaTypeToCompatibleHibTypes(fQTypeName);
		if (types != null && types.length != 0)
			return types[0];
		return null;
	}
	
	public static final Set<String> SORTED_COLLECTIONS = new LinkedHashSet<String>();
	public static final Set<String> UNSORTED_COLLECTIONS = new LinkedHashSet<String>();
	public static final Set<String> INDEXED_COLLECTIONS = new LinkedHashSet<String>();
	public static final Set<String> UNINDEXED_COLLECTIONS = new LinkedHashSet<String>();
	
	public static final Set<String> COLLECTIONS = new LinkedHashSet<String>();
	
	static
	{
		SORTED_COLLECTIONS.add("java.util.SortedSet");
		SORTED_COLLECTIONS.add("java.util.SortedMap");
		UNSORTED_COLLECTIONS.add("java.util.Collection");
		UNSORTED_COLLECTIONS.add("java.util.Set");
		INDEXED_COLLECTIONS.add("java.util.List");
		INDEXED_COLLECTIONS.add("java.util.Map");
		INDEXED_COLLECTIONS.add("java.util.SortedMap");
		
		UNINDEXED_COLLECTIONS.addAll(UNSORTED_COLLECTIONS);
		UNINDEXED_COLLECTIONS.add("java.util.SortedSet");
		
		COLLECTIONS.addAll(UNSORTED_COLLECTIONS);
		COLLECTIONS.addAll(INDEXED_COLLECTIONS);
		COLLECTIONS.addAll(SORTED_COLLECTIONS);
	}
	
	public static boolean isColumnSQLTypeReproducible(IDatabaseColumn column)
	{
		boolean compatible = false;
		if (column == null)
			return compatible;
		
		if (column.getPersistentValueMapping() instanceof SimpleValueMapping) {
			SimpleValueMapping svm = (SimpleValueMapping) column.getPersistentValueMapping();
			
			if(svm != null && svm.getType() != null)
			{
				/*    			if(thetype != null && thetype.getSqlType() == svm.getType().getSqlType())
				 {
				 compatible = true;
				 }
				 else // there is not compatible hibernate type for the column.
				 compatible = false;
				 */
				// changed by Nick 07.09.2005
				return !column.isNativeType();
			}
			else// svm has not a type.
				compatible = true;
		}
		else
			compatible = true;
		return compatible;
	}
	
	/**
	 * @author kaa 
	 * akuzmin@exadel.com
	 * 27.10.2005
	 * Method return test value for diferent types
	 * @param typeName
	 * @return string value
	 */
	public static PropertyInfoStructure getTestValuesForType(String typeName)
	{
		// #changed# by Konstantin Mishin on 28.01.2006 fixed for ESORM-29
//		Type[] types = javaTypeToCompatibleHibTypes(typeName);
//		if (types == null || types.length == 0)
//			return new PropertyInfoStructure("null",null);
//		
//		Type type = types[0];
//		
//		// #changed# by Konstantin Mishin on 10.01.2006 fixed for ESORM-409
////		if (INTEGER_TYPES.contains(type))
////			return new PropertyInfoStructure("new Integer(1)","java.lang.Integer");
////		if (FLOAT_TYPES.contains(type))
////			return new PropertyInfoStructure("new Float(1.0)","java.lang.Float");
////		if (STRING_TYPES.contains(type))
////			return new PropertyInfoStructure("\"string\"",null);
////		if (DATE_TYPES.contains(type))
////			return new PropertyInfoStructure("new Date()","java.util.Date");
////		if (BOOLEAN_TYPES.contains(type))
////			return new PropertyInfoStructure("true",null);
//		// #added# by Konstantin Mishin on 27.01.2006 fixed for ESORM-29
//		if(ClassUtils.isPrimitiveType(typeName)) {
//			if("boolean".equals(typeName))
//				return new PropertyInfoStructure("false",typeName);
//		if("char".equals(typeName))
//			return new PropertyInfoStructure("'c'",typeName);
//		return new PropertyInfoStructure("1",typeName);		
//		}
//		// #added#
//		if (INTEGER_TYPES.contains(type) || FLOAT_TYPES.contains(type))
//			return new PropertyInfoStructure("new "+ClassUtils.getUnqualifyName(type.getJavaType().getName())+"(\"1\")",type.getJavaType().getName());
//		if (STRING_TYPES.contains(type))
//			return new PropertyInfoStructure("\"string\"",null);
//		if (DATE_TYPES.contains(type))
//			return new PropertyInfoStructure("new "+ClassUtils.getUnqualifyName(type.getJavaType().getName())+"()",type.getJavaType().getName());
//		if (BOOLEAN_TYPES.contains(type))
//			return new PropertyInfoStructure("new "+ClassUtils.getUnqualifyName(type.getJavaType().getName())+"(false)",type.getJavaType().getName());
//		// #changed#
//		
//		return new PropertyInfoStructure("null",null);
		String propertyName = "null";
		boolean isArray = false;
		if(typeName.charAt(0)=='['){
			typeName = typeName.substring(1);
			isArray = true;
		}
		Type[] types = javaTypeToCompatibleHibTypes(typeName);
		if (types != null && types.length != 0) {			
			if(ClassUtils.isPrimitiveType(typeName)) {
				if("boolean".equals(typeName)) 
					propertyName = "false";
				else if("char".equals(typeName))
					propertyName = "'c'";
				else
					propertyName = "1";
			} else {
				if (INTEGER_TYPES.contains(types[0]) || FLOAT_TYPES.contains(types[0])){
					propertyName = "\"1\"";
				} else if (STRING_TYPES.contains(types[0])){
					// #added# by Konstantin Mishin on 08.02.2006 fixed for ESORM-510
					if("java.lang.Character".equals(typeName))
						propertyName = "'c'";
					else
					// #added#
						propertyName = "\"string\"";
				} else if (DATE_TYPES.contains(types[0])){
					propertyName = "";
				} else if (BOOLEAN_TYPES.contains(types[0])){
					propertyName = "false";
				}
				// #added# by Konstantin Mishin on 08.02.2006 fixed for ESORM-409
				if("null".equals(propertyName))
					typeName = null;
				else
				// #added#
					if(!"\"string\"".equals(propertyName)){
						propertyName = "new "+ClassUtils.getUnqualifyName(typeName)+"("+propertyName+")";
				}
			}
			if (isArray)
				propertyName = "new "+ClassUtils.getUnqualifyName(typeName)+"[]{"+propertyName+"}";

		}
		return new PropertyInfoStructure(propertyName,typeName);
		// #changed#
	}
	
}
