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
package org.jboss.tools.hibernate.internal.core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.SequencedHashMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.hibernate.core.IAutoMappingService;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Index;
import org.jboss.tools.hibernate.internal.core.data.Schema;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.XMLFileStorage;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.RuleUtils;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;

public abstract class AbstractMapping extends AbstractOrmElement implements IMapping {
	public static String[] TABLE_VIEW = {"TABLE","VIEW"};

	private SequencedHashMap classes=new SequencedHashMap();
	private SequencedHashMap tables=new SequencedHashMap();
	private SequencedHashMap schemas=new SequencedHashMap();
	private SequencedHashMap packages=new SequencedHashMap();
	
	private static final IDatabaseTable[] TABLES={};
	private static final IPersistentClass[] CLASSES={};
	private static final IDatabaseSchema[] SCHEMAS={};
	private static final IPackage[] PACKAGES={};
	protected OrmProject project;
	UpdateMappingVisitor updater;
	
	//TODO EXP 3d
	private boolean flagDirty = true;	
	
	public AbstractMapping(OrmProject project){
		this.project=project;
		updater = new UpdateMappingVisitor(this);
	}
	
	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#getProject()
	 */
	public IOrmProject getProject() {
		return project;
	}
	protected abstract void loadMappings();
	
	public synchronized void refresh(boolean reloadMappings, boolean doMappingsUpdate) throws IOException, CoreException {
		//TODO EXP 4d		
        // add tau 24.02.2006
        setFlagDirty(false);
        
        // add tau 13.03.2006
        project.synchronize();
        
		if(reloadMappings){
			classes.clear();
			tables.clear();
			schemas.clear();
			packages.clear();
		}
		if(reloadMappings)loadMappings();
		//	create/refresh classes

		IPersistentClassMapping[] maps = getPersistentClassMappings();
		PersistentClass[] cls =  new PersistentClass[maps.length];
		//XXX (5) find corresponding classes/fields in project tree
		for (int i = 0; i < maps.length; ++i) {
			PersistentClass pc = this.getOrCreatePersistentClass(maps[i]
					.getName());
			cls[i] = pc;
			pc.setPersistentClassMapping(maps[i]);
			maps[i].setPersistentClass(pc);
			pc.removeFields();
			Iterator it = maps[i].getFieldMappingIterator();
			while (it.hasNext()) {
				AbstractFieldMapping fm = (AbstractFieldMapping) it.next();
				PersistentField pf = pc.getOrCreateField(fm.getName());
				fm.setPersistentField(pf);
				pf.setMapping(fm);
			}
		}
		
		if(!reloadMappings){
			//clean classes without mappings
			IPersistentClass[] allClasses=this.getPertsistentClasses();
			for (int i = 0; i < allClasses.length; ++i) {
				if (allClasses[i].getPersistentClassMapping() == null) {
					removePersistentClassInternal(allClasses[i], true);
				}
			}
		}
       //update PC super classes
        updateSuperClasses();
        for(int i = 0 ; i < cls.length ; i++) {
			cls[i].refresh();
        }
	}

	public void reload(boolean doMappingsUpdate) throws IOException, CoreException {	
		refresh(true, doMappingsUpdate);
	}
	
    public void save() throws IOException, CoreException {
        //by Nick 31.03.2005
        class SaveRunnable implements IWorkspaceRunnable {
            private IMapping mapping;
            private SaveRunnable(IMapping mapping) {
                this.mapping = mapping;
            }
            
            public void run(IProgressMonitor monitor) throws CoreException {
                if (mapping != null)
                {
                    try {
                		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("SAVE() start -> mapping = " + mapping.getName());                    	
                        mapping.getConfiguration().save();
                        mapping.getProperties().save();
                		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("SAVE() end -> mapping = " + mapping.getName());                        
                    } catch (IOException e) {
                        throw new NestableRuntimeException(e);
                    }
                }
            }
        };
        SaveRunnable saver = new SaveRunnable(this);
        saver.run(null);
    }

    //add tau 29.03.2006
	public void saveAllMappingStorage() throws IOException, CoreException {
		IMappingStorage[] Storages = getMappingStorages();
		for (int i = 0; i < Storages.length; i++) {
			IMappingStorage storage = Storages[i];
			storage.setDirty(true);
		}
		save();		
	}

    //add tau 29.03.2006	
	public void savePackageMappingStorage(IPackage pack) throws IOException, CoreException {
        IPersistentClass[] persistentClasses = pack.getPersistentClasses();  
        for (int i = 0; i < persistentClasses.length; i++) {
			IPersistentClass pc = persistentClasses[i];
			pc.getPersistentClassMapping().getStorage().setDirty(true);
		}
		save();        
	}
	
    //add tau 06.04.2006	
	public void saveMappingStorageForPersistentClassMapping(IPersistentClassMapping[] persistentClassMappings) throws IOException, CoreException {
        for (int i = 0; i < persistentClassMappings.length; i++) {
			IPersistentClassMapping pcm = persistentClassMappings[i];
			pcm.getStorage().setDirty(true);
		}
		save();        
	}	

    //add tau 29.03.2006	
	public void saveMappingStorage(IMappingStorage mappingStorage) throws IOException, CoreException {
		mappingStorage.setDirty(true);
		save();		
	}
	
	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#findClass(java.lang.String)
	 */
	public IPersistentClass findClass(String fullyQualifiedName) {
		return (IPersistentClass)classes.get(fullyQualifiedName);
	}
	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#findTable(java.lang.String)
	 */
	public IDatabaseTable findTable(String fullyQualifiedName) {
		return (IDatabaseTable)tables.get(fullyQualifiedName);
	}
	
	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getDatabaseTables()
	 */
	public IDatabaseTable[] getDatabaseTables() {
		return (IDatabaseTable[])tables.values().toArray(TABLES);
	}

	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getPertsistentClasses()
	 */
	public IPersistentClass[] getPertsistentClasses() {
		return (IPersistentClass[])classes.values().toArray(CLASSES);
	}
	
	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getDatabaseSchemas()
	 */
	public IDatabaseSchema[] getDatabaseSchemas() {
		//TODO EXP 4d		
		if (isFlagDirty()){
			try {
				reload(true);
			} catch (IOException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			}			
		}
		return (IDatabaseSchema[])schemas.values().toArray(SCHEMAS);
	}
	
	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getPackages()
	 */
	public IPackage[] getPackages() {
		//TODO EXP 3d		
		if (isFlagDirty()){
			try {
				reload(true);
			} catch (IOException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			}			
		}
		return (IPackage[])packages.values().toArray(PACKAGES);
	}

	//added by Nick 16.04.2005
	//handle persistent class without updating when canUpdateNow = false
	//for proper update with super classes detected
	public PersistentClass getOrCreatePersistentClass(String fullyQualifiedName){
		PersistentClass res = (PersistentClass) classes.get(fullyQualifiedName);
		if (res == null) {
			res = new PersistentClass();
			res.setName(fullyQualifiedName);
			classes.put(fullyQualifiedName, res);
			Package pack = getOrCreatePackage(res.getPackageName());
			pack.addPersistentClass(res);
			res.setPackage(pack);
			res.setProjectMapping(this);
		}
		return res;
	}
	
	// added by Nick 17.06.2005
	public Package getPackage(String fullyQualifiedName) {
		return (Package) packages.get(fullyQualifiedName);
	}
    // by Nick
    
    private Package getOrCreatePackage(String fullyQualifiedName) {
    	// TODO !!! tau-> tau 09.12.2005 add res in this metod
        String packageName = fullyQualifiedName;
        
        // added by Nick 30.06.2005
        try {
        	// TODO EXP-10 getPackageSourcePathQic
            IPath fullPackagePath = ScanProject.getPackageSourcePathQic(fullyQualifiedName,project.getProject());
            
            if (fullPackagePath != null
					&& !project.getProject().getFullPath().isPrefixOf(
							fullPackagePath)) {
				packageName = fullPackagePath.segment(0) + "/"
						+ fullyQualifiedName;
			}
        } catch (CoreException e) {
            ExceptionHandler.logThrowableError(e,e.getMessage());
        }

        // by Nick
        
        Package res = getPackage(fullyQualifiedName);
		if (res == null) {
			res = new Package(this);
			res.setName(fullyQualifiedName);
			res.setProjectQualifiedName(packageName);
			packages.put(fullyQualifiedName, res);
		}
		return res;
	}
	
	public Table getOrCreateTable(String fullyQualifiedName) {
		Table res = (Table) tables.get(fullyQualifiedName);
		if (res == null) {
			res = new Table();
			res.setName(fullyQualifiedName);
			tables.put(fullyQualifiedName, res);
			Schema schema = getOrCreateSchema(res.getSchemaName());
			schema.addDatabaseTable(res);
			res.setSchema(schema);
		}
		return res;
	}

    public Schema getOrCreateSchema(String fullyQualifiedName){
		Schema res=(Schema)schemas.get(fullyQualifiedName);
		if(res==null){
			res=new Schema(this);
			res.setName(fullyQualifiedName);
			schemas.put(fullyQualifiedName,res);
		}
		return res;
	}
	
	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#addDatabaseTable(org.jboss.tools.hibernate.core.IDatabaseTable)
	 */
	public void addDatabaseTable(IDatabaseTable table) {
		if (tables.get(table.getName()) == null) {
			Schema schema = getOrCreateSchema(table.getSchemaName());
			schema.addDatabaseTable(table);
			table.setSchema(schema);
			tables.put(table.getName(), table);
		}
	}

	private PersistentClass locatePersistentSuperClass(PersistentClass pc)
			throws CoreException {
		PersistentClass result = null;
		IType pcType = pc.getType();

		IType var_type = ClassUtils.getSuperType(pcType);

		while (var_type != null && result == null) {
			String fQName = var_type.getFullyQualifiedName();
			if (classes.containsKey(fQName)) {
				result = getOrCreatePersistentClass(fQName);
				break;
			}

			var_type = ClassUtils.getSuperType(var_type);
		}
		return result;
	}
	
	// update super class for all persistent classes 
	private void updateSuperClasses() {
		PersistentClass[] pcArray = (PersistentClass[]) classes.values()
				.toArray(new PersistentClass[classes.size()]);

		// Add Tau 27.04.2005 for monitor
		OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor
				.getMonitor());
		monitor.setTaskParameters(15, pcArray.length);

		for (int i = 0; i < pcArray.length; ++i) {

			PersistentClass pc = pcArray[i];
			try {
				ICompilationUnit unit = pc.getSourceCode();

				if (unit != null) {
					IType primaryType = pc.getType();
					if (primaryType != null) {
						pc.setSuperClass(locatePersistentSuperClass(pc));
						if (pc.getSuperClass() == null) {
							// XXX Think about super-super class lookup if
							// direct super class is not persistent. Is it
							// required?
							// !!! thought, it would be better to lookup upper
							// persistent classes so we can stop fields
							// search in that class
							// XXX Check if any of implemented interfaces is a
							// persistent class. in that case such interface
							// should be

							// we should check implemented interfaces for
							// persistence
							IType type = pc.getType();
							if (type != null) {
								String[] iFaceNames = type
										.getSuperInterfaceNames();
								if (iFaceNames != null) {
									for (int j = 0; j < iFaceNames.length; j++) {
										String fQName = ClassUtils
												.resolveTypeName(type,
														iFaceNames[j]);
										if (fQName != null) {
											if (classes.containsKey(fQName)) {
												pc
														.setSuperClass(getOrCreatePersistentClass(fQName));
												break;
											}
										}
									}
								}
							}
						}
					}
				}

			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e,
						"Exception adding persistent classes");
				// OrmCore.log(e,"Exception adding persistent classes");
			}
			monitor.worked();
		}
	}
	//
    
    // added by Nick 04.06.2005
	private class AddDatabasesTablesRunnable implements IWorkspaceRunnable {
		private Connection con;

		private String[] fullyQualifiedNames;

		private boolean generateMapping, useHeuristicAlgorithms;

		private ReversStatistic info;

		AddDatabasesTablesRunnable(Connection con,
				String[] fullyQualifiedNames, boolean generateMapping,
				boolean useHeuristicAlgorithms, ReversStatistic info) {
			this.con = con;
			this.fullyQualifiedNames = fullyQualifiedNames;
			this.generateMapping = generateMapping;
			this.useHeuristicAlgorithms = useHeuristicAlgorithms;
			this.info = info;
		}

		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				addDatabasesTablesRunnable(con, fullyQualifiedNames,
						generateMapping, useHeuristicAlgorithms, info);
			} catch (IOException e) {
				throw new NestableRuntimeException(e);
			}
		}
	}
    
    private void addDatabasesTablesRunnable(Connection con,
			String[] fullyQualifiedNames, boolean generateMapping,
			boolean useHeuristicAlgorithms, ReversStatistic info)
			throws IOException, CoreException {
		OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor
				.getMonitor());

		IDatabaseTable[] dtArray = new IDatabaseTable[fullyQualifiedNames.length];

		monitor.setTaskParameters(5, fullyQualifiedNames.length);
		// added by Nick 06.06.2005
		// added by Nick 06.06.2005
		DatabaseMetaData md;
		// added by yk 29.08.2005
		Map createParams = new HashMap();
		// added by yk 29.08.2005.
		try {
			OrmProgressMonitor.getMonitor().setTaskName(
					"Loading database metadata ");// 7/12
			md = con.getMetaData();
			// by Nick

			// added by Nick 16.09.2005
			boolean saveNativeSQLTypes = ConfigurationReader
					.getAutomappingConfigurationInstance(this)
					.isPreserveNativeSQLTypes();

			if (saveNativeSQLTypes) {
				ResultSet rset = md.getTypeInfo();
				while (rset.next()) {
					createParams.put(rset.getString("TYPE_NAME"), rset
							.getString("CREATE_PARAMS"));
				}
				if (rset != null)
					rset.close();
			}
			// by Nick

			// first pass - create empty tables
			for (int i = 0; i < fullyQualifiedNames.length; i++) {
				try {
					boolean isNew = !tables.containsKey(fullyQualifiedNames[i]);
					// Table res=getOrCreateTable(fullyQualifiedNames[i]);
					Table res = getOrCreateTable(getNameWithHEscapeName(fullyQualifiedNames[i]));// //Escape
																									// add
																									// gavrs
																									// 8/3/2005
					dtArray[i] = res;
					if (isNew) {
						res.updateType(md);
					}
					monitor.worked();
				} catch (Exception e) {
					ExceptionHandler.logThrowableError(e, e.getMessage()
							.toString());
				}
			}

			monitor.setTaskParameters(40, dtArray.length);
			// second pass - create columns, pks, fks
			for (int i = 0; i < dtArray.length; i++) {
				try {
					IDatabaseTable res = dtArray[i];
					res.fillTableFromDBase(this, md, saveNativeSQLTypes);

					// added by Nick 06.06.2005
					if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
						ExceptionHandler
								.logObjectPlugin(
										String.valueOf(i + 1)
												+ " tables processed. Last processed table was: "
												+ res.getName(), OrmCore
												.getDefault().getBundle()
												.getSymbolicName(), this);
					monitor.worked();
					// by Nick
				} catch (Exception e) {
					// changed by Nick 01.06.2005
					ExceptionHandler.logThrowableError(e, e.getMessage());
					// ExceptionHandler.log(e, e.getMessage().toString());
					// by Nick
				}
			}
		} catch (SQLException e1) {
			ExceptionHandler.logThrowableError(e1,
					"Exception retreiving metadata");
		}
		OrmProgressMonitor.getMonitor().setTaskName("Creating mappings");

		if (generateMapping) {
			// move tau in addDatabasesTables(...
			// add tau 19.01.2006 ESORM-263- ResourceException: File not found -
			// automapping.HibernateAutoMapping$1FilterUnMappedUnits.isCompatible
			// project.synchroniz();

			IAutoMappingService.Settings settings = new IAutoMappingService.Settings();
			settings.canChangeClasses = true;
			settings.canUseHeuristicAlgorithms = useHeuristicAlgorithms;
			getAutoMappingService().generateMapping(dtArray, settings, info);
		}

		// added by yk 02.09.2005
		String param1 = "length";
		String param2 = "max length";
		String param3 = "precision";
		String param4 = "precision,scale";
		for (int i = 0; i < tables.size(); i++) {
			IDatabaseTable thetable = (IDatabaseTable) tables.getValue(i);
			Iterator itcolumns = thetable.getColumnIterator();
			while (itcolumns.hasNext()) {
				IDatabaseColumn column = (IDatabaseColumn) itcolumns.next();
				// added by Nick 16.09.2005
				if (!column.isNativeType())
					continue;
				// by Nick

				if (createParams.containsKey(column.getSqlTypeName())) {
					if (param1.equalsIgnoreCase((String) createParams
							.get(column.getSqlTypeName()))
							|| // changed by Nick 16.09.2005
							param2.equalsIgnoreCase((String) createParams
									.get(column.getSqlTypeName()))) // changed
																	// by Nick
																	// 16.09.2005
					{
						column.setPrecision(Column.DEFAULT_PRECISION);
						column.setScale(Column.DEFAULT_SCALE);
					} else if (param3.equalsIgnoreCase((String) createParams
							.get(column.getSqlTypeName()))) // changed by Nick
															// 16.09.2005
					{
						column.setLength(Column.DEFAULT_LENGTH);
						column.setScale(Column.DEFAULT_SCALE);

					} else if (param4.equalsIgnoreCase((String) createParams
							.get(column.getSqlTypeName()))) // changed by Nick
															// 16.09.2005
					{
						column.setLength(Column.DEFAULT_LENGTH);
					} else {
						column.setLength(Column.DEFAULT_LENGTH);
						column.setPrecision(Column.DEFAULT_PRECISION);
						column.setScale(Column.DEFAULT_SCALE);
					}
				}
			}
			// added by yk 05.10.2005
			setUniques(thetable);
			// added by yk 05.10.2005.

		}
		// added by yk 02.09.2005.

		save();

		// notify listeners that model has been changed
		// TODO (tau->tau) del? 27.01.2006
		// move tau 27.01.2006 -> tablesClassesWizardAction
		// if(fullyQualifiedNames.length > 0 )project.fireProjectChanged(this,
		// false);

	}
    // by Nick

    private void setUniques(IDatabaseTable thetable) {
		Iterator indexes = thetable.getIndexIterator();
		while (indexes.hasNext()) {
			Index index = (Index) indexes.next();
			if (index.getColumnSpan() == 1) {// 3.0.5
				IDatabaseColumn indexcolumn = (IDatabaseColumn) index
						.getColumnIterator().next();
				// TODO (tau->tau) error - testit 05.04.2006
				indexcolumn.setUnique(index.isUnique());
			}
		}
	}
    
    public void addDatabasesTables(Connection con,
			String[] fullyQualifiedNames, boolean generateMapping,
			boolean useHeuristicAlgorithms, ReversStatistic info)
			throws IOException, CoreException {
		// ESORM-316 - File not found - ScanProject.findClassInCU - Error
		// finding persistent class type
		project.synchronize();

		// add tau 12.05.2006 for ESORM-604
		setFlagDirty(false);

		AddDatabasesTablesRunnable runnable = new AddDatabasesTablesRunnable(
				con, fullyQualifiedNames, generateMapping,
				useHeuristicAlgorithms, info);
		// TODO(tau->tau) RULE
		ResourcesPlugin.getWorkspace().run(runnable, null);
	}
	
	// added by Nick 04.06.2005
	private class AddPersistentClassesRunnable implements IWorkspaceRunnable
    {
        private String[] fullyQualifiedNames;
        private boolean generateMapping, useHeuristicAlgorithms;
        private IMappingStorage storage;
        AddPersistentClassesRunnable(String[] fullyQualifiedNames, boolean generateMapping,boolean useHeuristicAlgorithms ,IMappingStorage storage) {
            this.fullyQualifiedNames = fullyQualifiedNames;
            this.generateMapping = generateMapping;
            this.storage = storage;
            this.useHeuristicAlgorithms=useHeuristicAlgorithms;
        }
        /*
         * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void run(IProgressMonitor monitor) throws CoreException {
            try {
                addPersistentClasses(fullyQualifiedNames,generateMapping,useHeuristicAlgorithms,storage);
            } catch (IOException e) {
                throw new NestableRuntimeException(e);
            }
        }
    }
    // by Nick

	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#addPersistentClasses(java.lang.String[], boolean, org.jboss.tools.hibernate.core.IMappingStorage)
	 */
	public void addPersistentClasses(String[] fullyQualifiedNames, boolean generateMapping,boolean useHeuristicAlgorithms, IMappingStorage storage) throws IOException, CoreException {
		// XXX Nick(4) implement the method
		//Algorithm:
		//1. Create PersistentClasse for each fullyQualifiedName 
		//2. search project for ICompilationUnit for each pc. set compilation unit in pc
		//3. from compilation unit get super class and if it is also pc then set super class for pc
		//   if compilation unit not found then leave super class null
		//4. if(generateMapping) mapping.getAutoMappingService().generateMapping(pc,new IAutoMappingService.Settings());
		//   else for each pc call mapping.getMappingForPersistentClass()
		PersistentClass[] pcArray = new PersistentClass[fullyQualifiedNames.length];
		//first pass to create all new PC
        for(int i = 0 ; i < fullyQualifiedNames.length ; i++) {
			//shouldn't update persistent classes now
        	pcArray[i]=getOrCreatePersistentClass(fullyQualifiedNames[i]);
        }
        //update PC super classes
        updateSuperClasses();

        for(int i = 0 ; i < pcArray.length ; i++) {
			pcArray[i].refresh();
        }
		
        if (generateMapping) {
            //XXX.toAlex is autoMappingService iface usage a correct solution?
        	//Yes it is
			IAutoMappingService.Settings settings=new IAutoMappingService.Settings();
			settings.canChangeTables=true;
            settings.canUseHeuristicAlgorithms = useHeuristicAlgorithms;
			getAutoMappingService().generateMapping(pcArray,settings);
		} else {
            for (int i = 0; i < pcArray.length; ++i) {
				getMappingForPersistentClass(pcArray[i], storage);
            }
		}

        save();   
	}

	/*
	 * @see org.jboss.tools.hibernate.core.IOrmProject#addPersistentClasses(java.lang.String[], boolean)
	 */
	public void addPersistentClasses(String[] fullyQualifiedNames,
			boolean generateMapping,boolean useHeuristicAlgorithms) throws IOException, CoreException {
        // added by Nick 04.06.2005
		AddPersistentClassesRunnable runnable = new AddPersistentClassesRunnable(fullyQualifiedNames,
                generateMapping, useHeuristicAlgorithms,null);
		
        //TODO(tau->tau) Yes DO RULE		
        ResourcesPlugin.getWorkspace().run(runnable, 
        		RuleUtils.getOrmProjectRule(project),
        		IWorkspace.AVOID_UPDATE,
        		new NullProgressMonitor());		
	}

	private void removePersistentClassInternal(IPersistentClass pc,boolean clearStorage)throws CoreException{
		classes.remove(pc.getName());
		removeMapping(pc,clearStorage);
		Package pack=(Package)pc.getPackage();
		pack.removePersistentClass(pc);
		// #added# by Konstantin Mishin on 2005/08/08 fixed fo ORMIISTUD-605
		if(pack.getPersistentClasses()==null || pack.getPersistentClasses().length==0) {
			packages.remove(pack.getName());
		}
		// #added#
	}
	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#removePersistentClass(java.lang.String)
	 */
	public void removePersistentClass(String fullyQualifiedName)throws CoreException {
		removePersistentClass(fullyQualifiedName,true);
	}
	
	public void removePersistentClass(String fullyQualifiedName,boolean clearStorage) throws CoreException {
		IPersistentClass pc=(IPersistentClass)classes.get(fullyQualifiedName);
		if(pc!=null){
			removePersistentClassInternal(pc,clearStorage);
			updateSuperClasses();
		}
	}
	
	// $changed$ by Konstantin Mishin on 2005/08/09 fixed fo ORMIISTUD-452
	public void removeDatabaseTable(String fullyQualifiedName)
			throws CoreException {
		removeDatabaseTableInternal(fullyQualifiedName);

		// added by Nick 12.08.2005
		final Set<IDatabaseTable> collectionTables = new HashSet<IDatabaseTable>();

		BaseMappingVisitor collectionCapturingVisitor = new BaseMappingVisitor() {
			/*
			 * @see org.jboss.tools.hibernate.internal.core.BaseMappingVisitor#visitCollectionMapping(org.jboss.tools.hibernate.core.hibernate.ICollectionMapping,
			 *      java.lang.Object)
			 */
			public Object visitCollectionMapping(ICollectionMapping mapping,
					Object argument) {
				super.visitCollectionMapping(mapping, argument);
				if (mapping.getCollectionTable() != null) {
					collectionTables.add(mapping.getCollectionTable());
				}
				return null;
			}
		};

		this.accept(collectionCapturingVisitor, null);
		// by Nick

		for (int i = 0; i < tables.size(); i++) {
			IDatabaseTable emptyTable = (IDatabaseTable) tables.getValue(i);
			// added by Nick 12.08.2005
			if (collectionTables.contains(emptyTable))
				continue;

			boolean shouldRemoveTable = (emptyTable
					.getPersistentClassMappings() == null || emptyTable
					.getPersistentClassMappings().length == 0);
			if (shouldRemoveTable) {
				if (emptyTable.getColumns() != null
						&& emptyTable.getColumns().length != 0) {
					// try to find mapped columns to skip this table deletion if
					// so
					Iterator columns = emptyTable.getColumnIterator();
					while (shouldRemoveTable && columns.hasNext()) {
						IDatabaseColumn column = (IDatabaseColumn) columns
								.next();
						if (column.getPersistentValueMapping() != null) {
							shouldRemoveTable = false;
						}
					}
				}

				if (shouldRemoveTable)
					;
				{
					removeDatabaseTableInternal(tables.get(i--));
				}
			}
		}
	}
	// $changed$
	
	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#renameClass(org.jboss.tools.hibernate.core.IPersistentClass,
	 *      java.lang.String)
	 */
	public void renameClass(IPersistentClass clazz, String newFullyQualifiedName)
			throws CoreException {
		String oldFQN = clazz.getName();
		classes.remove(oldFQN);
		Package pack = (Package) clazz.getPackage();
		String oldPkg = pack.getName();
		pack.removePersistentClass(clazz);
		if (pack.getPersistentClasses() == null
				|| pack.getPersistentClasses().length == 0) {
			packages.remove(pack.getName());
		}
		// rename class
		PersistentClass res = (PersistentClass) clazz;
		res.setName(newFullyQualifiedName);
		// #added# by Konstantin Mishin on 13.12.2005 fixed for ESORM-428
		IHibernateKeyMapping ihkm = res.getPersistentClassMapping()
				.getIdentifier();
		if (ihkm instanceof ComponentMapping) {
			ComponentMapping componentMapping = (ComponentMapping) ihkm;
			if (componentMapping.getComponentClass().getSourceCode()
					.getResource().equals(res.getSourceCode().getResource()))
				componentMapping.setComponentClassName(newFullyQualifiedName
						+ componentMapping.getComponentClassName().substring(
								oldFQN.length()));

		}
		// #added#
		classes.put(newFullyQualifiedName, res);
		// set package
		pack = getOrCreatePackage(res.getPackageName());
		pack.addPersistentClass(res);
		res.setPackage(pack);
		res.setProjectMapping(this);
		res.setSourceCode(null);
		if (oldPkg != null && !oldPkg.equals(res.getPackageName())) {

			IMappingStorage storage = res.getPersistentClassMapping()
					.getStorage();
			XMLFileStorage xmlStorage = (XMLFileStorage) storage;
			IProject project = getProject().getProject();

			xmlStorage.setDefaultPackage(res.getPackageName());

			if (xmlStorage.getResource().isAccessible()) { // class moving

				if (xmlStorage.getPersistentClassMappingCount() > 1) {
					xmlStorage.removePersistentClassMapping(res
							.getPersistentClassMapping());
				} else {

					IFile file = project.getFile(res.getPackageName()
							+ File.separator
							+ xmlStorage.getResource().getName());
					if (file != null) {
						if (storage.getPersistentClassMappingCount() > 1) {
							xmlStorage = (XMLFileStorage) xmlStorage
									.getHibernateConfiguration()
									.getOrCreateMappingStorage(
											res.getPersistentClassMapping(),
											null);
						} else {
							xmlStorage.getResource().move(file.getFullPath(),
									true, null);
						}
					}
				}

			}
			IResource resource = project.findMember(res.getPackageName()
					+ File.separator + xmlStorage.getResource().getName());
			if (resource != null) {
				xmlStorage.setResource(resource);
			}

		}
		// rename mapping & references
		RenameMappingVisitor rmv = new RenameMappingVisitor(oldFQN,
				newFullyQualifiedName);
		this.accept(rmv, null);
		// yan 20051016		
	}

	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#renameTable(org.jboss.tools.hibernate.core.IDatabaseTable, java.lang.String)
	 */
	public void renameTable(IDatabaseTable table, String newFullyQualifiedName) {
		String oldFQN = table.getName();
		tables.remove(oldFQN);
		Schema schema = (Schema) table.getSchema();
		schema.removeDatabaseTable(table);
		// rename table
		Table res = (Table) table;
		res.setName(newFullyQualifiedName);
		tables.put(newFullyQualifiedName, res);
		// set schema
		schema = getOrCreateSchema(res.getSchemaName());
		schema.addDatabaseTable(table);
		res.setSchema(schema);
	}

	/*
	 * @see org.jboss.tools.hibernate.core.IMapping#refreshClass(org.jboss.tools.hibernate.core.IPersistentClass)
	 */
	public void refreshClass(IPersistentClass clazz) {
		// #added# by Konstantin Mishin on 13.01.2006 fixed for ESORM-295
		if (clazz != null)
			// #added#
			clazz.refresh();
	}

	public String getNameWithHEscapeName(String name) {// Escape aad gavrs
														// 8/3/2005
		String tmp;
		int i = name.lastIndexOf('.');
		if (i == -1)
			return StringUtils.hibernateEscapeName(name);
		tmp = name.substring(0, i);
		return tmp + "."
				+ StringUtils.hibernateEscapeName(name.substring(i + 1));
	}
	
	// $added$ by Konstantin Mishin on 2005/08/09 fixed fo ORMIISTUD-452
	public void removeDatabaseTableInternal(Object fullyQualifiedName)
			throws CoreException {
		IDatabaseTable table = (IDatabaseTable) tables
				.remove(fullyQualifiedName);
		if (table != null) {
			IPersistentClassMapping[] pcms = table.getPersistentClassMappings();
			for (int i = 0; i < pcms.length; ++i) {
				// recreate empty mapping for a class:
				removeMapping(pcms[i].getPersistentClass());
				getMappingForPersistentClass(pcms[i].getPersistentClass(), null);
			}
			Schema scm = (Schema) table.getSchema();
			scm.removeDatabaseTable(table);
			if (scm.getDatabaseTables() == null
					|| scm.getDatabaseTables().length == 0)
				schemas.remove(scm.getName());
		}
	}
	// $added$
	//akuzmin 12.10.2005
    private void addDAOGenerationRunnable(IPersistentClass[] classes,
			boolean generateInterfaces, boolean isNeedLog,
			boolean generateTests, String packageName) throws IOException,
			CoreException {
		IAutoMappingService.Settings settings = new IAutoMappingService.Settings();
		getDAOGenerator().generateDAO(classes, settings, packageName,
				generateInterfaces, isNeedLog, generateTests);
	}

    //akuzmin 12.10.2005	
	private class AddDAOGenerationRunnable implements IWorkspaceRunnable {
		private IPersistentClass[] classes;
		private boolean generateInterfaces;
		private boolean generateLog;
		private boolean generateTests;
		private String packageName;

		AddDAOGenerationRunnable(IPersistentClass[] classes,
				boolean generateInterfaces, boolean generateLog,
				boolean generateTests, String packageName) {
			this.classes = classes;
			this.generateInterfaces = generateInterfaces;
			this.packageName = packageName;
			this.generateLog = generateLog;
			this.generateTests = generateTests;
		}

		public void run(IProgressMonitor monitor) throws CoreException {
			try {
				addDAOGenerationRunnable(classes, generateInterfaces,
						generateLog, generateTests, packageName);
			} catch (IOException e) {
				throw new NestableRuntimeException(e);
			}
		}
	}

	//akuzmin 12.10.2005
	public void addDAOClasses(IPersistentClass[] classes,
			boolean generateInterfaces, boolean generateLog,
			boolean generateTests, String packageName) throws IOException,
			CoreException {
		AddDAOGenerationRunnable runnable = new AddDAOGenerationRunnable(
				classes, generateInterfaces, generateLog, generateTests,
				packageName);
		//TODO(tau->tau) RULE    	
		ResourcesPlugin.getWorkspace().run(runnable, null);
	}

	public boolean isFlagDirty() {
		return flagDirty;
	}

	public void setFlagDirty(boolean flagDirty) {
		this.flagDirty = flagDirty;
	}

}
