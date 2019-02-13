package org.jboss.tools.hibernate.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.hibernate.exception.MappingException;

/**
 * Utility class for mapping between sqltypes and hibernate type names.
 * 
 * @author max (based on parts from Sql2Java from Middlegen)
 *
 */
public final class JDBCToHibernateTypeHelper {
   
	private JDBCToHibernateTypeHelper() {
		
	}
	
   /** The Map containing the preferred conversion type values. */
   private static final Map<Integer, String[]> PREFERRED_HIBERNATETYPE_FOR_SQLTYPE = new HashMap<Integer, String[]>();

   static {
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.TINYINT), new String[] { "byte", Byte.class.getName()} ); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.SMALLINT), new String[] { "short", Short.class.getName()} ); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.INTEGER), new String[] { "int", Integer.class.getName()} ); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.BIGINT), new String[] { "long", Long.class.getName()} ); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.REAL), new String[] { "float", Float.class.getName()} ); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.FLOAT), new String[] { "double", Double.class.getName()} ); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.DOUBLE), new String[] { "double", Double.class.getName()}); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.DECIMAL), new String[] { "big_decimal", "big_decimal" }); //$NON-NLS-1$ //$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.NUMERIC), new String[] { "big_decimal", "big_decimal" }); //$NON-NLS-1$ //$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.BIT), new String[] { "boolean", Boolean.class.getName()}); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.BOOLEAN), new String[] { "boolean", Boolean.class.getName()}); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.CHAR), new String[] { "char", Character.class.getName()}); //$NON-NLS-1$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.VARCHAR), new String[] { "string", "string" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.LONGVARCHAR), new String[] { "string", "string" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.BINARY), new String[] { "binary", "binary" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.VARBINARY), new String[] { "binary", "binary" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.LONGVARBINARY), new String[] { "binary", "binary" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.DATE), new String[] { "date", "date" }); //$NON-NLS-1$ //$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.TIME), new String[] { "time", "time" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.TIMESTAMP), new String[] { "timestamp", "timestamp" });  //$NON-NLS-1$//$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.CLOB), new String[] { "clob", "clob" }); //$NON-NLS-1$ //$NON-NLS-2$
      PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.put(new Integer(Types.BLOB), new String[] { "blob", "blob" }); //$NON-NLS-1$ //$NON-NLS-2$
	  
	  //Hibernate does not have any built-in Type for these:
      //preferredJavaTypeForSqlType.put(new Integer(Types.ARRAY), "java.sql.Array");
      //preferredJavaTypeForSqlType.put(new Integer(Types.REF), "java.sql.Ref");
      //preferredJavaTypeForSqlType.put(new Integer(Types.STRUCT), "java.lang.Object");
      //preferredJavaTypeForSqlType.put(new Integer(Types.JAVA_OBJECT), "java.lang.Object");
   }

   /* (non-Javadoc)
 * @see org.hibernate.cfg.JDBCTypeToHibernateTypesStrategy#getPreferredHibernateType(int, int, int, int)
 */
   public static String getPreferredHibernateType(int sqlType, int size, int precision, int scale, boolean nullable, boolean generatedIdentifier) {
	   boolean returnNullable = nullable || generatedIdentifier;
	if ( (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) && scale <= 0) { // <= 
		   if (precision == 1) {
			   // NUMERIC(1) is a often used idiom for storing boolean thus providing it out of the box.
			   return returnNullable?Boolean.class.getName():"boolean"; //$NON-NLS-1$
		   }
		   else if (precision < 3) {
			   return returnNullable?Byte.class.getName():"byte"; //$NON-NLS-1$
		   }
		   else if (precision < 5) {
			   return returnNullable?Short.class.getName():"short"; //$NON-NLS-1$
		   }
		   else if (precision < 10) {
			   return returnNullable?Integer.class.getName():"int"; //$NON-NLS-1$
		   }
		   else if (precision < 19) {
			   return returnNullable?Long.class.getName():"long"; //$NON-NLS-1$
		   }
		   else {
			   return "big_decimal"; //$NON-NLS-1$
		   }
	   }
	   
	   if ( sqlType == Types.CHAR && size>1 ) {
		  return "string"; //$NON-NLS-1$
	   }
	   
	   String[] result = (String[]) PREFERRED_HIBERNATETYPE_FOR_SQLTYPE.get(new Integer(sqlType) );
	   
	   if(result==null) {
		   return null;
	   } else if(returnNullable) {
		   return result[1];
	   } else {
		   return result[0];
	   }
	}
   
   static Map<String, Object> jdbcTypes; // Name to value
   static Map<Object, String> jdbcTypeValues; // value to Name
   
   public static String[] getJDBCTypes() {
	   checkTypes();
	   
	   return (String[]) jdbcTypes.keySet().toArray(new String[jdbcTypes.size()]);
   }
   
   public static int getJDBCType(String value) {
		checkTypes();
		
		Integer number = (Integer) jdbcTypes.get(value);
		
		if(number==null) {
			try {
				return Integer.parseInt(value);
			} 
			catch (NumberFormatException nfe) {
				throw new MappingException("jdbc-type: " + value + " is not a known JDBC Type nor a valid number"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} 
		else {
			return number.intValue();
		}
	}

   private static void checkTypes() {
	   if(jdbcTypes==null) {
		   jdbcTypes = new HashMap<String, Object>();
		   Field[] fields = Types.class.getFields();
		   for (int i = 0; i < fields.length; i++) {
			   Field field = fields[i];
			   if(Modifier.isStatic(field.getModifiers() ) ) {
				   try {
					   jdbcTypes.put(field.getName(), field.get(Types.class) );
				   } 
				   catch (IllegalArgumentException e) {
					   // ignore						
				   } 
				   catch (IllegalAccessException e) {
					   // ignore					
				   }
			   }
		   }
	   }
   }
   
    public static String getJDBCTypeName(int value) {
		if(jdbcTypeValues==null) {
			jdbcTypeValues = new HashMap<Object, String>();
			Field[] fields = Types.class.getFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(Modifier.isStatic(field.getModifiers() ) ) {
					try {
						jdbcTypeValues.put(field.get(Types.class), field.getName() );
					} 
					catch (IllegalArgumentException e) {
						// ignore						
					} 
					catch (IllegalAccessException e) {
						// ignore					
					}
				}
			}
		}
		
		String name = (String) jdbcTypeValues.get(new Integer(value) );
		
		if(name!=null) {
			return name;
		} 
		else {
			return ""+value; //$NON-NLS-1$
		}
	}

	/**
			 * @param table
			 * @param schema
			 * @param catalog
			 * @throws SQLException
			 */
			
	//		 scale and precision have numeric column
    public static boolean typeHasScaleAndPrecision(int sqlType) {
    	return (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC
    			|| sqlType == Types.REAL || sqlType == Types.FLOAT || sqlType == Types.DOUBLE);
    }
    
    // length is for string column
    public static boolean typeHasLength(int sqlType) {
    	return (sqlType == Types.CHAR || sqlType == Types.DATE
    			|| sqlType == Types.LONGVARCHAR || sqlType == Types.TIME || sqlType == Types.TIMESTAMP
    			|| sqlType == Types.VARCHAR );
    }
}

