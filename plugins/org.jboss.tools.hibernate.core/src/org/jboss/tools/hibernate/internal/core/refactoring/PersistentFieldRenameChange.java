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
import org.eclipse.jdt.core.IField;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;


public class PersistentFieldRenameChange extends Change {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentFieldRenameChange.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IField field;
	private String newName;
	
	public PersistentFieldRenameChange(IField field,String newName) {
		this.field=field;
		this.newName=newName;
	}

	public String getName() {
		return BUNDLE.getString("PersistentFieldRenameChange.taskName")+" "+field.getDeclaringType().getFullyQualifiedName()+"."+newName;
	}

	public void initializeValidationData(IProgressMonitor pm) {
		// TODO Auto-generated method stub
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		if (pm.isCanceled()) return null;
		try {
			IProject prj=field.getJavaProject().getProject();
			if (prj.hasNature(OrmCore.ORM2NATURE_ID)) {
				String fqn=field.getDeclaringType().getFullyQualifiedName();
				pm.subTask(BUNDLE.getString("PersistentFieldRenameChange.findPersistentClassTask"));
				IPersistentClass persistentClass=null;
				IOrmProject ormPrj=OrmCore.getDefault().create(prj);
				IMapping[] mappings=ormPrj.getMappings();
				for(int i=0; i<mappings.length; i++) {
					
					persistentClass=mappings[i].findClass(fqn);
					if (persistentClass!=null) {
						pm.subTask(BUNDLE.getString("PersistentFieldRenameChange.renamePersistentFieldTask"));
						IPersistentField persistentField=persistentClass.getField(field.getElementName());
						if (persistentField!=null) {
							persistentClass.renameField(persistentField,newName);
							mappings[i].save();
							break;
						}
					}
				}
				IProject[] refPrj=ormPrj.getReferencedProjects();
				for(int i=0; i<refPrj.length; i++) refPrj[i].touch(null);
			}
			
			
		} catch(Exception ex) {
			pm.setCanceled(true);
			ExceptionHandler.logThrowableError(ex,BUNDLE.getString("PersistentFieldRenameChange.renamePersistentField")+" "+field);
		}
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}



}
