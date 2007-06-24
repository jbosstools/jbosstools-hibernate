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
package org.jboss.tools.hibernate.refactoring;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.refactoring.*;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.view.ViewPlugin;



/**
 * @author yan
 *
 */
public class RefactoringSupport {
	
	
	public static void renamePersistentClass(Shell parent,IPersistentClass clazz) {
		ICompilationUnit cUnit=clazz.getSourceCode();
//		IJavaElement[] jes=null;
//		try {
//		jes=cUnit.getChildren();
//		} catch (JavaModelException e1) {
//		e1.printStackTrace();
//		}
		try {
			RenameSupport renameSupport=RenameSupport.create(cUnit,null,RenameSupport.UPDATE_REFERENCES);
			renameSupport.openDialog(parent);
		} catch (CoreException e) {
            ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in rename Persistent Class.");			
		}
		
	}
	public static void movePersistentClass(Shell parent,IPersistentClass clazz) {
		//TODO Yan (by Nick) 
		// 1) to open CU use: 
		//  ICompilationUnit wc = null;
		//  if (!cUnit.isWorkingCopy())
		//      wc = cUnit.getWorkingCopy(null)
		// 2) to mark as can be closed:
		//  if (wc != null)
		//      wc.discardWorkingCopy()
		
		ICompilationUnit cUnit=clazz.getSourceCode();
		IJavaElement[] jes=null;
		try {
			jes=cUnit.getChildren();
		} catch (JavaModelException e1) {
        	//TODO (tau-tau) for Exception			
			e1.printStackTrace();
		}
		try {
			RenameSupport renameSupport=RenameSupport.create(cUnit,null,RenameSupport.UPDATE_REFERENCES);
			renameSupport.openDialog(parent);
		} catch (CoreException e) {
        	//TODO (tau-tau) for Exception			
			e.printStackTrace();
		}
		
	}
	
	public static void renamePersistentField(Shell parent,IPersistentField field) {
		try {
			IPersistentClass clazz=field.getOwnerClass();
			if (clazz!=null) {
				IType type=clazz.getType();
				if (type!=null) {
					IField f=type.getField(field.getName());
					// #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-90
					IField fields1[] = type.getFields();
					// #added#
					if (f!=null) {
						RenameSupport renameSupport=RenameSupport.create(f,null,RenameSupport.UPDATE_REFERENCES|RenameSupport.UPDATE_GETTER_METHOD|RenameSupport.UPDATE_SETTER_METHOD);
						renameSupport.openDialog(parent);
						// #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-90
						if (renameSupport.preCheck().isOK()) {
							IField fields2[] = type.getFields();
							for (int i = 0; i < fields2.length; i++) {
								int j;
								for (j = 0; j < fields1.length; j++) 
									if(fields2[i].equals(fields1[j]))
										break;
								if (j==fields1.length) {
									clazz.renameField(field, fields2[i].getElementName());
									try {
										//edit tau 29.03.2006
										//clazz.getPersistentClassMapping().getStorage().save();									
										clazz.getPersistentClassMapping().getStorage().save(true);										
									} catch (IOException e) {
						            	//TODO (tau-tau) for Exception										
										ExceptionHandler.logThrowableError(e,e.getMessage());
									}
								}
							}
						}
						// #added#				
					}
				}
				
			}
		} catch (CoreException e) {
        	//TODO (tau-tau) for Exception			
			e.printStackTrace();
		}
	}
	
//	public static void movePersistentField(IPersistentField field) {
//		ICompilationUnit cUnit=field.getOwnerClass().getSourceCode();
//		IJavaElement[] jes=null;
//		try {
//		jes=cUnit.getChildren();
//		} catch (JavaModelException e1) {
//		e1.printStackTrace();
//		}
//		try {
//		RenameSupport renameSupport=RenameSupport.create(cUnit,null,RenameSupport.UPDATE_REFERENCES);
//		renameSupport.openDialog(parent);
//		} catch (CoreException e) {
//		e.printStackTrace();
//		}
//	}
	
}
