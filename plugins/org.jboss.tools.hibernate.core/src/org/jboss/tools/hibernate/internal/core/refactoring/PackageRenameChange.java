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

import java.util.HashSet;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;


/**
 * @author Yan
 *
 */

public class PackageRenameChange extends CompositeChange {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PackageRenameChange.class.getPackage().getName() + "." + BUNDLE_NAME);
	private HashSet<String> configSet;
	private String packageName;
	private IOrmProject ormProject;
	
	
	public PackageRenameChange(IPackageFragment packageFragment,String newName) throws OperationCanceledException {
		super(BUNDLE.getString("PackageRenameChange.taskName")+" "+packageFragment.getElementName());
		try {
			packageName=packageFragment.getElementName();
			IProject prj=packageFragment.getJavaProject().getProject();
			
			if (prj.hasNature(OrmCore.ORM2NATURE_ID)) {
				ormProject=OrmCore.getDefault().create(prj);
				IMapping[] mappings=ormProject.getMappings();
				if (mappings.length==0) return;
				ICompilationUnit[] cunits=packageFragment.getCompilationUnits();
				configSet=new HashSet<String>(mappings.length);
				for(int c=0; c<cunits.length; c++) {
					ICompilationUnit wc = null;
					try {
						if (!cunits[c].isWorkingCopy()) wc = cunits[c].getWorkingCopy(null);
						IType type=cunits[c].findPrimaryType();
						
						if (type!=null) {
							
							String fqn=type.getFullyQualifiedName();
							
							for(int i=0; i<mappings.length; i++) {
								
								IPersistentClass persistentClass=mappings[i].findClass(fqn);
								if (persistentClass != null) {
									configSet.add(mappings[i].getConfiguration().getResource().getName());
									add(new PersistentClassRenameChange(type,newName+"."+type.getElementName(),true));
								}
								
							}
							
						}
							
						} finally {
							if (wc != null) wc.discardWorkingCopy();
						}
					
					
				}
			}
			
		} catch(Exception ex) {
			//TODO silence send exception data to log  
			throw new OperationCanceledException(BUNDLE.getString("PackageRenameChange.createChangeError")+" "+packageFragment);
		}
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		Change ch=super.perform(pm);
		if (configSet!=null && ormProject!=null) {
			try {
				IMapping[] maps=ormProject.getMappings();
				for(int i=0; i<maps.length; i++) {
					if (configSet.contains(maps[i].getConfiguration().getResource().getName())) {
						maps[i].save();
					}
				
				}
			} catch (Exception e) {
				pm.setCanceled(true);
				ExceptionHandler.logThrowableWarning(e,BUNDLE.getString("PackageDeleteChange.createChangeError")+" "+packageName);
			}
		}
		return ch;
	}



}
