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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableColumnSet;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.internal.core.data.Constraint;
import org.jboss.tools.hibernate.internal.core.data.ForeignKey;
import org.jboss.tools.hibernate.internal.core.util.SearchUtils;


/**
 * @author Nick
 *
 */
public class SchemaProcessor {
	
	private Set<IDatabaseTable> tables = new HashSet<IDatabaseTable>();
	private Map<IDatabaseTable,Vector<IDatabaseTableForeignKey>> linkTablesMap = new HashMap<IDatabaseTable,Vector<IDatabaseTableForeignKey>>();
	//private Map prefices = new HashMap();
	private Pattern prefixPattern = null;
    
    private ConfigurationReader configurationReader;
    
    private Map<IDatabaseTable,IDatabaseTableColumnSet> identifiers = new HashMap<IDatabaseTable,IDatabaseTableColumnSet>();
    
    private Vector<IDatabaseTable> persistentTables = new Vector<IDatabaseTable>();
    private Vector<IDatabaseTable> linkingTables = new Vector<IDatabaseTable>();
    
    private boolean discoverLinkTables = true;
	
	private void buildPrefixQuery(String query)
	{
		if (query != null && query.length() != 0)
		{
			prefixPattern = Pattern.compile("^"+query);
		}
	}
	
	public SchemaProcessor(IDatabaseTable[] tables, ConfigurationReader configurationReader)
	{
        this.discoverLinkTables = configurationReader.isDiscoverLinkTables();
        
		for (int i = 0; i < tables.length; i++) {
			if (tables[i] != null)
			    this.tables.add(tables[i]);
		}
        
        this.configurationReader = configurationReader;
        
		buildPrefixQuery(configurationReader.getTableNamePrefix());
	}
	
	private void addToPKConstraint(IDatabaseTable table, IDatabaseTableForeignKey fKey) {
		Vector<IDatabaseTableForeignKey> value = linkTablesMap.get(table);
		if (value == null) {
			value = new Vector<IDatabaseTableForeignKey>();
            linkTablesMap.put(table,value);
		}
        value.add(fKey);
    }
	
	public void processTables() {
		IDatabaseTable[] tables_array = (IDatabaseTable[])tables.toArray(new IDatabaseTable[0]);
//		Map pKeyColsNames = new HashMap();
		//builds structures for discovering table prefices
		for (int i = 0 ; i < tables_array.length ; i++)
		{
			IDatabaseTable table = tables_array[i];
//			
//            String[] names;
//			IDatabaseTablePrimaryKey pk = table.getPrimaryKey();
//			if (pk != null)
//			{
//				names = new String[pk.getColumnSpan()];
//				Iterator itr = pk.getColumnIterator();
//				int k = 0;
//				while (itr.hasNext())
//				{
//					IDatabaseColumn column = (IDatabaseColumn)itr.next();
//					names[k] = table.getShortName() + "_" + column.getName();
//					k++;
//				}
//			}
//			else
//			{
//				names = new String[0];
//			}
//			pKeyColsNames.put(table,names);
            
            IDatabaseTableColumnSet id = buildIdentifierColumns(table);
            
            if (id != null) {
                identifiers.put(table,id);
            }
		}
		
		//builds structures for discovering many-to-many link tables
		for (int i = 0 ; i < tables_array.length ; i++)
		{
			IDatabaseTable table = tables_array[i];
			Iterator fKeys = table.getForeignKeyIterator();
			if (fKeys != null)
			{
				while (fKeys.hasNext())
				{
					ForeignKey fKey = (ForeignKey)fKeys.next();
					IDatabaseTable refTable = fKey.getReferencedTable();
					if (refTable != null)
					{
						IDatabaseTableColumnSet pKey = null;
                        if (tables.contains(refTable))
                            pKey = getIdentifier(refTable);
                        else if (HibernateAutoMappingHelper.tableHasMappings(refTable))
                            pKey = getExistingIdentifierColumns(refTable);
                        
						//check referenced PK existence
                        boolean fKeyTableHasPK = (pKey != null && pKey.getColumnSpan() != 0 && fKey.getColumnSpan() == pKey.getColumnSpan());

						boolean fKeyColsArePK = fKeyTableHasPK;
						
//						Iterator fkey_columns = fKey.getColumnIterator();
//						IDatabaseColumn[] columns = new IDatabaseColumn[fKey.getColumnSpan()];
						
                        IDatabaseTableColumnSet thisPKey = getIdentifier(table);
                        
                        //treat identifier column sets brought by Name Search as not helpful 
                        //in link tables discovery
                        if (! (thisPKey instanceof IDatabaseConstraint) )
                            thisPKey = null;
                        
                        if (thisPKey != null && thisPKey.getColumnSpan() != 0 && !thisPKey.includes(fKey))
                            fKeyColsArePK = false;
                        
                        if (fKeyColsArePK)
                        {
                            addToPKConstraint(table,fKey);

//                            for (int j = 0; j < columns.length; j++) {
//                                columns[j] = (IDatabaseColumn)fkey_columns.next();
//                                
//                                //try to find referenced table prefix
//                                String fKeyColumnName = columns[j].getName();
//                                String[] pKeyColumnsNamesArray = (String[]) pKeyColsNames.get(refTable);
//                                if (pKeyColumnsNamesArray != null)
//                                {
//                                    int index = 0;
//                                    boolean found = false;
//                                    while (index < pKeyColumnsNamesArray.length && !found)
//                                    {
//                                        if (pKeyColumnsNamesArray[index].endsWith(fKeyColumnName))
//                                        {
//                                            found = true;
//                                            int prefixLength = pKeyColumnsNamesArray[index].length() - fKeyColumnName.length();
//                                            Set preficesSet;
//                                            if (!prefices.containsKey(table.getSchema()))
//                                            {
//                                                preficesSet = new TreeSet();
//                                                prefices.put(table.getSchema(),preficesSet);
//                                            }
//                                            else
//                                                preficesSet = (Set)prefices.get(table.getSchema());
//                                            String prefix = pKeyColumnsNamesArray[index].substring(0,prefixLength);
//                                            preficesSet.add(prefix);
//                                        }
//                                        index++;
//                                    }
//                                }
//                                //
//                            }
                        }
					}
				}
			}
		}

        Iterator itr = tables.iterator();
        if (itr != null) {
            while (itr.hasNext()) {
                IDatabaseTable table = (IDatabaseTable)itr.next();
                Vector<IDatabaseTableForeignKey> value = linkTablesMap.get(table);
                if (value == null || (discoverLinkTables ? (value.size() != 2) : true) ) {
                    if (getIdentifier(table) != null) {
                        persistentTables.add(table);
                    }
                } else {
                    linkingTables.add(table);
                }
            }
        }
    }
	
    private IDatabaseTableColumnSet getExistingIdentifierColumns(IDatabaseTable table)
    {
        if (HibernateAutoMappingHelper.tableHasMappings(table))
        {
            IPersistentClassMapping cm = table.getPersistentClassMappings()[0];
            if (cm.getIdentifier() != null && cm.getIdentifier().getColumnSpan() != 0)
            {
                return new ColumnSet(cm.getIdentifier().getColumnIterator());
            }
        }
        return null;
    }
    
    private IDatabaseTableColumnSet buildIdentifierColumns(IDatabaseTable table)
    {
        if (table == null)
            return null;
        
        if (table.getPrimaryKey() != null && table.getPrimaryKey().getColumnSpan() != 0)
            return table.getPrimaryKey();
        
        IDatabaseTableColumnSet c = null;

        c = getExistingIdentifierColumns(table);
        if (c != null)
            return c;        
        
        Iterator uks = table.getIndexIterator();
        
        while (uks.hasNext())
        {
            Constraint uk = (Constraint) uks.next(); 
            if ( (c == null || uk.getColumnSpan() > c.getColumnSpan()) && uk.isUnique())
                c = uk;
        }
        
        if (c != null)
            return c;

        Iterator columns = table.getColumnIterator();
        while (columns.hasNext() && c == null)
        {
            IDatabaseColumn column = (IDatabaseColumn) columns.next();
            
            if (column.isUnique())
                c = new ColumnSet(column);
        }
        
        if (c != null)
            return c;

        // edit tau 07.06.2006
        // TODO 07.08.2006 -> configurationReader.get... 
        //IDatabaseColumn column = SearchUtils.findColumn(table.getColumns(),configurationReader.getIdentifierColumnName(),configurationReader.isUseFuzzySearch());
        IDatabaseColumn column = SearchUtils.findColumn(table.getColumns(),configurationReader.getIdentifierQuery(),configurationReader.isUseFuzzySearch());        
        
        if (column != null)
            return new ColumnSet(column);
        
        return null;
    }
    
	public IDatabaseTable[] getPersistentTables()
	{
		return (IDatabaseTable[]) persistentTables.toArray(new IDatabaseTable[0]);
	}
    
    public IDatabaseTable[] getLinkingTables()
    {
        return (IDatabaseTable[]) linkingTables.toArray(new IDatabaseTable[0]);
    }
	
	public String getUnPrefixedTableName(IDatabaseTable table)
	{
		String tableName = table.getShortName();
		if (prefixPattern == null)
		{
//			Set set = (Set)prefices.get(table.getSchema());
//			if (set != null)
//			{
//				boolean found = false;
//				Iterator itr = set.iterator();
//				while (itr.hasNext() && !found)
//				{
//					String prefix = (String)itr.next();
//					if (tableName.startsWith(prefix))
//					{
//						String str = tableName.substring(prefix.length());
//						if (str.length() != 0)
//                        {
//                            tableName = str;
//                            found = true;
//                        }
//					}
//				}
//			}
		}
		else
		{
			String newTableName = prefixPattern.matcher(tableName).replaceFirst("");
			if (newTableName != null && newTableName.length() != 0)
				tableName = newTableName;
		}
		return tableName;
	}
	
    public IDatabaseTableForeignKey[] getLinkedTables (IDatabaseTable table) {
        Vector<IDatabaseTableForeignKey> vector = linkTablesMap.get(table);
        if (vector == null)
            return null;
        return vector.toArray(new IDatabaseTableForeignKey[0]);
    }
    
    public IDatabaseTableColumnSet getIdentifier(IDatabaseTable table) {
        if (table == null)
            return null;        
        if (identifiers.containsKey(table)) {
            return identifiers.get(table);
        }        
        return null;
    }

}
