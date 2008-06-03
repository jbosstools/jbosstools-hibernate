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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.OrmCore;


/**
 * @author Yan
 *
 */

public class OrmFileDeleteChange extends Change {
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(OrmFileDeleteChange.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IFile ormFile;
	
	public OrmFileDeleteChange(IFile ifile) {
		ormFile=ifile;
	}
	
	
	public String getName() {
		return BUNDLE.getString("OrmFileDeleteChange.taskName")+" "+ormFile.getName();
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
			
			if (ormFile!=null) {
				
				if (ormFile.getName().endsWith("cfg.xml")) {
					
					IOrmProject[] projects=OrmCore.getDefault().getOrmModel().getOrmProjects();
					
					String ormFilePath=ormFile.getFullPath().toString();
					
					for(int i=0; i<projects.length && !pm.isCanceled(); i++) {
						
						IMapping[] mappings=projects[i].getMappings();
						
						for(int m=0; m<mappings.length && !pm.isCanceled(); m++) {
							
							String path=mappings[m].getConfiguration().getResource().getFullPath().toString();
							if (path.equals(ormFilePath)) {
								
								projects[i].removeMapping(mappings[m]);
								
						        // TODO (tau->tau) del? 27.01.2006
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
									OrmCore.getPluginLog().logInfo("STOP? OrmFileDeleteChange.perform()");								
								}
								projects[i].fireProjectChanged(this, false);
								return null;
							}
							
						}
						
						
					}
					
				} else if (ormFile.getName().endsWith("hbm.xml")) {
					
					IOrmProject[] projects=OrmCore.getDefault().getOrmModel().getOrmProjects();
					
					String ormFilePath=ormFile.getFullPath().toString();
					
					for(int i=0; i<projects.length && !pm.isCanceled(); i++) {
						
						IMapping[] mappings=projects[i].getMappings();
						
						for(int m=0; m<mappings.length && !pm.isCanceled(); m++) {
							
							IMappingStorage[] storages=mappings[m].getMappingStorages();
							
							for(int s=0; s<storages.length && !pm.isCanceled(); s++) {
								
								String path=storages[s].getResource().getFullPath().toString();
								if (path.equals(ormFilePath)) {
									
									mappings[m].removeMappingStorage(storages[s]);
									return null;
									
								}
								
							}
							
						}
						
						
					}
					
					
				}
				
			}
			
			
			
		} catch(Exception ex) {
			pm.setCanceled(true);
			OrmCore.getPluginLog().logError(BUNDLE.getString("OrmFileDeleteChange.deleteOrmFile")+" "+ormFile, ex);
		}
		return null;
	}

	public Object getModifiedElement() {
		return null;
	}


}
