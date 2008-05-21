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
package org.jboss.tools.hibernate.core;

import java.io.IOException;
import java.sql.Connection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;


/**
 * @author alex
 *
 * Represents view to mapping resources of the project.
 */
public interface IMapping extends IOrmElement {
	
	public IOrmProject getProject();
	public IPersistentClassMapping[] getPersistentClassMappings();
	
	public void moveTo(IPath newPath) throws CoreException ;
	/**
	 * Returns mapping configuration of the project
	 * */
	public IMappingConfiguration getConfiguration();
	/**
	 * Returns mapping properties of the project
	 * */
	public IMappingProperties getProperties();
	
	/**
	 * Returns array of IMappingStorage
	 * */
	public IMappingStorage[] getMappingStorages();
	public IMappingStorage getMappingStorage(String id); 
	// #changed# by Konstantin Mishin on 10.09.2005 fixed for ORMIISTUD-660
	//public IMappingStorage addMappingStorage(IPath path) throws IOException, CoreException;
	public IMappingStorage addMappingStorage(IFile path) throws IOException, CoreException;
	// #changed#
	public void removeMappingStorage(IMappingStorage storage)throws IOException, CoreException;
	
	// edit tau 17.11.2005 - add doMappingsUpdate
	public void refresh(boolean reloadMappings, boolean doMappingsUpdate)throws IOException, CoreException;
	// edit tau 17.11.2005 - add doMappingsUpdate	
	public void reload(boolean doMappingsUpdate)throws IOException, CoreException;
	public void save() throws IOException ,CoreException;
	public void saveAllMappingStorage() throws IOException ,CoreException;
	public void savePackageMappingStorage(IPackage pack) throws IOException ,CoreException;
	public void saveMappingStorage(IMappingStorage mappingStorage) throws IOException ,CoreException;
	// add tau 06.04.2006
	public void saveMappingStorageForPersistentClassMapping(IPersistentClassMapping[] persistentClassMappings)  throws IOException ,CoreException;	
	
	/**
	 * Return mapping for given persistent class. if class does not have one it will be created.
	 * @throws CoreException
	 * */
	public IPersistentClassMapping getMappingForPersistentClass(IPersistentClass clazz, IMappingStorage storage) throws CoreException;
	/**
	 * Replaces a class mapping with a new mapping specified by newMappingType. newMappingType is a mapping specific constant.
	 * For Hibernate mapping see OrmConfiguration.TABLE_PER_* constants.
	 * */
	public IPersistentClassMapping replaceMappingForPersistentClass(IPersistentClass clazz, String newMappingType) throws CoreException;

	public IAutoMappingService getAutoMappingService();
	public IValidationService getValidationService();
	//akuzmin 12.10.2005
	public IDAOGenerator getDAOGenerator();
	public void addDAOClasses(IPersistentClass[] classes,boolean generateInterfaces,boolean generateLog,boolean generateTests,String packageName)throws IOException, CoreException;	
	//methods were moved from IOrmProject:
	public IPersistentClass[] getPertsistentClasses();
	public IDatabaseTable[] getDatabaseTables();
	public IPackage[] getPackages();
	public IDatabaseSchema[] getDatabaseSchemas();
	//akuzmin 04.07.2005
	public Table getOrCreateTable(String fullyQualifiedName);	
	public IPersistentClass findClass(String fullyQualifiedName);	
	public IDatabaseTable findTable(String fullyQualifiedName);
	public void addDatabaseTable(IDatabaseTable table);
	//added 22.03.05
	public void addDatabasesTables(Connection conn, String[] fullyQualifiedNames,boolean generateMapping,boolean useHeuristicAlgorithms,ReversStatistic info)throws IOException, CoreException;
	public void addPersistentClasses(String[] fullyQualifiedNames, boolean generateMapping,boolean useHeuristicAlgorithms) throws IOException, CoreException;
	public void addPersistentClasses(String[] fullyQualifiedNames, boolean generateMapping,boolean useHeuristicAlgorithms, IMappingStorage storage) throws IOException, CoreException;
	
	public void removePersistentClass(String fullyQualifiedName)throws CoreException;
	public void removePersistentClass(String fullyQualifiedName,boolean clearStorage)throws CoreException; //yan 20051003
	public void removeDatabaseTable(String fullyQualifiedName)throws CoreException;
	public void removeMapping(IPersistentClass pc)throws CoreException;
	public void removeMapping(IPersistentClass pc,boolean clearStorage)throws CoreException; // yan 20051003

	public void renameClass(IPersistentClass clazz, String newFullyQualifiedName) throws CoreException;
	public void renameTable(IDatabaseTable table, String newFullyQualifiedName);
	
	public void refreshClass(IPersistentClass clazz);	
	
    //added By Nick 19.05.2005
    public void resourcesChanged();
    //by Nick

	//TODO EXP 4d    
    public boolean isFlagDirty();
	public void setFlagDirty(boolean flagDirty);
	
}
