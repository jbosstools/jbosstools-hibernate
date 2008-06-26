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
public class RenameParticipant extends org.eclipse.ltk.core.refactoring.participants.RenameParticipant {
	
	public static final String PARTICIPANT_NAME="ORM2-RenameParticipant";
	
	private IType type;
	private IField field;
	private IPackageFragment packageFragment;

	public RenameParticipant() {
		super();
	}

	protected boolean initialize(Object element) {
		if (element instanceof IType) {
			type=(IType)element;
		} else if (element instanceof IField) {
			field=(IField)element;
		} else if (element instanceof IPackageFragment) {
			packageFragment=(IPackageFragment)element;
		}
		return type!=null || field!=null || packageFragment!=null;
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
			if (type!=null) {
				String newName=getArguments().getNewName();
				IPackageFragment pf=type.getPackageFragment();
				if (pf!=null) {
					newName=pf.getElementName()+"."+newName;
				}
				return new PersistentClassRenameChange(type,newName,false);
			}
			else if (field!=null) return new PersistentFieldRenameChange(field,getArguments().getNewName());
			else if (packageFragment!=null) return new PackageRenameChange(packageFragment,getArguments().getNewName());
		}
		return null;
		
	}

}
