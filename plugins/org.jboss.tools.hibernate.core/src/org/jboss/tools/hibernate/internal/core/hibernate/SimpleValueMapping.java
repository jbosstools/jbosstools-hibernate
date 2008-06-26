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
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.AbstractValueMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForSimpleMapingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.PropertyMappingForVersionDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.SimpleValueMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.VersionMappingDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;



/**
 * Any value that maps to columns.
 */
public class SimpleValueMapping extends AbstractValueMapping implements ISimpleValueMapping {
	private static final long serialVersionUID = 1L;
	private final List<IDatabaseColumn> columns = new ArrayList<IDatabaseColumn>();
	private String typeName;
	private String nullValue;
	private IDatabaseTable table;
	private String foreignKeyName;
	private boolean alternateUniqueKey;
	private Properties typeParameters;
	private boolean cascadeDeleteEnabled;
	private String formula;
	private Type type;
	private Properties identifierGeneratorProperties;
	private String identifierGeneratorStrategy = "assigned";

	//for keys only
	private boolean nullable;
	// #changed# by Konstantin Mishin on 13.12.2005 fixed for ESORM-430
	//private boolean updateable;
	private boolean updateable = true;
	// #changed#
	
	public SimpleValueMapping(IDatabaseTable table) {
		this.table = table;
	}

	public SimpleValueMapping() {}

	//XXX.toAlex (Nick) maybe that's wrong that getName() returns typeName? Is is not the same as setName() sets
	// name property is used only by view so it's really does not matter 
    //by Nick 15.03.2005
/*	public String getName(){
	    return typeName;
	}
*/    //by Nick

	public boolean isCascadeDeleteEnabled() {
		return cascadeDeleteEnabled;
	}

	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
	}

		
	public void addColumn(IDatabaseColumn column) {
		if ( !columns.contains(column) ) columns.add(column);
		column.setPersistentValueMapping(this);
	}
//	akuzmin 02.06.2005
	public void addColumn(IDatabaseColumn column,int pos) {
		if ( !columns.contains(column) ) columns.add(pos,column);
		column.setPersistentValueMapping(this);
	}
	
//akuzmin 13.04.2005	
	public void removeColumn(IDatabaseColumn column) {
// added by yk 22.07.2005
		if(column == null)return;
// added by yk 22.07.2005 stop
		if ( columns.contains(column) ) columns.remove(column);
	}
	
	/**
	 * @return Returns the type.
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * @return Returns the formula.
	 */
	public String getFormula() {
		return formula;
	}
	/**
	 * @param formula The formula to set.
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public boolean hasFormula() {
		return formula!=null;
	}

	public int getColumnSpan() {
		return columns.size();
	}
	
	public Iterator<IDatabaseColumn> getColumnIterator() {
		return columns.iterator();
	}
	public List getConstraintColumns() {
		return columns;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String type) {
		this.typeName = type;
	}
	public void setTable(IDatabaseTable table) {
		this.table = table;
	}


	public String getNullValue() {
		return nullValue;
	}

	public IDatabaseTable getTable() {
		return table;
	}
//akuzmin 12.05.2005
	public String getTableName() {
		if (table!=null)
			return table.getName();
		else return null;
	}	

	/**
	 * Sets the nullValue.
	 * @param nullValue The nullValue to set
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	public String getForeignKeyName() {
		return foreignKeyName;
	}

	public void setForeignKeyName(String foreignKeyName) {
		this.foreignKeyName = foreignKeyName;
	}

	public boolean isAlternateUniqueKey() {
		return alternateUniqueKey;
	}

	public void setAlternateUniqueKey(boolean unique) {
		this.alternateUniqueKey = unique;
	}

	public boolean isSimpleValue() {
		return true;
	}

	public boolean isTypeSpecified() {
		return typeName!=null;
	}

	public void setTypeParameters(Properties parameterMap) {
		this.typeParameters = parameterMap;
	}
	
	public Properties getTypeParameters() {
		return typeParameters;
	}
	
	public String getFetchMode() {
		return "select";
	}

	//akuzmin 15.06.2005
	public void setFetchMode(String fetchMode)	{}	
	
	public String toString() {
		return getClass().getName() + '(' + columns.toString() + ')';
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitSimpleValueMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}


	public Properties getIdentifierGeneratorProperties() {
		return identifierGeneratorProperties;
	}
	
	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
		this.identifierGeneratorProperties = identifierGeneratorProperties;
	}
	
	public String getIdentifierGeneratorStrategy() {
		return identifierGeneratorStrategy;
	}
	
	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentValueMapping#clear()
	 */
	public void clear() {
		if (table == null) //by Nick 19.04.2005
			return;
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			IDatabaseColumn col=(IDatabaseColumn) iter.next();
			if (col != null) //by Nick 19.04.2005
				table.removeColumn(col.getName());
		}
		columns.clear();
	}

//	akuzmin 21/04/2005	
	public IPropertySource2 getPropertySource()
	{
		BeanPropertySourceBase bp = new BeanPropertySourceBase(this);
		bp.setPropertyDescriptors(getPropertyDescriptorHolder());		
		return bp;
	}
//	akuzmin 05/05/2005
	public PropertyDescriptorsHolder getPropertyDescriptorHolder() {
		return SimpleValueMappingDescriptorsHolder.getInstance(getTable());
	}

//	akuzmin 24/05/2005
	public PropertyDescriptorsHolder getPropertyMappingDescriptorHolder() {
		return PropertyMappingForSimpleMapingDescriptorsHolder.getInstance();
	}
	
	//akuzmin 05.05.2005
	public String getMappingColumn() {
		if (getColumnIterator().hasNext())
		{
			Column col=(Column) getColumnIterator().next();
			if (getTable().getColumn(col.getName())!=null)
				return col.getName();
		}
		return "";
	}
	
	//akuzmin 05.05.2005
	public void setMappingColumn(String keyColumn) {
			if (getColumnSpan()==1)
			{
			  Column col=(Column)getColumnIterator().next();
			  if (getTable().getColumn(keyColumn)!=null)
			  {
				  removeColumn(col);
				  if ((keyColumn!=null)&&(!keyColumn.trim().equals("")))
				  {
					  addColumn(getTable().getColumn(keyColumn));
				  }
			  }
			  else if ((keyColumn!=null)&&(!keyColumn.trim().equals(""))) 
				  		getTable().renameColumn(col,keyColumn);
			  	   else removeColumn(col);
			}
			else 
				if ((getColumnSpan()==0)&&(keyColumn!=null)&&(!keyColumn.trim().equals("")))
					addColumn(getTable().getOrCreateColumn(keyColumn));	
	}
	
	public boolean isNullable() {
//		akuzmin 08.07.2005
	    boolean Null = true;
        Iterator itr = getColumnIterator();
        if (itr != null)
            while (itr.hasNext() && Null)
            {
                IDatabaseColumn column = (IDatabaseColumn) itr.next();
                if (!column.isNullable())
                	Null = false;
            }
        nullable=Null;
		return nullable;
	
	}
	
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
		//akuzmin 08.07.2005
	    Iterator itr = getColumnIterator();
        if (itr != null)
            while (itr.hasNext())
            {
                IDatabaseColumn column = (IDatabaseColumn) itr.next();
                column.setNullable(nullable);
            }		
	}
	
	public boolean isUpdateable() {
		return updateable;
	}
	
	public void setUpdateable(boolean updateable) {
		this.updateable = updateable;
	}
	
//	akuzmin 25.05.2005
	public void setTypeByString(String type) {
		if ((Type.getType(type)!=null)||((getFieldMapping()!=null)&&(getFieldMapping().getPersistentField().getOwnerClass().getProjectMapping().findClass(type)!=null)))
		{
			setType(Type.getOrCreateType(type));
			if (getColumnSpan()==1)
			{
			  Column col=(Column)getColumnIterator().next();
			  col.setSqlTypeCode(getType().getSqlType());
			}
		}
		}
	
//	akuzmin 25.05.2005	
	public String getTypeByString() {
		if (getType()==null)
		{
			if ((getFieldMapping()==null) ||
			(getFieldMapping().getPersistentField()==null) ||		
			(getFieldMapping().getPersistentField().getType()==null))
				return null;
			setType(Type.getOrCreateType(getFieldMapping().getPersistentField().getType()));			
		}
		return getType().getName();		
	}

	public PropertyDescriptorsHolder getPropertyVersionMappingDescriptorHolder() {
		return PropertyMappingForVersionDescriptorsHolder.getInstance();

	}

	public PropertyDescriptorsHolder getVersionDescriptorHolder() {
		return VersionMappingDescriptorsHolder.getInstance(getTable());

	}

    // added by Nick 08.07.2005
    public boolean containsColumn(IDatabaseColumn column) {
        boolean contains = false;
        
        if (column == null)
            return false;
        
        Iterator columns = getColumnIterator();
        while (!contains && columns.hasNext())
        {
            IDatabaseColumn otherColumn = (IDatabaseColumn) columns.next();
            if (otherColumn.getName().equals(column.getName()) && 
                    otherColumn.getOwnerTable() == column.getOwnerTable())
                contains = true;
                
        }
        return contains;
    }
    //by Nick
    
}