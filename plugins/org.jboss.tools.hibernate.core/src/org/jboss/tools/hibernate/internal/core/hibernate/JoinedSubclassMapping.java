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
package org.jboss.tools.hibernate.internal.core.hibernate;

import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IJoinedSubclassMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.JoinedSubclassMappingPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;


/**
 * A subclass in a table-per-subclass mapping
 */
public class JoinedSubclassMapping extends SubclassMapping implements IJoinedSubclassMapping {
	private static final long serialVersionUID = 1L;
	private IDatabaseTable table;
	private IHibernateKeyMapping key;

	public JoinedSubclassMapping(IHibernateClassMapping superclass) {
		super(superclass);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClassMapping#setDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void setDatabaseTable(IDatabaseTable table) {
		this.table=table;
		//akuzmin 27.10.2005
		if ((key!=null)&&(table!=key.getTable()))
		{
			if (key.getColumnSpan()>0)
			{
				Column col;
				while(key.getColumnIterator().hasNext())
				{
					col=(Column)key.getColumnIterator().next();
					((SimpleValueMapping)key).removeColumn(col);
				}
			}
			key.setTable(table);
		}
	}

	public IDatabaseTable getDatabaseTable() {
		return table;
	}

	public IHibernateKeyMapping getKey() {
		return key;
	}

	public void setKey(IHibernateKeyMapping key) {
		this.key = key;
	}

	//akuzmin 04.05.2005
	public String getKeyColumn() {
		if ((key!=null)&&(key.getColumnSpan()>0))
		{
			String keyColumns=null;
			Column fkcolumn=null;
			Iterator iter=key.getColumnIterator();
			while (iter.hasNext())
			{
				fkcolumn=(Column)iter.next();
				if (key.getTable().getColumn(fkcolumn.getName())!=null)
					if (keyColumns!=null) 
						keyColumns=keyColumns+","+fkcolumn.getName();
					else keyColumns=fkcolumn.getName();
			}
			return keyColumns;
		}
		return null;
	}
	
	//akuzmin 04.05.2005
	public void setKeyColumn(String keyColumns) {
		if (key instanceof SimpleValueMapping)
		{
			String[] keyNames = new String[0];
			if (keyColumns!=null)
				keyNames = keyColumns.split(",");
			if ((keyNames!=null)&&(keyNames.length>=0))
			{
				Column col;
				while(key.getColumnIterator().hasNext())
				{
					col=(Column)key.getColumnIterator().next();
					((SimpleValueMapping)key).removeColumn(col);
				}
				for(int i=0;i<keyNames.length;i++)
					if (key.getTable().getColumn(keyNames[i])!=null)
						((SimpleValueMapping)key).addColumn(key.getTable().getColumn(keyNames[i]));
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#isSubclass()
	 */
	public boolean isSubclass() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#isJoinedSubclass()
	 */
	public boolean isJoinedSubclass() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitJoinedSubclassMapping(this,argument);
		return visitor.visitPersistentClassMapping(this,argument);
	}
	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(JoinedSubclassMappingPropertyDescriptorsHolder.getInstance(this));
		return bp;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.SubclassMapping#copyFrom(org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping)
	 */
	public void copyFrom(IHibernateClassMapping hcm) {
		table=hcm.getDatabaseTable();
		super.copyFrom(hcm);
	}	

}
