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
package org.jboss.tools.hibernate.internal.core;

import java.util.ArrayList;
import java.util.Iterator;

import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 22, 2005
 */
public class CorrectMappingVisitor extends BaseMappingVisitor {
	
	public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
        Object result = super.visitComponentMapping(mapping, argument);
        
        if (mapping.getComponentClass() != null)
        {
            mapping.getComponentClass().refresh();
        }
        
        return result;
    }

    public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
		if ((mapping.getPersistentField()==null)||(mapping.getPersistentField().getType()==null))
		{
			HibernateAutoMappingHelper.removeMapping(mapping);
			return null;
		}
		
		if(mapping.getValue()!=null)
		{
			mapping.getValue().accept(this, mapping);
		}
		return null;
	}

	public Object visitHibernateClassMapping(IHibernateClassMapping mapping, Object argument){
		Iterator<IPropertyMapping> it = mapping.getFieldMappingIterator();
		ArrayList<IPropertyMapping> mappings = new ArrayList<IPropertyMapping>();
		while(it.hasNext()) {
			mappings.add(it.next());
		}
		for(int i = 0; i < mappings.size(); i++)
			((IOrmElement)mappings.get(i)).accept(this,mapping);
		
		if (mapping.getPersistentClass() != null) {
		    mapping.getPersistentClass().refresh();      
        }
        
        return null;
		
	}	
}
