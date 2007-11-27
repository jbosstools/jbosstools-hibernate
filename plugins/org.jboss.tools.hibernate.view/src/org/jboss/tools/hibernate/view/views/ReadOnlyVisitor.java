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
import org.eclipse.jface.viewers.TreeViewer;
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

public class ReadOnlyVisitor implements IOrmModelVisitor {
	private static Boolean resultTtrue = new Boolean(true);

	public Object visitOrmProject(IOrmProject project, Object argument) {
		return null;
	}

	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		if (schema == null)	return null;			
		
		IDatabaseTable[] databaseTables = schema.getDatabaseTables();
		for (int i = 0; i < databaseTables.length; i++) {
			IDatabaseTable table = databaseTables[i];
			Object readOnlyTable = table.accept(ViewsUtils.readOnlyVisitor, argument); 		
			if (readOnlyTable != null) return readOnlyTable;
		}

		return null;
	}

	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		if (table == null) return null;

		if (ViewsUtils.isReadOnlyMappingStoragesForDatabaseTable(table.getPersistentClassMappings())){
	           return resultTtrue;			
		}
		
		return null;
	}

	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		return null;
	}

	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
		return null;
	}

	public Object visitPackage(IPackage pakage, Object argument) {
		
		IMapping mapping = pakage.getProjectMapping();
		if (mapping == null)
			return null;
		
		Object readOnlyMapping = mapping.accept(ViewsUtils.readOnlyVisitor, argument);
		if (readOnlyMapping != null) return readOnlyMapping;
		
		IPersistentClass[] persistentClasses = pakage.getPersistentClasses();
		for (int i = 0; i < persistentClasses.length; i++) {
			IPersistentClass class1 = persistentClasses[i];
			IPersistentClassMapping persistentClassMapping = class1.getPersistentClassMapping();
			if ((persistentClassMapping != null)&& (persistentClassMapping.getStorage() != null)){
				Object result = persistentClassMapping.getStorage().accept(ViewsUtils.readOnlyVisitor, argument);
				if (result != null) return result;
			}			
		}
		
		return null;
	}

	public Object visitMapping(IMapping mapping, Object argument) {
		
		IMappingConfiguration configuration = mapping.getConfiguration();
		if (configuration == null) return null;
		
		IResource configurationResource = configuration.getResource();
		if (configurationResource == null) return null;
		
		ResourceAttributes attributesConfigurationResource = configurationResource.getResourceAttributes();
		if ((attributesConfigurationResource != null) && attributesConfigurationResource.isReadOnly()){
		           return resultTtrue;
		}
    	
		return null;
	}

	// edit 15.02.2006
	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		Object result = null;
		if (storage != null) {
			IResource resourceStorage = storage.getResource();
			if (resourceStorage != null) {
				ResourceAttributes attributesResourceStorage = resourceStorage.getResourceAttributes();
				if ((attributesResourceStorage != null)	&& attributesResourceStorage.isReadOnly()) {
					result = resultTtrue;
				}
			}
		}
		
		return result;		
	}

	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		IMapping mapping = clazz.getProjectMapping();
		if (mapping == null)
			return null;
		
		Object readOnlyMapping = mapping.accept(ViewsUtils.readOnlyVisitor, argument);
		if (readOnlyMapping != null) {
	           return readOnlyMapping;
		}

		IPersistentClassMapping persistentClassMapping = clazz.getPersistentClassMapping();
		if ((persistentClassMapping != null) && (persistentClassMapping.getStorage() != null)){ 
			Object result = persistentClassMapping.getStorage().accept(ViewsUtils.readOnlyVisitor, argument);
			if (result != null) return result;
		}
		
		return null;
	}

	public Object visitPersistentField(IPersistentField field, Object argument) {
		IMappingStorage mappingStorage = null;		
		mappingStorage = (IMappingStorage)ViewsUtils.getSelectedObject(IMappingStorage.class, (TreeViewer) argument);
		if (mappingStorage == null){
            IPersistentClass clazz=(IPersistentClass)ViewsUtils.getSelectedObject(IPersistentClass.class, (TreeViewer) argument);
            if (clazz != null) {
            	IPersistentClassMapping persistentClassMapping = clazz.getPersistentClassMapping();
            	if (persistentClassMapping != null){
            		mappingStorage = persistentClassMapping.getStorage();
            	}
            }
		}
		
		return mappingStorage.accept(ViewsUtils.readOnlyVisitor, argument);

	}

	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
		return mapping.getPersistentClass().accept(this, argument);
	}

	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
		return null;
	}

	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		return null;
	}
	
}
