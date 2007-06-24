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
package org.jboss.tools.hibernate.core.hibernate;

import java.util.Properties;

import org.jboss.tools.hibernate.core.IDatabaseColumn;

/**
 * @author alex
 *
 *  Any value that maps to columns.
 */
public interface ISimpleValueMapping extends IHibernateKeyMapping {
	public Properties getIdentifierGeneratorProperties();
	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties);
	public String getIdentifierGeneratorStrategy();
	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy);
	public String getNullValue();
	public void setNullValue(String nullValue);
	public void setFormula(String formula);
	public String getFormula();
	public void setTypeParameters(Properties parameterMap);
	public Properties getTypeParameters();
	// added by Nick 28.07.2005
	public void addColumn(IDatabaseColumn column);
    public void removeColumn(IDatabaseColumn column);
    // by Nick
	
}
