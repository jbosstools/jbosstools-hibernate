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
package org.jboss.tools.hibernate.internal.core.refactoring;

import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;


/**
 * @author Yan
 *
 */

public class PersistentClassDeleteChange extends Change {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassDeleteChange.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IType type;
	private IPersistentClass persistentClass;
	private boolean multiple;
	
	public PersistentClassDeleteChange(IType type,boolean multiple) {
		this.type=type;
		this.multiple=multiple;
	}
	
	public PersistentClassDeleteChange(IPersistentClass clazz,boolean multiple) {
		this.persistentClass=clazz;
		this.multiple=multiple;
	}
	
	private String getFQN() {
		if (persistentClass!=null) {
			return persistentClass.getPackage()+"."+persistentClass.getName();
		} 
		if (type!=null) {
			return type.getFullyQualifiedName();
		}
		return null;
	}

	public String getName() {
		return BUNDLE.getString("PersistentClassDeleteChange.taskName")+" "+getFQN();
	}

	public void initializeValidationData(IProgressMonitor pm) {
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}
	
	public Change perform(IProgressMonitor pm) throws CoreException {
		if (pm.isCanceled()) return null;
		try {
			
			if (persistentClass!=null) {
				IMapping[] maps=ClassUtils.getReferencedMappings(persistentClass);
				pm.subTask(BUNDLE.getString("PersistentClassDeleteChange.deletePersistentClassTask"));
				for(int i=0; i<maps.length; i++) {
					maps[i].removePersistentClass(persistentClass.getName());
					persistentClass.getPersistentClassMapping();
					if (!multiple) {
						maps[i].getConfiguration().save();
					}
				}
			} else {
				
				IProject prj=type.getJavaProject().getProject();
				
				if (prj.hasNature(OrmCore.ORM2NATURE_ID)) {
					pm.subTask(BUNDLE.getString("PersistentClassDeleteChange.findPersistentClassTask"));
					IOrmProject ormPrj=OrmCore.getDefault().create(prj);
					IMapping[] mappings=ormPrj.getMappings();
					for(int i=0; i<mappings.length; i++) {
						
						persistentClass=mappings[i].findClass(type.getFullyQualifiedName());
						if (persistentClass!=null) {
							pm.subTask(BUNDLE.getString("PersistentClassDeleteChange.deletePersistentClassTask"));
							mappings[i].removePersistentClass(persistentClass.getName());
							if (!multiple) mappings[i].getConfiguration().save();
						}
					}
				}
				
			}
			
			
		} catch(Exception ex) {
			pm.setCanceled(true);
			ExceptionHandler.logThrowableError(ex,BUNDLE.getString("PersistentClassDeleteChange.deletePersistentClass")+" "+type);
		}
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}
	


}
