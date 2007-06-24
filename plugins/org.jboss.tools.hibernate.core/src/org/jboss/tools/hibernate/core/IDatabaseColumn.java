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


/**
 * @author alex
 *
 * A column of a relational database table
 */
public interface IDatabaseColumn extends IOrmElement {
	public String getName();
	public String getSqlTypeName();
	public int getSqlTypeCode();
	
	public boolean isDefaultValues(); // returns false if non-default column 
	/**
	 * JDBC column size
	 * */
	public int getLength();
	public int getPrecision();
	public int getScale();
	public boolean isPrimaryKey();
	public boolean isUnique();
	public boolean isNullable();
	public IDatabaseTable getOwnerTable();
	public String getCheckConstraint();
	/**
	 * Returns value mapping that the field was used in.
	 * */
	public IPersistentValueMapping getPersistentValueMapping();
	public void setPersistentValueMapping(IPersistentValueMapping mapping);
	
	public void setLength(int length);
	public void setNullable(boolean nullable);
	public void setPrecision(int precision);
	public void setSqlTypeCode(int sqlTypeCode);

    // added by Nick 29.08.2005
	public boolean isNativeType();
    // by Nick
    
    public void setSqlTypeName(String typeName);
    
    public void setUnique(boolean unique);
	public void setOwnerTable(IDatabaseTable ownerTable);
	
	public void setCheckConstraint(String checkConstraint);
	public void setScale(int scale);
	
	// added by yk 30.09.2005
	public boolean isLengthPrecisionScaleDefaultValues();
	// added by yk 30.09.2005.

}
