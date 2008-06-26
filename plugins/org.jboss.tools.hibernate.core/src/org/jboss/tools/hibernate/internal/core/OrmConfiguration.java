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
package org.jboss.tools.hibernate.internal.core;


import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author alex
 *
 * XXX bela_n(1) Implement IOrmConfiguration. Save/load OrmConfiguration in a file in project root folder.
 */

// XXX toAlex is OrmConfiguration stored in a file named orm2.cfg mentioned in Architecture overview
//		file? Should this file be .property-styled?
// orm2.cfg was preliminary name. I think properties file format is enough for us. So use properties file
//format. You can use orm2.properties as file name.
public class OrmConfiguration extends AbstractConfigurationResource implements IOrmConfiguration {

	//bela_n
	private static final String ORM2_CFG_FILENAME = "orm2.properties";
	
	private IProject project;
	private String baseclass;
	//Storage strategy constants
	public static final String HIBERNATE_STORAGE="hibernate.storage";
	public static final String[] STORAGE_STRATEGY_NAMES=new String[]{"XML file per hierarchy","XML file per package","XML file per project"/*,"Java 5 annotations","XDoclet tags"*/};
	// Nick, because you replaced SequencedHashMap with Properties in PropertySourceBase you should change types for property values from Integer to String. 
	//Properties cannot store Objects but only strings
	public static final String XML_PER_HIERARCHY="hierarchy";
	public static final String XML_PER_PACKAGE="package";
	public static final String XML_PER_PROJECT="project";
	public static final String XDOCLET_TAGS="tags";
	public static final String JAVA5_ANNOTATIONS="annotations";
	
	public static final String[] STORAGE_STRATEGY_VALUES=new String[]{XML_PER_HIERARCHY,XML_PER_PACKAGE,XML_PER_PROJECT/*,JAVA5_ANNOTATIONS, XDOCLET_TAGS*/};
	public static final String DEFAULT_STORAGE_STRATEGY = STORAGE_STRATEGY_VALUES[0];
	
	//Inheritance mapping constants
	public static final String HIBERNATE_INHERITANCE="hibernate.inheritance";
	public static final String[] INHERITANCE_MAP_STRATEGY_NAMES=new String[]{"Table per hierarchy (subclass)","Table per subclass (joined-subclass)","Table per concrete class (class)","Table per concrete class (union-subclass)"};
	public static final String TABLE_PER_HIERARCHY="subclass";
	public static final String TABLE_PER_SUBCLASS="joined-subclass";
	public static final String TABLE_PER_CLASS="class";
	public static final String TABLE_PER_CLASS_UNION="union-subclass";
	public static final String[] INHERITANCE_MAP_STRATEGY_VALUES=new String[]{TABLE_PER_HIERARCHY,TABLE_PER_SUBCLASS,TABLE_PER_CLASS,TABLE_PER_CLASS_UNION};
	public static final String DEFAULT_INHERITANCE_MAP_STRATEGY = INHERITANCE_MAP_STRATEGY_VALUES[0];
	
	//Optimistic locking strategy constants
	public static final String HIBERNATE_OPTIMISTIC = "hibernate.optimistic";
	public static final String[] OPTIMISTIC_STRATEGY_NAMES=new String[]{"None","Check version","Check all fields","Check dirty fields"};
    public static final String CHECK_NONE="none";
	public static final String CHECK_VERSION="version";
	public static final String CHECK_ALL_FIELDS="all";
	public static final String CHECK_DIRTY_FIELDS="dirty";

	public static final String[] OPTIMISTIC_STRATEGY_VALUES=new String[]{CHECK_NONE,CHECK_VERSION,CHECK_ALL_FIELDS,CHECK_DIRTY_FIELDS};
	public static final String DEFAULT_OPTIMISTIC_STRATEGY = OPTIMISTIC_STRATEGY_VALUES[0];
	
	//versioning method	
	//XXX Eventually remove commented code because timestamp == version with timestamp type
/*	public static final String[] VERSIONING_METHOD_NAMES=new String[]{"Use version field","Use timestamp field"};
	public static final String VERSIONING_VERSION="version";
	public static final String VERSIONING_TIMESTAMP="timestamp";
	public static final String[] VERSIONING_METHOD_VALUES=new String[]{VERSIONING_VERSION,VERSIONING_TIMESTAMP};
	public static final String DEFAULT_VERSIONING_METHOD = VERSIONING_METHOD_VALUES[0];
	*/
	//Version types
	public static final String HIBERNATE_VERSION_DATATYPE = "hibernate.version.datatype";
	public static final String[] VERSION_TYPES=new String[]{"integer","long","short","timestamp","calendar"};
	public static final String DEFAULT_VERSION_TYPE = VERSION_TYPES[0];
	
	//Version unsaved values
	public static final String VERSION_UNSAVED_QUERY = "hibernate.version.unsaved-value";
    public static final String[] VERSION_UNSAVED_VALUES = {"null","negative","undefined"};
	public static final String DEFAULT_VERSION_UNSAVED = VERSION_UNSAVED_VALUES[0];

	//Timestamp unsaved values.use version unsaved values instead
	//XXX Eventually remove commented code
/*	public static final String[] TIMESTAMP_UNSAVED_VALUES = {"null","undefined"};
	public static final String DEFAULT_TIMESTAMP_UNSAVED = TIMESTAMP_UNSAVED_VALUES[0];*/

	//Cascading types
	public static final String HIBERNATE_CASCADE = "hibernate.cascade";
	public static final String[] CASCADE_VALUES = {"none", "all", "save-update", "create", "merge", "delete", "lock", "refresh", "evict", "replicate"};
	public static final String DEFAULT_CASCADE = CASCADE_VALUES[0];

	//Fields access type
	public static final String HIBERNATE_ACCESS = "hibernate.access";
	public static final String[] ACCESS_VALUES = {"property","field"};
	public static final String DEFAULT_ACCESS = ACCESS_VALUES[1];
	
	public static final String[] BOOLEAN_VALUES=new String[]{"false","true"};
	
	public static final String HIBERNATE_LAZY = "hibernate.lazy";
    
    public static final String HIBERNATE_ASSOCIATIONS_LAZY = "hibernate.associations.lazy";
    public static final String HIBERNATE_COLLECTIONS_LAZY = "hibernate.collections.lazy";

    // #changed# by Konstantin Mishin on 11.03.2006 fixed for ESORM-525
    //public static final String[] ASSOCIATIONS_LAZY_VALUES = {"proxy","true", "false"};
    public static final String[] ASSOCIATIONS_LAZY_VALUES = {"proxy","no-proxy", "false"};
    // #changed#
	public static final String ASSOCIATIONS_DEFAULT_LAZY = ASSOCIATIONS_LAZY_VALUES[0];

	//Discriminator types
	public static final String HIBERNATE_DISCRIMINATOR_DATATYPE = "hibernate.discriminator.datatype";
	public static final String[] DISCRIMINATOR_DATATYPES=new String[]{"string","character","integer","byte","short","boolean","yes_no","true_false"};
	public static final String DEFAULT_DISCRIMINATOR_DATATYPE = DISCRIMINATOR_DATATYPES[0];
	
	public static final String DEFAULT_FORCE_DISCRIMINATOR = BOOLEAN_VALUES[0];
	
	public static final String DEFAULT_INSERT_DISCRININATOR = BOOLEAN_VALUES[1];
	
	public static final String DEFAULT_DISCRIMINATOR_FORMULA = "";
	
	//Hibernate id types
	//XXX Nick(2) Specify all hibernare id types refer 5.2.2 section of hibernate 3 documentation for more
	//XXX.toAlex not sure whether "text" type can be used here, because can be stored as binary type CLOB 
	public static String HIBERNATE_ID_DATATYPE = "hibernate.id.datatype";
	public static final String[] ID_DATATYPES=new String[]{"integer","long","short","float","double","character","byte","boolean","yes_no","true_false",
	        "string","date","time","timestamp","calendar","calendar_date",/* added by Nick - 12.07.2005*/"big_decimal"/**/,"big_integer","locale","timezone","currency"};
	public static final String DEFAULT_ID_DATATYPE = ID_DATATYPES[0];

	
	//Id unsaved value
	public static final String HIBERNATE_ID_UNSAVED_VALUE = "hibernate.id.unsaved-value";
    public static final String[] ID_UNSAVED_VALUES = {"null","any","none","undefined"};
	public static final String DEFAULT_ID_UNSAVED = ID_UNSAVED_VALUES[0];
	
	public static final String HIBERNATE_ID_GENERATOR = "hibernate.id.generator";
    public static final String DEFAULT_ID_GENERATOR = "native";
    
    // #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-401
    public static final String HIBERNATE_ID_GENERATOR_QUALITY = "hibernate.id.generator.quality";
    public static final String DEFAULT_ID_GENERATOR_QUALITY = BOOLEAN_VALUES[0];
    // #added#

	//PK generators
	public static final String[] PK_GENERATOR_VALUES = {"increment","identity","sequence","hilo","seqhilo","uuid","guid","native","assigned","select","foreign"};
	public static final String DEFAULT_PK_GENERATOR = PK_GENERATOR_VALUES[7];
	public static final String[][] PK_GENERATOR_PARAMS ={{"table","column"},//increment
														 {},//identity
														 {"sequence","parameters"},//sequence
														 {"table","column","max_lo"},//hilo
														 {"sequence","max_lo","parameters"},//seqhilo
														 {"separator"},//uuid
														 {},//guid
														 {"sequence","max_lo","parameters"},//native
														 {},//assigned
														 {"key"},//select
														 {"property",}};//foreign
	//key params
	public static final String[] KEY_PARAMS_NAME = {"on-delete","property-ref","not-null","update","unique"};
	public static final String[][] KEY_PARAMS_VALUES ={{"noaction","cascade"},//on-delete
														 {},//property-ref
														 {"true","false"},//not-null
														 {"true","false"},//update
														 {"true","false"}};//unique
	
	
	//Types of associations cascading
	public static final String HIBERNATE_ASSOCIATIONS_CASCADE = "hibernate.associations.cascade";
    public static final String[] ASSOCIATIONS_CASCADE_VALUES = {"all","none","save-update","delete","all-delete-orphan"};
	public static final String DEFAULT_ASSOCIATIONS_CASCADE = ASSOCIATIONS_CASCADE_VALUES[1];
	
	//OnDelete triggering
	
    public static final String HIBERNATE_KEY_ON_DELETE = "hibernate.key.on-delete";
    public static final String[] KEY_ON_DELETE_VALUES = {"noaction","cascade"};
	public static final String DEFAULT_KEY_ON_DELETE = KEY_ON_DELETE_VALUES[0];

	public static final String DEFAULT_SCHEMA = "";
	public static final String DEFAULT_CATALOG = "";
	public static final String DEFAULT_PACKAGE = "";
	
	//Type mappings
	public static final String[] TYPE_INT={"int","Integer"};
	public static final String[] TYPE_LONG={"long","Long"};
	public static final String[] TYPE_SHORT={"short","Short"};
	public static final String[] TYPE_FLOAT={"float","Float"};
	public static final String[] TYPE_DOUBLE={"double","Double"};
	public static final String[] TYPE_CHAR={"char","Character"};
	public static final String[] TYPE_BYTE={"byte","Byte"};
	public static final String[] TYPE_BOOLEAN={"boolean","Boolean"};
	
    // added by Nick 12.07.2005
	public static final String[] TYPE_TIME = {"java.util.Date","java.sql.Time"};
    public static final String[] TYPE_DATE = {"java.util.Date","java.sql.Date"};
    public static final String[] TYPE_TIMESTAMP = {"java.util.Date","java.sql.Timestamp"};
    public static final String[] TYPE_CALENDAR = {"java.util.Calendar","java.sql.Timestamp"};
    public static final String[] TYPE_CALENDAR_DATE = {"java.util.Calendar","java.sql.Date"};
    // by Nick
    
	public static final String DEFAULT_TYPE_INT = TYPE_INT[1];
	public static final String DEFAULT_TYPE_LONG = TYPE_LONG[1];
	public static final String DEFAULT_TYPE_SHORT = TYPE_SHORT[1];
	public static final String DEFAULT_TYPE_FLOAT = TYPE_FLOAT[1];
	public static final String DEFAULT_TYPE_DOUBLE = TYPE_DOUBLE[1];
	public static final String DEFAULT_TYPE_CHAR = TYPE_CHAR[1];
	public static final String DEFAULT_TYPE_BYTE = TYPE_BYTE[1];
	public static final String DEFAULT_TYPE_BOOLEAN = TYPE_BOOLEAN[1];
	
	public static final String DEFAULT_DYNAMIC_UPDATE = BOOLEAN_VALUES[0];
	public static final String DEFAULT_DYNAMIC_INSERT = BOOLEAN_VALUES[0];
	public static final String DEFAULT_SELECT_BEFORE_UPDATE = BOOLEAN_VALUES[0];
	
	public static int DEFAULT_JDBC_BATCH_SIZE = 17;
	public static String DEFAULT_CLASS_BATCH_SIZE = "1";
	
	public static final String CLASS_DYNAMIC_UPDATE = "hibernate.class.dynamic-update";
	public static final String CLASS_DYNAMIC_INSERT = "hibernate.class.dynamic-insert";
	public static final String CLASS_SELECT_BEFORE_UPDATE = "hibernate.class.select-before-update";
	public static final String CLASS_BATCH_SIZE = "hibernate.class.batch_size";
	
	public static final String VERSION_COLUMN_NAME = "hibernate.column.version";
	public static final String DISCRIMINATOR_COLUMN_NAME = "hibernate.column.discriminator";
	public static final String IDENTIFIER_COLUMN_NAME = "hibernate.column.id";
	public static final String DEFAULT_VERSION_COLUMN_NAME = "version";
	public static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
	public static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
	
	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
	public static final String DEFAULT_INDEX_COLUMN_NAME="idx";
	public static final String VERSION_QUERY = "hibernate.query.version";
	public static final String DISCRIMINATOR_QUERY = "hibernate.query.discriminator";
	public static final String IDENTIFIER_QUERY = "hibernate.query.id";
	
	public static final String TABLE_PREFIX_QUERY = "hibernate.reverse.prefix";
	
	public static final String HAM_SAFE_COLLECTION = "hibernate.ham.Collection.SafeType";
	public static final String HAM_COLLECTION_ELEMENT_TYPE = "hibernate.ham.Collection.ElementType";

	public static final String[] HAM_SAFE_COLLECTION_VALUES = (String[]) TypeUtils.UNINDEXED_COLLECTIONS.toArray(new String[0]);
	public static final String[] HAM_COLLECTION_ELEMENT_TYPE_VALUES = new String[Type.getHibernateTypes().length];

    public static final String HAM_REACHABILITY = "hibernate.ham.reachability";
    public static final String[] HAM_REACHABILITY_VALUES = new String[]{"Automap everything","Automap identifiers"};
    
    public static final String HAM_ACCESSOR = "hibernate.ham.accessor";
    public static final String[] HAM_ACCESSOR_VALUES = new String[]{"properties only","properties and fields"};
    public static final String HAM_ACCESSOR_DEFAULT = HAM_ACCESSOR_VALUES[0];
    
    public static final String HAM_IGNORE_LIST = "hibernate.ham.ignore_list";
    
    public static final String REVTYPE_NUMERIC_1_0 = "hibernate.numeric_1_0";
    public static final String[] REVTYPE_NUMERIC_1_0_VALUES = new String[]{"short","byte","boolean"};
    
    public static final String REVTYPE_NUMERIC_CONVERT = "hibernate.numeric_type.convert";
    public static final String REVTYPE_NUMERIC_X_Y = "hibernate.numeric_x_y";
    
	static 
	{
	    Type[] types = Type.getHibernateTypes();
	    for (int i = 0 ; i < HAM_COLLECTION_ELEMENT_TYPE_VALUES.length ; i++ )
	        HAM_COLLECTION_ELEMENT_TYPE_VALUES[i] = types[i].getName();
	}
	
	public static final String HAM_SAFE_COLLECTION_DEFAULT = HAM_SAFE_COLLECTION_VALUES[0];
	public static final String HAM_COLLECTION_ELEMENT_TYPE_DEFAULT = Type.STRING.getName();
	
    public static final String REVERSING_BASE_CLASS = "hibernate.reversing.base_class";
    
    public static final String HAM_QUERY_FUZZINESS = "hibernate.ham.queries.fuzziness";
    public static final String HAM_QUERY_FUZZY_ON = "hibernate.ham.queries.fuzzy_on";

    public static final String HAM_LINK_TABLES = "hibernate.ham.link_tables";
    public static final String[] HAM_LINK_TABLES_VALUES = new String[]{"entities","values"};
    public static final String HAM_LINK_TABLES_DEFAULT = HAM_LINK_TABLES_VALUES[1];

    public static final String POJO_RENDERER = "hibernate.ham.pojo_renderer";

    public static final String REVERSING_NATIVE_SQL_TYPES = "hibernate.reversing.use_native_types";
    
	//akuzmin 21.08.2005
    public static final String[] dialects={
			"",
			"org.hibernate.dialect.DB2Dialect",
			"org.hibernate.dialect.DB2400Dialect",
			"org.hibernate.dialect.DB2390Dialect",
			"org.hibernate.dialect.PostgreSQLDialect",
			"org.hibernate.dialect.MySQLDialect",
			"org.hibernate.dialect.MySQLInnoDBDialect",
			"org.hibernate.dialect.MySQLMyISAMDialect",
			"org.hibernate.dialect.OracleDialect",
			"org.hibernate.dialect.Oracle9Dialect",
			"org.hibernate.dialect.SybaseDialect",
			"org.hibernate.dialect.SybaseAnywhereDialect",
			"org.hibernate.dialect.SQLServerDialect",
			"org.hibernate.dialect.SAPDBDialect",
			"org.hibernate.dialect.InformixDialect",
			"org.hibernate.dialect.HSQLDialect",
			"org.hibernate.dialect.IngresDialect",
			"org.hibernate.dialect.ProgressDialect",
			"org.hibernate.dialect.MckoiDialect",
			"org.hibernate.dialect.InterbaseDialect",
			"org.hibernate.dialect.PointbaseDialect",
			"org.hibernate.dialect.FrontbaseDialect",
			"org.hibernate.dialect.FirebirdDialect"
		};
    public static final String [] urls =new String[]{
			//"",
			"jdbc:db2://[hostname]:[port]/[dbName]",
			"jdbc:as400://[hostname]",
			"jdbc:db2://[hostname]:50000;DatabaseName=[dbName]",
			"jdbc:postgresql://[hostname]:5432/[dbName]",
			"jdbc:mysql://[hostname]:3306/[dbName]",
			"jdbc:mysql://[hostname]:3306/[dbName]",
			"jdbc:mysql://[hostname]:3306/[dbName]",
 			"jdbc:oracle:thin:@[hostname]:1521:[dbName]",
 			"jdbc:oracle:thin:@[hostname]:1521:[dbName]",
 			"jdbc:sybase:Tds:[hostname]:[port]/[dbName]",
			"jdbc:sybase:Tds:[hostname]:[port]/[dbName]",
			"jdbc:microsoft:sqlserver://[hostname]:1433;databaseName=[dbName]",
			"jdbc:sapdb://[hostname]/[dbName]",
			"jdbc:informix-sqli://[hostname]:1025/[dbName]:INFORMIXSERVER=[serverName]",
			"jdbc:hsqldb:hsql:[hostname]",
			"jdbc:ingres://[hostname]:[port]/[dbName]",
			"jdbc:JdbcProgress:T:[hostname]:[port]:[dbName]",
			"jdbc:mckoi://[hostname]:[port]/[dbName]",
			"jdbc:interbase://[serverName]/[DataSourcePath]",
			"jdbc:pointbase:server://[hostname]/[dbName]",
			"jdbc:FrontBase://[hostname]/[dbName]",
			"jdbc:firebirdsql://[hostname]:3050/[DataSourcePath]"	
			};
	
    public static final String [] drivers =new String[]{
			//"",
			"COM.ibm.db2.jdbc.app.DB2Driver",
			"com.exadel.ibm.as400.access.AS400JDBCDriver",
			"com.exadel.ibm.db2.jcc.DB2Driver",
			"org.postgresql.Driver",
			"com.mysql.jdbc.Driver",
			"com.mysql.jdbc.Driver",
			"com.mysql.jdbc.Driver",//org\git\mm\mysql\Driver.class
 			"oracle.jdbc.driver.OracleDriver",
 			"oracle.jdbc.OracleDriver",
 			"com.sybase.jdbc2.jdbc.SybDriver",
 			"com.sybase.jdbc2.jdbc.SybDriver",
			"com.microsoft.jdbc.sqlserver.SQLServerDriver",
			"com.sap.dbtech.jdbc.DriverSapDB",
			"com.informix.jdbc.IfxDriver",
			"org.hsqldb.jdbcDriver",
			"ca.ingres.jdbc.IngresDriver",
			"com.progress.sql.jdbc.JdbcProgressDriver",
			"com.mckoi.JDBCDriver",
			"interbase.interclient.Driver",
			"com.pointbase.jdbc.jdbcUniversalDriver",
			"com.frontbase.jdbc.FBJDriver",
			"org.firebirdsql.jdbc.FBDriver"	
			};
    
    public OrmConfiguration(IProject project)  {
		this.project = project;
//akuzmin 22.07.2005
//		this.setPropertyDescriptors(OrmPropertyDescriptorsHolder.getInstance(null));
	}
	
    // add tau 16.12.2005    
	public OrmConfiguration(OrmPropertyDescriptorsHolder instance) {
    	this.setPropertyDescriptorsHolder(instance);
	}

	public IResource findResource() throws CoreException{
		return ScanProject.scannerCP("/"+ORM2_CFG_FILENAME,project);
	}
	public IResource createResource() throws CoreException{
		IPath path=project.getProjectRelativePath();
		return project.getFile(path.append(ORM2_CFG_FILENAME));
	}
	public String getBaseClass()
	{
		return baseclass;
	}

	public void setBaseClass(String classname)
	{
		baseclass=classname;
	}

	// add tau 22.12.2005
	public Properties getProperties() {
		return super.getProperties();
	}

	// add tau 14.02.2006
	public void save(boolean flagSaveMappingStorages) throws IOException, CoreException {
		save();
		
	}
	
}
