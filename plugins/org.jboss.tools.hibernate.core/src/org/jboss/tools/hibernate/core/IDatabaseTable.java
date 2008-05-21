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
package org.jboss.tools.hibernate.core;

import java.sql.DatabaseMetaData;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.hibernate.internal.core.data.Column;



/**
 * @author alex
 *
 * Interface of database table related information.
 */
public interface IDatabaseTable extends IOrmElement  {
	public String getShortName();
    public IDatabaseSchema getSchema();
	public String getSchemaName();
    public IDatabaseColumn[] getColumns();
	public Iterator<IDatabaseColumn> getColumnIterator();
    public IDatabaseColumn getColumn(String columnName);
    //akuzmin 17.06.2005
	public IDatabaseColumn getOrCreateColumn(String columnName);    
	public void renameColumn(IDatabaseColumn column, String newColumnName);

    public IDatabaseTablePrimaryKey getPrimaryKey();
    public Iterator getForeignKeyIterator();
    public Iterator getIndexIterator();
	public Iterator getUniqueKeyIterator();
	
    // changed by Nick 12.09.2005 - changed to List
	public IDatabaseConstraint createUniqueKey(List columns);
	// by Nick
    
	
    //add gavrs 31. 3.05
    
    // changed by Nick 06.06.2005 - Metadata object reused now
	//public void fillTableFromDBase(IMapping imap,Connection con);
    // changed by Nick 30.08.2005 - signature extended - useNativeTypes
    public void fillTableFromDBase(IMapping imap,DatabaseMetaData md, boolean useNativeTypes);
	//by Nick
    /**
	 * Returns list of PC Mappings that the table is participated in
	 * */
	public IPersistentClassMapping[] getPersistentClassMappings();
	public void addPersistentClassMapping(IPersistentClassMapping mapping);
	public void removePersistentClassMapping(IPersistentClassMapping mapping);
	
	public boolean isView();
	public void setSchema(IDatabaseSchema schema);
	public void addColumns(IDatabaseTable source);
	public void addColumn(IDatabaseColumn column); 
	public void removeColumn(String columnName); 
	
	public boolean isForeignKey(String columnName); //by Nick 22.04.2005
    public void setPrimaryKey(IDatabaseTablePrimaryKey pk);
	
	public String getIndexName(String columnName);
	public String getUniqueKeyName(String columnName);
	
    // added by Nick 16.06.2005
	public IDatabaseTableForeignKey getForeignKey(String fkName);    
    // by Nick
    
    // added by Nick 19.07.2005
	public IDatabaseTableForeignKey[] getForeignKeys(String columnName);
    // by Nick
	
// added by yk 26.07.2005
	public Column getColumn(Column column);
// added by yk 26.07.2005 stop
	//akuzmin 13.09.2005
    public boolean isColumnExists(String colname);


}
