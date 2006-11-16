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

/**
 * @author max
 */
public class DriverClassHelpers {

    private Map dialectNames = new HashMap();
    private Map connectionUrls = new HashMap();
    private Map driverClasses = new HashMap();
    
    public DriverClassHelpers() {
        // externalize this!
        dialectNames.put("DB2", "org.hibernate.dialect.DB2Dialect");
        dialectNames.put("DB2390", "org.hibernate.dialect.DB2390Dialect");
        dialectNames.put("DB2400", "org.hibernate.dialect.DB2400Dialect");
        dialectNames.put("Derby", "org.hibernate.dialect.DerbyDialect");
        dialectNames.put("FrontBase", "org.hibernate.dialect.FrontBaseDialect");
        dialectNames.put("Generic", "org.hibernate.dialect.GenericDialect");
        dialectNames.put("HSQL", "org.hibernate.dialect.HSQLDialect");
        dialectNames.put("Informix", "org.hibernate.dialect.InformixDialect");
        dialectNames.put("Ingres", "org.hibernate.dialect.IngresDialect");
        dialectNames.put("Interbase", "org.hibernate.dialect.InterbaseDialect");
        dialectNames.put("Firebird", "org.hibernate.dialect.FirebirdDialect");
        dialectNames.put("Mckoi SQL", "org.hibernate.dialect.MckoiDialect");
        dialectNames.put("MySQL", "org.hibernate.dialect.MySQLDialect");
        dialectNames.put("MySQLInnoDB", "org.hibernate.dialect.MySQLInnoDBDialect");
        dialectNames.put("MySQLISAMDB", "org.hibernate.dialect.MySQLISAMDBDialect");
        dialectNames.put("Oracle9", "org.hibernate.dialect.Oracle9Dialect");
        dialectNames.put("Oracle","org.hibernate.dialect.OracleDialect");
        dialectNames.put("Pointbase", "org.hibernate.dialect.PointbaseDialect");
        dialectNames.put("PostgreSQL", "org.hibernate.dialect.PostgreSQLDialect");
        //dialectNames.put("Postgress", "org.hibernate.dialect.PostgressDialect");
        dialectNames.put("Progress", "org.hibernate.dialect.ProgressDialect");        
        dialectNames.put("SAP DB", "org.hibernate.dialect.SAPDBDialect");
        dialectNames.put("Sybase", "org.hibernate.dialect.SybaseDialect");
        dialectNames.put("Sybase11", "org.hibernate.dialect.Sybase11Dialect");
        dialectNames.put("SybaseAnywhere", "org.hibernate.dialect.SybaseAnywhereDialect");
        dialectNames.put("SQLServer", "org.hibernate.dialect.SQLServerDialect");

        addDriverAndURLS("org.hibernate.dialect.HSQLDialect",
                         "org.hsqldb.jdbcDriver", 
                         new String[] {
                            "jdbc:hsqldb:hsql://<host>", 
                            "jdbc:hsqldb:<dbname>",
                            "jdbc:hsqldb:." }
                         );

        addDriverAndURLS("org.hibernate.dialect.OracleDialect", 
                         "oracle.jdbc.driver.OracleDriver",
                         new String[] {
                            "jdbc:oracle:thin:@localhost:1521:orcl",
                            "jdbc:oracle:thin:@<host>:<port1521>:<sid>" }
                         );
        
        addDriverAndURLS("org.hibernate.dialect.Oracle9Dialect", 
                "oracle.jdbc.driver.OracleDriver",
                new String[] {
                   "jdbc:oracle:thin:@localhost:1521:orcl",
                   "jdbc:oracle:thin:@<host>:<port1521>:<sid>" }
                );

        addDriverAndURLS("org.hibernate.dialect.MySQLDialect", 
                         "org.gjt.mm.mysql.Driver",
                         new String[] {
                            "jdbc:mysql://<hostname>/<database>",
                            "jdbc:mysql:///test",
                            "jdbc:mysql:///<name>",
                             }
                        );

        addDriverAndURLS("org.hibernate.dialect.MySQLDialect", 
                "com.mysql.jdbc.Driver",
                new String[] {        		
                   "jdbc:mysql://<hostname>/<database>",
                   "jdbc:mysql:///test",
                   "jdbc:mysql:///<name>",
                   "jdbc:mysql://<host><:port>/<database>"
                    }
               );
        addDriverAndURLS("org.hibernate.dialect.MySQLInnoDBDialect", 
                    "org.gjt.mm.mysql.Driver",
                    new String[] {
                       "jdbc:mysql://<hostname>/<database>",
                       "jdbc:mysql:///test",
                       "jdbc:mysql:///<name>" }
                   );
        

        addDriverAndURLS("org.hibernate.dialect.MySQLMyISAMDBDialect", 
                    "org.gjt.mm.mysql.Driver",
                    new String[] {
                       "jdbc:mysql://<hostname>/<database>",
                       "jdbc:mysql:///test",
                       "jdbc:mysql:///<name>" }
                   );
        
        addDriverAndURLS("org.hibernate.dialect.PostgreSQLDialect", 
                "org.postgresql.Driver",
                new String[] {
                   "jdbc:postgresql:template1",
                   "jdbc:postgresql:<name>" }
               );
        
        addDriverAndURLS("org.hibernate.dialect.ProgressDialect", 
                "com.progress.sql.jdbc.JdbcProgressDriver",
                new String[] {
                   "jdbc:JdbcProgress:T:host:port:dbname;WorkArounds=536870912",
                   }
               );
        
        addDriverAndURLS("org.hibernate.dialect.DB2Dialect", "COM.ibm.db2.jdbc.app.DB2Driver",
                          new String[] { "jdbc:db2:test", "jdbc:db2:<name>" });
        
        addDriverAndURLS("org.hibernate.dialect.DB2400Dialect", "com.ibm.as400.access.AS400JDBCDriver",
                new String[] { "jdbc:as400://<systemname>", "jdbc:db2:<name>" });
        
        addDriverAndURLS("org.hibernate.dialect.DerbyDialect", "org.apache.derby.jdbc.EmbeddedDriver",
                new String[] { "jdbc:derby:/test;create=true" });
        
        addDriverAndURLS("org.hibernate.dialect.SybaseDialect", "com.sybase.jdbc2.jdbc.SybDriver",
                new String[] { "jdbc:sybase:Tds:co3061835-a:5000/tempdb" });
        
        addDriverAndURLS("org.hibernate.dialect.MckoiDialect", "com.mckoi.JDBCDriver",
                new String[] { "jdbc:mckoi:///", "jdbc:mckoi:local://C:/mckoi1.00/db.conf" });
        
        addDriverAndURLS("org.hibernate.dialect.SAPDBDialect", "com.sap.dbtech.jdbc.DriverSapDB",
                new String[] { "jdbc:sapdb://localhost/TST" });
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "com.jnetdirect.jsql.JSQLDriver",
                new String[] { "jdbc:JSQLConnect://1E1/test" });

        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "com.newatlanta.jturbo.driver.Driver",
                new String[] { "jdbc:JTurbo://1E1:1433/test" });
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "weblogic.jdbc.mssqlserver4.Driver",
                new String[] { "jdbc:weblogic:mssqlserver4:1E1:1433" });
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "com.microsoft.jdbc.sqlserver.SQLServerDriver",
                new String[] { "jdbc:microsoft:sqlserver://1E1;DatabaseName=test;SelectMethod=cursor" });
        
        addDriverAndURLS("org.hibernate.dialect.SQLServerDialect", "net.sourceforge.jtds.jdbc.Driver",
                new String[] { "jdbc:jtds:sqlserver://1E1/test" });
        
        addDriverAndURLS("org.hibernate.dialect.InterbaseDialect", "interbase.interclient.Driver",
                new String[] { "jdbc:interbase://localhost:3060/C:/firebird/test.gdb" });
        
        addDriverAndURLS("org.hibernate.dialect.InterbaseDialect", "org.firebirdsql.jdbc.FBDriver",
                new String[] { "jdbc:interbase://localhost:3060/C:/firebird/test.gdb" });
        
        addDriverAndURLS("org.hibernate.dialect.PointbaseDialect", "com.pointbase.jdbc.jdbcUniversalDriver",
                new String[] { "jdbc:pointbase:embedded:sample" });
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
    }
   
    /**
     * @param connectionUrls2
     * @param string
     * @param string2
     */
    private void add(Map map, String key, String value) {
        Set existing = (Set) map.get(key);
        if(existing==null) {
            existing = new HashSet();
            map.put(key,existing);
        }
        existing.add(value);
    }

    public String[] getDialectNames() {
        List list = new ArrayList(dialectNames.keySet() );
        Collections.sort(list);
        return (String[]) list.toArray(new String[list.size()]);
    }
    
    /**
     * 
     * @param dialectName
     * @return corresponding class name if available, otherwise return dialectName assuming it is a "raw" classname
     */
    public String getDialectClass(String dialectName) {
        if(dialectNames.containsKey(dialectName) ) {
            return (String) dialectNames.get(dialectName);
        } else {
            return dialectName;
        }
    }
    
    public String[] getDriverClasses(String dialectName) {
        Set result = (Set) (driverClasses.get(dialectName) );
        if(result!=null) {
            return (String[]) result.toArray(new String[result.size()]);
        } else {
            return new String[0];
        }
    }
    
    public String[] getConnectionURLS(String driverclass) {
        Set result = (Set) (connectionUrls.get(driverclass) );
        if(result!=null) {
            return (String[]) result.toArray(new String[result.size()]);
        } else {
            return new String[0];
        }
    }
}
