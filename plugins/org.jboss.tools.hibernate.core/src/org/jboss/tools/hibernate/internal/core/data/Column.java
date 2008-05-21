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

import java.sql.Types;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author alex
 *
 * A column of a relational database table
 * @see org.hibernate.mapping.Column
 */
public class Column extends DataObject implements IDatabaseColumn {
	private static final long serialVersionUID = 1L;
	private IPersistentValueMapping value;
	//private int typeIndex = 0;
	public static final int DEFAULT_LENGTH = 0/*255*/;    //
	public static final int DEFAULT_PRECISION = 0/*19*/; // changed by Nick 02.09.2005
	public static final int DEFAULT_SCALE = -1/*2*/;     //
	
	private int length=DEFAULT_LENGTH;
	private int precision=DEFAULT_PRECISION;
	private int scale=DEFAULT_SCALE;
	
	private boolean nullable=true;
	private boolean unique=false;

	private String sqlType;
	private int sqlTypeCode; 
	private boolean quoted=false;
	private String checkConstraint;
	
	private IDatabaseTable ownerTable;

	public Column(){}
	public Column(Column column){
		length=column.length;
		precision=column.precision;
		scale=column.scale;
		nullable=column.nullable;
		unique=column.unique;
		sqlType=column.sqlType;
		sqlTypeCode=column.sqlTypeCode;
		quoted=column.quoted;
		checkConstraint=column.checkConstraint;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#isDefaultValues()
	 */
	public boolean isDefaultValues() {
		if(length > 0/*!=DEFAULT_LENGTH*/) return false;        //
		if(precision > 0/*!=DEFAULT_PRECISION*/) return false; // changed by Nick 02.09.2005
		if(scale >= 0/*!=DEFAULT_SCALE*/) return false;       //
		if(!nullable) return false;
		if(unique) return false;
// added by yk 29.07.2005
		//if(!TypeUtils.isColumnTypeCompatible(this, ((SimpleValueMapping)getPersistentValueMapping()).getType())) return false;
		
		// changed by Nick 05.08.2005
        //if(getSqlTypeCode() != ((SimpleValueMapping)getPersistentValueMapping()).getType().getSqlType() ) return false;
        // by Nick
		if (!TypeUtils.isColumnSQLTypeReproducible(this))
            return false;
        
// added by yk 29.07.2005 stop

		return true;
	}
	// added by yk 30.09.2005
	public boolean isLengthPrecisionScaleDefaultValues()
	{
		boolean defaultvalue = true;
		if(length > 0 || precision > 0 || scale >= 0)
			defaultvalue = false;
		return defaultvalue;
	}
	// added by yk 30.09.2005.

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#getLength()
	 */
	public int getLength() {
		return length;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#getOwnerTable()
	 */
	public IDatabaseTable getOwnerTable() {
		return ownerTable;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#getPersistentValueMapping()
	 */
	public IPersistentValueMapping getPersistentValueMapping() {
		return value;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#getPrecision()
	 */
	public int getPrecision() {
		return precision;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#getTypeIndex()
	 */
	public int getSqlTypeCode() {
		return sqlTypeCode;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#getTypeName()
	 */
	public String getSqlTypeName() {
		//akuzmin 22.06.2005
		if (sqlType==null) 
		{
			// changed by Nick 26.08.2005
            //sqlType=TypeUtils.SQLTypeToName(getSqlTypeCode());	
            if (getSqlTypeCode() == Types.NULL)
            {
                return "";
            }
            else
            {
                return TypeUtils.SQLTypeToName(getSqlTypeCode());    
            }
            // by Nick
        }
		return sqlType;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#isNullable()
	 */
	public boolean isNullable() {
		return nullable;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#isPrimaryKey()
	 */
	public boolean isPrimaryKey() {
		if(ownerTable!=null && ownerTable.getPrimaryKey()!=null) return ownerTable.getPrimaryKey().containsColumn(getName());
		return false;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IDatabaseColumn#isUnique()
	 */
	public boolean isUnique() {
		return unique;
	}
	
	/**
	 * @param length The length to set.
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @param nullable The nullable to set.
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	/**
	 * @param ownerTable The ownerTable to set.
	 */
	public void setOwnerTable(IDatabaseTable ownerTable) {
		this.ownerTable = ownerTable;
	}
	/**
	 * @param precision The precision to set.
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	/**
	 * @param sqlTypeCode The sqlTypeCode to set.
	 */
	public void setSqlTypeCode(int sqlTypeCode) {
		this.sqlTypeCode = sqlTypeCode;
//		akuzmin 15.08.05
		
        // changed by Nick 26.08.2005
        //sqlType=null;
		// by Nick
    }
	
    public void setSqlTypeName(String typeName) {
        this.sqlType = typeName;
    }

	/**
	 * @param unique The unique to set.
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	/**
	 * @param value The value to set.
	 */
	public void setPersistentValueMapping(IPersistentValueMapping value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitDatabaseColumn(this,argument);
	}
	/**
	 * @return Returns the checkConstraint.
	 */
	public String getCheckConstraint() {
		return checkConstraint;
	}
	/**
	 * @param checkConstraint The checkConstraint to set.
	 */
	public void setCheckConstraint(String checkConstraint) {
		this.checkConstraint = checkConstraint;
	}
	/**
	 * @return Returns the scale.
	 */
	public int getScale() {
		return scale;
	}
	/**
	 * @param scale The scale to set.
	 */
	public void setScale(int scale) {
		this.scale = scale;
	}

    // added by Nick 29.08.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IDatabaseColumn#isNativeType()
     */
    public boolean isNativeType() {
        return (sqlType != null);
    }
    // by Nick
}
