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

import java.util.ArrayList;

import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;

/**
 * @author alex
 *
 * A database schema
 */
public class Schema extends DataObject implements IDatabaseSchema {
	private static final long serialVersionUID = 1L;
	private String catalog;
	private IMapping projectMapping;
	//XXX.toAlex (Nick) comparator object added because addDatabaseTable() method caused class cast exception
	// when adding 2 tables into sorted set. 
	// Very good.
	//akuzmin remove SortedSet 16.06.2005.SortedSet didn't remove table  
	private ArrayList<IDatabaseTable> tables = new ArrayList<IDatabaseTable>();
//	SortedSet tables=new TreeSet(
//	        new Comparator(){
//	            public int compare(Object o1,
//	                    Object o2)
//	            {
//	                return o1.equals(o2) ? 0 : 1;
//	            }
//	            public boolean equals(Object obj)
//	            {
//	                return false;
//	            }
//	        }
//	);
	private static final IDatabaseTable[] TABLES={};
	
	
	public Schema(IMapping projectMapping){
		this.projectMapping=projectMapping;
	}
	
	public String getShortName(){
		String name=getName();
		int i=name.lastIndexOf('.');
		if(i==-1)return name;
		return name.substring(i+1);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseSchema#getProjectMapping()
	 */
	public IMapping getProjectMapping() {
		return projectMapping;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseSchema#getCatalog()
	 */
	public String getCatalog() {
		return catalog;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseSchema#getDatabaseTables()
	 */
	public IDatabaseTable[] getDatabaseTables() {
		return (IDatabaseTable[])tables.toArray(TABLES);
	}

	/**
	 * @param catalog The catalog to set.
	 */
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	public void addDatabaseTable(IDatabaseTable table){
		if(table.getSchema()!=null){
			table.getSchema().removeDatabaseTable(table);
		}
		tables.add(table);
	}
	public boolean removeDatabaseTable(IDatabaseTable table){
		return tables.remove(table);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitDatabaseSchema(this,argument);
	}

}
