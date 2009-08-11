/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/**
 * @author max
 */
public class DriverClassHelpers {

    private Map<String, String> dialectNames = new HashMap<String, String>();
    private Map<String, Set<String>> connectionUrls = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> driverClasses = new HashMap<String, Set<String>>();
    private Map<String, String> driverToDialect = new HashMap<String, String>();
    
    public DriverClassHelpers() {
        // externalize this!
        dialectNames.put("DB2", "org.hibernate.dialect.DB2Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("DB2/390", "org.hibernate.dialect.DB2390Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("DB2/400", "org.hibernate.dialect.DB2400Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Derby", "org.hibernate.dialect.DerbyDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("FrontBase", "org.hibernate.dialect.FrontBaseDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        //Not in 3.2 anymore dialectNames.put("Generic", "org.hibernate.dialect.GenericDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("HSQL", "org.hibernate.dialect.HSQLDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("H2", "org.hibernate.dialect.H2Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Informix", "org.hibernate.dialect.InformixDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Ingres", "org.hibernate.dialect.IngresDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Interbase", "org.hibernate.dialect.InterbaseDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Firebird", "org.hibernate.dialect.FirebirdDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Mckoi SQL", "org.hibernate.dialect.MckoiDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("MySQL", "org.hibernate.dialect.MySQLDialect");         //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("MySQL (InnoDB)", "org.hibernate.dialect.MySQLInnoDBDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("MySQL (MyISAM)", "org.hibernate.dialect.MySQLMyISAMDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("MySQL 5", "org.hibernate.dialect.MySQL5Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("MySQL 5 (InnoDB)", "org.hibernate.dialect.MySQL5InnoDBDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        //dialectNames.put("Oracle","org.hibernate.dialect.OracleDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Oracle 8i","org.hibernate.dialect.Oracle8iDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        //dialectNames.put("Oracle 9", "org.hibernate.dialect.Oracle9Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Oracle 9i", "org.hibernate.dialect.Oracle9iDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Oracle 10g", "org.hibernate.dialect.Oracle10gDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Pointbase", "org.hibernate.dialect.PointbaseDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("PostgreSQL", "org.hibernate.dialect.PostgreSQLDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Progress", "org.hibernate.dialect.ProgressDialect"); //$NON-NLS-1$ //$NON-NLS-2$        
        dialectNames.put("SAP DB", "org.hibernate.dialect.SAPDBDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Sybase", "org.hibernate.dialect.SybaseDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Sybase 11", "org.hibernate.dialect.Sybase11Dialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Sybase Anywhere", "org.hibernate.dialect.SybaseAnywhereDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("SQL Server", "org.hibernate.dialect.SQLServerDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        dialectNames.put("Teiid", "org.teiid.dialect.TeiidDialect"); //$NON-NLS-1$ //$NON-NLS-2$

        addDriverAndURLS("org.hibernate.dialect.HSQLDialect", //$NON-NLS-1$
                         "org.hsqldb.jdbcDriver",  //$NON-NLS-1$
                         new String[] {
                            "jdbc:hsqldb:hsql://<host>",  //$NON-NLS-1$
                            "jdbc:hsqldb:<dbname>", //$NON-NLS-1$
                            "jdbc:hsqldb:." } //$NON-NLS-1$
                         );

        addDriverAndURLS("org.hibernate.dialect.H2Dialect", //$NON-NLS-1$
                "org.h2.Driver", //$NON-NLS-1$ 
                new String[] {
                   "jdbc:h2:<filename>", //$NON-NLS-1$
                   "jdbc:h2:mem:", //$NON-NLS-1$
                   "jdbc:h2:mem:<databaseName>", //$NON-NLS-1$
                   "jdbc:h2:tcp://<server>[:<port>]/<databaseName>" //$NON-NLS-1$                   
                   }
                );

        addDriverAndURLS("org.hibernate.dialect.OracleDialect",  //$NON-NLS-1$
                         "oracle.jdbc.driver.OracleDriver", //$NON-NLS-1$
                         new String[] {
                            "jdbc:oracle:thin:@localhost:1521:orcl", //$NON-NLS-1$
                            "jdbc:oracle:thin:@<host>:<port1521>:<sid>" } //$NON-NLS-1$
                         );
        
        addDriverAndURLS("org.hibernate.dialect.Oracle9Dialect", //$NON-NLS-1$ 
                "oracle.jdbc.driver.OracleDriver", //$NON-NLS-1$
                new String[] {
                   "jdbc:oracle:thin:@localhost:1521:orcl", //$NON-NLS-1$
                   "jdbc:oracle:thin:@<host>:<port1521>:<sid>" } //$NON-NLS-1$
                );

        addDriverAndURLS("org.hibernate.dialect.Oracle10gDialect",  //$NON-NLS-1$
                "oracle.jdbc.driver.OracleDriver", //$NON-NLS-1$
                new String[] {
                   "jdbc:oracle:thin:@localhost:1521:orcl", //$NON-NLS-1$
                   "jdbc:oracle:thin:@<host>:<port1521>:<sid>" } //$NON-NLS-1$
                );

        addDriverAndURLS("org.hibernate.dialect.Oracle8iDialect",  //$NON-NLS-1$
                "oracle.jdbc.driver.OracleDriver", //$NON-NLS-1$
                new String[] {
                   "jdbc:oracle:thin:@localhost:1521:orcl", //$NON-NLS-1$
                   "jdbc:oracle:thin:@<host>:<port1521>:<sid>" } //$NON-NLS-1$
                );

        addDriverAndURLS("org.hibernate.dialect.MySQLDialect",  //$NON-NLS-1$
                         "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                         new String[] {
                            "jdbc:mysql://<hostname>/<database>", //$NON-NLS-1$
                            "jdbc:mysql:///test", //$NON-NLS-1$
                            "jdbc:mysql:///<name>", //$NON-NLS-1$
                             }
                        );

        addDriverAndURLS("org.hibernate.dialect.MySQLDialect",  //$NON-NLS-1$
                "com.mysql.jdbc.Driver", //$NON-NLS-1$
                new String[] {        		
                   "jdbc:mysql://<hostname>/<database>", //$NON-NLS-1$
                   "jdbc:mysql:///test", //$NON-NLS-1$
                   "jdbc:mysql:///<name>", //$NON-NLS-1$
                   "jdbc:mysql://<host><:port>/<database>" //$NON-NLS-1$
                    }
               );
        addDriverAndURLS("org.hibernate.dialect.MySQLInnoDBDialect", //$NON-NLS-1$ 
                    "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                    new String[] {
                       "jdbc:mysql://<hostname>/<database>", //$NON-NLS-1$
                       "jdbc:mysql:///test", //$NON-NLS-1$
                       "jdbc:mysql:///<name>" } //$NON-NLS-1$
                   );
        

        addDriverAndURLS("org.hibernate.dialect.MySQLMyISAMDialect",  //$NON-NLS-1$
                    "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                    new String[] {
                       "jdbc:mysql://<hostname>/<database>", //$NON-NLS-1$
                       "jdbc:mysql:///test", //$NON-NLS-1$
                       "jdbc:mysql:///<name>" } //$NON-NLS-1$
                   );
        
        addDriverAndURLS("org.hibernate.dialect.MySQL5Dialect", //$NON-NLS-1$ 
                "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                new String[] {
                   "jdbc:mysql://<hostname>/<database>", //$NON-NLS-1$
                   "jdbc:mysql:///test", //$NON-NLS-1$
                   "jdbc:mysql:///<name>" } //$NON-NLS-1$
               );
    
        addDriverAndURLS("org.hibernate.dialect.MySQL5InnoDBDialect", //$NON-NLS-1$ 
                "org.gjt.mm.mysql.Driver", //$NON-NLS-1$
                new String[] {
                   "jdbc:mysql://<hostname>/<database>", //$NON-NLS-1$
                   "jdbc:mysql:///test", //$NON-NLS-1$
                   "jdbc:mysql:///<name>" } //$NON-NLS-1$
               );
    
        addDriverAndURLS("org.hibernate.dialect.PostgreSQLDialect",  //$NON-NLS-1$
                "org.postgresql.Driver", //$NON-NLS-1$
                new String[] {
                   "jdbc:postgresql:template1", //$NON-NLS-1$
                   "jdbc:postgresql:<name>" } //$NON-NLS-1$
               );
        
        addDriverAndURLS("org.hibernate.dialect.ProgressDialect", //$NON-NLS-1$ 
                "com.progress.sql.jdbc.JdbcProgressDriver", //$NON-NLS-1$
                new String[] {
                   "jdbc:JdbcProgress:T:host:port:dbname;WorkArounds=536870912", //$NON-NLS-1$
                   }
               );
        
        addDriverAndURLS("org.hibernate.dialect.DB2Dialect", "COM.ibm.db2.jdbc.app.DB2Driver", //$NON-NLS-1$  //$NON-NLS-2$
                          new String[] { "jdbc:db2:test", "jdbc:db2:<name>" }); //$NON-NLS-1$  //$NON-NLS-2$
        
        addDriverAndURLS("org.hibernate.dialect.DB2400Dialect", "com.ibm.as400.access.AS400JDBCDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:as400://<systemname>", "jdbc:db2:<name>" }); //$NON-NLS-1$  //$NON-NLS-2$
        
        addDriverAndURLS("org.hibernate.dialect.DerbyDialect", "org.apache.derby.jdbc.EmbeddedDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:derby:/test;create=true" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.SybaseDialect", "com.sybase.jdbc2.jdbc.SybDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:sybase:Tds:co3061835-a:5000/tempdb" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.MckoiDialect", "com.mckoi.JDBCDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:mckoi:///", "jdbc:mckoi:local://C:/mckoi1.00/db.conf" }); //$NON-NLS-1$  //$NON-NLS-2$
        
        addDriverAndURLS("org.hibernate.dialect.SAPDBDialect", "com.sap.dbtech.jdbc.DriverSapDB", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:sapdb://localhost/TST" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "com.jnetdirect.jsql.JSQLDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:JSQLConnect://1E1/test" }); //$NON-NLS-1$

        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "com.newatlanta.jturbo.driver.Driver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:JTurbo://1E1:1433/test" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "weblogic.jdbc.mssqlserver4.Driver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:weblogic:mssqlserver4:1E1:1433" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "com.microsoft.jdbc.sqlserver.SQLServerDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:microsoft:sqlserver://1E1;DatabaseName=test;SelectMethod=cursor" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "net.sourceforge.jtds.jdbc.Driver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:jtds:sqlserver://1E1/test" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.InterbaseDialect", "interbase.interclient.Driver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:interbase://localhost:3060/C:/firebird/test.gdb" }); //$NON-NLS-1$ 
        
        addDriverAndURLS("org.hibernate.dialect.InterbaseDialect", "org.firebirdsql.jdbc.FBDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:interbase://localhost:3060/C:/firebird/test.gdb" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.hibernate.dialect.PointbaseDialect", "com.pointbase.jdbc.jdbcUniversalDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { "jdbc:pointbase:embedded:sample" }); //$NON-NLS-1$
        
        addDriverAndURLS("org.teiid.dialect.TeiidDialect", "org.teiid.jdbc.TeiidDriver", //$NON-NLS-1$  //$NON-NLS-2$
                new String[] { 
        			"jdbc:teiid:vdb@mm://<host>:<port>;user=<user>;password=<password>",  //$NON-NLS-1$ 
        			"jdbc:teiid:vdb@mms://<host>:<port>;user=<user>;password=<password>",  //$NON-NLS-1$ 
        			"jdbc:teiid:vdb@<path_to>/deploy.properties;user=<user>;password=<password>",  //$NON-NLS-1$
       			}
        );
    }

    /**
     * @param dialect TODO
     * 
     */
    private void addDriverAndURLS(String dialect, String driverclass, String[] urls) {        
        add(driverClasses,  dialect, driverclass);
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            add(connectionUrls, driverclass, url);    
        }
        driverToDialect.put(driverclass, dialect);
    }
   
    /**
     * @param connectionUrls2
     * @param string
     * @param string2
     */
    private void add(Map<String, Set<String>> map, String key, String value) {
        Set<String> existing = map.get(key);
        if(existing==null) {
            existing = new HashSet<String>();
            map.put(key,existing);
        }
        existing.add(value);
    }

    public String[] getDialectNames() {
        List<String> list = new ArrayList<String>(dialectNames.keySet() );
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
    
    /**
     * 
     * @param fullName
     * @return short dialect name by corresponding class if available, otherwise return fullName assuming it is a "raw" classname.
     */
    public String getShortDialectName(String fullName) {
    	for (Entry<String, String> entry : dialectNames.entrySet()) {
    		if (entry.getValue().equals(fullName)) return entry.getKey();
		}
    	return fullName;
    }
    
    /**
     * 
     * @param driverclass
     * @return dialect by driverclass.
     */
    public String getDialect(String driverclass) {
    	if (driverclass == null) return null;
    	return driverToDialect.get(driverclass);
    }
    
    /**
     * 
     * @param dialectName
     * @return corresponding class name if available, otherwise return dialectName assuming it is a "raw" classname
     */
    public String getDialectClass(String dialectName) {
    	String dialectClass = dialectNames.get(dialectName);
    	return dialectClass != null ? dialectClass : dialectName;
    }
    
    public String[] getDriverClasses(String dialectName) {
        Set<String> result = driverClasses.get(dialectName);
        if(result != null) {
            return result.toArray(new String[result.size()]);
        } else {
            return new String[0];
        }
    }
    
    public String[] getConnectionURLS(String driverclass) {
        Set<String> result = connectionUrls.get(driverclass);
        if(result != null) {
            return result.toArray(new String[result.size()]);
        } else {
            return new String[0];
        }
        
    }
}
