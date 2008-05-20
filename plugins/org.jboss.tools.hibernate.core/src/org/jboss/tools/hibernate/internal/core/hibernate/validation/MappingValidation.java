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
package org.jboss.tools.hibernate.internal.core.hibernate.validation;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;

public class MappingValidation extends HibernateValidationProblem {

	protected MappingValidation(){
		super();
	}	

	public void validateMapping(IMapping mapping) {
// added by yk 17.08.2005
//		MappingValidationCorrectVisitor correctvisitor = new MappingValidationCorrectVisitor(mapping);
//		correctvisitor.verifyValidtion();
// added by yk 17.08.2005.
		MappingValidationVisitor visitor=new MappingValidationVisitor(this, mapping);
		mapping.accept(visitor, null);
	}

    // added by Nick 22.09.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationProblem#validateMapping(org.jboss.tools.hibernate.core.IMapping, org.eclipse.core.resources.IResource)
     */
    public void validateMapping(IMapping model, IResource scope) {
        if (scope == null)
            return;        
        try {
            MappingValidationVisitor visitor=new MappingValidationVisitor(this, model);
            
            IPersistentClassMapping[] mappings = model.getPersistentClassMappings();
            for (int i = 0; i < mappings.length; i++) {
                IPersistentClassMapping mapping = mappings[i];
                
                if (mapping.getStorage() != null) {
                    if (scope.equals(mapping.getStorage().getResource())) {
                        HibernateValidationProblem.deleteMarkers(mapping.getStorage().getResource(),scope.getFullPath().toString(),this.getClass());
                        mapping.accept(visitor, null);
                    }
                }
            }
        } finally {
            HibernateValidationProblem.clearSingleResourceClearSet();
        }
    }
    // by Nick
}
