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
import java.util.Iterator;
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

public class PackageDeleteChange extends CompositeChange {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PackageDeleteChange.class.getPackage().getName() + "." + BUNDLE_NAME);
	private HashSet mappingSet;
	private String packageName;
	
	public PackageDeleteChange(IPackageFragment packageFragment) throws OperationCanceledException {
		super(BUNDLE.getString("PackageDeleteChange.taskName")+" "+packageFragment.getElementName());
		try {
			packageName=packageFragment.getElementName();
			IProject prj=packageFragment.getJavaProject().getProject();
			
			if (prj.hasNature(OrmCore.ORM2NATURE_ID)) {
				IOrmProject ormPrj=OrmCore.getDefault().create(prj);
				IMapping[] mappings=ormPrj.getMappings();
				if (mappings.length==0) return;
				ICompilationUnit[] cunits=packageFragment.getCompilationUnits();
				mappingSet=new HashSet(mappings.length);
				for(int c=0; c<cunits.length; c++) {
					
					// added by Nick 08.10.2005
				    ICompilationUnit wc = null;
                    
				    try
				    {
				        if (!cunits[c].isWorkingCopy())
				            wc = cunits[c].getWorkingCopy(null);
				        // by Nick
				        
				        IType type=cunits[c].findPrimaryType();
				        
				        if (type!=null) {
				            
				            String fqn=type.getFullyQualifiedName();
				            
				            for(int i=0; i<mappings.length; i++) {
				                
				                IPersistentClass persistentClass=mappings[i].findClass(fqn);
				                if (persistentClass!=null) {
				                    if (!mappingSet.contains(mappings[i])) mappingSet.add(mappings[i]);
				                    add(new PersistentClassDeleteChange(persistentClass,true));
				                }
				                
				            }
				            
				        }
				        
				        // added by Nick 08.10.2005
				        
				    }
				    finally {
				        if (wc != null)
				            wc.discardWorkingCopy();
				    }
                    // by Nick
					
				}
			}
			
		} catch(Exception ex) {
			//TODO silence send exception data to log  
			throw new OperationCanceledException(BUNDLE.getString("PackageDeleteChange.createChangeError")+" "+packageFragment);
		}
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
		Change ch=super.perform(pm);
		ExceptionHandler.logInfo("========== PERFORM PACKAGE DELETE "+this);
		if (mappingSet!=null) {
			try {
				for(Iterator it=mappingSet.iterator(); it.hasNext();) {
					IMapping mapping=(IMapping)it.next();
					mapping.save();
				}
			} catch (Exception e) {
				pm.setCanceled(true);
				ExceptionHandler.logThrowableWarning(e,BUNDLE.getString("PackageDeleteChange.createChangeError")+" "+packageName);
			}
		}
		return ch;
	}



}
