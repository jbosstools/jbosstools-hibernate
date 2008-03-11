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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;


// create tau 13.02.2006

public class ViewsUtils {

	public static ReadOnlyVisitor readOnlyVisitor = new ReadOnlyVisitor();

	public static boolean isReadOnlyMappimg(IMapping mapping) {
		
		if (isReadOnlyConfigurationResource(mapping))
			return true;			

		IMappingStorage[] mappingStorages = mapping.getMappingStorages();
		for (int j = 0; j < mappingStorages.length; j++) {
			IMappingStorage storage = mappingStorages[j];
			IResource resourceStorage = storage.getResource();
			ResourceAttributes attributesResourceStorage = resourceStorage.getResourceAttributes();
			if ((attributesResourceStorage == null)	|| attributesResourceStorage.isReadOnly()) {
				return true;
			}
		}

		return false;
	}
	

	// add tau 16.02.2006
	public static boolean isReadOnlyMappimgAndJava(IMapping mapping) {

		if (isReadOnlyMappimg(mapping)) return true;
		
		IPersistentClass[] pertsistentClasses = mapping.getPertsistentClasses();
		for (int i = 0; i < pertsistentClasses.length; i++) {
			IPersistentClass persistentClass = pertsistentClasses[i];
			ICompilationUnit sourceCode = persistentClass.getSourceCode();
			if (sourceCode != null) {
				IResource resource = sourceCode.getResource();
				if (resource != null){
					ResourceAttributes attributesResourceStorage = resource.getResourceAttributes();
					if ((attributesResourceStorage == null)	|| attributesResourceStorage.isReadOnly()) {
						return true;
					}					
				}
			}
		}

		return false;
	}	
	
	public static boolean isReadOnlyPackageMappimg(IPackage pakage) {

		if (pakage == null)
			return true;			
		
		IMapping mapping = pakage.getProjectMapping();
		if (isReadOnlyConfigurationResource(mapping))
			return true;			
		
		IPersistentClass[] persistentClasses = pakage.getPersistentClasses();
		for (int i = 0; i < persistentClasses.length; i++) {
			IPersistentClass class1 = persistentClasses[i];
			IPersistentClassMapping persistentClassMapping = class1.getPersistentClassMapping();
			IResource resourceStorage = persistentClassMapping.getStorage().getResource();
			ResourceAttributes attributesResourceStorage = resourceStorage.getResourceAttributes();
			if ((attributesResourceStorage == null)	|| attributesResourceStorage.isReadOnly()) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean isReadOnlyMappingStorage(IMappingStorage mappingStorage) {
		if (mappingStorage == null)
			return true;
		
		//IMapping mapping = mappingStorage.getProjectMapping();
		//if (isReadOnlyConfigurationResource(mapping))
		//	return true;			
		
		IResource mappingStorageResource = mappingStorage.getResource();
		if (mappingStorageResource == null)
			return true;
		
		ResourceAttributes attributesmappingStorageResource = mappingStorageResource.getResourceAttributes();
		if ((attributesmappingStorageResource == null) || attributesmappingStorageResource.isReadOnly()) {
			return true;
		}		
		
		return false;
	}
	
	private static boolean isReadOnlyConfigurationResource(IMapping mapping) {
		
		if (mapping == null)
			return true;
		
		IMappingConfiguration configuration = mapping.getConfiguration();
		if (configuration == null)
			return true;

		IResource configurationResource = configuration.getResource();
		if (configurationResource == null)
			return true;

		ResourceAttributes attributesConfigurationResource = configurationResource.getResourceAttributes();
		if ((attributesConfigurationResource == null) || attributesConfigurationResource.isReadOnly()) {
			return true;
		}
		
		return false;
		
	}

	public static boolean isReadOnlyMappingStoragesForDatabaseTable(IPersistentClassMapping[] persistentClassMappings) {
		for (int i = 0; i < persistentClassMappings.length; i++) {
			IPersistentClassMapping persistentClassMapping = persistentClassMappings[i];
			IResource resourceStorage = persistentClassMapping.getStorage().getResource();
			ResourceAttributes attributesResourceStorage = resourceStorage.getResourceAttributes();
			if ((attributesResourceStorage == null)	|| attributesResourceStorage.isReadOnly()) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isReadOnlyMappingConfiguration(IMapping mapping, TreeViewer viewer) {
		if (mapping == null)
			return true;
		
	    Object readOnlyMappingConfiguration = mapping.accept(ViewsUtils.readOnlyVisitor, viewer);
	    if (readOnlyMappingConfiguration instanceof Boolean){
	    	return ((Boolean)readOnlyMappingConfiguration).booleanValue();            	
	    }
	    
		return false;	    
	}
	
	public static Object getSelectedObject(Class clazz, TreeViewer viewer ) { // add tau 26.05.2005

        Tree tree = viewer.getTree();
        
        TreeItem[] items = tree.getSelection();
        if (items.length == 0) return null;
        
        TreeItem parentItem = items[0];
        TreeItem ormItem; 
        do {
            ormItem = parentItem;
            if (clazz.isInstance(ormItem.getData()))
                return ormItem.getData();
            parentItem = parentItem.getParentItem();
        } while (parentItem != null);
        
        return null;        
    }	

}
