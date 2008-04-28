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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

/**
 * @author Yan
 *
 */
public class DeleteParticipant extends
		org.eclipse.ltk.core.refactoring.participants.DeleteParticipant {

	public static final String PARTICIPANT_NAME="ORM2-DeleteParticipant";
	public static final String[] ORM_PROJECT_FILE_EXTENTIONS={"cfg.xml","hbm.xml"};
	private IType type;
	private IField field;
	private IPackageFragment packageFragment;
	private IFile ormFile; 

	public DeleteParticipant() {
		super();
	}

	protected boolean initialize(Object element) {
		if (element instanceof IType) {
			type=(IType)element;
		} else if (element instanceof IField) {
			field=(IField)element;
		} else if (element instanceof IPackageFragment) {
			packageFragment=(IPackageFragment)element;
		} else if (element instanceof IFile) {
			IFile iFile=(IFile)element;
			for(int i=0; i<ORM_PROJECT_FILE_EXTENTIONS.length; i++) {
				if (iFile.getName().endsWith(ORM_PROJECT_FILE_EXTENTIONS[i])) {
					ormFile=iFile;
					break;
				}
			}
		}
		return type!=null || field!=null || packageFragment!=null || ormFile!=null;
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		
		if (!pm.isCanceled()) {
			
			if (type!=null) return new PersistentClassDeleteChange(type,false);
			else if (field!=null) {
				return new PersistentFieldDeleteChange(field);
			} else if (packageFragment!=null) {
				PackageDeleteChange change=new PackageDeleteChange(packageFragment);
				if (change.getChildren()!=null && change.getChildren().length>0) {
					return change;
				}
			} else if (ormFile!=null) {
				return new OrmFileDeleteChange(ormFile);
			}
			//TODO What to do for cancel?
		}
		
		return null;
		
	}
}
