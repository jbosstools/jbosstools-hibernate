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


import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.IManyToAnyMapping;


/**
 * @author Nick
 *
 */
public class ManyToAnyMapping extends AnyMapping implements IManyToAnyMapping {

    public ManyToAnyMapping() {
		super(null);
	}
    public ManyToAnyMapping(IDatabaseTable table) {
		super(table);
	}


	public String getName(){
		String name=super.getName();
		return name==null?"many-to-many":name; 
	}

    public boolean isSimpleValue() {
		return false;
	}

    public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitManyToAnyMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}

}
