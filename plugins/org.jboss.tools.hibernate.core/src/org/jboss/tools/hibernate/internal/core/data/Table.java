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
package org.jboss.tools.hibernate.internal.core.data;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IDatabaseTableForeignKey;
import org.jboss.tools.hibernate.core.IDatabaseTablePrimaryKey;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.UpdateMappingVisitor;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;



/**
 * @author alex
 *
 * A relational table
 * @see org.hibernate.mapping.Table
 */
public class Table extends DataObject implements IDatabaseTable {
	private static final long serialVersionUID = 1L;
	public static String[] TABLE_VIEW = {"TABLE","VIEW"};
	public static String[] TABLE = {"TABLE"};
//	private static final String HIBERNATE_QUOTE = "`";
	public static String COLUMN_NAME= "COLUMN_NAME",COLUMN_SIZE="COLUMN_SIZE", TYPE_NAME="TYPE_NAME",DATA_TYPE="DATA_TYPE", NULLABLE="NULLABLE",
						INDEX_NAME="INDEX_NAME",TYPE="TYPE", NON_UNIQUE="NON_UNIQUE",
						PK_NAME ="PK_NAME",PKTABLE_NAME="PKTABLE_NAME",FKTABLE_NAME="FKTABLE_NAME",PKCOLUMN_NAME="PKCOLUMN_NAME",
						FK_NAME="FK_NAME";
	private IDatabaseSchema schema;
	/**
	 * contain all columns, including what is inside PrimaryKey
	 */
	private Map columns = new SequencedHashMap();
	private IDatabaseTablePrimaryKey primaryKey;
	private Map<String,Index> indexes = new HashMap<String,Index>();
	private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
	private Map<String,Constraint> uniqueKeys = new HashMap<String,Constraint>();
	private boolean quoted;
	private boolean view=false;
	
	
	private List<IPersistentClassMapping> classMappings = new ArrayList<IPersistentClassMapping>();
	private static final IPersistentClassMapping[] MAPPINGS={};
	private static final IDatabaseColumn[] COLUMNS={};
	
	public Table() {
	}
	
	public Table(String name) {
		this();
		setName(name);
	}

	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#getColumn(java.lang.String)
	 */
	public IDatabaseColumn getColumn(String columnName) {
		return (IDatabaseColumn)columns.get(columnName);
	}

	/**
	 * @param columnName
	 * @return column
	 * akuzmin 17.06.2005
	 */
	public IDatabaseColumn getOrCreateColumn(String columnName) {
		if (getColumn(columnName)==null)
		{
			Column newcol=new Column();
			newcol.setName(columnName);
			addColumn(newcol);
		}
		return getColumn(columnName);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#getColumns()
	 */
	public IDatabaseColumn[] getColumns() {
		// add tau 24.03.2006 from AbstractMapping.refresh(...)
		// TODO (tau->tau) 24.03.2006
    	UpdateMappingVisitor updatevisitor = new UpdateMappingVisitor(getSchema().getProjectMapping());
    	updatevisitor.doMappingsUpdate(getPersistentClassMappings());		
		
		return (IDatabaseColumn[])columns.values().toArray(COLUMNS);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#getPersistentClassMappings()
	 */
	public IPersistentClassMapping[] getPersistentClassMappings() {
		return (IPersistentClassMapping[])classMappings.toArray(MAPPINGS);
	}
	public String getSchemaName(){
		String name=getName();
		int i=name.lastIndexOf('.');
		if(i==-1)return "";
		return name.substring(0,i);
	}
	public String getShortName(){
		String name=getName();
		int i=name.lastIndexOf('.');
		if(i==-1)return name;
		//if(name.startsWith(HIBERNATE_QUOTE))
		//	return  HIBERNATE_QUOTE+name.substring(i+1);
		return name.substring(i+1);
	}
	public static String toFullyQualifiedName(String catalog, String schema, String table){
		String res=table;
		if(schema!=null && schema.length() > 0) res=schema+"."+res;
		if(catalog!=null  && catalog.length() > 0) res= catalog+"."+res;
		return res;
	}

	public void addPersistentClassMapping(IPersistentClassMapping classMapping){
		// added by Nick 16.06.2005
	    if (!classMappings.contains(classMapping))
        // by Nick
	        classMappings.add(classMapping);
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#removePersistentClassMapping(org.jboss.tools.hibernate.core.IPersistentClassMapping)
	 */
	public void removePersistentClassMapping(IPersistentClassMapping mapping) {
		classMappings.remove(mapping);
		
	}

	/**
	 * Return the column which is identified by column provided as argument.
	 *
	 * @param column column with atleast a name.
	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
	 */
	public Column getColumn(Column column) {
		Column myColumn = ( Column ) columns.get( column.getName() );

		if ( column.equals( myColumn ) ) {
			return myColumn;
		}
		else {
			return null;
		}
	}

	public Column getColumn(int n) {
		Iterator iter = columns.values().iterator();
		for ( int i = 0; i < n - 1; i++ ) iter.next();
		return ( Column ) iter.next();
	}

	public void addColumn(IDatabaseColumn column) {
		IDatabaseColumn old = ( IDatabaseColumn ) columns.get( column.getName() );
		if ( old == null ) {
			columns.put( column.getName(), column );
		}
		column.setOwnerTable(this);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#addColumns(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void addColumns(IDatabaseTable source) {
		Iterator it=source.getColumnIterator();
		while(it.hasNext()){
			IDatabaseColumn col=(IDatabaseColumn) it.next();
			addColumn(col);
		}
	}
	

	public int getColumnSpan() {
		return columns.size();
	}

	public Iterator<IDatabaseColumn> getColumnIterator() {
		final Iterator it = columns.values().iterator();
		return new Iterator<IDatabaseColumn>(){
			public boolean hasNext() {
				return it.hasNext();
			}

			public IDatabaseColumn next() {
				return (IDatabaseColumn)it.next();
			}

			public void remove() {
				it.next();
			}			
		};
	}

	public Iterator getIndexIterator() {
		return indexes.values().iterator();
	}

	public Iterator getForeignKeyIterator() {
		return foreignKeys.iterator();
	}
	
	public void addForeignKey(String columnName, ForeignKey foreignKey){
	    // added by Nick 16.06.2005
	    if (!foreignKeys.contains(foreignKey))
        // by Nick
	        foreignKeys.add(foreignKey);
	}
	
	public IDatabaseTableForeignKey[] getForeignKeys(String columnName){
		// added by Nick 19.07.2005
        List<IDatabaseTableForeignKey> result = new ArrayList<IDatabaseTableForeignKey>();
        
        Iterator fKeys = foreignKeys.iterator();
        while (fKeys.hasNext()) {
            IDatabaseTableForeignKey fKey = (IDatabaseTableForeignKey) fKeys.next();
            if (fKey.containsColumn(columnName))
                result.add(fKey);
        }
        return result.toArray(new IDatabaseTableForeignKey[0]);
        // by Nick
    }
	
	public Iterator getUniqueKeyIterator() {
		return uniqueKeys.values().iterator();
	}


	public IDatabaseTablePrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(IDatabaseTablePrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
		if(primaryKey!=null) primaryKey.setTable(this);
	}


	public Index getOrCreateIndex(String indexName) {
		Index index = ( Index ) indexes.get( indexName );

		if ( index == null ) {
			index = new Index();
			index.setName( indexName );
			index.setTable( this );
			indexes.put( indexName, index );
		}

		return index;
	}

	public Index getIndex(String indexName) {
		return ( Index ) indexes.get( indexName );
	}

	public Index addIndex(Index index) {
		Index current = ( Index ) indexes.get( index.getName() );
		if ( current != null ) {
			throw new RuntimeException( "Index " + index.getName() + " already exists!" );
		}
		indexes.put( index.getName(), index );
		return index;
	}

	public IDatabaseSchema getSchema() {
		return schema;
	}

	public void setSchema(IDatabaseSchema schema) {
		this.schema = schema;
	}

	public boolean isQuoted() {
		return quoted;
	}

	public void setQuoted(boolean quoted) {
		this.quoted = quoted;
	}


	public boolean containsColumn(Column column) {
		return columns.containsValue( column );
	}


	public String toString() {
		StringBuffer buf = new StringBuffer().append( getClass().getName() )
			.append('(');
		if ( getSchema()!=null ) buf.append( getSchema() );
		buf.append( getName() ).append(')');
		return buf.toString();
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitDatabaseTable(this,argument);
	}
	
	// changed by Nick 06.06.2005 - Metadata object reused now
	private void loadColumns(DatabaseMetaData md, boolean useNativeSQLType) {
//  private void loadColumns(Connection con) {
		//by Nick
        String nameColum = null;
		ResultSet columnsRS = null;

        List<IDatabaseColumn> processedColumns = new ArrayList<IDatabaseColumn>();
		
        try {
			// changed by Nick 06.06.2005
            //DatabaseMetaData md = con.getMetaData();
		    //by Nick

            columnsRS = md.getColumns(this.schema.getCatalog(), this.schema
					.getShortName(), StringUtils.hibernateUnEscapeName(getShortName()), null);// Retrieves a
															// description of
															// table columns
															// available in the
															// specified
           												// catalog.
			while (columnsRS.next())
			{
                nameColum = StringUtils.hibernateEscapeName(columnsRS.getString(COLUMN_NAME));
				IDatabaseColumn c = (IDatabaseColumn) getOrCreateColumn(nameColum);
				c.setSqlTypeCode(columnsRS.getInt(DATA_TYPE));

				// changed by Nick 28.06.2005
				int length = columnsRS.getInt(COLUMN_SIZE);
				if (c.getSqlTypeCode() == Types.VARCHAR || c.getSqlTypeCode() == Types.CHAR)
				    c.setLength(length);
				
				if (c.getSqlTypeCode() == Types.NUMERIC || c.getSqlTypeCode() == Types.DECIMAL)
				{
				    c.setPrecision(length);
				    c.setScale(columnsRS.getInt("DECIMAL_DIGITS"));
				}

                if(useNativeSQLType)
                {
	                String rawTypeName = columnsRS.getString(TYPE_NAME);
	                
                    // changed by Nick 30.08.2005
                        c.setSqlTypeName(rawTypeName.replaceAll("[\\s|\\(][\\W\\w\\s]*",""));
                    //by Nick
                    // added  by yk 30/08/2005
	                final String identity = "identity";
	                if(rawTypeName.indexOf(identity) != -1)
	                { 	
	                	if(identity.equals(StringUtils.getWord(rawTypeName," ", 1)) )
	                		rawTypeName = StringUtils.getWord(rawTypeName," ", 0);		}
	                // added  by yk 30/08/2005.
                }// if(saveNativeSQLTypes)
//                else
//                {
//    				//by akuzmin 30.06.2005
//    				if (TypeUtils.columnTypeToHibType(c, true, null)==null)
//    				    c.setSqlTypeName(columnsRS.getString(TYPE_NAME));					
//                }
				int nullable = columnsRS.getInt(NULLABLE);
				boolean isNull = (nullable == 1);
				c.setNullable(isNull);
				addColumn(c);
                processedColumns.add(c);
				c.setOwnerTable(this);
			}// while
		} catch (Exception ex) {
			ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
		} finally {
			try {
				if (columnsRS != null)
					columnsRS.close();
			} catch (SQLException ex) {
				ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
			}
		}
        columns.values().retainAll(processedColumns);
	}
	
// removed by Nick 16.09.2005
// added by yk 29.08.2005
//	private void fillCreateParams(DatabaseMetaData md) throws SQLException
//	{
//        ResultSet rset = md.getTypeInfo();
//        while(rset.next())
//        {		TypeUtils.createParams.put(rset.getString("TYPE_NAME"), rset.getString("CREATE_PARAMS"));		}
//	}
// added by yk 29.08.2005.
// by Nick
    
    // changed by Nick 06.06.2005 - Metadata object reused now
    // changed by Nick 30.08.2005 - signature extended - useNativeTypes
	public void fillTableFromDBase(IMapping imap,DatabaseMetaData md,boolean useNativeTypes){
        //public void fillTableFromDBase(IMapping imap,Connection con){
        
// added by yk 29.08.2005
//		if(useNativeTypes)
//		{
//			try{	fillCreateParams(md);	}
//			catch(SQLException exc)
//			{		ExceptionHandler.logInfo("Error of createParams getting."); }
//		}
// added by yk 29.08.2005.
		
        // changed by Nick 30.08.2005
        loadColumns(md,useNativeTypes);
        // by Nick
        
        // added by Nick 06.06.2005
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin(">>> Loaded - columns",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        // by Nick
		createPrimaryKey(md);
        // added by Nick 06.06.2005
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin(">>> Loaded - PK",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        // by Nick
		createForeignKey(imap, md);
        // added by Nick 06.06.2005
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin(">>> Loaded - FK's",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        // by Nick
        //comment by akuzmin 29.06.2005
		loadIndexes(md);
        // added by Nick 06.06.2005
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logObjectPlugin(">>> Loaded - indices",
                OrmCore.getDefault().getBundle().getSymbolicName(), this);
        // by Nick
	}
    //by Nick
	
    // changed by Nick 06.06.2005 - Metadata object reused now
	private void createPrimaryKey(DatabaseMetaData md){
	    //by Nick
        ResultSet rsPK=null;
		try{
			PrimaryKey prKey=new PrimaryKey();
            
			rsPK=md.getPrimaryKeys(this.schema.getCatalog(),this.schema.getShortName(),StringUtils.hibernateUnEscapeName( getShortName()));
			while (rsPK.next()) {	
			        // changed by Nick 28.07.2005
			        //String columnName = rsPK.getString(COLUMN_NAME);
			        String columnName = StringUtils.hibernateEscapeName(rsPK.getString(COLUMN_NAME));
			        
                    // by Nick
                Column col=(Column)getColumn(columnName);//column already exist(created in load column)
//					if(!col.isPrimaryKey()){
						prKey.addColumn(col);
						// changed by Nick 28.07.2005
//						String pkName = rsPK.getString(PK_NAME);
                        String pkName = StringUtils.hibernateEscapeName(rsPK.getString(PK_NAME));
                        // by Nick
                        prKey.setName(pkName);
						prKey.setTable(this);
//						setPrimaryKey(prKey);
//					}
			}

            // added by Nick 19.09.2005
			if (prKey.getColumnSpan() != 0)
            // by Nick
			    setPrimaryKey(prKey);
        
        }catch(SQLException ex) {
			ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
		}finally {
			try{
				if (rsPK != null) rsPK.close();
			}catch(SQLException ex) {
				ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
			}
		}

	}
	
    // changed by Nick 06.06.2005 - Metadata object reused now
	private void createForeignKey(IMapping mod, DatabaseMetaData md){
	    //by Nick
        ResultSet rsEK = null;
		IDatabaseTable refTable = null;
		try{
			rsEK = md.getImportedKeys(this.schema.getCatalog(),this.schema.getShortName(), StringUtils.hibernateUnEscapeName( getShortName()));

			this.foreignKeys.clear();
			Map<String,ForeignKey> FKs = new HashMap<String,ForeignKey>();
            while (rsEK.next()) {
				
				//String fkTableCat=rsEK.getString("PKTABLE_CAT");
            	//String fkTableSch = StringUtils.hibernateEscapeName(rsEK.getString("PKTABLE_SCHEM"));
				String fkTableSch = rsEK.getString("PKTABLE_SCHEM");
            	String fkTableNam = StringUtils.hibernateEscapeName(rsEK.getString("PKTABLE_NAME"));
				//String fkTableNam=rsEK.getString("PKTABLE_NAME");
				String fullyfkTableNam = toFullyQualifiedName("", fkTableSch, fkTableNam);// gavrs:6.30.2005 Catalog=""
				if(fullyfkTableNam != null) //by Nick 22.04.2005
	            	refTable = mod.findTable(fullyfkTableNam);

				if(refTable == null) continue; // by alex 04/27/05 - skip unselected tables
                
                int columnSeqIndex = -1;
                
                //String pkColumn = rsEK.getString("PKCOLUMN_NAME"); // changed by Nick 19.11.2005
                String pkColumn = StringUtils.hibernateEscapeName(rsEK.getString("PKCOLUMN_NAME"));
                
                if (refTable.getPrimaryKey() != null)
                {
                    columnSeqIndex = refTable.getPrimaryKey().getColumnIndex(refTable.getColumn(pkColumn));
                }

				String fkColumn = StringUtils.hibernateEscapeName(rsEK.getString("FKCOLUMN_NAME"));
	           // String fkColumn = rsEK.getString("FKCOLUMN_NAME");
				String fkName = StringUtils.hibernateEscapeName(rsEK.getString(FK_NAME));
	            //String fkName=rsEK.getString(FK_NAME);
				//by Nick 22.04.2005
				//String refEntityName = rsEK.getString("PKCOLUMN_NAME");
				//by Nick
				
				ForeignKey fk = FKs.get(fkName);
				if(fk == null) {
					fk = new ForeignKey();
					FKs.put(fkName, fk);
					fk.setName(fkName);
					fk.setReferencedTable(refTable);
					
                    //by Nick 01.06.2005
                    fk.setTable(this);
                    //by Nick
                    //by Nick 22.04.2005
					//by alex - FK referenced PK of the referenced table so you don't need to remember PK columns 
					//fk.setReferencedEntityName(refEntityName); 
					//by Nick
				}
				
				Column col= (Column)getColumn(fkColumn);
				if(col==null){
					col=new Column();
		            col.setName(fkColumn);
					addColumn(col);
				}
				fk.addColumn(col);
                if (columnSeqIndex != -1)
                {
                    fk.setColumnPKOrder(col,columnSeqIndex);
                }
            }
			foreignKeys.addAll(FKs.values());
			
		}catch(SQLException ex) {
			ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
		}finally {
			try{
				if (rsEK != null) rsEK.close();
			}catch(SQLException ex) {
				ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
			}
		}
	}

    
    // changed by Nick 06.06.2005 - Metadata object reused now
    private void loadIndexes(DatabaseMetaData md){
//    	IDatabaseTableForeignKey[] Fks;
        //by Nick
        ResultSet rsInd=null;
		PreparedStatement WSps=null;
//		ResultSet rs =null;
		try{
//			rs = md.getTables(this.schema.getCatalog(),this.schema.getShortName(), StringUtils.hibernateUnEscapeName(getShortName()), TABLE_VIEW);//TABLE);
//			if (rs.next())
//			{
//akuzmin 30.06.2005
			if (md.getDatabaseProductName().equals("Oracle"))
			{
            //WSps = md.getConnection().prepareStatement("SELECT * FROM USER_IND_COLUMNS WHERE TABLE_NAME='"+StringUtils.hibernateUnEscapeName(this.getShortName())+"'");
            // changed by Nick 15.09.2005
            WSps = md.getConnection().prepareStatement("SELECT USER_IND_COLUMNS.*,USER_CONSTRAINTS.CONSTRAINT_TYPE FROM USER_IND_COLUMNS JOIN USER_CONSTRAINTS ON USER_CONSTRAINTS.CONSTRAINT_NAME=USER_IND_COLUMNS.INDEX_NAME and USER_CONSTRAINTS.TABLE_NAME=USER_IND_COLUMNS.TABLE_NAME WHERE USER_IND_COLUMNS.TABLE_NAME='"+StringUtils.hibernateUnEscapeName(this.getShortName())+"'");
            // by Nick
            
	        WSps.executeQuery();
	        rsInd= WSps.getResultSet();
				while (rsInd.next())
				{
					String indexName = StringUtils.hibernateEscapeName(rsInd.getString(INDEX_NAME));
					//String indexName=rsInd.getString(INDEX_NAME);//primary,...
					if(indexName!=null){
						if(!indexes.containsKey(indexName)){
							//String columnName = rsInd.getString(COLUMN_NAME);
							String columnName = StringUtils.hibernateEscapeName( rsInd.getString(COLUMN_NAME));
							Column col=(Column)getColumn(columnName);
						
							Index i=getOrCreateIndex( indexName); 
							i.addColumn(col);
							// changed by Nick 15.09.2005
                            i.setUnique("U".equalsIgnoreCase(rsInd.getString("CONSTRAINT_TYPE")) || "P".equalsIgnoreCase(rsInd.getString("CONSTRAINT_TYPE")));
                            // by Nick
                            }
						}
					
				}
			}
			else
			{
				rsInd=md.getIndexInfo(this.schema.getCatalog(),this.schema.getShortName(),StringUtils.hibernateUnEscapeName( getShortName()),false,false);
				while (rsInd.next()) {
				String indexName = StringUtils.hibernateEscapeName(rsInd.getString(INDEX_NAME));
				//String indexName=rsInd.getString(INDEX_NAME);//primary,...
				if(indexName!=null){
					//if(!indexes.containsKey(indexName)){// comment gavrs 9/15/2005
						String columnName = StringUtils.hibernateEscapeName(rsInd.getString(COLUMN_NAME));
						//String columnName = rsInd.getString(COLUMN_NAME);
						Column col=(Column)getColumn(columnName);
						
						//add tau 10.05.2006 for ESORM-566
						if (col== null) {
							throw new Exception("Column: ''" + columnName + "'' no in the table: ''" + this.getName()+ "''");
						}
						
         ////////////////		
					//	Fks=col.getOwnerTable().getForeignKeys(getColumn(columnName).getName());
					//if(col.getOwnerTable().isForeignKey(getColumn(columnName).getName()))
						//if(col.getOwnerTable().getForeignKeys(getColumn(columnName).getName()).length>1)
							//noUnique=true;
					//for(int j=0;j<col.getOwnerTable().getForeignKeys(getColumn(columnName).getName()).length;j++)
					//	Fks[j].getReferencedTable().getName();
		////////////////////	
						Index i=getOrCreateIndex( indexName); 
						i.addColumn(col);
					//i.setTable(this);
						//col.setTypeIndex(rsInd.getInt(TYPE));//tableIndexStatistic,tableIndexHashed 
						
                        // changed by Nick 12.09.2005
                       // col.setUnique(!rsInd.getBoolean(NON_UNIQUE));
						//if(col.getOwnerTable().getForeignKeys(getColumn(columnName).getName()).length==1)
							i.setUnique(!rsInd.getBoolean(NON_UNIQUE));
						//	i.setUnique(true);
						//else i.setUnique(false);
                        // by Nick
                        
                    //}// comment gavrs 9/15/2005
					}
				}
			}
//			}
		}catch(SQLException ex) {
			ExceptionHandler.logThrowableError(ex, ex.getMessage());
		} catch (Exception e) {
			ExceptionHandler.logThrowableError(e, null);			
		}finally {
			try{
//				if (rs != null) rs.close();
				if (rsInd != null) rsInd.close();
				if (WSps != null) WSps.close();
			}catch(SQLException ex) {
				ExceptionHandler.logThrowableError(ex, ex.getMessage());
			}
		}
		
	}
	
	//kaa 07.04.2005
	public void removeColumn(String columnname) {
		IDatabaseColumn column=(IDatabaseColumn)columns.remove(columnname);
		if (column!=null){
			column.setOwnerTable(null);
			if(primaryKey!=null) primaryKey.removeColumn(columnname);
			Iterator itr = getForeignKeyIterator();
			while (itr.hasNext())
			{
				IDatabaseConstraint fk = (IDatabaseConstraint)itr.next();
				fk.removeColumn(columnname);

                // added by Nick 19.11.2005 - ???????
                //                            ???????
                // POSSIBLE SIDE EFFECTS!
				if (fk.getColumnSpan() == 0)
                {
				    itr.remove();
                }
				// by Nick
            }
            
            // added by Nick 19.11.2005
			itr = getUniqueKeyIterator();
            while (itr.hasNext())
            {
                IDatabaseConstraint c = (IDatabaseConstraint) itr.next();
                c.removeColumn(columnname);

                // POSSIBLE SIDE EFFECTS!
                if (c.getColumnSpan() == 0)
                {
                    itr.remove();
                }
            }

            itr = getIndexIterator();
            while (itr.hasNext())
            {
                IDatabaseConstraint c = (IDatabaseConstraint) itr.next();
                c.removeColumn(columnname);

                // POSSIBLE SIDE EFFECTS!
                if (c.getColumnSpan() == 0)
                {
                    itr.remove();
                }
            }
            // by Nick
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#isView()
	 */
	public boolean isView() {
		return view;
	}
	
	public void setView(boolean view){
		this.view=view;
	}
    
    // changed by Nick 06.06.2005 - Metadata object reused now
	public void updateType(DatabaseMetaData metadata){
	    //by Nick
		ResultSet rs =null;
		try{
			rs = metadata.getTables(this.schema.getCatalog(),this.schema.getShortName(),StringUtils.hibernateUnEscapeName(getShortName()), TABLE_VIEW);
			if (rs.next()) {
				if("VIEW".equals(rs.getString("TABLE_TYPE"))) view=true;
			}
		} catch (SQLException ex){
			ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
		}finally {
			try{ 
				if(rs!=null) rs.close();
			}catch(SQLException ex) {
				ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
			}
		}
	}

	public boolean isForeignKey(String columnName) { //by Nick 22.04.2005
		Iterator itr = getForeignKeyIterator();
		boolean found = false;
		if (itr != null)
		{
			while (itr.hasNext() && !found)
			{
				IDatabaseConstraint fk = (IDatabaseConstraint)itr.next();
				found = fk.containsColumn(columnName);///??? DEBUG
			}
		}
		return found;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#getIndexName(java.lang.String)
	 */
	public String getIndexName(String columnName) {
		Iterator itr = this.getIndexIterator();
		columnName=StringUtils.hibernateEscapeName(columnName);///added 28/7
		if (itr != null)
		{
			while (itr.hasNext())
			{
				IDatabaseConstraint idx = (IDatabaseConstraint)itr.next();
				if(idx.containsColumn(columnName))return StringUtils.hibernateUnEscapeName(idx.getName());///added 28/7
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#getUniqueKeyName(java.lang.String)
	 */
	public String getUniqueKeyName(String columnName) {
		Iterator itr = this.getUniqueKeyIterator();
		columnName=StringUtils.hibernateEscapeName(columnName);///added 28/7
		if (itr != null)
		{
			while (itr.hasNext())
			{
				IDatabaseConstraint uk = (IDatabaseConstraint)itr.next();
				if(uk.containsColumn(columnName))return StringUtils.hibernateUnEscapeName(uk.getName());///added 28/7
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#renameColumn(org.jboss.tools.hibernate.core.IDatabaseColumn, java.lang.String)
	 */
	public void renameColumn(IDatabaseColumn column, String newColumnName) {
		String oldName=column.getName();
		columns.remove(oldName);
		Column res=(Column) column;
		newColumnName=StringUtils.hibernateEscapeName(newColumnName);///added 28/7
		res.setName(newColumnName);
		columns.put(newColumnName, column);
		res.setOwnerTable(this);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseTable#createUniqueKey(java.util.Collection)
	 */
	public IDatabaseConstraint createUniqueKey(List columns) {
		Constraint uk = new Constraint();
		String ukName="UK"+getShortName()+String.valueOf(uniqueKeys.size());
		uk.setName( ukName );
		uk.setTable( this );
		/* rem by yk 2005/10/18
        Iterator columnItr = columns.iterator();
        while (columnItr.hasNext())
        {
            uk.addColumn((IDatabaseColumn) columnItr.next()); 
        }
        */
        uniqueKeys.put( ukName, uk );
		return uk;
	}

    // added by Nick 16.06.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseTable#getForeignKey(java.lang.String)
     */
    public IDatabaseTableForeignKey getForeignKey(String fkName) {
        Iterator fKeys = getForeignKeyIterator();
        IDatabaseTableForeignKey result = null;
        
        if (fkName == null)
            return null;
        fkName=StringUtils.hibernateEscapeName(fkName);///added 28/7
        while(fKeys.hasNext() && result == null)
        {
            IDatabaseTableForeignKey fKey = (IDatabaseTableForeignKey)fKeys.next();
            if (fkName.equals(StringUtils.hibernateEscapeName(fKey.getName()) )) ///added 28/7
                result = fKey;
        }
        return result;
    }
    // by Nick
    
    /**
     * akuzmin 13.09.2005
     * @param colname
     * @return 
     * search all columns ussing upper case 
     */
    public boolean isColumnExists(String colname)
    {
    	if (colname!=null)
    	{
	    	Iterator coliter=getColumnIterator();
	    	while (coliter.hasNext())
	    	{
	    		Column value=(Column)coliter.next();
	    		if (value.getName().toUpperCase().equals(colname.toUpperCase()))
	    			return true;
	    	}
    	}
    	return false;
    }
}
