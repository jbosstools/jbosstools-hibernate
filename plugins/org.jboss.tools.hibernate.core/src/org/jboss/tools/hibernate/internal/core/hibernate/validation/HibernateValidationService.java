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

import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IValidationService;

/**
 * @author alex
 *
 * Hibernate mapping validation
 */
public class HibernateValidationService implements IValidationService {
	private IMapping mapping;
	public HibernateValidationService(IMapping mapping){
		this.mapping=mapping;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IValidationService#run()
	 */
	public void run() {
		// 27.05.2005 tau move this metod in OrmBuilder.fullBuild(IProgressMonitor monitor)  
		//HibernateValidationProblem.deleteMarkers(mapping.getProject().getProject());
		Iterator it=HibernateValidationProblem.getProblemIterator();
		HibernateValidationProblem problem=null;
		while(it.hasNext()){
			problem=(HibernateValidationProblem)it.next();
			problem.validateMapping(mapping);
		}
	}
    
    // added by Nick 22.09.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IValidationService#incrementalRun(org.eclipse.core.resources.IResource)
     */
    public void incrementalRun(IResource resource) {
        Iterator it=HibernateValidationProblem.getProblemIterator();
        HibernateValidationProblem problem=null;
        while(it.hasNext()){
            problem=(HibernateValidationProblem)it.next();
            problem.validateMapping(mapping, resource);
        }
    }
    // by Nick
}
