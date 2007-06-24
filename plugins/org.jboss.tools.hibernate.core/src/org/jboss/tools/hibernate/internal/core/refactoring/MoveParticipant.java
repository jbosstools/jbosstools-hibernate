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
public class MoveParticipant extends
		org.eclipse.ltk.core.refactoring.participants.MoveParticipant {

	public static final String PARTICIPANT_NAME="ORM2-MoveParticipant";
	private IType type;
	private IField field;
	
	public MoveParticipant() {
		super();
	}

	protected boolean initialize(Object element) {
		if (element instanceof IType) {
			type=(IType)element;
		} else if (element instanceof IField) {
			field=(IField)element;
		}
		return type!=null || field!=null;
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
			
			Object destination=getArguments().getDestination();
			if (destination instanceof IPackageFragment) {
				
				String dstName=((IPackageFragment)destination).getElementName();
				
				if (type!=null) {
					return new PersistentClassRenameChange(type,dstName+"."+type.getElementName(),false);
				}
//				if (type!=null) return new PersistentClassMoveChange(type,dstName,false);
				else if (field!=null) return new PersistentFieldRenameChange(field,dstName);
				
			}
			
		}
		
		return null;
		
	}

}
