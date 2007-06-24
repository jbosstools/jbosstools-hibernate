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
import org.jboss.tools.hibernate.core.IAutoMappingService;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IJoinMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMappingHolder;
import org.jboss.tools.hibernate.dialog.AutoMappingSetting;
import org.jboss.tools.hibernate.dialog.ColumnPropertyDialog;
import org.jboss.tools.hibernate.dialog.RemoveConfirmCheckDialog;
import org.jboss.tools.hibernate.dialog.RemoveConfirmRadioDialog;
import org.jboss.tools.hibernate.dialog.RenameTableObjectDialog;
import org.jboss.tools.hibernate.dialog.RunTimeSetting;
import org.jboss.tools.hibernate.dialog.StatisticDialog;
import org.jboss.tools.hibernate.internal.core.BaseMappingVisitor;
import org.jboss.tools.hibernate.internal.core.CorrectMappingVisitor;
import org.jboss.tools.hibernate.internal.core.OrmBuilder;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.ClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.RootClassMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.XMLFileStorage;
import org.jboss.tools.hibernate.internal.core.hibernate.XMLFileStorageDublicate;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMappingHelper;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;
import org.jboss.tools.hibernate.internal.core.hibernate.query.NamedQueryDefinition;
import org.jboss.tools.hibernate.internal.core.properties.AddColumnDialog;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.refactoring.RefactoringSupport;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.wizard.fetchstrategy.FetchStrategyWizard;
import org.jboss.tools.hibernate.wizard.fieldmapping.FieldMappingWizard;
import org.jboss.tools.hibernate.wizard.generateDAO.GenerateDAOWizard;
import org.jboss.tools.hibernate.wizard.generateDDL.GenerateDDLWizard;
import org.jboss.tools.hibernate.wizard.hibernatecachewizard.HibernateCacheWizard;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizard;
import org.jboss.tools.hibernate.wizard.mappingwizard.MappingWizard;
import org.jboss.tools.hibernate.wizard.persistentclasses.PersistentClassesWizard;
import org.jboss.tools.hibernate.wizard.queries.DuplicateQueryNameException;
import org.jboss.tools.hibernate.wizard.queries.NamedQueriesWizard;
import org.jboss.tools.hibernate.wizard.queries.QueryTestWizardDialog;
import org.jboss.tools.hibernate.wizard.refreshmappingwizard.RefreshMappingWizard;
import org.jboss.tools.hibernate.wizard.tablesclasses.TablesClassesWizard;


public class ViewsAction {
	
	//TODO (tau-tau) for Exception ALL	
	
	static  ActionOrmTree hibernateConnectionWizardAction;
    static  ActionOrmTree hibernateAddMappingWizardAction;
    static  ActionOrmTree persistentClassesWizardAction;
    static  ActionOrmTree fieldMappingWizardAction;
    //akuzmin 06.10.2005
    static  ActionOrmTree fieldMappingWizardNewAction;
    
    static  ActionOrmTree tablesClassesWizardAction;
    static  ActionOrmTree mappingWizardAction;
    static  ActionOrmTree cacheWizardAction;
    
    static  ActionOrmTree clearPackageMappingAction;
    static  ActionOrmTree clearClassMappingAction;
    static  ActionOrmTree clearFieldMappingAction;

    static  ActionOrmTree removeClassAction;
    
    static  ActionOrmTree classAutoMappingAction;
    static  ActionOrmTree packageAutoMappingAction;
    
    static  ActionOrmTree autoMappingSettingAction;
    static  ActionOrmTree runTimeSettingAction;
    
    // add 22.04.2005
    static  ActionOrmTree generateDDLWizard;  
    
    // add 23.05.2005
    static  ActionOrmTree refactoringDialogClassAction;
    static  ActionOrmTree refactoringDialogFieldAction;
    static  ActionOrmTree fetchStrategyWizardAction; 
    static  ActionOrmTree collapseAllAction;
    static  ActionOrmTree expandAllAction;
    
    // add 24.05.2005   
    static  ActionOrmTree refreshMappingAction;
    static  ActionOrmTree renameTableObjectAction;
    static  ActionOrmTree addColumnDialogAction;
    static  ActionOrmTree removeTableAction;
    
    // add 26.05.2005
    static  ActionOrmTree renameTableObjectDialogForColumnAction;
    
    // add 31.05.2005
    static  ActionOrmTree addMappingStorageAction;
    
    // add 03.06.2005
    static  ActionOrmTree columnPropertyDialogAction;
    static  ActionOrmTree viewTableAction;
    
    // add 16.06.2005
    static  ActionOrmTree refreshOrmProjectAction;
    
    // 20050618 <yan>
    static  ActionOrmTree removeClassesAction;
    
    static  ActionOrmTree removeConfigAction;
    //</yan>
    
    // 20050620 <yan>
    static  ActionOrmTree openSourceAction;
    static  ActionOrmTree openMappingStorageAction;
    static  ActionOrmTree openMappingAction;
    // </yan>
    
    // 20050727 <yan>
    static  ActionOrmTree editNamedQueryAction,addNamedQueryAction,removeNamedQueryAction,testQueryAction;
    // </yan>
    
    // add tau 12.07.2005
    static  ActionOrmTree refreshSchemaMappingAction;
    
    // add tau 20.07.2005
    static  ActionOrmTree refreshClassSchemaMappingAction;
    static  ActionOrmTree refreshPackageSchemaMappingAction;
    static  ActionOrmTree refreshMappingSchemaMappingAction;
    
    // add tau 26.07.2005
    static  ActionOrmTree refreshDatabaseTableSchemaMappingAction;
    //akuzmin 16.08.2005
    static  ActionOrmTree GenerateDAOWizardAction;  
    // #added# by Konstantin Mishin on 25.03.2006 fixed for ESORM-28
    static  ActionOrmTree openEditorAction;
    // #added#

    // add 09.03.2005
    static private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
    static private ResourceBundle BUNDLE = ResourceBundle.getBundle(ActionExplorerVisitor.class.getPackage().getName() + ".views");

    // added by yk 12.09.2005.
    static  ActionOrmTree showHibernateConsole;
    
    static {
    
    hibernateConnectionWizardAction = new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping==null) return;
                new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new HibernateConnectionWizard(mapping, this.getViewer()))
                        .open();
                
                // add tau 27.01.2006
                project.fireProjectChanged(this, false);                
                //project.fireProjectChanged(this);     tau 04.08.2005      
            }
        }; 
        
        hibernateConnectionWizardAction.setText(BUNDLE.getString("Explorer.HibernateConnectionWizardName"));
        hibernateConnectionWizardAction.setToolTipText(BUNDLE.getString("Explorer.HibernateConnectionWizardToolTipText"));
        
        hibernateAddMappingWizardAction = new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                IMapping mapping = project.createMapping(OrmCore.ORM2NATURE_ID);
                
                new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new HibernateConnectionWizard(mapping, this.getViewer()))
                        .open();
                
                project.fireProjectChanged(this, false);                
            }
        }; 
        
        hibernateAddMappingWizardAction.setText(BUNDLE.getString("Explorer.hibernateAddMappingWizardName"));
        hibernateAddMappingWizardAction.setToolTipText(BUNDLE.getString("Explorer.hibernateAddMappingWizardToolTipText"));
        hibernateAddMappingWizardAction.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.hibernateAddMappingWizard")));        
        
        persistentClassesWizardAction = new ActionOrmTree() {
            public void rush() {
            	
            	// add tau 27.01.2006
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
            	
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if(mapping==null) return;
                
                //add tau 05.05.2006 for ESORM-538
            	project.setDirty(true);
            	
                int dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new PersistentClassesWizard(mapping, this.getViewer()))
                        .open();
                
                // edit 07.03.2006 - tau
                if (dialog == Dialog.OK){
                	project.setDirty(true); // add tau 07.03.2006                 	
                    project.fireProjectChanged(this, false);                
            	}
           }
        }; 
        
        persistentClassesWizardAction.setText(BUNDLE.getString("Explorer.PersistentClassesWizardName"));
        persistentClassesWizardAction.setToolTipText(BUNDLE.getString("Explorer.PersistentClassesWizardToolTipText"));
        
//akuzmin 16.08.2005
                
        GenerateDAOWizardAction = new ActionOrmTree() {
            public void rush() {
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if(mapping==null) return;
                new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new GenerateDAOWizard(mapping))
                        .open();
            }
        }; 
        
        GenerateDAOWizardAction.setText(BUNDLE.getString("Explorer.GenerateDAOWizardName"));
        GenerateDAOWizardAction.setToolTipText(BUNDLE.getString("Explorer.GenerateDAOWizardToolTipText"));
        
//   yan 20050927
        showHibernateConsole = new ActionOrmTree() {
            public void rush() {
                try {
                    ViewPlugin.getDefault().getWorkbench().showPerspective("org.hibernate.eclipse.console.HibernateConsolePerspective",ViewPlugin.getActiveWorkbenchWindow());
                } catch (WorkbenchException e) {
                    ExceptionHandler.logThrowableWarning(e,"Show Hibernate Console");              
                }
            }
        };
        showHibernateConsole.setText(BUNDLE.getString("Explorer.HibernateConsole"));
        showHibernateConsole.setToolTipText(BUNDLE.getString("Explorer.HibernateConsoleToolTipText"));
        
        fieldMappingWizardAction = new ActionOrmTree() {
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                //akuzmin 14.04.2005
                final IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());              
                if(mapping == null) return;
                
                // edit 20.05.2005 tau
                IPersistentClass persClass = null;
                
                if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){             
                    persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                    
                    if(persClass == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER){
                    persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());               
                    if(persClass == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                    IPersistentClassMapping persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());                
                    if(persistentClassMapping == null) return;
                    
                    persClass = persistentClassMapping.getPersistentClass();
                    if(persClass == null) return;                   
                }
                
                final IPersistentField field = (IPersistentField) ViewsUtils.getSelectedObject(IPersistentField.class, this.getViewer());                
                if(field == null) return;
                
                final IPersistentClass persistentClass = persClass;
                
                //add tau 17.03.2006
                final TreeItem parentItem = this.getViewer().getTree().getSelection()[0].getParentItem();
                final TreeViewer actionViewer = this.getViewer();

                // add IWorkspaceRunnable 15.06.2005
                IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
                    public void run(IProgressMonitor monitor) throws CoreException {
                                // add tau 09.06.2005
                                WizardDialog dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        new FieldMappingWizard(mapping, persistentClass, field, parentItem, false, actionViewer));
                                dialog.open();
                                //$changed$ by Konstantin Mishin on 2005/08/16 fixed for ORMIISTUD-617
                            }
                };

                // add tau 09.11.2005 
                try {
                    project.getProject().getWorkspace().run(runnable, new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Edit Field Mapping.");
                }

                // add tau 09.11.2005               
                project.fireProjectChanged(this, false);               
                
            }               
        }; 
        
        fieldMappingWizardAction.setText(BUNDLE.getString("Explorer.FieldMappingWizardName"));
        fieldMappingWizardAction.setToolTipText(BUNDLE.getString("Explorer.FieldMappingWizardToolTipText"));

        fieldMappingWizardNewAction = new ActionOrmTree() {
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                //akuzmin 14.04.2005
                final IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());              
                if(mapping == null) return;
                
                // edit 20.05.2005 tau
                IPersistentClass persClass = null;
                
                if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){             
                    persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                    
                    if(persClass == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER){
                    persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());               
                    if(persClass == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                    IPersistentClassMapping persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());                
                    if(persistentClassMapping == null) return;
                    
                    persClass = persistentClassMapping.getPersistentClass();
                    if(persClass == null) return;                   
                }
                
                final IPersistentField field = (IPersistentField) ViewsUtils.getSelectedObject(IPersistentField.class, this.getViewer());                
                if(field == null) return;
                
                final IPersistentClass persistentClass = persClass;
                
                //add tau 17.03.2006
                final TreeItem parentItem = this.getViewer().getTree().getSelection()[0].getParentItem();
                final TreeViewer actionViewer = this.getViewer();

                // add IWorkspaceRunnable 15.06.2005
                IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
                    public void run(IProgressMonitor monitor) throws CoreException {
                                // add tau 09.06.2005
                                WizardDialog dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                                        new FieldMappingWizard(mapping, persistentClass, field, parentItem, true, actionViewer));
                                dialog.open();
                            }
                };
                try {
                    project.getProject().getWorkspace().run(runnable, new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in New Field Mapping.");
                }
                
                // add tau 09.11.2005               
                project.fireProjectChanged(this, false);               
                
                
            }               
        }; 
        
        fieldMappingWizardNewAction.setText(BUNDLE.getString("Explorer.FieldMappingWizardNameNew"));
        fieldMappingWizardNewAction.setToolTipText(BUNDLE.getString("Explorer.FieldMappingWizardToolTipText"));
        
        tablesClassesWizardAction = new ActionOrmTree() {
            public void rush() {
            	
            	// add tau 27.01.2006
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
            	
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping==null) return;
                ReversStatistic results=new ReversStatistic();
                WizardDialog reversedlg=new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new TablesClassesWizard(mapping, results, this.getViewer()));
                //akuzmin 29.07.2005                
                reversedlg.open();
                if (reversedlg.getReturnCode()==WizardDialog.OK)
                {
                String[] statisticlabel={BUNDLE.getString("Explorer.TablesClassesWizardInfoCreate"),
                                        BUNDLE.getString("Explorer.TablesClassesWizardInfonoPK"),
                                        BUNDLE.getString("Explorer.TablesClassesWizardInfoLink")};
                String[] statisticstatus={"INFO","WARNING","WARNING"};
                new StatisticDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                                    results.getAllResults(),
                                    statisticlabel,statisticstatus,BUNDLE.getString("Explorer.TablesClassesWizardInfoTitle")).open();
                }
                
                //TODO (!tau->tau) test fireProjectChanged ?
                // add tau 27.01.2006 from AbstractMapping addDatabasesTablesRunnable
                project.fireProjectChanged(this, false);                
            }
        }; 
        
        tablesClassesWizardAction.setText(BUNDLE.getString("Explorer.TablesClassesWizardName"));
        tablesClassesWizardAction.setToolTipText(BUNDLE.getString("Explorer.TablesClassesWizardToolTipText"));

        // 20050727 <yan>
        editNamedQueryAction=new ActionOrmTree() {
            public void rush() {
                XMLFileStorage queryStorage=(XMLFileStorage)ViewsUtils.getSelectedObject(XMLFileStorage.class, this.getViewer());
                if (queryStorage==null) {
                    IPersistentClass clazz=(IPersistentClass)ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                    if (clazz!=null) {
                        IMappingStorage storage=clazz.getPersistentClassMapping().getStorage();
                        if (storage instanceof XMLFileStorage) queryStorage=(XMLFileStorage)storage;
                    }
                }
                NamedQueryDefinition query=(NamedQueryDefinition)ViewsUtils.getSelectedObject(NamedQueryDefinition.class, this.getViewer()); 
                if (queryStorage!=null && query!=null) {
                    try {
                    	// TODO (tau -> tau) this.getViewer().refresh(queryStorage.getProjectMapping()); ?
                        if(new WizardDialog(
                                ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                                new NamedQueriesWizard(queryStorage.getProjectMapping(),queryStorage,query,false , this.getViewer())
                        ).open()==WizardDialog.OK)
                            this.getViewer().refresh(queryStorage.getProjectMapping());
                    } catch (DuplicateQueryNameException e) {
                        MessageDialog.openError(
                                ViewPlugin.getActiveWorkbenchShell(),
                                BUNDLE.getString("Explorer.DuplicateQueryNameErrorTitle"),
                                e.getMessage()
                        );
                    }
                }
            }
        }; 
        
        editNamedQueryAction.setText(BUNDLE.getString("Explorer.EditNamedQueryName"));
        editNamedQueryAction.setToolTipText(BUNDLE.getString("Explorer.EditNamedQueryToolTipText"));

        addNamedQueryAction=new ActionOrmTree() {
            public void rush() {
                XMLFileStorage queryStorage=(XMLFileStorage)ViewsUtils.getSelectedObject(XMLFileStorage.class, this.getViewer());
                if (queryStorage!=null) {
                    String qname=BUNDLE.getString("Explorer.AddNamedQuery.DefaultNewName");                	
                    NamedQueryDefinition query=new NamedQueryDefinition(
                            queryStorage,
                            qname,
                            "",
                            false,
                            null,
                            null,
                            null,
                            null
                    );
                    try {
                    	
                    	// TODO (tau -> tau) this.getViewer().refresh(queryStorage.getProjectMapping()); ?                    	
                        if(new WizardDialog(
                                ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                                new NamedQueriesWizard(queryStorage.getProjectMapping(),queryStorage,query,true, this.getViewer())
                        ).open()==WizardDialog.OK)
                            this.getViewer().refresh(queryStorage.getProjectMapping());
                    } catch (DuplicateQueryNameException e) {
                        MessageDialog.openError(
                                ViewPlugin.getActiveWorkbenchShell(),
                                BUNDLE.getString("Explorer.DuplicateQueryNameErrorTitle"),
                                e.getMessage()
                        );
                    }
                }
            }
        }; 
        
        addNamedQueryAction.setText(BUNDLE.getString("Explorer.AddNamedQueryName"));
        addNamedQueryAction.setToolTipText(BUNDLE.getString("Explorer.AddNamedQueryToolTipText"));

        // 20050916 <yan>
        testQueryAction=new ActionOrmTree() {
            public void rush() {
                
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IPersistentClass pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                if (pc==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) pc=persistentClassMapping.getPersistentClass();
                }
                if (pc != null) {
                    String query=MessageFormat.format(BUNDLE.getString("Explorer.TestQuery.DefaultQuery"),new Object[]{pc.getName()});
                    NamedQueryDefinition qdef=new NamedQueryDefinition(
                            pc.getPersistentClassMapping().getStorage(),
                            pc.getName(),
                            query,
                            false,
                            null,
                            null,
                            null,
                            null
                    );
                    try {
                        new QueryTestWizardDialog(
                                ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                                new NamedQueriesWizard(pc.getProjectMapping(),null,qdef,true, this.getViewer())
                        ).open();
                    } catch (Exception e) {
                        ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Named Query Wizard.");
                    }
                }
            }
        }; 
        testQueryAction.setText(BUNDLE.getString("Explorer.TestQueryName"));
        testQueryAction.setToolTipText(BUNDLE.getString("Explorer.TestQueryToolTipText"));
        // </yan>
        
        
        removeNamedQueryAction=new ActionOrmTree() {
            public void rush() {
                IMapping mapping=(IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping==null)return;
                XMLFileStorage queryStorage=(XMLFileStorage)ViewsUtils.getSelectedObject(XMLFileStorage.class, this.getViewer());
                if (queryStorage==null) {
                    IPersistentClass clazz=(IPersistentClass)ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                    if (clazz!=null) {
                        IMappingStorage storage=clazz.getPersistentClassMapping().getStorage();
                        if (storage instanceof XMLFileStorage) queryStorage=(XMLFileStorage)storage;
                    }
                }
                NamedQueryDefinition query=(NamedQueryDefinition)ViewsUtils.getSelectedObject(NamedQueryDefinition.class, this.getViewer());
                // #added# by Konstantin Mishin on 19.12.2005 fixed for ESORM-358
               	if(!MessageDialog.openQuestion(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.RemoveNamedQueryTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.RemoveNamedQueryMessage"),
                                new Object[]{query.getName()})))
            		return;
               	// #added#
                if (queryStorage!=null && mapping!=null && query!=null) {
                    try {
                        queryStorage.removeQuery(query.getName());
                        queryStorage.save(true);                        
                        
                        // #added# by Konstantin Mishin on 29.08.2005 fixed for ORMIISTUD-695
                    	// TODO (tau -> tau) this.getViewer().refresh(queryStorage); ?                        
                        this.getViewer().refresh(queryStorage);
                        // #added#
                    } catch (Exception e) {
                        ExceptionHandler.logThrowableError(e,null);
                    }
                    // TODO (!tau->tau) test
                    mapping.resourcesChanged();
                }
            }
        }; 
        
        removeNamedQueryAction.setText(BUNDLE.getString("Explorer.RemoveNamedQueryName"));
        removeNamedQueryAction.setToolTipText(BUNDLE.getString("Explorer.RemoveNamedQueryToolTipText"));
        // </yan>
        
        mappingWizardAction = new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping==null) return;

                IPersistentClass persistentClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                // #added# by Konstantin Mishin on 04.09.2005 fixed for ORMIISTUD-723
                if(persistentClass == null){
                    RootClassMapping rcm = (RootClassMapping) ViewsUtils.getSelectedObject(RootClassMapping.class, this.getViewer());
                    if(rcm != null)
                        persistentClass =  rcm.getPersistentClass();
                }
                
                // add tau 09.11.2005 
               
                // #added#
                if(persistentClass == null) {
                	//from visitPackage:
                    new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                            new MappingWizard(mapping, this.getViewer())).open();
                } else {
                    new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                            new MappingWizard(mapping, persistentClass, this.getViewer())).open();
                    
                }
                // add tau 09.11.2005               
                project.fireProjectChanged(this, false);               
            }
        }; 
        
        mappingWizardAction.setText(BUNDLE.getString("Explorer.MappingWizardName"));
        mappingWizardAction.setToolTipText(BUNDLE.getString("Explorer.MappingWizardToolTipText"));
        
        // add tau 22.04.2005
        generateDDLWizard = new ActionOrmTree() {
            public void rush() {
            	// add tau 14.01.2006 fixed(?) for ESORM-470
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project == null) return;
                if(!project.getProject().isSynchronized(IResource.DEPTH_INFINITE)) {
                	try {
                		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("ActionExplorerVisitor -> generateDDLWizard -> refreshLocal for: " + project);                		
						project.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (CoreException e) {
	                    ExceptionHandler.handle(e,
	                    		ViewPlugin.getActiveWorkbenchShell(),
	                    		BUNDLE.getString("Explorer.GenerateDDLErrorTitle"),
	                    		e.getMessage());
                        return;
                    }
                }
            	// -- 14.01.2006 fixed(?) for ESORM-470
            	
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if(mapping==null) return;
                // #added# by Konstantin Mishin on 12.12.2005 fixed for ESORM-423
                if(!mapping.getProject().getProject().getWorkspace().isAutoBuilding()) 
                	if(!MessageDialog.openQuestion(ViewPlugin.getActiveWorkbenchShell(),
                            BUNDLE.getString("Explorer.AutoBuildingFalseTitle"),
                            BUNDLE.getString("Explorer.AutoBuildingFalseMessage")))
                		return;
                // #added#
                new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new GenerateDDLWizard(mapping))
                        .open();
            }
        }; 
        
        generateDDLWizard.setText(BUNDLE.getString("Explorer.GenerateDDLWizardName"));
        generateDDLWizard.setToolTipText(BUNDLE.getString("Explorer.GenerateDDLWizardToolTipText"));
        
        // add tau 11.05.2005       
        cacheWizardAction = new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if(mapping==null) return;

                WizardDialog dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        new HibernateCacheWizard(mapping, this.getViewer()));
                if (dialog.open() == Dialog.OK) 
                    project.fireProjectChanged(this, false);               
            }
        }; 
        
        cacheWizardAction.setText(BUNDLE.getString("Explorer.HibernateCacheWizardName"));
        cacheWizardAction.setToolTipText(BUNDLE.getString("Explorer.HibernateCacheWizardToolTipText"));
        
        // add tau 23.05.2005       
        fetchStrategyWizardAction = new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if(mapping==null) return;
                
                WizardDialog dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench()
                        .getActiveWorkbenchWindow().getShell(),
                        new FetchStrategyWizard(mapping, this.getViewer()));
                if (dialog.open() == Dialog.OK) 
                    project.fireProjectChanged(this, false);               
            }
        }; 
        
        fetchStrategyWizardAction.setText(BUNDLE.getString("Explorer.FetchStrategyWizarddName"));
        fetchStrategyWizardAction.setToolTipText(BUNDLE.getString("Explorer.FetchStrategyWizardToolTipText"));

        //Setings ----------------//
        autoMappingSettingAction = new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                Dialog dialog = new AutoMappingSetting(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        project);
                dialog.open();//add  9.03.05
                
            }
        }; 
        
        autoMappingSettingAction.setText(BUNDLE.getString("Explorer.AutoMappingSettingName"));
        autoMappingSettingAction.setToolTipText(BUNDLE.getString("Explorer.AutoMappingSettingToolTipText"));

        runTimeSettingAction = new ActionOrmTree() {
            public void rush() {
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if(mapping==null){
                    IOrmProject project=getOrmProject(this.getViewer().getTree());
                    if(project==null) return;               
                    IMapping[] maps=project.getMappings();
                    if(maps.length==0)return;
                    mapping=maps[0];
                }
                Dialog dialog = new RunTimeSetting(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),mapping);
                dialog.open();//add  9.03.05
            }
        }; 
        
        runTimeSettingAction.setText(BUNDLE.getString("Explorer.RunTimeSettingName"));
        runTimeSettingAction.setToolTipText(BUNDLE.getString("Explorer.RunTimeSettingToolTipText"));
        
        renameTableObjectAction = new ActionOrmTree() { // by tau on 24.05.2005
            public void rush() {
                final IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());              
                if (mapping == null) return;
//akuzmin 15.06.2005
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                IDatabaseTable databaseTable = (IDatabaseTable) ViewsUtils.getSelectedObject(IDatabaseTable.class, this.getViewer());
                if (databaseTable == null) return;              
                
                Dialog dialog = new RenameTableObjectDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        mapping,
                        databaseTable);
//              akuzmin 07.09.2005              
                if (dialog.open() == Dialog.OK) 
                    project.fireProjectChanged(this, false);               
            }
        }; 
        
        renameTableObjectAction.setText(BUNDLE.getString("Explorer.RenameTableObjectActionName"));
        renameTableObjectAction.setToolTipText(BUNDLE.getString("Explorer.RenameTableObjectActionToolTipText"));

        renameTableObjectDialogForColumnAction = new ActionOrmTree() { // by tau on 26.05.2005
            public void rush() {
                // add tau 09.11.2005
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;
                
                // IDatabaseTable databaseTable = getSelectedDatabaseTable();
                IDatabaseTable databaseTable = (IDatabaseTable) ViewsUtils.getSelectedObject(IDatabaseTable.class, this.getViewer());
                
                // edit 06.06.2005
                if (databaseTable == null) {
                    // edit 20.05.2005 tau
                    IPersistentClass persClass = null;
                    IPersistentClassMapping persistentClassMapping = null;                  
                    if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){             
                        persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());               
                        if(persClass == null) return;

                        persistentClassMapping = persClass.getPersistentClassMapping();
                        if (persistentClassMapping == null) return;
                        
                        databaseTable = persistentClassMapping.getDatabaseTable();
                        
                    } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER){
                        persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());               
                        if(persClass == null) return;
                        
                        persistentClassMapping = persClass.getPersistentClassMapping();
                        if (persistentClassMapping == null) return;
                        
                        databaseTable = persistentClassMapping.getDatabaseTable();
                        
                    } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                        persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());                
                        if(persistentClassMapping == null) return;

                        databaseTable = persistentClassMapping.getDatabaseTable();                      
                    
                    }
                    databaseTable = persistentClassMapping.getDatabaseTable();                  
                    if (databaseTable == null) return;                  
                }
                
                Column column = (Column) ViewsUtils.getSelectedObject(Column.class, this.getViewer());               
                if (column == null) return;
                
                Dialog dialog = new RenameTableObjectDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        mapping,
                        databaseTable,
                        column);
                
                if (dialog.open() == Dialog.OK) 
                    project.fireProjectChanged(this, false);                
                
                //TODO (!tau->tau) test fireProjectChanged ? dublicate              
                
            }
        }; 
        
        renameTableObjectDialogForColumnAction.setText(BUNDLE.getString("Explorer.renameTableObjectDialogForColumnActionName"));
        renameTableObjectDialogForColumnAction.setToolTipText(BUNDLE.getString("Explorer.renameTableObjectDialogForColumnActionToolTipText"));
        
        addColumnDialogAction = new ActionOrmTree() { // by tau on 24.05.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                IDatabaseTable databaseTable = (IDatabaseTable) ViewsUtils.getSelectedObject(IDatabaseTable.class, this.getViewer());
                if (databaseTable == null) return;
                
                Dialog dialog = new AddColumnDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        databaseTable);
                if (dialog.open() == Dialog.OK) 
                    project.fireProjectChanged(this, false);
            }
        }; 
        
        addColumnDialogAction.setText(BUNDLE.getString("Explorer.AddColumnDialogActionName"));
        addColumnDialogAction.setToolTipText(BUNDLE.getString("Explorer.AddColumnDialogActionToolTipText"));
        
        columnPropertyDialogAction = new ActionOrmTree() { // by tau on 03.06.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;
            
                Column column = (Column) ViewsUtils.getSelectedObject(Column.class, this.getViewer());               
                if (column == null) return;
                
                Dialog dialog = new ColumnPropertyDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        mapping, column, this.getViewer());
                dialog.open();
                project.fireProjectChanged(this, false);               

            }
        }; 
        
        columnPropertyDialogAction.setText(BUNDLE.getString("Explorer.ColumnPropertyDialogActionName"));
        columnPropertyDialogAction.setToolTipText(BUNDLE.getString("Explorer.ColumnPropertyDialogActionToolTipText"));
        
        viewTableAction = new ActionOrmTree() { // by tau on 03.06.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;
                
                IPersistentClass pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                // 20050620 <yan>
                if (pc==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) pc=persistentClassMapping.getPersistentClass();
                }
                // </yan>
                if(pc==null || pc.getPersistentClassMapping()==null) return;
                
                IPersistentClassMapping classMapping = pc.getPersistentClassMapping();
                if (classMapping == null) return; 
                
                IDatabaseTable table = HibernateAutoMappingHelper.getPrivateTable(classMapping); // upd tau 18.04.2005
                if (table == null) return;
                
                // add 29.07.2005
                IDatabaseSchema databaseSchema = table.getSchema();
                if (databaseSchema == null) return;             
                
                
                 //activates Relational schema view and selects class table
                IWorkbench workbench = PlatformUI.getWorkbench();
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                try {
                    ExplorerBase explorerBaseView = (ExplorerBase) window.getActivePage().showView(ExplorerBase.getExplorerId());
                    explorerBaseView.showOrmElement(project, mapping, databaseSchema, table, true);
                    explorerBaseView.setFocus();
                } catch (PartInitException e) {
                    ExceptionHandler.logThrowableError(e,null);
                }

                
            }
        }; 
        
        viewTableAction.setText(BUNDLE.getString("Explorer.ViewTableActionName"));
        viewTableAction.setToolTipText(BUNDLE.getString("Explorer.ViewTableActionToolTipText"));
        
        clearPackageMappingAction = new ActionOrmTree() { // by alex on 04/14/05
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                
                IPackage pack = (IPackage) ViewsUtils.getSelectedObject(IPackage.class, this.getViewer());
                if(pack == null) return;
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;
                
                if (!MessageDialog.openQuestion(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.ClearPackageMappingTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.ClearPackageMappingQuestion"),
                                new Object[]{(pack.getName() == null || pack.getName().trim().length() == 0 )?
                                        BUNDLE.getString("OrmModelNameVisitor.DefaultPackageName"):pack.getName()}))) return;                       
                                // #changed#

                try{
                    
                    IPersistentClass[] pcs=pack.getPersistentClasses();
                    HashSet<IMapping> refrMaps = new HashSet<IMapping>();
                    for(int i=0;i<pcs.length;++i) clearPersistentClassMapping(pcs[i],refrMaps);
                    refreshMappings(refrMaps);
// yan                  
                } catch(Exception ex){
                    ExceptionHandler.handle(ex, null, clearPackageMappingAction.getText(), null);
                }
                
                project.fireProjectChanged(this, false);
            }
        }; 
        clearPackageMappingAction.setText(BUNDLE.getString("Explorer.ClearPackageMappingName"));
        clearPackageMappingAction.setToolTipText(BUNDLE.getString("Explorer.ClearPackageMappingToolTipText"));
        
        clearClassMappingAction = new ActionOrmTree() { // by alex on 04/14/05
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                
                IPersistentClass pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                // 20050620 <yan>
                if (pc==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) pc=persistentClassMapping.getPersistentClass();
                }
                // </yan>
                if(pc==null || pc.getPersistentClassMapping()==null) return;
                
                if (!MessageDialog.openQuestion(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.ClearClassMappingTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.ClearClassMappingQuestion"),
                                new Object[]{pc.getName()}))) return;
                
                
                try{
                    
// yan 2005.09.22                   
                    HashSet<IMapping> refrMaps = new HashSet<IMapping>();
                    clearPersistentClassMapping(pc,refrMaps);
                    refreshMappings(refrMaps);
// yan                  
                } catch(Exception ex){
                    ExceptionHandler.handle(ex, null, clearClassMappingAction.getText(), null);
                }
                
                project.fireProjectChanged(this, false);
            }
        }; 
        clearClassMappingAction.setText(BUNDLE.getString("Explorer.ClearClassMappingName"));
        clearClassMappingAction.setToolTipText(BUNDLE.getString("Explorer.ClearClassMappingToolTipText"));
        
        removeClassAction = new ActionOrmTree() { // by alex on 04/14/05
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IPersistentClass pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                // 20050620 <yan>
                if (pc==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) pc=persistentClassMapping.getPersistentClass();
                }
                // </yan>
                if(pc == null) return;
                
                // add 07.07.2005 tau               
                RemoveConfirmCheckDialog dialog = new RemoveConfirmCheckDialog(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.RemoveClassAction.QuestionDialog.Title"),
                        MessageFormat.format(BUNDLE.getString("Explorer.RemoveClassAction.QuestionDialog.Message"),
                                new Object[]{pc.getName()}),                        
                        new String[]{
                            BUNDLE.getString("Explorer.RemoveClassAction.QuestionDialog.Radio1"),
                            BUNDLE.getString("Explorer.RemoveClassAction.QuestionDialog.Radio2")                    
                        });
                
                if (dialog.open() != Dialog.OK) return;
                final boolean removeStorages = dialog.getDeleteMapping();               
                final boolean removeSourceCode = dialog.getDeleteSourceCode();
                
                try {
                    IMappingStorage storage = null;
                    IResource storageResource = null;                   
                    if (removeStorages && pc.getPersistentClassMapping() != null) {
                        // get storage and storageResource  
                        storage = pc.getPersistentClassMapping().getStorage();
                        if (storage != null){
                            storageResource = storage.getResource();
                        }
                    }
                    HashSet<IMapping> refrMaps = new HashSet<IMapping>();
                    ExceptionHandler.logInfo("--- Remove persistent class "+pc.getName());
                    removePersistentClass(pc,removeSourceCode,removeStorages,refrMaps);
                    refreshMappings(refrMaps);
// yan                  
                    if (removeSourceCode &&
                            pc.getSourceCode() != null &&
                            pc.getSourceCode().getResource() != null &&
                            pc.getSourceCode().getResource().exists()){
                        // Delete SourceCode
                    	// add tau 16.02.2006 + attributesResource
            			ResourceAttributes attributesResource = pc.getSourceCode().getResource().getResourceAttributes();
            			if (!(attributesResource == null || attributesResource.isReadOnly())) {
                            pc.getSourceCode().getResource().delete(true, new NullProgressMonitor());
            			}                    	
                    }
                    
                    // removeStorages flag added yan
                    if (removeStorages && storage != null &&
                    // queryes
                            storage.getPersistentClassMappings().length == 0
                            && storageResource != null
                            && storageResource.exists()) {
                        // Delete mapping-file if in him there are no others
                        // mappings

                        try {
                            storage.getProjectMapping().removeMappingStorage(storage);
                        } catch (Exception e) {
                            throw new NestableRuntimeException(e);
                        }
                        
                    	// add tau 16.02.2006 + attributesResource
            			ResourceAttributes attributesResource = storageResource.getResourceAttributes();
            			if (!(attributesResource == null || attributesResource.isReadOnly())) {
                            storageResource.delete(true, new NullProgressMonitor());
            			}                    	                        
                    }
                } catch(Exception ex){
                    ExceptionHandler.handle(ex, null, removeClassAction.getText(), null);
                }
                
                // add tau 09.11.2005
            	project.setDirty(true); // add tau 09.03.2006                
                project.fireProjectChanged(this, false);
                
                
                // yan 2005.09.22
                IProject[] prjs=project.getReferencedProjects();
                for(int i=0; i<prjs.length; i++) {
                    try {
                        prjs[i].refreshLocal(IResource.DEPTH_INFINITE,null);
                    } catch (CoreException e) {
                        ExceptionHandler.logThrowableWarning(e,"Refresh referenced project "+prjs[i].getName());
                    }
                }
                // yan
            }
        }; 
        removeClassAction.setText(BUNDLE.getString("Explorer.RemoveClassActionName"));
        removeClassAction.setToolTipText(BUNDLE.getString("Explorer.RemoveClassActionToolTipText"));
        removeClassAction.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.Delete")));
        
        // 20050618 <yan>
        removeClassesAction=new ActionOrmTree() {
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                final IPackage pack = (IPackage) ViewsUtils.getSelectedObject(IPackage.class, this.getViewer());
                if(pack == null) return;
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;
                
                // add 08.07.2005 tau               
                RemoveConfirmCheckDialog dialog = new RemoveConfirmCheckDialog(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.RemoveClassesAction.QuestionDialog.Title"),
                        MessageFormat.format(BUNDLE.getString("Explorer.RemoveClassesAction.QuestionDialog.Message"),
                                new Object[]{(pack.getName() == null || pack.getName().trim().length() == 0 )?
                                        BUNDLE.getString("OrmModelNameVisitor.DefaultPackageName"):pack.getName()}),                        
                        new String[]{
                            BUNDLE.getString("Explorer.RemoveClassesAction.QuestionDialog.Radio1"),
                            BUNDLE.getString("Explorer.RemoveClassesAction.QuestionDialog.Radio2")                  
                        });
                
                if (dialog.open() != Dialog.OK) return;
                final boolean removeStorages = dialog.getDeleteMapping();               
                final boolean removeSourceCode = dialog.getDeleteSourceCode();
                
                try{
                    
                    IWorkspaceRunnable runnable = new IWorkspaceRunnable()
                    {
                        public void run(IProgressMonitor monitor) throws CoreException {
                           final HashSet<IMapping> refrMaps=new HashSet<IMapping>();
                            IPersistentClass[] pcs=pack.getPersistentClasses();
                            for (int i = 0; i < pcs.length; i++) {
                                IPersistentClass pc = pcs[i];
                                IMappingStorage storage = null;
                                IResource storageResource = null;                   
                                if (removeStorages && pc.getPersistentClassMapping() != null) {
                                    // get storage and storageResource  
                                    storage = pc.getPersistentClassMapping().getStorage();
                                    if (storage != null){
                                        storageResource = storage.getResource();
                                    }
                                }
                                removePersistentClass(pc,removeSourceCode,removeStorages,refrMaps);
//         yan                      
                                if (removeSourceCode &&
                                        pc.getSourceCode() != null &&
                                        pc.getSourceCode().getResource() != null &&
                                        pc.getSourceCode().getResource().exists()){
                                    // Delete SourceCode                        
                                    pc.getSourceCode().getResource().delete(true, new NullProgressMonitor());
                                }
                                // removeStorages flag added yan
                                if (removeStorages && storage != null && 
                                        storage.getPersistentClassMappings().length == 0 && 
                                        storageResource != null &&
                                        storageResource.exists()){
                                    // Delete mapping-file if in him there are no others mappings                       
                                    try {
                                        storage.getProjectMapping().removeMappingStorage(storage);
                                    } catch (Exception e) {
                                        throw new NestableRuntimeException(e);
                                    }
                                    storageResource.delete(true, new NullProgressMonitor());                        
                                }                       
                                
                                 }
                            try {
                                        refreshMappings(refrMaps);
                                    } catch (Exception e) {
                                        ExceptionHandler.handle(e, null, removeClassesAction.getText(), null);
                                    }
                        }
                        
                    };
                    ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
                    
                } catch(Exception ex){
                    ExceptionHandler.handle(ex, null, removeClassesAction.getText(), null);
                }
                
                // add tau 09.11.2005 
            	project.setDirty(true); // add tau 09.03.2006
                project.fireProjectChanged(this, false);
                
                // yan 2005.09.22
                IProject[] prjs=project.getReferencedProjects();
                for(int i=0; i<prjs.length; i++) {
                    try {
                        prjs[i].refreshLocal(IResource.DEPTH_INFINITE,null);
                    } catch (CoreException e) {
                        ExceptionHandler.logThrowableWarning(e,"Refresh referenced project "+prjs[i].getName());
                    }
                }
                // yan
            }
        };
        removeClassesAction.setText(BUNDLE.getString("Explorer.RemoveClassesActionName"));
        removeClassesAction.setToolTipText(BUNDLE.getString("Explorer.RemoveClassesActionToolTipText"));
        removeClassesAction.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.Delete")));
        // </yan>
        
        clearFieldMappingAction= new ActionOrmTree() { // by alex on 04/14/05
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                // edit 20.05.2005 tau
                IPersistentClass persClass = null;
                
                if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){             
                    persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());               
                    if(persClass == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER){
                    persClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());               
                    if(persClass == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                    IPersistentClassMapping persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());                
                    if(persistentClassMapping == null) return;
                    
                    persClass = persistentClassMapping.getPersistentClass();
                    if(persClass == null) return;                   
                }
                
                IPersistentField field = (IPersistentField) ViewsUtils.getSelectedObject(IPersistentField.class, this.getViewer());              
                if(field == null || field.getMapping()==null) return;
                
                if (!MessageDialog.openQuestion(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.ClearFieldMappingTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.ClearFieldMappingQuestion"),
                                new Object[]{field.getName()}))) return;

                
                final IPersistentClass persistentClass = persClass;
                
                // add tau 09.11.2005 
                
                try{			
   			    
                    //akuzmin 14.09.2005 remove parent
                    if ((((PropertyMapping)field.getMapping()).getPropertyMappingHolder() instanceof ComponentMapping)
                            &&(field.getName().equals(((ComponentMapping)((PropertyMapping)field.getMapping()).getPropertyMappingHolder()).getParentProperty())))
                        ((ComponentMapping)((PropertyMapping)field.getMapping()).getPropertyMappingHolder()).setParentProperty(null);
// add by yk Jun 10, 2005.
                    IPropertyMappingHolder pmh = ((IPropertyMapping)field.getMapping()).getPropertyMappingHolder();
                    if (pmh != null)
                        pmh.removeProperty((IPropertyMapping)field.getMapping());
                    IMappingStorage stg = persistentClass.getPersistentClassMapping().getStorage();
                    // #added# by Konstantin Mishin on 27.12.2005 fixed for ESORM-434
            		IJoinMapping joinMapping = null;
                	Iterator iter =((IHibernateClassMapping)(persistentClass.getPersistentClassMapping())).getJoinIterator();			
                	while ( iter.hasNext() ) {
                		IJoinMapping jm =(IJoinMapping)iter.next();
                			if (jm.containsProperty((PropertyMapping)field.getMapping()))
                				joinMapping = jm;
                	}
                	if(joinMapping != null)
                		((ClassMapping)(persistentClass.getPersistentClassMapping())).removeJoin(joinMapping);
                    // #added#
                    // added by Nick 10.06.2005
                    field.setMapping(null);
                    // by Nick
                    
                    stg.save(true);
                    
// add by yk Jun 10, 2005 stop.
                    
                } catch(Exception ex){
                    ExceptionHandler.handle(ex, null, clearFieldMappingAction.getText(), null);
                }

                project.fireProjectChanged(this, false);
            }
        }; 
        clearFieldMappingAction.setText(BUNDLE.getString("Explorer.ClearFieldMappingActionName"));
        clearFieldMappingAction.setToolTipText(BUNDLE.getString("Explorer.ClearFieldMappingActionToolTipText"));

        classAutoMappingAction = new ActionOrmTree() { // by alex on 04/14/05
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                
                IPersistentClass pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                // 20050620 <yan>
                if (pc==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) pc=persistentClassMapping.getPersistentClass();
                }
                // </yan>
                if(pc == null) return;
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;                

                IAutoMappingService.Settings setting=new IAutoMappingService.Settings();
                setting.canChangeTables=true;
                setting.canChangeClasses=true;
                
                try{
                    mapping.getAutoMappingService().generateMapping(new IPersistentClass[]{pc},setting );
                    mapping.saveMappingStorage(pc.getPersistentClassMapping().getStorage());                    
                    
                } catch (Exception ex){
                    ExceptionHandler.handle(ex, null, classAutoMappingAction.getText(), null);
                }
                
                project.fireProjectChanged(this, false);
                
            }
        };
        classAutoMappingAction.setText(BUNDLE.getString("Explorer.ClassAutoMappingActionName"));
        classAutoMappingAction.setToolTipText(BUNDLE.getString("Explorer.ClassAutoMappingActionToolTipText"));

        //called on class
        refreshClassSchemaMappingAction = new ActionOrmTree() { // by tau 20.07.2005
            public void rush() {
                
                IOrmProject project = getOrmProject(this.getViewer().getTree());
                if (project == null) return ;
                
                IPersistentClass pc = null;
                
                // add 01.08.2005 
                if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){             
                    pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());              
                    if(pc == null) return;
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                    IPersistentClassMapping persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());                
                    if(persistentClassMapping == null) return;
                    pc = persistentClassMapping.getPersistentClass();
                    if(pc == null) return;                  
                }                
                
                if (!MessageDialog.openConfirm(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.RefreshClassSchemaMappingTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.RefreshClassSchemaMappingQuestion"),
                                new Object[]{pc.getName()}))) return;
                
                // added by Nick 25.07.2005
                BaseMappingVisitor syncVisitor = new CorrectMappingVisitor();
                    
                IPersistentClassMapping cm = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());                    
                if (cm == null)
                {
                    if (pc != null && pc.getPersistentClassMapping() != null)
                    {
                        cm = pc.getPersistentClassMapping();
                    }
                    else
                    {
                        return ;
                    }
                }

                cm.accept(syncVisitor,null);
                // by Nick
                // edit tau 28.03.2006
                classAutoMappingAction.setViewer(this.getViewer());
                classAutoMappingAction.run();
                
                //TODO (!tau->tau) test fireProjectChanged ?                
            }
        };
        refreshClassSchemaMappingAction.setText(BUNDLE.getString("Explorer.RefreshClassSchemaMappingActionName"));
        refreshClassSchemaMappingAction.setToolTipText(BUNDLE.getString("Explorer.RefreshClassSchemaMappingActionToolTipText"));
        
        packageAutoMappingAction= new ActionOrmTree() { // by alex on 04/14/05, edit tau 29.06.2005
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                final IPackage pack = (IPackage) ViewsUtils.getSelectedObject(IPackage.class, this.getViewer());             
                if (pack == null) return;
                
                final IAutoMappingService.Settings setting=new IAutoMappingService.Settings();
                setting.canChangeTables=true;
                setting.canChangeClasses=true;
                
                //add tau 17.03.2006
                final TreeViewer actionViewer = this.getViewer();
                
                
                IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
                    public void run(IProgressMonitor monitor) throws CoreException {
                        IRunnableWithProgress operation = new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(
                                        MessageFormat.format(BUNDLE.getString("Explorer.PackageAutoMappingAction.ProgressMonitor"),
                                                new Object[]{pack.getName()}),
                                                100);
                                OrmProgressMonitor.setMonitor(monitor);
                                try {
                                    IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, actionViewer);                    
                                    mapping.getAutoMappingService().generateMapping(pack.getPersistentClasses(),setting );
                                    
                                    // add tau 29.07.2005 from refreshPackageSchemaMappingAction
                                    pack.accept(new CorrectMappingVisitor(),null);
                                    
                                    mapping.savePackageMappingStorage(pack);                                    
                                    
                                    // #added# by Konstantin Mishin on 2005/08/22 fixed for ORMIISTUD-653
                                    actionViewer.refresh(pack);
                                    // #added#
                                } catch (IOException e) {
                                    throw new InvocationTargetException(e);
                                } catch (CoreException e) {
                                    throw new InvocationTargetException(e);
                                } finally {
                                    monitor.done();
                                    OrmProgressMonitor.setMonitor(null);
                                }
                            }
                        };              
                        ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
                        try {
                            progress.run(false, true, operation);
                        } catch (InvocationTargetException e) {
                            ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),packageAutoMappingAction.getText(), "Error in package Auto Mapping Action.");
                        } catch (InterruptedException e1) {
                            // Cancelled.
                        }
                    }
                };
                
                try {
                    project.getProject().getWorkspace().run(runnable, new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),packageAutoMappingAction.getText(), "Error in package Auto Mapping Action.");
                }
                //TODO (!tau->tau) test fireProjectChanged ?                
            }
        };
        
        packageAutoMappingAction.setText(BUNDLE.getString("Explorer.PackageAutoMappingActionName"));
        packageAutoMappingAction.setToolTipText(BUNDLE.getString("Explorer.PackageAutoMappingActionToolTipText"));
        
        //called on package
        refreshPackageSchemaMappingAction= new ActionOrmTree() { // add by tau on 20.07.2005
            public void rush() {
                IOrmProject project = getOrmProject(this.getViewer().getTree());
                if (project == null)
                    return ;

                IPackage pkg = (IPackage) ViewsUtils.getSelectedObject(IPackage.class, this.getViewer());

                if (pkg == null) return ;               
                
                if (!MessageDialog.openConfirm(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.RefreshPackageSchemaMappingTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.RefreshClassSchemaMappingQuestion"),
                                new Object[]{pkg.getName()}))) return;
                        
                // edit tau 28.03.2006
                packageAutoMappingAction.setViewer(this.getViewer());                
                packageAutoMappingAction.run();
    
                // by Nick
                
                //TODO (!tau->tau) test fireProjectChanged ? dublicate              
            }
        };
        
        refreshPackageSchemaMappingAction.setText(BUNDLE.getString("Explorer.RefreshPackageSchemaMappingActionName"));
        refreshPackageSchemaMappingAction.setToolTipText(BUNDLE.getString("Explorer.RefreshPackageSchemaMappingActionToolTipText"));
        
        //called on Hib cfg
        refreshMappingSchemaMappingAction = new ActionOrmTree() { // add by tau on 20.07.2005
            public void rush() {
                final IOrmProject project = getOrmProject(this.getViewer().getTree());
                if (project == null) return;
                final IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());              
                if (mapping == null) return;
                
                if (!MessageDialog.openConfirm(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.RefreshMappingSchemaMappingTitle"),
                        MessageFormat.format(BUNDLE.getString("Explorer.RefreshClassSchemaMappingQuestion"),
                                new Object[]{mapping.getName()}))) return;
                        
                final IAutoMappingService.Settings setting = new IAutoMappingService.Settings();
                setting.canChangeTables = true;
                setting.canChangeClasses = true;
                
                //add tau 17.03.2006
                final TreeViewer actionViewer = this.getViewer();
                
                IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor) throws CoreException {
                        IRunnableWithProgress operation = new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(
                                                MessageFormat
                                                        .format(
                                                                BUNDLE
                                                                        .getString("Explorer.RefreshMappingSchemaMapping.ProgressMonitor"),
                                                                new Object[] { mapping.getName() }),100);
                                IOrmProject project = getOrmProject(actionViewer.getTree());
                                if (project == null)
                                    return ;
                                
                                OrmProgressMonitor.setMonitor(monitor);
                                try {
                                    mapping.getAutoMappingService()
                                            .generateMapping(mapping.getPertsistentClasses(),setting);
                                    // added by Nick 25.07.2005
                                    mapping.accept(new CorrectMappingVisitor(),null);
                                    // by Nick
                                    
                                    mapping.saveAllMappingStorage();
                                } catch (CoreException e) {
                                    throw new InvocationTargetException(e);
                                } catch (IOException e) {
                                    throw new InvocationTargetException(e);
                                } finally {
                                    monitor.done();
                                    OrmProgressMonitor.setMonitor(null);
                                }
                            }
                        };
                        ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
                        try {
                            progress.run(false, true, operation);
                        } catch (InvocationTargetException e) {
                            ExceptionHandler.handle(e, ViewPlugin
                                    .getActiveWorkbenchShell(),
                                    refreshMappingSchemaMappingAction.getText(),
                                    "Error in Refresh Mapping.");
                        } catch (InterruptedException e1) {
                            // Cancelled.
                        }
                    }
                };

                try {
                    project.getProject().getWorkspace().run(runnable, new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e, ViewPlugin
                            .getActiveWorkbenchShell(),
                            refreshMappingSchemaMappingAction.getText(),
                            "Error in Refresh Mapping.");
                }
                
                //TODO (!tau->tau) test fireProjectChanged ?
            }
        };
        
        refreshMappingSchemaMappingAction.setText(BUNDLE.getString("Explorer.RefreshMappingSchemaMappingActionName"));
        refreshMappingSchemaMappingAction.setToolTipText(BUNDLE.getString("Explorer.RefreshMappingSchemaMappingActionToolTipText"));        

        
        refactoringDialogClassAction = new ActionOrmTree() { // by tau on 23/05/2005
            public void rush() {
                
                IPersistentClass pc = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                // 20050620 <yan>
                if (pc==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) pc=persistentClassMapping.getPersistentClass();
                }
                // </yan>
                if(pc == null) return;
                
                // 20050524 20:17
                // Yan
                RefactoringSupport.renamePersistentClass(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),pc);
            }               
        };
        refactoringDialogClassAction.setText(BUNDLE.getString("Explorer.RefactoringDialogClassActionName"));
        refactoringDialogClassAction.setToolTipText(BUNDLE.getString("Explorer.RefactoringDialogClassActionToolTipText"));
        
        refactoringDialogFieldAction = new ActionOrmTree() { // by tau on 23/05/2005
            public void rush() {
                // 20050609 <yan>
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IPersistentField pf = (IPersistentField)ViewsUtils.getSelectedObject(IPersistentField.class, this.getViewer());
                if(pf==null) return;
                RefactoringSupport.renamePersistentField(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),pf);
            }
        };
        refactoringDialogFieldAction.setText(BUNDLE.getString("Explorer.RefactoringDialogFieldActionName"));
        refactoringDialogFieldAction.setToolTipText(BUNDLE.getString("Explorer.refactoringDialogFieldActionToolTipText"));
        
        collapseAllAction= new ActionOrmTree() { // by tau 23/05/2005 edit 10.06.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                this.getViewer().collapseToLevel(project,AbstractTreeViewer.ALL_LEVELS);
            }
        }; 
        collapseAllAction.setText(BUNDLE.getString("Explorer.CollapseAllActionName"));
        collapseAllAction.setToolTipText(BUNDLE.getString("Explorer.CollapseAllActionToolTipText"));
        
        expandAllAction= new ActionOrmTree() { // by tau 23/05/2005 edit 10.06.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                int level = 0;
                if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){             
                    level = 3;              
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER){
                    level = 2;              
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                    level = 2;                  
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER){
                    level = 3;                  
                } else if (((OrmContentProvider)this.getViewer().getContentProvider()).getTip() == OrmContentProvider.TABLE_COLUMN_CONTENT_PROVIDER){
                    level = 2;                  
                }
                
                this.getViewer().getTree().setRedraw(false);
                this.getViewer().expandToLevel(project,level);
                this.getViewer().getTree().setRedraw(true);               

            }
        }; 
        expandAllAction.setText(BUNDLE.getString("Explorer.ExpandAllActionName"));
        expandAllAction.setToolTipText(BUNDLE.getString("Explorer.ExpandAllActionToolTipText"));

        refreshMappingAction = new ActionOrmTree() { // by tau 24.05.2005
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                final IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());              
                if(mapping==null) return;
                
                IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
                    public void run(IProgressMonitor monitor) throws CoreException {
                        IRunnableWithProgress operation = new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(BUNDLE.getString("Explorer.refreshMappingAction.ProgressMonitor"),100);
                                OrmProgressMonitor.setMonitor(monitor);
                                try {
                                	
                                	//TODO !!!! ?????
                                	// add tau 07.03.2006
                                	project.setDirty(true);
                                	
                                    mapping.reload(true);  // edit tau 23.11.2005 do doMappingsUpdate                                   
                                    
                                    OrmProgressMonitor monitorValidation = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());
                                    OrmBuilder.fullBuild(project, monitorValidation, this);
                                    
                                } catch (IOException e) {
                                    throw new InvocationTargetException(e);
                                } catch (CoreException e) {
                                    throw new InvocationTargetException(e);
                                } finally {
                                    monitor.done();
                                    OrmProgressMonitor.setMonitor(null);
                                }
                            }
                        };              
                        ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
                        try {
                            progress.run(false, true, operation);
                        } catch (InvocationTargetException e) {
                            ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Reload Mapping");
                        } catch (InterruptedException e1) {
                            // Cancelled.
                        }
                    }
                };
                
                try {
                    project.getProject().getWorkspace().run(runnable, new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Refresh.");
                } finally {
                	// 	add 27.01.2006 true or false?                 	
                	project.fireProjectChanged(this, false);                	
                }
            }
        };

        
        refreshMappingAction.setActionDefinitionId("org.eclipse.ui.file.refresh");      
        refreshMappingAction.setText(BUNDLE.getString("Explorer.refreshMappingActionName"));
        refreshMappingAction.setToolTipText(BUNDLE.getString("Explorer.refreshMappingActionToolTipText"));
        
        refreshOrmProjectAction = new ActionOrmTree() { // by tau 24.05.2005
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                // add tau 03.03.2006
                project.setDirty(true);
                
                IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
                    public void run(IProgressMonitor monitor) throws CoreException {
                        IRunnableWithProgress operation = new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(
                                        MessageFormat.format(BUNDLE.getString("Explorer.refreshOrmProjectAction.ProgressMonitor"),
                                                new Object[]{project.getName()}),
                                                100);
                                OrmProgressMonitor.setMonitor(monitor);
                                try {
                                	// add tau 07.03.2006
                                	project.setDirty(true);
                                	
                                    project.refresh(true);  // edit tau 23.11.2005 do doMappingsUpdate                                  
                                    
                                    OrmProgressMonitor monitorValidation = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());
                                    OrmBuilder.fullBuild(project, monitorValidation, this);
                                } catch (CoreException e) {
                                    throw new InvocationTargetException(e);
                                } finally {
                                    monitor.done();
                                    OrmProgressMonitor.setMonitor(null);
                                }
                            }
                        };              
                        ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
                        try {
                            progress.run(false, true, operation);
                        } catch (InvocationTargetException e) {
                            ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Refresh Project.");
                        } catch (InterruptedException e1) {
                            // Cancelled.
                        }
                    }
                };
                
                try {
                    project.getProject().getWorkspace().run(runnable, new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Refresh Project.");
                } finally {
                	// TODO (tau->tau) 27.01.2006 ???
                    // add 27.01.2006 true or false?                 	
                	project.fireProjectChanged(this, false);                	
                }
            }
        };

        refreshOrmProjectAction.setActionDefinitionId("org.eclipse.ui.file.refresh");
        refreshOrmProjectAction.setText(BUNDLE.getString("Explorer.refreshOrmProjectActionName"));
        refreshOrmProjectAction.setToolTipText(BUNDLE.getString("Explorer.refreshOrmProjectActionToolTipText"));        
        
        
        removeTableAction= new ActionOrmTree() { // by tau on 24.05.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;
                
                IDatabaseTable databaseTable = (IDatabaseTable) ViewsUtils.getSelectedObject(IDatabaseTable.class, this.getViewer());
                if (databaseTable == null) return;              

                    if (MessageDialog.openConfirm(ViewPlugin.getActiveWorkbenchShell(),
                            BUNDLE.getString("Explorer.RemoveTableConfirmDeleteTitle"),
                            BUNDLE.getString("Explorer.RemoveTableQuestion") + databaseTable.getName() + "'?")) {
                        
                        try {                       
                            mapping.removeDatabaseTable(databaseTable.getName());
                            mapping.save();                 
                        } catch (CoreException e) {
                            ExceptionHandler.displayMessageDialog(e, ViewPlugin.getActiveWorkbenchShell(),removeTableAction.getText(),null);
                        } catch (IOException ex) {
                            ExceptionHandler.displayMessageDialog(ex, ViewPlugin.getActiveWorkbenchShell(),removeTableAction.getText(),null);                   
                        }
                        project.fireProjectChanged(this, false);
                        
                    }
                
            }
        };
        removeTableAction.setText(BUNDLE.getString("Explorer.RemoveTableActionName"));
        removeTableAction.setToolTipText(BUNDLE.getString("Explorer.RemoveTableActionTipText"));
        removeTableAction.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.Delete")));     
        
        addMappingStorageAction = new ActionOrmTree() { // by tau on 31.05.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;
                
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());                
                if (mapping == null) return;

                HBMViewerFilter filter = new HBMViewerFilter(mapping.getMappingStorages());             
                ILabelProvider lp= new WorkbenchLabelProvider();
                ITreeContentProvider cp= new WorkbenchContentProvider();

                ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(ViewPlugin.getActiveWorkbenchShell(), lp, cp);
                dialog.setTitle(BUNDLE.getString("Explorer.XMLFileStorageDublicateTitle")); 
                dialog.setMessage(BUNDLE.getString("Explorer.AddMappingStorageActionMessage")); 
                dialog.addFilter(filter);
                dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());  
                dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));

                if (dialog.open() == Window.OK) {
                    boolean isSave = false;
                    boolean isError = false;                    
                    IJavaProject javaProject =  JavaCore.create(project.getProject());
                    HashSet<IClasspathEntry> ehs = new HashSet<IClasspathEntry>();
                    HashSet<IClasspathEntry> nehs = new HashSet<IClasspathEntry>();
                    try {
                        ehs.addAll(Arrays.asList(javaProject.getRawClasspath()));
                    } catch (JavaModelException e) {
                        ExceptionHandler.logThrowableError(e, null);
                        return;
                    }   
                    Object[] elements = dialog.getResult();
                    HashSet<Object> hs = new HashSet<Object>();
                    for (int i= 0; i < elements.length; i++) 
                        if (elements[i] instanceof IContainer) 
                            traverse((IContainer)elements[i], hs, filter);  
                        else if (elements[i] instanceof IFile)
                            hs.add(elements[i]);
                    Object[] files = hs.toArray();
                    for (int i= 0; i < files.length; i++) {
                        IPath path = ((IResource)files[i]).getLocation();
                        // is a file in the Workspace?
                        IFile fileForLocation = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
                        if (fileForLocation == null){
                            MessageDialog.openError(ViewPlugin.getActiveWorkbenchShell(),
                            		BUNDLE.getString("Explorer.FileNoErrorProjectDialogTitle"),
                                    MessageFormat.format(BUNDLE.getString("Explorer.FileNoErrorProjectDialogMessage"),
                                            new Object[]{path.toString(), project.getName()}));
                            continue;
                            
                        }
                        
                        if (!mapping.getProject().getProject().equals(
                                fileForLocation.getProject())) {
                            IClasspathEntry classpathEntry = JavaCore
                                    .newProjectEntry(fileForLocation
                                            .getProject().getFullPath(), false);
                            // #added# by Konstantin Mishin on 21.09.2005 fixed
                            // for ESORM-137
                            if (!javaProject.isOnClasspath(fileForLocation.getProject()) &&
                            		!ehs.contains(classpathEntry)
                                    && !nehs.contains(classpathEntry)) {
                                String dialogButtonLabels[] = { BUNDLE.getString("Explorer.ConfirmProjectToPathDialogButtonLabelOK"),
                                		BUNDLE.getString("Explorer.ConfirmProjectToPathDialogButtonLabelCancel")};
                                MessageDialog messageDialog = new MessageDialog(
                                        ViewPlugin.getActiveWorkbenchShell(),
                                        BUNDLE
                                                .getString("Explorer.ConfirmProjectToPathDialogTitle"),
                                        null,
                                        MessageFormat
                                                .format(
                                                        BUNDLE
                                                                .getString("Explorer.ConfirmProjectToPathDialogMessage"),
                                                        new Object[] { fileForLocation
                                                                .getProject()
                                                                .getName() }),
                                        MessageDialog.QUESTION,
                                        dialogButtonLabels, 0);
                                if (messageDialog.open() == 0)
                                    ehs.add(classpathEntry);
                                else
                                    nehs.add(classpathEntry);
                            }
                            if (nehs.contains(classpathEntry))
                                continue;
                            // #added#
                        }
                        
                        try {
                            // edit tau 04.10.2005
                            XMLFileStorageDublicate.set(null);
                            mapping.addMappingStorage(fileForLocation);
                            if (XMLFileStorageDublicate.get() != null) {
                                MessageDialog.openError(ViewPlugin.getActiveWorkbenchShell(),
                                                BUNDLE.getString("Explorer.XMLFileStorageDublicateTitle"),
                                                MessageFormat.format(BUNDLE.getString("Explorer.XMLFileStorageDublicateMessage"),
                                                                new Object[] {fileForLocation.getName(),
                                                                        XMLFileStorageDublicate.get() }));
                                isError = true;
                            } else {
                                isSave = true;
                            }

                        } catch (IOException e) {
                            ExceptionHandler.handle(e, ViewPlugin
                                    .getActiveWorkbenchShell(),
                                    addMappingStorageAction.getText(), null);
                        } catch (CoreException e) {
                            ExceptionHandler.handle(e, ViewPlugin
                                    .getActiveWorkbenchShell(),
                                    addMappingStorageAction.getText(), null);
                        }   
                            
                    }
                    if (isSave && !isError) {
                        IClasspathEntry[] newClasspath = new IClasspathEntry[ehs.size()];
                        //TODO (tau->tau) (IClasspathEntry)ehs.toArray()[i]???
                        for (int i = 0; i < newClasspath.length; i++)
                            newClasspath[i] = (IClasspathEntry)ehs.toArray()[i];
                        try {
                            mapping.refresh(false, false);  // edit tau 17.11.2005                          
                            mapping.save();
                            
                            // #added#
                            javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());
                            
                            // add tau 07.03.2006
                            project.setDirty(true);
                            
                            project.fireProjectChanged(this, false);
                            
                        } catch (IOException e) {
                            ExceptionHandler.handle(e, ViewPlugin.getActiveWorkbenchShell(), addMappingStorageAction.getText(),null);
                        } catch (CoreException e) {
                            ExceptionHandler.handle(e, ViewPlugin.getActiveWorkbenchShell(), addMappingStorageAction.getText(),null); 
                        }   
                    }
                }
                // #changed#
            }
        };
        addMappingStorageAction.setText(BUNDLE.getString("Explorer.AddMappingStorageActionName"));
        addMappingStorageAction.setToolTipText(BUNDLE.getString("Explorer.AddMappingStorageActionTipText"));
                
        removeConfigAction=new ActionOrmTree() { // by tau 24.05.2005
            public void rush() {
                final IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                final IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());              
                if(mapping==null) return;

                // add 07.07.2005 tau               
                RemoveConfirmRadioDialog dialog = new RemoveConfirmRadioDialog(ViewPlugin.getActiveWorkbenchShell(),
                        BUNDLE.getString("Explorer.removeConfigAction.QuestionDialog.Title"),
                        MessageFormat.format(BUNDLE.getString("Explorer.removeConfigAction.QuestionDialog.Message"),
                                new Object[]{mapping.getName()}),                       
                        new String[]{
                            BUNDLE.getString("Explorer.removeConfigAction.QuestionDialog.Radio1"),
                            BUNDLE.getString("Explorer.removeConfigAction.QuestionDialog.Radio2")                   
                        });
                
                if (dialog.open() != Dialog.OK) return;
                final boolean removeStorages = dialog.getDeleteMapping();
                
                IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
                    public void run(IProgressMonitor monitor) throws CoreException {
                        IRunnableWithProgress operation = new IRunnableWithProgress() {
                            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                                IMappingStorage[] storages=mapping.getMappingStorages();
                                monitor.beginTask(
                                        MessageFormat.format(BUNDLE.getString("Explorer.removeConfigAction.ProgressMonitor"),
                                                new Object[]{mapping.getName()}),
                                                removeStorages?storages.length:1);
                                OrmProgressMonitor.setMonitor(monitor);
                                try {
                                    
                                    IMappingConfiguration conf=mapping.getConfiguration();
                                    IResource res=null;
                                    
                                    if (removeStorages) {
                                        
                                        for(int i=0; i<storages.length; i++) {
                                            
                                            monitor.subTask(MessageFormat.format(BUNDLE.getString("Explorer.removeConfigAction.ProgressMonitor"),
                                                    new Object[]{storages[i].getName()}));
                                            
                                            res=storages[i].getResource();
                                            if (res!=null) res.delete(true,monitor);
                                            monitor.worked(i+1);
                                            
                                        }
                                    }
                                    
                                    res=conf.getResource();
                                    monitor.subTask(MessageFormat.format(BUNDLE.getString("Explorer.removeConfigAction.ProgressMonitor"),
                                            new Object[]{res.getName()}));
                                    
                                    res.delete(true,monitor);
                                    project.removeMapping(mapping);
                                    
                                } catch(CoreException ex) { 
                                    ExceptionHandler.handle(ex,ViewPlugin.getActiveWorkbenchShell(),null, "Error in remove hibernate configuration action.");
                                } finally {
                                    monitor.done();
                                    OrmProgressMonitor.setMonitor(null);
                                }
                            }
                        };              
                        ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
                        try {
                            progress.run(false, true, operation);
                        } catch (InvocationTargetException e) {
                            ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in remove hibernate configuration action.");
                        } catch (InterruptedException e1) {
                            // Cancelled.
                        }
                    }
                };
                try {
                    project.getProject().getWorkspace().run(runnable,new NullProgressMonitor());
                } catch (CoreException e) {
                    ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in remove hibernate configuration action.");
                }
                
                project.fireProjectChanged(this, false);
            }
        };
        removeConfigAction.setText(BUNDLE.getString("Explorer.removeConfigActionName"));
        removeConfigAction.setToolTipText(BUNDLE.getString("Explorer.removeConfigActionToolTipText"));
        removeConfigAction.setImageDescriptor(ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Explorer.Delete")));        
        
        // 20050620 <yan>
        openSourceAction=new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping==null) return;

                IPersistentClass persistentClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                if (persistentClass==null) {
                    IPersistentClassMapping persistentClassMapping=(IPersistentClassMapping)ViewsUtils.getSelectedObject(IPersistentClassMapping.class, this.getViewer());
                    if (persistentClassMapping!=null) persistentClass=persistentClassMapping.getPersistentClass();
                }
                
                if(persistentClass!=null) {
                    
                    ICompilationUnit sourceCode = persistentClass.getSourceCode();
                    if (sourceCode != null) {
                        IWorkbenchPage page = ViewPlugin.getPage();
                        IResource resource = sourceCode.getResource();
                        if (resource instanceof IFile){
                            try {
                                IDE.openEditor(page, (IFile) resource);
                            } catch (PartInitException e) {
                                ExceptionHandler.logThrowableError(e, null);
                            }               
                        }
                    }
                    
                    
                }
            }
        }; 
        openSourceAction.setText(BUNDLE.getString("Explorer.openSourceActionName"));
        openSourceAction.setToolTipText(BUNDLE.getString("Explorer.openSourceActionToolTipText"));
        
        openMappingStorageAction=new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping==null) return;
                
                IMappingStorage storage=(IMappingStorage)ViewsUtils.getSelectedObject(IMappingStorage.class, this.getViewer());
                
                if (storage==null) {
                    IPersistentClass persistentClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, this.getViewer());
                    storage=persistentClass.getPersistentClassMapping().getStorage();
                }

                if (storage != null) {
                    IWorkbenchPage page = ViewPlugin.getPage();
                    IResource resource = storage.getResource();
                    if (resource instanceof IFile){
                        try {
                            IDE.openEditor(page, (IFile) resource);
                        } catch (PartInitException e) {
                            ExceptionHandler.logThrowableError(e, null);
                        }               
                    }
                }
                
            }
        };
        openMappingStorageAction.setText(BUNDLE.getString("Explorer.openMappingStorageActionName"));
        openMappingStorageAction.setToolTipText(BUNDLE.getString("Explorer.openMappingStorageActionToolTipText"));
        
        openMappingAction=new ActionOrmTree() {
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project==null) return;               
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping!=null) {
                    
                    IWorkbenchPage page = ViewPlugin.getPage();
                    IResource resource = mapping.getConfiguration().getResource();
                    if (resource instanceof IFile){
                        try {
                            IDE.openEditor(page, (IFile) resource);
                        } catch (PartInitException e) {
                            ExceptionHandler.logThrowableError(e, null);
                        }               
                    }
                    
                }
                
            }
        };
        openMappingAction.setText(BUNDLE.getString("Explorer.openMappingActionName"));
        openMappingAction.setToolTipText(BUNDLE.getString("Explorer.openMappingActionToolTipText"));
        //</yan>

        //called on schema
        refreshSchemaMappingAction = new ActionOrmTree() { // by tau on 12.07.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project == null) return;             
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping == null) return;
                
                IDatabaseSchema databaseSchema = (IDatabaseSchema) ViewsUtils.getSelectedObject(IDatabaseSchema.class, this.getViewer());
                
                // add tau 29.07.2005
                Object type = null;
                if(databaseSchema == null) type = mapping;
                else type = databaseSchema;
                
                // edit tau 19.07.2005, 20.07.2005
                Dialog dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new RefreshMappingWizard(type, mapping, this.getViewer()));
                
                if( dialog.open() != Dialog.CANCEL) {
                    project.fireProjectChanged(this, false);                   
                }
                
            }
        }; 
        refreshSchemaMappingAction.setText(BUNDLE.getString("Explorer.RefreshSchemaMappingActionName"));
        refreshSchemaMappingAction.setToolTipText(BUNDLE.getString("Explorer.RefreshSchemaMappingActionToolTipText"));

        //called on table
        refreshDatabaseTableSchemaMappingAction = new ActionOrmTree() { // by tau on 26.07.2005
            public void rush() {
                IOrmProject project=getOrmProject(this.getViewer().getTree());
                if(project == null) return;             
                IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, this.getViewer());
                if(mapping == null) return;
                IDatabaseTable databaseTable = (IDatabaseTable) ViewsUtils.getSelectedObject(IDatabaseTable.class, this.getViewer());
                if(databaseTable == null) return;
                
                Dialog dialog = new WizardDialog(ViewPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                        new RefreshMappingWizard(databaseTable, mapping, this.getViewer()));
                
                if( dialog.open() != Dialog.CANCEL) {
                    project.fireProjectChanged(this, false);                   
                }
            }
        }; 
        refreshDatabaseTableSchemaMappingAction.setText(BUNDLE.getString("Explorer.RefreshDatabaseTableSchemaMappingActionName"));
        refreshDatabaseTableSchemaMappingAction.setToolTipText(BUNDLE.getString("Explorer.RefreshDatabaseTableSchemaMappingActionToolTipText"));
 
        // #added# by Konstantin Mishin on 25.03.2006 fixed for ESORM-28
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
					ExceptionHandler.logThrowableError(e,"OpenEditor");              
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
    static private IOrmProject getOrmProject(Tree tree) {
        TreeItem[] items = tree.getSelection();
        
        // 20050623 <yan>
        if (items.length==0) return null;
        // </yan>
        
        TreeItem parentItem = items[0];
        TreeItem ormItem; 
        do {
            ormItem = parentItem;
            parentItem = parentItem.getParentItem();
        } while (parentItem != null);
        return (IOrmProject)ormItem.getData();      
    }

    // #added# by Konstantin Mishin on 11.09.2005 fixed for ORMIISTUD-660
    static private void traverse(IContainer container, HashSet<Object> hs, HBMViewerFilter vf) {
        try {   
            IResource[] resources = container.members();
            for (int i = 0; i < resources.length; i++) {
                IResource resource = resources[i];
                if (resource instanceof IFile) {
                    String ext = resource.getName();
                    if ((ext != null && (ext.toLowerCase().endsWith(".hbm") || ext.toLowerCase().endsWith(".hbm.xml"))) && vf.isSelectedMappingStorage(resource) && vf.isSrc(resource.getFullPath().toString())) {
                        hs.add(resource);
                    }
                } else if (resource.isAccessible() && resource  instanceof IContainer) {
                    traverse((IContainer)resource, hs, vf);
                }
            }
        } catch (CoreException e) {
            ExceptionHandler.logThrowableError(e, null);
        }
    }
    // #added#
    
    // yan 2005.09.22
    static private void removePersistentClass(IPersistentClass pc, boolean delSource, boolean delMapping, HashSet<IMapping> refrMaps) throws CoreException {
        if (!delMapping) {
            pc.getProjectMapping().removePersistentClass(pc.getName(),false);
            refrMaps.add(pc.getProjectMapping());
        } else {
            IMapping[] maps = ClassUtils.getReferencedMappings(pc);
            for(int i=0; i<maps.length; i++) {
                
                maps[i].removePersistentClass(pc.getName(),true);
                refrMaps.add(maps[i]);
                
            }
            
        }
    }
    // yan
    // yan 2005.09.22
    static private void clearPersistentClassMapping(IPersistentClass pc, HashSet<IMapping> refrMaps) throws CoreException {
        IMapping[] maps = ClassUtils.getReferencedMappings(pc);
        for(int i = 0; i < maps.length; i++) {
            IPersistentClass tpc = maps[i].findClass(pc.getName());
            if (tpc!=null) {
                tpc.clearMapping();
                refrMaps.add(maps[i]);
            }
        }
    }
    
    static private void refreshMappings(HashSet refrMaps) {
        for(Iterator it=refrMaps.iterator(); it.hasNext();) {
            try {
                ((IMapping)it.next()).save();
            } catch (IOException e) {
                ExceptionHandler.logThrowableError(e,"Save project's mapping.");
            } catch (CoreException e) {
                ExceptionHandler.logThrowableWarning(e,"Save project's mapping.");
            }
        }
    }
    // yan

}
