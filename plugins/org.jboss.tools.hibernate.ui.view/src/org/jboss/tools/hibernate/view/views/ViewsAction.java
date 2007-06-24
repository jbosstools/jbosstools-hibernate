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
package org.jboss.tools.hibernate.view.views;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.jboss.tools.hibernate.view.ViewPlugin;


public class ViewsAction {
	
	//TODO (tau-tau) for Exception ALL	
	
//	static  ActionOrmTree hibernateConnectionWizardAction;
//    static  ActionOrmTree hibernateAddMappingWizardAction;
//    static  ActionOrmTree persistentClassesWizardAction;
//    static  ActionOrmTree fieldMappingWizardAction;
    //akuzmin 06.10.2005
//    static  ActionOrmTree fieldMappingWizardNewAction;
//    
//    static  ActionOrmTree tablesClassesWizardAction;
//    static  ActionOrmTree mappingWizardAction;
//    static  ActionOrmTree cacheWizardAction;
//    
//    static  ActionOrmTree clearPackageMappingAction;
//    static  ActionOrmTree clearClassMappingAction;
//    static  ActionOrmTree clearFieldMappingAction;
//
//    static  ActionOrmTree removeClassAction;
//    
//    static  ActionOrmTree classAutoMappingAction;
//    static  ActionOrmTree packageAutoMappingAction;
//    
//    static  ActionOrmTree autoMappingSettingAction;
//    static  ActionOrmTree runTimeSettingAction;
    
    // add 22.04.2005
//    static  ActionOrmTree generateDDLWizard;  
    
    // add 23.05.2005
//    static  ActionOrmTree refactoringDialogClassAction;
//    static  ActionOrmTree refactoringDialogFieldAction;
//    static  ActionOrmTree fetchStrategyWizardAction; 
//    static  ActionOrmTree collapseAllAction;
//    static  ActionOrmTree expandAllAction;
    
    // add 24.05.2005   
//    static  ActionOrmTree refreshMappingAction;
//    static  ActionOrmTree renameTableObjectAction;
//    static  ActionOrmTree addColumnDialogAction;
//    static  ActionOrmTree removeTableAction;
    
    // add 26.05.2005
//    static  ActionOrmTree renameTableObjectDialogForColumnAction;
    
    // add 31.05.2005
//    static  ActionOrmTree addMappingStorageAction;
    
    // add 03.06.2005
//    static  ActionOrmTree columnPropertyDialogAction;
//    static  ActionOrmTree viewTableAction;
    
    // add 16.06.2005
//    static  ActionOrmTree refreshOrmProjectAction;
    
    // 20050618 <yan>
//    static  ActionOrmTree removeClassesAction;
    
//    static  ActionOrmTree removeConfigAction;
    //</yan>
    
    // 20050620 <yan>
//    static  ActionOrmTree openSourceAction;
//    static  ActionOrmTree openMappingStorageAction;
//    static  ActionOrmTree openMappingAction;
    // </yan>
    
    // 20050727 <yan>
//    static  ActionOrmTree editNamedQueryAction,addNamedQueryAction,removeNamedQueryAction,testQueryAction;
    // </yan>
    
    // add tau 12.07.2005
//    static  ActionOrmTree refreshSchemaMappingAction;
    
    // add tau 20.07.2005
//    static  ActionOrmTree refreshClassSchemaMappingAction;
//    static  ActionOrmTree refreshPackageSchemaMappingAction;
//    static  ActionOrmTree refreshMappingSchemaMappingAction;
    
    // add tau 26.07.2005
//    static  ActionOrmTree refreshDatabaseTableSchemaMappingAction;
    //akuzmin 16.08.2005
//    static  ActionOrmTree GenerateDAOWizardAction;  
    // #added# by Konstantin Mishin on 25.03.2006 fixed for ESORM-28
    static  ActionOrmTree openEditorAction;
    // #added#

    // add 09.03.2005
    static private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
    static private ResourceBundle BUNDLE = ResourceBundle.getBundle(ViewsAction.class.getPackage().getName() + ".views");

    // added by yk 12.09.2005.
//    static  ActionOrmTree showHibernateConsole;
    
    static {
        openEditorAction = new ActionOrmTree() { 
        	HashMap<Object,ObjectEditorInput> hashMap = new HashMap<Object,ObjectEditorInput>();
			public void rush() {
				ObjectEditorInput input = hashMap.get(this.getViewer().getTree().getSelection()[0].getData());
				if(input == null) {
					input = new ObjectEditorInput(this.getViewer().getTree().getSelection()[0].getData());
					hashMap.put(this.getViewer().getTree().getSelection()[0].getData(), input);
				}
				try {
					IDE.openEditor(ViewPlugin.getPage(),input ,"org.jboss.tools.hibernate.veditor.editors.vizualeditor");
				} catch (PartInitException e) {
//					ExceptionHandler.logThrowableError(e,"OpenEditor");              
				}
			}
		}; 
		openEditorAction.setText(BUNDLE.getString("Explorer.OpenEditorActionName"));
		openEditorAction.setToolTipText(BUNDLE.getString("Explorer.OpenEditorActionToolTipText"));
	    // #added#
    }
    
    /**
     * @return IOrmProject
     */
//    static private IOrmProject getOrmProject(Tree tree) {
//        TreeItem[] items = tree.getSelection();
//        
//        // 20050623 <yan>
//        if (items.length==0) return null;
//        // </yan>
//        
//        TreeItem parentItem = items[0];
//        TreeItem ormItem; 
//        do {
//            ormItem = parentItem;
//            parentItem = parentItem.getParentItem();
//        } while (parentItem != null);
//        return (IOrmProject)ormItem.getData();      
//    }

    // #added# by Konstantin Mishin on 11.09.2005 fixed for ORMIISTUD-660
//    static private void traverse(IContainer container, HashSet<Object> hs, HBMViewerFilter vf) {
//        try {   
//            IResource[] resources = container.members();
//            for (int i = 0; i < resources.length; i++) {
//                IResource resource = resources[i];
//                if (resource instanceof IFile) {
//                    String ext = resource.getName();
//                    if ((ext != null && (ext.toLowerCase().endsWith(".hbm") || ext.toLowerCase().endsWith(".hbm.xml"))) && vf.isSelectedMappingStorage(resource) && vf.isSrc(resource.getFullPath().toString())) {
//                        hs.add(resource);
//                    }
//                } else if (resource.isAccessible() && resource  instanceof IContainer) {
//                    traverse((IContainer)resource, hs, vf);
//                }
//            }
//        } catch (CoreException e) {
//            ExceptionHandler.logThrowableError(e, null);
//        }
//    }
    // #added#
    
    // yan 2005.09.22
//    static private void removePersistentClass(IPersistentClass pc, boolean delSource, boolean delMapping, HashSet<IMapping> refrMaps) throws CoreException {
//        if (!delMapping) {
//            pc.getProjectMapping().removePersistentClass(pc.getName(),false);
//            refrMaps.add(pc.getProjectMapping());
//        } else {
//            IMapping[] maps = ClassUtils.getReferencedMappings(pc);
//            for(int i=0; i<maps.length; i++) {
//                
//                maps[i].removePersistentClass(pc.getName(),true);
//                refrMaps.add(maps[i]);
//                
//            }
//            
//        }
//    }
    // yan
    // yan 2005.09.22
//    static private void clearPersistentClassMapping(IPersistentClass pc, HashSet<IMapping> refrMaps) throws CoreException {
//        IMapping[] maps = ClassUtils.getReferencedMappings(pc);
//        for(int i = 0; i < maps.length; i++) {
//            IPersistentClass tpc = maps[i].findClass(pc.getName());
//            if (tpc!=null) {
//                tpc.clearMapping();
//                refrMaps.add(maps[i]);
//            }
//        }
//    }
    
//    static private void refreshMappings(HashSet refrMaps) {
//        for(Iterator it=refrMaps.iterator(); it.hasNext();) {
//            try {
//                ((IMapping)it.next()).save();
//            } catch (IOException e) {
//                ExceptionHandler.logThrowableError(e,"Save project's mapping.");
//            } catch (CoreException e) {
//                ExceptionHandler.logThrowableWarning(e,"Save project's mapping.");
//            }
//        }
//    }
    // yan

}
