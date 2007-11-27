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
package org.jboss.tools.hibernate.xml.model.helpers;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class HibernateTypeHelper {
	static Properties hToJ = new Properties();
	static Properties jToH = new Properties();
	static Set<String> primitives = new HashSet<String>();
	
	public static String[] TYPE_NAMES = new String[]{
		"binary", "big_decimal", "blob", "boolean", "byte",
		"calendar", "calendar_date", "character", "class", 
		"clob", "currency",	"date", "double", "float", 
		"integer", "locale", "long", "serializable", "short",		 
		"string", "text", "time", "timestamp", "timezone", 
		"true_false", "yes_no"
	};
	
	public static String[] VERSION_TYPE_NAMES = new String[]{
		"integer", "long", "short", "timestamp", "calendar" 
	};	

	public static String[] TIMESTAMP_TYPE_NAMES = new String[]{
		"timestamp" 
	};	

	public static String[] DISCRIMINATOR_TYPE_NAMES = new String[]{
		"string", "character", "integer", "byte", "short", "boolean", "yes_no", "true_false"
	};	

	public static String[] COLLECTION_TYPE_NAMES = new String[]{
		"array", "bag", "idbag", "list", "map", "primitive-array", "set"
		
	};

	static {
		addType("integer", new String[]{"int", "java.lang.Integer"}, true);
		addType("long", new String[]{"long", "java.lang.Long"}, true);
		addType("short", new String[]{"short", "java.lang.Short"}, true);
		addType("float", new String[]{"float", "java.lang.Float"}, true);
		addType("double", new String[]{"double", "java.lang.Double"}, true);
		addType("character", new String[]{"char", "java.lang.Character"}, true);
		addType("byte", new String[]{"byte", "java.lang.Byte"}, true);
		addType("boolean", new String[]{"boolean", "java.lang.Boolean"}, true);
		addType("yes_no", "boolean", true);
		addType("true_false", "boolean", true);
		addType("string", "java.lang.String", true);
		addType("date", "java.util.Date", true);
		addType("time", "java.util.Time", true);
		addType("timestamp", "java.sql.Timestamp", true);
		addType("calendar", "java.util.Calendar", true);
		addType("calendar_date", "java.util.Calendar", true);
		addType("big_decimal", "java.math.BigDecimal", true);
		addType("locale", "java.util.Locale", true);
		addType("timezone", "java.util.TimeZone", true);
		addType("currency", "java.util.Currency", true);
		addType("class", "java.lang.Class", true);
		addType("text", "java.lang.String", true);
		addType("binary", "byte[]", false);
		addType("serializable", "java.io.Serializable", false);
		addType("clob", "java.sql.Clob", false);
		addType("blob", "java.sql.Blob", false);

		addType("map", new String[]{"java.util.Map", "java.util.HashMap", "java.util.TreeMap"}, false);
		addType("set", new String[]{"java.util.Set", "java.util.HashSet", "java.util.TreeSet"}, false); 
		addType("list", new String[]{"java.util.List", "java.util.ArrayList", "java.util.Vector"}, false); 
		addType("bag", "java.util.List", false);
		addType("idbag", "java.util.List", false); 
		addType("array", "java.lang.String[]", false); 
		addType("primitive-array", "int[]", false); 

	}

	private static void addType(String hibernateType, String javaType, boolean isPrimitive) {
		addType(hibernateType, new String[]{javaType}, isPrimitive);
	}
	
	private static void addType(String hibernateType, String[] javaTypes, boolean isPrimitive) {
		hToJ.setProperty(hibernateType, javaTypes[0]);
		for (int i = 0; i < javaTypes.length; i++) {
			if(!jToH.containsKey(javaTypes[i])) {
				jToH.setProperty(javaTypes[i], hibernateType);
			}
		}
		if(isPrimitive) {
			primitives.add(hibernateType);
			for (int i = 0; i < javaTypes.length; i++)
				primitives.add(javaTypes[i]);
		}
	}
	
	public static String getJavaTypeForHibernateType(String hibernateType) {
		String s = hToJ.getProperty(hibernateType);
		return (s == null) ? hibernateType : s;
	}
	
	public static String getHibernateTypeForJavaType(String javaType) {
		String s = jToH.getProperty(javaType);
		return (s == null) ? javaType : s;
	}
	
	public static boolean isPrimitive(String type) {
		return type != null && type.length() > 0 && primitives.contains(type);
	}
	
	public static boolean isHibernateType(String type)
	{
		boolean result = false;
		
		for (int i = 0; i < TYPE_NAMES.length && !result; i++)
			result = TYPE_NAMES[i].equals(type);
		
		return result;	
	}

	public static void mergeAttributes(XModelObject destination, XModelObject source) {
		XModelObjectLoaderUtil.mergeAttributes(destination, source);
		
		if (destination.getModelEntity().getAttribute("type") != null &&
				source.getModelEntity().getAttribute("type") == null &&
				source.getModelEntity().getAttribute("class") != null) {
			destination.setAttributeValue("type", source.getAttributeValue("class"));
		} else if (destination.getModelEntity().getAttribute("class") != null &&
				source.getModelEntity().getAttribute("class") == null &&
				source.getModelEntity().getAttribute("type") != null) {
			String className = getJavaTypeForHibernateType(source.getAttributeValue("type"));
			if (className == null || className.length() <= 0)
				className = source.getAttributeValue("type");
			destination.setAttributeValue("class", className);
		}
	}
	
	public static String getHibernateTypeBySQLType(String sqlType) {
		if(sqlType == null || sqlType.length() == 0) return "";
		if("BIGINT".equals(sqlType)) return "long";
		if("BINARY".equals(sqlType)) return "binary";
		if("BIT".equals(sqlType)) return "boolean";
		if("BLOB".equals(sqlType)) return "blob";
		if("CHAR".equals(sqlType)) return "string";
		if("CLOB".equals(sqlType)) return "clob";
		if("DATE".equals(sqlType)) return "date";
		if("DECIMAL".equals(sqlType)) return "double";
		if("DOUBLE".equals(sqlType)) return "double";
		if("FLOAT".equals(sqlType)) return "float";
		if("INTEGER".equals(sqlType)) return "integer";
		if("LONG_RAW".equals(sqlType)) return "";
		if("LONGVARBINARY".equals(sqlType)) return "binary";
		if("LONGVARCHAR".equals(sqlType)) return "binary";
		if("NUMBER".equals(sqlType)) return "integer";
		if("NUMERIC".equals(sqlType)) return "integer";
		if("REAL".equals(sqlType)) return "double";
		if("SMALL_INT".equals(sqlType)) return "short";
		if("TIME".equals(sqlType)) return "time";
		if("TIMESTAMP".equals(sqlType)) return "timestamp";
		if("TINYINT".equals(sqlType)) return "byte";
		if("VARBINARY".equals(sqlType)) return "binary";
		if("VARCHAR".equals(sqlType)) return "string";
		if("VARCHAR2".equals(sqlType)) return "string";
		//Mssql
		if("CHAR".equals(sqlType)) return "string";
		if("NCHAR".equals(sqlType)) return "string";
		if("DATETIME".equals(sqlType)) return "date";
		if("NVARCHAR".equals(sqlType)) return "string";
		if("TEXT".equals(sqlType)) return "string";
		if("NTEXT".equals(sqlType)) return "string";
		if("INT".equals(sqlType)) return "int";
		return "";
	}
	
}
