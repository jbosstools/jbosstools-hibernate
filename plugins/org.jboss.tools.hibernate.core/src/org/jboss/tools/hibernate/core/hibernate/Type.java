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
package org.jboss.tools.hibernate.core.hibernate;

import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;

/**
 * @author alex
 *
 * Defines a mapping from a Java type to an JDBC datatype. 
 */
public class Type {
	private String name;
	private boolean custom;
	private Class javaType;
	private int sqlType;
	
	private static HashMap<String,Type> hibernateTypes = new HashMap<String,Type>();
	private static final Type[] TYPES = {};
	
	protected Type(){}
	
	protected Type(String name, Class javaType, int sqlType) {
		this.name = name;
		this.javaType = javaType;
		this.sqlType = sqlType;
		//XXX.toAlex (Nick) putting standard types into Map would be a good thing, cause it gives us a possibility
		// to retrieve this types by name. Is it OK? It's done already. See far below.
		//You can use getType or getHibernateTypes
		// ok
	}
	
	/**
	 * Create a custom type
	 * */
	protected Type(String fullyQualifiedName){
		this.custom=true;
		this.name=fullyQualifiedName;
	}
	/**
	 * Returns the abbreviated name of the type or fully qualified custom type class name
	 *
	 * @return String the Hibernate type name
	 */
	public String getName(){return name;}


	/**
	 * @return Returns the custom.
	 */
	public boolean isCustom() {
		return custom;
	}
	/**
	 * @param custom The custom to set.
	 */
	protected void setCustom(boolean custom) {
		this.custom = custom;
	}
	/**
	 * @param name The name to set.
	 */
	protected void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the javaType.
	 */
	public Class getJavaType() {
		return javaType;
	}
	/**
	 * @param javaType The javaType to set.
	 */
	public void setJavaType(Class javaType) {
		this.javaType = javaType;
	}
	/**
	 * @return Returns the sqlType.
	 */
	public int getSqlType() {
		return sqlType;
	}
	/**
	 * @param sqlType The sqlType to set.
	 */
	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}
	
	public static Type getType(String name){
		return (Type)hibernateTypes.get(name);
	}
	
	public static Type[] getHibernateTypes(){
		return (Type[])hibernateTypes.values().toArray(TYPES);
	}
	
    // added by Nick 17.06.2005
	public static boolean isHibernateType(Type type)
    {
     return hibernateTypes.containsValue(type);   
    }
    // by Nick
    
	public static Type getOrCreateType(String name){
		Type t=getType(name);
		if(t==null) return new Type(name);
		return t;
	}
	
	/**
	 Standard hibernate type mappings
	 * */
	
		/**
		 * Hibernate <tt>long</tt> type.
		 */
		public static final Type LONG = new Type("long",Long.class,Types.BIGINT);
		/**
		 * Hibernate <tt>short</tt> type.
		 */
		public static final Type SHORT = new Type("short",Short.class,Types.SMALLINT);
		/**
		 * Hibernate <tt>integer</tt> type.
		 */
		public static final Type INTEGER = new Type("integer",Integer.class,Types.INTEGER);
		/**
		 * Hibernate <tt>byte</tt> type.
		 */
		public static final Type BYTE =  new Type("byte",Byte.class,Types.TINYINT);
		/**
		 * Hibernate <tt>float</tt> type.
		 */
		public static final Type FLOAT = new Type("float",Float.class,Types.FLOAT);
		/**
		 * Hibernate <tt>double</tt> type.
		 */
		public static final Type DOUBLE = new Type("double",Double.class,Types.DOUBLE);
		
		/**
		 * Hibernate <tt>character</tt> type.
		 */
		public static final Type CHARACTER = new Type("character",Character.class,Types.CHAR);
		/**
		 * Hibernate <tt>string</tt> type.
		 */
		public static final Type STRING = new Type("string",String.class,Types.VARCHAR);
		/**
		 * Hibernate <tt>time</tt> type.
		 */
		public static final Type TIME = new Type("time",java.util.Date.class,Types.TIME);
		/**
		 * Hibernate <tt>date</tt> type.
		 */
		public static final Type DATE = new Type("date",java.util.Date.class,Types.DATE);
		/**
		 * Hibernate <tt>timestamp</tt> type.
		 */
		public static final Type TIMESTAMP = new Type("timestamp",java.util.Date.class,Types.TIMESTAMP);
		/**
		 * Hibernate <tt>boolean</tt> type.
		 */
		public static final Type BOOLEAN = new Type("boolean",Boolean.class,Types.BIT);
		/**
		 * Hibernate <tt>true_false</tt> type.
		 */
		public static final Type TRUE_FALSE = new Type("true_false",Boolean.class,Types.CHAR);
		/**
		 * Hibernate <tt>yes_no</tt> type.
		 */
		public static final Type YES_NO = new Type("yes_no",Boolean.class,Types.CHAR);
		/**
		 * Hibernate <tt>big_decimal</tt> type.
		 */
		public static final Type BIG_DECIMAL = new Type("big_decimal",BigDecimal.class,Types.NUMERIC);
		
        // added by Nick 12.07.2005
        /**
         * Hibernate <tt>big_integer</tt> type.
         */
		public static final Type BIG_INTEGER = new Type("big_integer",BigInteger.class, Types.NUMERIC);
        // by Nick
        /**
		 * Hibernate <tt>binary</tt> type.
		 */
		public static final Type BINARY = new Type("binary",byte[].class,Types.VARBINARY);
		/**
		 * Hibernate <tt>text</tt> type.
		 */
		public static final Type TEXT = new Type("text",String.class,Types.CLOB);
		/**
		 * Hibernate <tt>blob</tt> type.
		 */
		public static final Type BLOB = new Type("blob",Blob.class,Types.BLOB);
		/**
		 * Hibernate <tt>clob</tt> type.
		 */
		public static final Type CLOB = new Type("clob",Clob.class,Types.CLOB);
		/**
		 * Hibernate <tt>calendar</tt> type.
		 */
		public static final Type CALENDAR = new Type("calendar",Calendar.class,Types.TIMESTAMP);
		/**
		 * Hibernate <tt>calendar_date</tt> type.
		 */
		public static final Type CALENDAR_DATE = new Type("calendar_date",Calendar.class,Types.DATE);
		/**
		 * Hibernate <tt>locale</tt> type.
		 */
		public static final Type LOCALE = new Type("locale",Locale.class,Types.VARCHAR);
		/**
		 * Hibernate <tt>currency</tt> type.
		 */
		public static final Type CURRENCY = new Type("currency",Currency.class,Types.VARCHAR);
		/**
		 * Hibernate <tt>timezone</tt> type.
		 */
		public static final Type TIMEZONE = new Type("timezone",TimeZone.class,Types.VARCHAR);
		/**
		 * Hibernate <tt>class</tt> type.
		 */
		public static final Type CLASS = new Type("class",Class.class,Types.VARCHAR);
		/**
		 * Hibernate <tt>serializable</tt> type.
		 */
		public static final Type SERIALIZABLE = new Type("serializable",Serializable.class,Types.VARBINARY);
		static{
			hibernateTypes.put(LONG.getName(), LONG);
			hibernateTypes.put(SHORT.getName(), SHORT);
			hibernateTypes.put(INTEGER.getName(), INTEGER);
			hibernateTypes.put(BYTE.getName(), BYTE);
			hibernateTypes.put(FLOAT.getName(), FLOAT);
			hibernateTypes.put(DOUBLE.getName(), DOUBLE);
			hibernateTypes.put(CHARACTER.getName(), CHARACTER);
			hibernateTypes.put(STRING.getName(), STRING);
			hibernateTypes.put(TIME.getName(), TIME);
			hibernateTypes.put(DATE.getName(), DATE);
			hibernateTypes.put(TIMESTAMP.getName(), TIMESTAMP);
			hibernateTypes.put(BOOLEAN.getName(), BOOLEAN);
			hibernateTypes.put(TRUE_FALSE.getName(), TRUE_FALSE);
			hibernateTypes.put(YES_NO.getName(), YES_NO);
			// added by Nick 12.07.2005
            hibernateTypes.put(BIG_INTEGER.getName(), BIG_INTEGER);
			// by Nick
            hibernateTypes.put(BIG_DECIMAL.getName(), BIG_DECIMAL);
			hibernateTypes.put(BINARY.getName(), BINARY);
			hibernateTypes.put(TEXT.getName(), TEXT);
			hibernateTypes.put(BLOB.getName(), BLOB);
			hibernateTypes.put(CLOB.getName(), CLOB);
			hibernateTypes.put(CALENDAR.getName(), CALENDAR);
			hibernateTypes.put(CALENDAR_DATE.getName(), CALENDAR_DATE);
			hibernateTypes.put(LOCALE.getName(), LOCALE);
			hibernateTypes.put(CURRENCY.getName(), CURRENCY);
			hibernateTypes.put(TIMEZONE.getName(), TIMEZONE);
			hibernateTypes.put(CLASS.getName(), CLASS);
			hibernateTypes.put(SERIALIZABLE.getName(), SERIALIZABLE);
		}
}
