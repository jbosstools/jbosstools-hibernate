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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;


/**
 * @author tau
 * 
 */

public class ActionExplorerVisitor implements IOrmModelVisitor {
    
    private TreeViewer viewer;  
    
    public ActionExplorerVisitor(TreeViewer viewer) {
        super();
        this.viewer = viewer;
    }
    
    // yan 20050927 HT alpha5 
    private boolean isHibernateToolsExists() {
        try {
            Class.forName("org.hibernate.eclipse.hqleditor.HQLEditor");
            Class.forName("org.hibernate.eclipse.hqleditor.HQLEditorInput");
            Class.forName("org.hibernate.eclipse.hqleditor.HQLEditorStorage");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }
    
    
    public Object visitOrmProject(IOrmProject project, Object argument) {
        // release 23.05.2005 tau
        //  add submenu 'Outlining' with the following commands: 
        //    'Collapse All' command - collapses all children of the project 
        //    'Expand All' command - expands all children of the project.
    	// add IComponentMapping for test

        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument;
            manager.add(ViewsAction.hibernateAddMappingWizardAction.setViewer(viewer));
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));         
            manager.add(ViewsAction.autoMappingSettingAction.setViewer(viewer));
            if(project.getMappings().length >0)manager.add(ViewsAction.runTimeSettingAction.setViewer(viewer));
            
            // add tau 16.06.2005
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));         
            manager.add(ViewsAction.refreshOrmProjectAction.setViewer(viewer));           
            
            
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            if (viewer.getExpandedState(project))
                manager.add(ViewsAction.collapseAllAction.setViewer(viewer));
            manager.add(ViewsAction.expandAllAction.setViewer(viewer));
            
            // yan 20050927
            if (isHibernateToolsExists()) {
                manager.add(new Separator());
                manager.add(ViewsAction.showHibernateConsole.setViewer(viewer));
            }
            // yan
        }       
        return null;
    }

    public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument;

            IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, viewer);            
            
            ViewsAction.refreshSchemaMappingAction.setEnabled(!ClassUtils.hasMappingClassErrors(mapping) && 
                    mapping.getPersistentClassMappings() !=null &&
                    mapping.getPersistentClassMappings().length != 0 &&
                    !ViewsUtils.isReadOnlyMappimgAndJava(mapping)); // add tau 13.02.2006
            
            manager.add(ViewsAction.refreshSchemaMappingAction.setViewer(viewer));
            manager.add(new Separator());           
        }           
        return null;
    }

    // edit tau 16.02.2006
    public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument;

            IMapping mapping = (IMapping) ViewsUtils.getSelectedObject(IMapping.class, viewer);            
            
            ViewsAction.refreshDatabaseTableSchemaMappingAction.setEnabled(!ClassUtils.hasMappingClassErrors(mapping) && 
                    mapping.getPersistentClassMappings() !=null &&
                    mapping.getPersistentClassMappings().length != 0 &&
                    !ViewsUtils.isReadOnlyMappimgAndJava(mapping)); // add tau 13.02.2006
            
            manager.add(ViewsAction.refreshDatabaseTableSchemaMappingAction.setViewer(viewer));
            manager.add(new Separator());
            
            // add tau 16.02.2005
            ViewsAction.renameTableObjectAction.setEnabled(!ViewsUtils.isReadOnlyMappingStoragesForDatabaseTable(table.getPersistentClassMappings()));
            manager.add(ViewsAction.renameTableObjectAction.setViewer(viewer));
            
            manager.add(ViewsAction.addColumnDialogAction.setViewer(viewer));
        }
        return null;
    }

    // edit tau 14.02.2006
    public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
        //    add 'Rename...' column. Use IDatabaseTable.renameColumn(IDatabaseColumn column, String newColumnName), IMapping.save(); IOrmProject.fireProjectChanged 
        // add 'Properties...' command. Use dialog with PropertySheet to show/edit column properties.IMapping.save(); IOrmProject.fireProjectChanged
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument;

        	IPersistentClassMapping[] persistentClassMappings = null;            
            
            IPersistentClass persistentClass = (IPersistentClass) ViewsUtils.getSelectedObject(IPersistentClass.class, viewer);
            if (persistentClass != null) {
            	persistentClassMappings = persistentClass.getPersistentClassMapping().getDatabaseTable().getPersistentClassMappings();
            } else {
            	IDatabaseTable databaseTable = (IDatabaseTable) ViewsUtils.getSelectedObject(IDatabaseTable.class, viewer);
                if (databaseTable != null){
                	persistentClassMappings = databaseTable.getPersistentClassMappings();                	
                } else {
                	IPersistentClassMapping persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, viewer);
                    if (persistentClassMapping != null){
                    	persistentClassMappings = persistentClassMapping.getDatabaseTable().getPersistentClassMappings();
                    }
                }
            }
            
            boolean flagReadOnlyMappingStoragesForDatabaseTable = false;                
            if (persistentClassMappings != null) {
            	flagReadOnlyMappingStoragesForDatabaseTable = ViewsUtils.isReadOnlyMappingStoragesForDatabaseTable(persistentClassMappings);
            }
            
            ViewsAction.renameTableObjectDialogForColumnAction.setEnabled(!flagReadOnlyMappingStoragesForDatabaseTable);            	
            manager.add(ViewsAction.renameTableObjectDialogForColumnAction.setViewer(viewer));
            
            manager.add(ViewsAction.columnPropertyDialogAction.setViewer(viewer));            
   
        }
        return null;
    }

    public Object visitDatabaseConstraint(IDatabaseConstraint constraint,
            Object argument) {
        return null;
    }

    // edit tau 14.02.2006    
    public Object visitPackage(IPackage pakage, Object argument) {
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument;
            
            // add 01.08.2005           
            manager.add(ViewsAction.mappingWizardAction.setViewer(viewer));
            manager.add(new Separator());
            
            boolean flagReadOnlyPackageMappimg = ViewsUtils.isReadOnlyPackageMappimg(pakage);            

            ViewsAction.refreshPackageSchemaMappingAction.setEnabled(!(ClassUtils.hasPackageErrors(pakage) || flagReadOnlyPackageMappimg));         
            manager.add(ViewsAction.refreshPackageSchemaMappingAction.setViewer(viewer));         
            manager.add(new Separator()); // 20050618 yan
            // add tau 20.06.2005
            
            // edit tau 14.02.2006
            if(pakage != null && pakage.hasMappedFields() ) {
            	ViewsAction.clearPackageMappingAction.setEnabled(!flagReadOnlyPackageMappimg);
                manager.add(ViewsAction.clearPackageMappingAction.setViewer(viewer));             
            }
            
            ViewsAction.removeClassesAction.setEnabled(!flagReadOnlyPackageMappimg);            
            manager.add(ViewsAction.removeClassesAction.setViewer(viewer));
        }
        
        return null;
    }

    // edit tau 14.02.2006    
    public Object visitMapping(IMapping mapping, Object argument) {
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument;
            
    		//TODO EXP 5d tau 27.02.2006
            IMappingConfiguration configuration = mapping.getConfiguration();
            if (configuration instanceof HibernateConfiguration){
            	((HibernateConfiguration)configuration).verifyHibernateMapping();
            }
            
            boolean flagReadOnlyMappingConfiguration = ViewsUtils.isReadOnlyMappingConfiguration(mapping, viewer);            
            
            // 20050620 <yan>
            manager.add(ViewsAction.openMappingAction.setViewer(viewer));
            //</yan>
            manager.add(ViewsAction.hibernateConnectionWizardAction.setViewer(viewer));
            
            // edit tau 13.02.2006
            ViewsAction.persistentClassesWizardAction.setEnabled(!flagReadOnlyMappingConfiguration);            
            manager.add(ViewsAction.persistentClassesWizardAction.setViewer(viewer));

            manager.add(ViewsAction.GenerateDAOWizardAction.setViewer(viewer));
            if(mapping.getPersistentClassMappings()==null || mapping.getPersistentClassMappings().length==0) 
            	ViewsAction.GenerateDAOWizardAction.setEnabled(false);
            else
            	ViewsAction.GenerateDAOWizardAction.setEnabled(true);

            
            manager.add(ViewsAction.tablesClassesWizardAction.setViewer(viewer));
            manager.add(ViewsAction.generateDDLWizard.setViewer(viewer));
            //$added$ by Konstantin Mishin on 2005/08/15 fixed for ORMIISTUD-589
            if(mapping.getPersistentClassMappings()==null || mapping.getPersistentClassMappings().length==0) 
            	ViewsAction.generateDDLWizard.setEnabled(false);
            else
            	ViewsAction.generateDDLWizard.setEnabled(true);
            //$added$
            // 20050727 <yan>
            manager.add(new Separator());
            // </yan>
            
//          add tau 01.08.2005
            if (((OrmContentProvider)viewer.getContentProvider()).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER){
                // edit tau 15.10.2005 fixed for /ESORM-87
            	ViewsAction.refreshMappingSchemaMappingAction.setEnabled(!ClassUtils.hasMappingClassErrors(mapping) && 
                        mapping.getPersistentClassMappings() !=null &&
                        mapping.getPersistentClassMappings().length != 0 &&
                        !ViewsUtils.isReadOnlyMappimg(mapping)); // add tau 13.02.2006
                manager.add(ViewsAction.refreshMappingSchemaMappingAction.setViewer(viewer));             
            } else if (((OrmContentProvider)viewer.getContentProvider()).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER){
                // edit tau 15.10.2005 fixed for /ESORM-87              
            	ViewsAction.refreshMappingSchemaMappingAction.setEnabled(!ClassUtils.hasMappingClassErrors(mapping) && 
                        mapping.getPersistentClassMappings() !=null &&
                        mapping.getPersistentClassMappings().length != 0 &&
                        !ViewsUtils.isReadOnlyMappimg(mapping)); // add tau 13.02.2006
                manager.add(ViewsAction.refreshMappingSchemaMappingAction.setViewer(viewer));                 
            } else if (((OrmContentProvider)viewer.getContentProvider()).getTip() == OrmContentProvider.SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER){
                // add tau 15.11.2005
            	ViewsAction.refreshSchemaMappingAction.setEnabled(!ClassUtils.hasMappingClassErrors(mapping) && 
                        mapping.getPersistentClassMappings() !=null &&
                        mapping.getPersistentClassMappings().length != 0 &&
                        !ViewsUtils.isReadOnlyMappimg(mapping)); // add tau 13.02.2006

                manager.add(ViewsAction.refreshSchemaMappingAction.setViewer(viewer));
                manager.add(new Separator());                   
            }           
            
            // optimizations
            //Hibernate Optimistic Lock Wizard(?)
            manager.add(ViewsAction.cacheWizardAction.setViewer(viewer));
            //$added$ by Konstantin Mishin on 2005/08/15 fixed for ORMIISTUD-589
            if(mapping.getPersistentClassMappings()==null || mapping.getPersistentClassMappings().length==0) 
            	ViewsAction.cacheWizardAction.setEnabled(false);
            else
            	ViewsAction.cacheWizardAction.setEnabled(true);
            //$added$
            manager.add(ViewsAction.fetchStrategyWizardAction.setViewer(viewer));
            //$added$ by Konstantin Mishin on 2005/08/16 fixed for ORMIISTUD-589
            if(mapping.getPersistentClassMappings()==null || mapping.getPersistentClassMappings().length==0) 
            	ViewsAction.fetchStrategyWizardAction.setEnabled(false);
            else
            	ViewsAction.fetchStrategyWizardAction.setEnabled(true);
            
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            // tau 31.05.2005
            
            // edit tau 13.02.2006            
            ViewsAction.addMappingStorageAction.setEnabled(!flagReadOnlyMappingConfiguration);
            manager.add(ViewsAction.addMappingStorageAction.setViewer(viewer));
    
            // tau 24.05.2005
            manager.add(ViewsAction.refreshMappingAction.setViewer(viewer));

            //Add mapping file         
            // 20050618 <yan>
            manager.add(new Separator());
            
            // edit tau 13.02.2006            
            ViewsAction.removeConfigAction.setEnabled(!flagReadOnlyMappingConfiguration);
            manager.add(ViewsAction.removeConfigAction.setViewer(viewer));
            //</yan>
            
            // 20050920 <yan>
            manager.add(new Separator());
            // </yan>
        }
        return null;        
    }

    public Object visitMappingStorage(IMappingStorage storage, Object argument) {
        //Add Persistent Classes
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument; 
            // 20050620 <yan>
            manager.add(ViewsAction.openMappingStorageAction.setViewer(viewer));
            //</yan>
            
            boolean flagReadOnlyMappingConfiguration = ViewsUtils.isReadOnlyMappingConfiguration(storage.getProjectMapping(), viewer);
            ViewsAction.persistentClassesWizardAction.setEnabled(!flagReadOnlyMappingConfiguration);            
            manager.add(ViewsAction.persistentClassesWizardAction.setViewer(viewer));
            // 20050727 <yan>

            // del tau 15.02.2006            
            //boolean flagReadOnlyMappingStorage = ViewsUtils.isReadOnlyMappingStorage(storage);
            //addNamedQueryAction.setEnabled(!flagReadOnlyMappingStorage); 
            
            manager.add(ViewsAction.addNamedQueryAction.setViewer(viewer));
        // </yan>
            
            //Show mapping, ???
            //Remove mappings ???
            //Delete         ???   
        }

        return null;
    }

    // edit tau 14.02.2006    
    public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		// add 'View table' command that activates Relational schema view and
		// selects class table.

		if (argument instanceof IMenuManager) {
			IMenuManager manager = (IMenuManager) argument;
			// 20050620 <yan>
			manager.add(ViewsAction.openMappingStorageAction.setViewer(viewer));
			manager.add(ViewsAction.openSourceAction.setViewer(viewer));
			// </yan>
			manager.add(ViewsAction.mappingWizardAction.setViewer(viewer));
			// 20050627 <yan>
			manager.add(new Separator());
			// </yan>

			// edit tau 14.02.2006

			IMapping mapping = clazz.getProjectMapping();
			boolean flagReadOnlyMappimg = ViewsUtils.isReadOnlyMappimg(mapping);

			ViewsAction.refreshClassSchemaMappingAction.setEnabled(!(ClassUtils
					.hasClassErrors(clazz) || flagReadOnlyMappimg));
			manager.add(ViewsAction.refreshClassSchemaMappingAction
					.setViewer(viewer));
			manager.add(new Separator());

			manager.add(ViewsAction.viewTableAction.setViewer(viewer));

			// add tau 20.06.2005
			if (clazz != null && clazz.hasMappedFields()) {
				ViewsAction.clearClassMappingAction
						.setEnabled(!flagReadOnlyMappimg);
				manager.add(ViewsAction.clearClassMappingAction
						.setViewer(viewer));
			}

			manager.add(ViewsAction.removeClassAction.setViewer(viewer));
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

			ViewsAction.refactoringDialogClassAction
					.setEnabled(!flagReadOnlyMappimg);
			manager.add(ViewsAction.refactoringDialogClassAction
					.setViewer(viewer));

			// added by yk 12.09.2005
			manager.add(new Separator());
			// 20050916 <yan>
			manager.add(ViewsAction.testQueryAction.setViewer(viewer));
			// </yan>
			// added by yk 12.09.2005.
			// #added# by Konstantin Mishin on 25.03.2006 fixed for ESORM-28
			manager.add(ViewsAction.openEditorAction.setViewer(viewer));
			// #added#
		}

		return null;
	}

    // edit tau 14.02.2006
    public Object visitPersistentField(IPersistentField field, Object argument) {
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument; 
        
            IPersistentClass persistentClass = (IPersistentClass)ViewsUtils.getSelectedObject(IPersistentClass.class, viewer);
            if (persistentClass == null) {            	
            	IPersistentClassMapping persistentClassMapping = (IPersistentClassMapping) ViewsUtils.getSelectedObject(IPersistentClassMapping.class, viewer);            	
                if (persistentClassMapping != null) {
                	persistentClass = persistentClassMapping.getPersistentClass();                	
                }
            }
            
        	boolean flagReadOnlyMappingStorage = false;            
            if (persistentClass != null){
            	IPersistentClassMapping persistentClassMapping = persistentClass.getPersistentClassMapping();
            	if (persistentClassMapping != null){
            		IMappingStorage mappingStorage = persistentClassMapping.getStorage();
            		if (mappingStorage != null) {
            			flagReadOnlyMappingStorage = ViewsUtils.isReadOnlyMappingStorage(mappingStorage);
            		} else return null;
            	} else  return null;
            } else return null;
            
            if(field != null && field.getMapping() != null) {
            	manager.add(ViewsAction.fieldMappingWizardAction.setViewer(viewer));
            }
            
            manager.add(ViewsAction.fieldMappingWizardNewAction.setViewer(viewer));
            
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            
            if(field != null && field.getMapping() != null) {
            	ViewsAction.clearFieldMappingAction.setEnabled(!flagReadOnlyMappingStorage);            	
                manager.add(ViewsAction.clearFieldMappingAction.setViewer(viewer));               
            }

            // add tau 23.05.2005
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            
            ViewsAction.refactoringDialogFieldAction.setEnabled(!flagReadOnlyMappingStorage);            
            manager.add(ViewsAction.refactoringDialogFieldAction.setViewer(viewer));          
        }
        
        return null;
    }

    public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
        // 20050620 <yan>
        if (mapping!=null) return visitPersistentClass(mapping.getPersistentClass(),argument);
        // </yan>
        return null;
    }

    public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping,
            Object argument) {
        return null;
    }

    public Object visitPersistentValueMapping(IPersistentValueMapping mapping,
            Object argument) {
        return null;
    }
    
    // add tau 27.07.2005
    public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
        if (argument instanceof IMenuManager) {
            IMenuManager manager = (IMenuManager) argument; 
            // 20050727 <yan>
            manager.add(ViewsAction.editNamedQueryAction.setViewer(viewer));
            
            // add tau 15.02.2006
        	boolean flagReadOnlyMappingStorage = false;
        	IMappingStorage mappingStorage = (IMappingStorage)ViewsUtils.getSelectedObject(IMappingStorage.class, viewer);        	
            if (mappingStorage != null){
            	flagReadOnlyMappingStorage = ViewsUtils.isReadOnlyMappingStorage(mappingStorage);            
            }
            
            ViewsAction.removeNamedQueryAction.setEnabled(!flagReadOnlyMappingStorage);            
            manager.add(ViewsAction.removeNamedQueryAction.setViewer(viewer));
        }
        return null;
    }

	public TreeViewer getViewer() {
		return viewer;
	}   
    
}
