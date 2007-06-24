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


/**
 * @author Yan
 *
 */
public class PersistentClassMoveChange extends Change {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassMoveChange.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IType type;
	private String newName;
	private IPersistentClass persistentClass;
	private boolean multiple=false;
	
	public PersistentClassMoveChange(IType type,String destination,boolean multiple) {
		this.type=type;
		newName=destination.length()>0?destination+"."+type.getElementName():type.getElementName();
		this.multiple=multiple;
	}
	
	public PersistentClassMoveChange(IPersistentClass clazz,String destination,boolean multiple) {
		persistentClass=clazz;
		newName=destination.length()>0?destination+"."+clazz.getShortName():clazz.getShortName();
		this.multiple=multiple;
	}

	public String getName() {
		return BUNDLE.getString("PersistentClassMoveChange.taskName")+" "+newName;
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
			
			IOrmProject ormProject=null;
			IMapping mapping=null;
			
			if (persistentClass!=null) {
				mapping=persistentClass.getProjectMapping();
				ormProject=mapping.getProject();
			} else {
				
				IProject prj=type.getJavaProject().getProject();
				
				if (prj.hasNature(OrmCore.ORM2NATURE_ID)) {
					pm.subTask(BUNDLE.getString("PersistentClassMoveChange.findPersistentClassTask"));
					ormProject=OrmCore.getDefault().create(prj);
					IMapping[] mappings=ormProject.getMappings();
					for(int i=0; i<mappings.length; i++) {
						persistentClass=mappings[i].findClass(type.getFullyQualifiedName());
						if (persistentClass!=null) {
							mapping=mappings[i];
							break;
						}
					}
					
				}
				
			}
			
			
			if (persistentClass!=null && ormProject!=null && mapping!=null) {
				pm.subTask(BUNDLE.getString("PersistentClassMoveChange.movePersistentClassTask"));
				mapping.renameClass(persistentClass,newName);
				refresh(mapping);
			}
			
			
		} catch(Exception ex) {
			pm.setCanceled(true);
			ExceptionHandler.logThrowableError(ex,BUNDLE.getString("PersistentClassMoveChange.movePersistentClass")+" "+type);
		}
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}

	private void refresh(IMapping mapping) throws Exception {
		if (multiple) return;
		mapping.save();
        // TODO (tau->tau) del? 27.01.2006		
		mapping.getProject().fireProjectChanged(this, false);
		IProject[] refPrj=mapping.getProject().getReferencedProjects();
		for(int i=0; i<refPrj.length; i++) refPrj[i].touch(null);
		
	}

}
