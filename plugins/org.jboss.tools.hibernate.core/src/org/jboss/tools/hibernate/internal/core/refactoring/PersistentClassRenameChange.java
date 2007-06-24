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

public class PersistentClassRenameChange extends Change {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassRenameChange.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IType type;
	private String newName;
	private boolean multiple;
	
	public PersistentClassRenameChange(IType type,String newName,boolean multiple) {
		this.type=type;
		this.newName=newName;
		this.multiple=multiple;
	}

	public String getName() {
		return BUNDLE.getString("PersistentClassRenameChange.taskName")+" "+newName;
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
			
			IProject prj=type.getJavaProject().getProject();
			
			if (prj.hasNature(OrmCore.ORM2NATURE_ID)) {
				pm.subTask(BUNDLE.getString("PersistentClassRenameChange.findPersistentClassTask"));
				IPersistentClass persistentClass=null;
				IOrmProject ormPrj=OrmCore.getDefault().create(prj);
				IMapping[] mappings=ormPrj.getMappings();
				for(int i=0; i<mappings.length; i++) {
					
					persistentClass=mappings[i].findClass(type.getFullyQualifiedName());
					if (persistentClass!=null) {
						pm.subTask(BUNDLE.getString("PersistentClassRenameChange.renamePersistentClassTask"));
						mappings[i].renameClass(persistentClass,newName);
						
						// add tau 06.05.2006 for ESORM-596
						persistentClass.getPersistentClassMappingStorage().setDirty(true);
						
						//if (!multiple) {
							mappings[i].save();
						//}
					}
				}
			}
			
			
		} catch(Exception ex) {
			pm.setCanceled(true);
			ExceptionHandler.logThrowableError(ex,BUNDLE.getString("PersistentClassRenameChange.renamePersistentClass")+" "+type);
		}
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}


}
