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
package org.jboss.tools.hibernate.internal.core.hibernate;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmProject;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.HibernatePropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.PropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySourceWrapper;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;

/**
 * @author alex
 *
 * Object oriented presentation of Hibernate configuration file
  */
public class HibernateConfiguration extends PropertySourceBase implements IHibernateConfiguration{

    private OrmProject ormProject;
    private HibernateMapping hbMapping;
    private IFile resource;
//    private static final Logger logger = Logger.getLogger(HibernateConfiguration.class.getName());
    
    private long timeStamp = 0;
    
    private Map<String,IPersistentClassMapping> classMappings = new HashMap<String,IPersistentClassMapping>();
    private Map<String,IMappingStorage> storages = new HashMap<String,IMappingStorage>();
    private List filterDefs=new ArrayList(); //??? unused code
//    private List classCaches = new ArrayList(); yan 20051003 unused code
//    private List collectionCaches = new ArrayList(); yan 20051003 unused code
    private Map<String,String> listeners = new HashMap<String,String>();
    private List grant = new ArrayList();
    
    private static final IPersistentClassMapping[] CLASSES={};
    private static final IMappingStorage[] STORAGES={};
    
    private boolean modificationFlag = false;           //when element is created it add in DOM tree once because this flag is set false
    
    // added by yk 29.09.2005
	private Set<Object> errorMarkers = new HashSet<Object>();
    // added by yk 29.09.2005.
    
    public HibernateConfiguration(HibernateMapping projectMapping, IFile resource){
        this.ormProject=(OrmProject)projectMapping.getProject();
        this.hbMapping=projectMapping;
        this.resource=resource;
        this.setPropertyDescriptorsHolder(HibernatePropertyDescriptorsHolder.getInstance());
    }
    
    private boolean saveInProgress;
    
    private synchronized boolean isSaveInProgress(){
        return saveInProgress;
    }
    private synchronized void setSaveInProgress(boolean isInProgress){
        saveInProgress=isInProgress;
    }
    
    
    protected void reset() {
        // added by Nick 16.06.2005
        Iterator itr = classMappings.values().iterator();
        if (itr != null) {
            while (itr.hasNext()) {
                IPersistentClassMapping mapping = (IPersistentClassMapping) itr.next();
                if (mapping != null) {
                    IDatabaseTable table = mapping.getDatabaseTable();
                    if (table != null)
                        table.removePersistentClassMapping(mapping);
                }
            }
        }
        // by Nick
        //akuzmin 04.08.2005
        replaceProperties(new Properties());
        //akuzmin
        classMappings.clear();
        filterDefs.clear();
        storages.clear();
//        collectionCaches.clear(); yan 20051003
        listeners.clear();
        grant.clear();
    }
    
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IMappingConfiguration#getResource()
     */
    public IResource getResource() {
        return resource;
    }
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IMappingConfiguration#reload()
     */
    public void reload() throws IOException, CoreException{
		//TODO EXP 5d tau 27.02.2006
        reset();
        //akuzmin 15.08.2005
        if (resource.exists()) {
        InputStream input = null;
        try {
            input = resource.getContents(true);            
            // added by yk 21.09.2005
            if(input.available() < 1)
            {// the file is empty
            	HibernateConfigurationWriter writer = new HibernateConfigurationWriter(this, resource);
            	
            	// edit tau 18.01.2006
    			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?            	
            	//resource.setContents(writer.writeDefaultConfiguration(),IResource.FILE, null);            	
            	//resource.setContents(writer.writeDefaultConfiguration(),IResource.FORCE, null);
            	resource.setContents(writer.writeDefaultConfiguration(false),IResource.NONE, null); // edit  tau 14.02.2006 - add false            	
            	
            	input = resource.getContents(true);
            }
            // added by yk 21.09.2005.

            HibernateConfigurationReader reader=new HibernateConfigurationReader(this, ormProject.getProject());
            reader.read(input);
        } finally {
            if(input != null)input.close();
            timeStamp = refreshTimeStamp();
        }
        }
    }
    
    
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IMappingConfiguration#save()
     */
    public void save()  throws IOException, CoreException {
    	save(true);
    	
    }

    // add tau 14.02.2006 from save() - add boolean flagSaveMappingStorages 
    public void save(boolean flagSaveMappingStorages)  throws IOException, CoreException {
        setSaveInProgress(true);
        InputStream input=null;
        try{
            HibernateConfigurationWriter writer=new HibernateConfigurationWriter(this,resource);
            input=writer.write(flagSaveMappingStorages);
            if(input!=null){
            	
            	// edit tau 18.01.2006
    			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?            	
                if(!resource.exists()) resource.create(null, IResource.NONE, null);//add  gavrs 4.03.05
                //resource.setContents(input, IResource.FILE, null);
                //if(!resource.exists()) resource.create(null, IResource.FORCE, null);//add  gavrs 4.03.05
                //resource.setContents(input, IResource.FORCE, null);
                resource.setContents(input, IResource.NONE, null);            	
            	
            }
        } finally {
            timeStamp = refreshTimeStamp();
            setSaveInProgress(false);
            if(input!=null) input.close();
        }
    }
    
    public synchronized IPersistentClassMapping[] getPersistentClassMappings() {
        return (IPersistentClassMapping[])classMappings.values().toArray(CLASSES);
    }
    
    /**
     * Get the mapping for a particular class
     */
    public synchronized IPersistentClassMapping getClassMapping(String fullyQualifiedName) {
        return ( IPersistentClassMapping ) classMappings.get( fullyQualifiedName );
    }
    private IHibernateClassMapping createMappingInternal(IPersistentClass clazz, String newMappingType){
        IHibernateClassMapping mapping=null;
        IHibernateClassMapping superMapping;
        String strategy=newMappingType;
        IPersistentClass superClass=clazz.getSuperClass();
        if(superClass == null || OrmConfiguration.TABLE_PER_CLASS.equals(strategy)){ //root class
            mapping=new RootClassMapping();
        } else if(OrmConfiguration.TABLE_PER_CLASS_UNION.equals(strategy)){
            superMapping=(IHibernateClassMapping)getClassMapping(superClass.getName());
            mapping=new UnionSubclassMapping(superMapping);
            superMapping.addSubclass(mapping);
        } else if(OrmConfiguration.TABLE_PER_SUBCLASS.equals(strategy)){
            superMapping=(IHibernateClassMapping)getClassMapping(superClass.getName());
            mapping=new JoinedSubclassMapping(superMapping);
            superMapping.addSubclass(mapping);
        } else { //default:TABLE_PER_HIERARCHY
            superMapping=(IHibernateClassMapping)getClassMapping(superClass.getName());
            mapping=new SubclassMapping(superMapping);
            superMapping.addSubclass(mapping);
        }
        mapping.setClassName(clazz.getName());
        mapping.setPersistentClass(clazz);
        clazz.setPersistentClassMapping(mapping);
        return mapping;
    }
    // It is not fully implemented method.
    public IPersistentClassMapping replaceMappingForPersistentClass(IPersistentClass clazz, String newMappingType) throws CoreException {
        String strategy=newMappingType;
        IPersistentClass superClass=clazz.getSuperClass();
        IHibernateClassMapping oldMapping=(IHibernateClassMapping)clazz.getPersistentClassMapping();
        // check if it is needed
        if(superClass == null || OrmConfiguration.TABLE_PER_CLASS.equals(strategy)){ //root class
            if(oldMapping.isClass()) return oldMapping;
        } else if(OrmConfiguration.TABLE_PER_CLASS_UNION.equals(strategy)){
            if(oldMapping.isUnionSubclass()) return oldMapping;
        } else if(OrmConfiguration.TABLE_PER_SUBCLASS.equals(strategy)){
            if(oldMapping.isJoinedSubclass()) return oldMapping;
        } else { //TABLE_PER_HIERARCHY
            if(oldMapping.isSubclass()) return oldMapping;
            strategy = OrmConfiguration.TABLE_PER_HIERARCHY;
        } 
        // create new
        IHibernateClassMapping newMapping = createMappingInternal(clazz, strategy);
        // copy props
        newMapping.copyFrom(oldMapping);
        // copy table if it is needed
        // remove old
        classMappings.remove( clazz.getName() );
        IMappingStorage storage=oldMapping.getStorage();
        storage.removePersistentClassMapping(oldMapping);
        storage.addPersistentClassMapping(newMapping);
        modificationFlag=true;
        return newMapping;
    }

    private IPersistentClassMapping createEmptyClassMapping(
            IPersistentClass clazz, IDatabaseSchema schema, IDatabaseTable table) { //by Nick 6.05.2005
        String strategy=ormProject.getOrmConfiguration().getProperty(OrmConfiguration.HIBERNATE_INHERITANCE);
        IHibernateClassMapping mapping=createMappingInternal(clazz, strategy);
        if(!mapping.isSubclass()){ // subclass use root table
            //Also generate table name using HibernateAutoMapping.classToTableName and getOrCreate table
            
            IDatabaseTable classTable = table;
            if (classTable == null) {
                //changed by Nick 22.04.2005
                String defCatalog = null;
                String defSchema = null;
                if (schema != null) {
                    // changed by Nick 30.06.2005
                    // ORMIISTUD-359
                    //defCatalog = schema.getCatalog();
                    // by Nick
                    defSchema = schema.getShortName();
                }
                // changed by Nick 30.06.2005
                // ORMIISTUD-359
                //if(defCatalog==null || defCatalog.length() == 0 ) 
                    //defCatalog = model.getConfiguration().getProperty("hibernate.default_catalog");
                // by Nick
                //if(defCatalog==null || defCatalog.length() == 0 ) 
                //  defCatalog = getProperty("hibernate.default_catalog");
                if(defSchema==null || defSchema.length() == 0 ) 
                    defSchema = ormProject.getOrmConfiguration().getProperty("hibernate.default_schema");
                //if(defSchema==null || defSchema.length() == 0 ) 
                //  defSchema = getProperty("hibernate.default_schema");
                
                //by Nick
                
                String tableName=Table.toFullyQualifiedName(defCatalog,defSchema,HibernateAutoMapping.classToTableName(clazz.getName()));
                classTable=hbMapping.getOrCreateTable(tableName);
            }

            mapping.setDatabaseTable(classTable);
            classTable.addPersistentClassMapping(mapping);
        }
        return mapping;
    }
    
    //added by Nick 6.05.2005
    private void bindMapping(IPersistentClassMapping mapping, IMappingStorage storage) throws CoreException {
        getOrCreateMappingStorage(mapping, storage);
        IPersistentClass clazz = mapping.getPersistentClass();
        classMappings.put(clazz.getName(), mapping);
        modificationFlag=true;
    }
    
    public synchronized IPersistentClassMapping getOrCreateClassMappingForTable(IPersistentClass clazz, IDatabaseTable table, IMappingStorage storage) throws CoreException {
        IPersistentClassMapping mapping=(IPersistentClassMapping)classMappings.get( clazz.getName() );
        if(mapping==null){
            mapping = createEmptyClassMapping(clazz,null,table);
            bindMapping(mapping, storage);
        }
        return mapping;
    }
    //by Nick
    
    //added by Nick 22.04.2005
    public synchronized IPersistentClassMapping getOrCreateClassMapping(IPersistentClass clazz, IDatabaseSchema schema, IMappingStorage storage) throws CoreException {
        IPersistentClassMapping mapping=(IPersistentClassMapping)classMappings.get( clazz.getName() );
        if(mapping==null){
            //get or create mapping for super class 
            if(clazz.getSuperClass() != null) getOrCreateClassMapping(clazz.getSuperClass(),schema, storage);
            mapping = createEmptyClassMapping(clazz,schema,null);
            
            bindMapping(mapping, storage); //by Nick 6.05.2005
        }
        return mapping;
    }
    //by Nick
        
    //stub method to use default schema
    public synchronized IPersistentClassMapping getOrCreateClassMapping(IPersistentClass clazz) throws CoreException{
        return getOrCreateClassMapping(clazz,null, null);
    }
    
    public void removeClassMapping(IPersistentClass clazz)throws CoreException{
   	 removeClassMapping(clazz,true);
    }
    public void removeClassMapping(IPersistentClass clazz,boolean clearStorage)throws CoreException{
        // added by Nick 19.11.2005
        if (clazz == null)
            return ;
        // by Nick
        
        IPersistentClassMapping mapping=(IPersistentClassMapping)classMappings.remove( clazz.getName() );
        if(mapping!=null){
            // yan 20051002
      	  IMappingStorage storage=mapping.getStorage();
      	  if (storage.getPersistentClassMappingCount()==1) {
      		  storages.remove(storage.getName());
      	  }
            if (clearStorage/* && ClassUtils.getReferencedMappings(clazz).length==0*/) { // yan 20051013
               mapping.clear();
               storage.removePersistentClassMapping(mapping);
            }
            // yan
            modificationFlag=true;
            clazz.setPersistentClassMapping(null);
            IDatabaseTable table=mapping.getDatabaseTable();
            if(table!=null){
                table.removePersistentClassMapping(mapping);
                if(table.getPersistentClassMappings().length==0) hbMapping.removeDatabaseTable(table.getName());
            }
        }
    }
    
/*    
    private IPath getPackagePath(String packageName)throws CoreException {
        IPackageFragment thePackage;
        thePackage = ScanProject.findFirstPackageFragment(packageName,ormProject.getProject());
        IPath path = ormProject.getProject().getProjectRelativePath();
        if (thePackage != null)
            path.append(thePackage.getPath());
        return path;
    }
*/
    
    public synchronized IMappingStorage getOrCreateMappingStorage(IPersistentClassMapping mapping, IMappingStorage defStorage) throws CoreException{
        if(mapping.getStorage() !=null)
            return mapping.getStorage();
        IMappingStorage storage=defStorage;
        if(storage==null){
            String strategy = ormProject.getOrmConfiguration().getProperty(OrmConfiguration.HIBERNATE_STORAGE);
            IPath storagePath = null;

            // added by Nick 29.06.2005
            IPackage thePackage = mapping.getPersistentClass().getPackage();
            String packageName = null;
            if (thePackage != null)
            {
                packageName = thePackage.getName();
            }
            storagePath = ScanProject.getPackageSourcePath(packageName,ormProject.getProject());

            // #added# by Konstantin Mishin on 10.12.2005 fixed for ESORM-422
            if (storagePath == null) {
            	IClasspathEntry classpathEntry[] = JavaCore.create(ormProject.getProject()).getRawClasspath();
            	for (int i = 0; i < classpathEntry.length; i++) {
            		if(classpathEntry[i].getEntryKind()== IClasspathEntry.CPE_PROJECT)
            			storagePath = ScanProject.getPackageSourcePath(packageName,ormProject.getProject().getWorkspace().getRoot().getProject(classpathEntry[i].getPath().lastSegment()));
				}
            }
            // #added#
            
            if (storagePath == null)
                storagePath = resource.getProjectRelativePath().removeLastSegments(1);
            // by Nick
            
            if(OrmConfiguration.XML_PER_PROJECT.equals(strategy)){
                //XXX Nick(4) Implement getOrCreate project storage. storage name should be project.hbm.xml in project root folder
                // 1. create name
//              storagePath = resource.getProjectRelativePath().removeLastSegments(1);
                storagePath = storagePath.append("mapping.hbm.xml");
            } else {
    
                if(OrmConfiguration.XML_PER_PACKAGE.equals(strategy)){
                    //XXX Nick(4) Implement getOrCreate package storage. storage name should be package.hbm.xml in package folder 
                    // 1. create name
//                  storagePath = resource.getProjectRelativePath().removeLastSegments(1);
                    storagePath = storagePath.append(mapping.getPersistentClass().getPackage().getName().replace('.','/'));
                    storagePath = storagePath.append("mapping.hbm.xml");
                }
                else { //default strategy : OrmConfiguration.XML_PER_HIERARCHY
                    //XXX Nick(4) Implement getOrCreate hierarchy storage. storage name should be <root class name>.hbm.xml in root's class package folder
                    // 1. create name
                    IPersistentClass root=mapping.getPersistentClass().getRootClass();
//                  storagePath = resource.getProjectRelativePath().removeLastSegments(1);
                    storagePath = storagePath.append(root.getName().replace('.','/')+".hbm.xml");
                }
            }
            
            // 2. check if storage with that name exist
            // added by Nick 08.06.2005 ORMIISTUD-113, ORMIISTUD-114
//          IPackage thePackage = mapping.getPersistentClass().getPackage();
//            String packageName = null;
//            if (thePackage != null)
//            {
//                packageName = thePackage.getName();
//            }
//            IPath packagePath = ScanProject.getPackageSourcePath(packageName,model.getProject());
//            if (packagePath != null)
//            {
//                if (storagePath.matchingFirstSegments(packagePath) != packagePath.segmentCount())
//                    storagePath = packagePath.append(storagePath);
//            }
            // by Nick

//            IFile file = model.getProject().getFile(storagePath);
            IFile file = ormProject.getProject().getWorkspace().getRoot().getFile(storagePath);
            storage=(IMappingStorage)storages.get(file.getFullPath().toString());
            if (storage==null)
            {
                // 3  create resource, XMLFileStorage
                //if storages map was updated then set modificationFlag=true;
                storage = new XMLFileStorage(this,file);
                storages.put(storage.getName(),storage);
            }
        }
        storage.addPersistentClassMapping(mapping);
        mapping.setStorage(storage);
        modificationFlag = true;
        return storage;
    }
    
    
    public synchronized IMappingStorage[] getMappingStorages() {
		//TODO EXP 3d
    	verifyHibernateMapping();
        return (IMappingStorage[])storages.values().toArray(STORAGES);
    }
    /**
     * Get the storage for a particular file
     */
    public synchronized IMappingStorage getMappingStorage(String storage) {
        return ( IMappingStorage ) storages.get( storage );
    }

    public List getFilterDefs() {
        return filterDefs;
    }
    
    public Map getListeners() {
        return listeners;
    }
    
    public void addListener(String type, String listenerClass){
        //XXX toAlex (Slava)    Implement more
        listeners.put(type, listenerClass);
    }
    
    public void addMappingStorage(IMappingStorage mappingStorage){
        IPersistentClassMapping[] maps=mappingStorage.getPersistentClassMappings();
        for(int i=0;i<maps.length;++i)  {
            classMappings.put(maps[i].getName(),maps[i]);
            maps[i].setStorage(mappingStorage);
        }
        storages.put(mappingStorage.getName(),mappingStorage);
        modificationFlag = true;
    }
    
    
    // #changed# by Konstantin Mishin on 10.09.2005 fixed for ORMIISTUD-660
    //public IMappingStorage addMappingStorage(IPath path) throws IOException, CoreException {
    public IMappingStorage addMappingStorage(IFile file) throws IOException, CoreException {
    	//IFile file = model.getProject().getFile(path);
    // #changed#
        String storageId=file.getFullPath().toString();
        IMappingStorage storage = getMappingStorage(storageId);
        if(storage==null){
            storage = new XMLFileStorage(this, file);
            storage.reload();
            modificationFlag = true;
        }
        return storage;
    }
    public void removeMappingStorage(IMappingStorage storage) throws IOException, CoreException{
        IPersistentClassMapping pcm[]=storage.getPersistentClassMappings();
        for(int i=0;i<pcm.length;++i){
        	// add tau 04.11.2005
        	IPersistentClass pc = pcm[i].getPersistentClass();
        	if (pc != null) {
                this.removeClassMapping(pc);        		
        	}
        }
        storages.remove(storage.getName());
    }
    
    /* 
     * return modification flag - true if it was modified
     */
    public boolean isModified() {
        return modificationFlag;
    }
    
    /**
     * @return Returns the model.
     */
    public OrmProject getOrmProject() {
        return ormProject;
    }
    public IProject getProject() {
        return ormProject.getProject();
    }
    public HibernateMapping getHibernateMapping(){
        return hbMapping;
    }
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.hibernate.IHibernateConfiguration#getManagedConfiguration()
     */
    public IPropertySource2 getManagedConfiguration() {
        return new PropertySourceWrapper(this, HibernatePropertyDescriptorsHolder.getManagedPropertyDescriptors());
    }
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.hibernate.IHibernateConfiguration#getPoolConfiguration()
     */
    public IPropertySource2 getPoolConfiguration() {
        return new PropertySourceWrapper(this, HibernatePropertyDescriptorsHolder.getPoolPropertyDescriptors());
    }
   
	// #added# by Konstantin Mishin on 26.05.2006 fixed for ESORM-528
    public IPropertySource2 getSpringConfiguration() {
        return new PropertySourceWrapper(this, HibernatePropertyDescriptorsHolder.getSpringPropertyDescriptors());
    }
	// #added#   
    
    //akuzmin 31.05.2005
    public PropertySourceWrapper getGeneralConfiguration(boolean isDialectChange) {
        this.setPropertyDescriptorsHolder(HibernatePropertyDescriptorsHolder.getGeneralPropertyDescriptors(isDialectChange));
        PropertySourceWrapper result=new PropertySourceWrapper(this, HibernatePropertyDescriptorsHolder.getGeneralPropertyDescriptors(isDialectChange));
        this.setPropertyDescriptorsHolder(HibernatePropertyDescriptorsHolder.getInstance());
        return result;
    }
    
    public String getPath(){
        return resource.getProjectRelativePath().toString();
    }

    //added 19.05.2005 by Nick
    private long refreshTimeStamp()
    {
        IResource resource = this.getResource();
        long result = 0;
        
        if (resource != null)
        {
            try {
                resource.refreshLocal(IResource.DEPTH_ZERO,null);
                result = resource.getLocalTimeStamp();
            } 
            catch (CoreException e) {
            	OrmCore.getPluginLog().logError("Exception refreshing resource timestamp...",e);
            }
        }
        return result;
   }
    //by Nick

    //added 19.05.2005 by Nick
    public boolean resourceChanged() {
        if(isSaveInProgress())return false;
        return (timeStamp != refreshTimeStamp());
    }
    //by Nick
    public void resourcesChanged(){
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("HibernateConfiguration.resourcesChanged()");
        }
        
        // del tau 02.11.2005
        // added by yk 01.10.2005
        /*
        Object[] temp = null;
        if(errorMarkers.size() > 0)
        	temp = errorMarkers.toArray();
        */
        clearErrorMarkers();
        // added by yk 01.10.2005.
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("HibernateConfiguration.resourcesChanged(), clearErrorMarkers() END");        
        }

        IMappingStorage[] storages = getMappingStorages();
        boolean changed=false;
        for (int i = 0; i < storages.length; i++) {
            IMappingStorage storage = storages[i];
            
            if ( storage.getResource().isLocal(IResource.DEPTH_ZERO)&& storage.resourceChanged())
            {
                if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
                	OrmCore.getPluginLog().logInfo("HibernateConfiguration.resourcesChanged():" + storage.getName());
                }
                try {
                    changed=true;
                    storage.reload();
                } catch (Exception e) {
                	OrmCore.getPluginLog().logError("Exception refreshing resources...",e);
                }
            }
        }
        
        
        if(!changed){
            IPersistentClassMapping maps[]= getPersistentClassMappings();
            if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
            	OrmCore.getPluginLog().logInfo("HibernateConfiguration.resourcesChanged() for maps[]:" + maps.length);            
            }
            for(int i=0;i<maps.length;++i){
                IPersistentClass pc=maps[i].getPersistentClass();
                if(pc!=null && pc.isResourceChanged()) {
                    changed=true;
                    break;
                }
            }
            
            // del tau 02.11.2005
            //if(temp != null) errorMarkers.addAll(Arrays.asList(temp));
        }
        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("HibernateConfiguration.hbMapping.refresh(false) START:" + hbMapping.getName());        
        }
        if(changed) {
			try {
				hbMapping.refresh(false, true);
			} catch (IOException e) {
				OrmCore.getPluginLog().logError(e.getMessage().toString(),e);
			} catch (CoreException e) {
				OrmCore.getPluginLog().logError( e.getMessage().toString(),e);
			} // do doMappingsUpdate - add tau 17.11.2005
        }
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("HibernateConfiguration.hbMapping.refresh(false) END:" + hbMapping.getName());        
        }
    }

    // added by yk 30.09.2005
	public Set getErrorMarkers() 
	{		return errorMarkers;					}
    // added by yk 29.09.2005.
	public void setErrorMarkers(Set<Object> errorMarkers) 
	{		this.errorMarkers = errorMarkers;		}
	public void clearErrorMarkers()
	{		errorMarkers.clear();					}
	public void addErrorMarker(Object errormarker)
	{		errorMarkers.add(errormarker);			}
	
	// add tau 23.02.2006
	//TODO EXP 3d	
	public void verifyHibernateMapping() {
		if (hbMapping.isFlagDirty()) {
			try {
				hbMapping.reload(true);
			} catch (IOException e) {
				OrmCore.getPluginLog().logError(e.getMessage(),e);
			} catch (CoreException e) {
				OrmCore.getPluginLog().logError(e.getMessage(),e);
			}
		}
	}
	
}
