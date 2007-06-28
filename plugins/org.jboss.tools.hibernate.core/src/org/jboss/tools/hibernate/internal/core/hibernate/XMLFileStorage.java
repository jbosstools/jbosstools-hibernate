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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.SequencedHashMap;
import org.dom4j.DocumentException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.internal.core.data.Schema;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.query.NamedQueryDefinition;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.xml.sax.SAXException;


/**
 * @author alex
 *
 * TODO (5) implement Hibernate hbm.xml file loading/saving
 * A collection of mappings from classes and collections to
 * relational database tables. (Represents a single
 * <tt>&lt;hibernate-mapping&gt;</tt> element.)
 * @see org.hibernate.cfg.Mappings
 */
public class XMLFileStorage implements IMappingStorage {

	private static final long serialVersionUID = -772873827386119755L;
	private static final IPersistentClassMapping[] MAPPINGS={};
	private static final NamedQueryDefinition[] QUERIES={};
	private IResource resource;
	private HibernateConfiguration hc;

	private SequencedHashMap classes = new SequencedHashMap();
	private Map<String,String> imports = new HashMap<String,String>();
	private Map<String,FilterDef> filterDefinitions = new HashMap<String,FilterDef>();
	private Map<String,NamedQueryDefinition> queries = new HashMap<String,NamedQueryDefinition>();
	private Map<String,TypeDef> typeDefs = new HashMap<String,TypeDef>();

	private String schemaName;
    private String catalogName;
	private String defaultCascade;
	private String defaultPackage;
	private String defaultAccess;
	private boolean autoImport=true;
	private boolean defaultLazy=true;
    
    private long timeStamp = 0;
    
    // add tau 28.03.2006
    // true - need save
    private boolean dirty = true;    

	public XMLFileStorage(HibernateConfiguration hc, IResource resource) {
		this.resource = resource;
		this.hc = hc;
	}
	
	// yan 20051016
	public HibernateConfiguration getHibernateConfiguration() {
		return hc;
	}

	private boolean saveInProgress;
	
	private synchronized boolean isSaveInProgress(){
		return saveInProgress;
	}
	private synchronized void setSaveInProgress(boolean isInProgress){
		saveInProgress=isInProgress;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMappingStorage#getProjectMapping()
	 */
	public IMapping getProjectMapping() {
		return hc.getHibernateMapping();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMappingStorage#addPersistentClassMapping(org.jboss.tools.hibernate.core.IPersistentClassMapping)
	 */
	public void addPersistentClassMapping(IPersistentClassMapping mapping) {
		if(hc.getClassMapping(mapping.getName())!=null){ 
			//class already loaded
			OrmCore.getPluginLog().logInfo("Duplicated class mapping ignored : " + mapping.getName());
			XMLFileStorageDublicate.set(mapping.getName());
			return;
		}
		Object old	= classes.get(mapping.getName());
		if(old!=null){
			OrmCore.getPluginLog().logInfo("Duplicated class mapping ignored: " + mapping.getName());
			return;
		}
		classes.put(mapping.getName(),mapping);
		mapping.setStorage(this);
		
        //add tau 28.03.2006 
        setDirty(true);		
		
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMappingStorage#removePersistentClassMapping(org.jboss.tools.hibernate.core.IPersistentClassMapping)
	 */
	public void removePersistentClassMapping(IPersistentClassMapping mapping) {
		classes.remove(mapping.getName());
		mapping.setStorage(null);
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	public IPersistentClassMapping getPersistentClassMapping(String className){
		return (IPersistentClassMapping)classes.get(className);
	}
	
	public int getPersistentClassMappingCount() {
		return classes.size();
	}
	
	public int getNamedQueryMappingCount() {
		return queries.size();
	}
	/**
	 * @return Returns the autoImport.
	 */
	public boolean isAutoImport() {
		return autoImport;
	}
	/**
	 * @param autoImport The autoImport to set.
	 */
	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	/**
	 * @return Returns the defaultLazy.
	 */
	public boolean isDefaultLazy() {
		return defaultLazy;
	}
	/**
	 * @param defaultLazy The defaultLazy to set.
	 */
	public void setDefaultLazy(boolean defaultLazy) {
		this.defaultLazy = defaultLazy;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	/**
	 * @return Returns the catalogName.
	 */
	public String getCatalogName() {
		return catalogName;
	}
	/**
	 * @param catalogName The catalogName to set.
	 */
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	/**
	 * @return Returns the defaultAccess.
	 */
	public String getDefaultAccess() {
		return defaultAccess;
	}
	/**
	 * @param defaultAccess The defaultAccess to set.
	 */
	public void setDefaultAccess(String defaultAccess) {
		this.defaultAccess = defaultAccess;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	/**
	 * @return Returns the defaultCascade.
	 */
	public String getDefaultCascade() {
		return defaultCascade;
	}
	/**
	 * @param defaultCascade The defaultCascade to set.
	 */
	public void setDefaultCascade(String defaultCascade) {
		this.defaultCascade = defaultCascade;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	/**
	 * @return Returns the defaultPackage.
	 */
	public String getDefaultPackage() {
		return defaultPackage;
	}
	/**
	 * @param defaultPackage The defaultPackage to set.
	 */
	public void setDefaultPackage(String defaultPackage) {
		this.defaultPackage = defaultPackage;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	/**
	 * @return Returns the schemaName.
	 */
	public String getSchemaName() {
		return schemaName;
	}
	/**
	 * @param schemaName The schemaName to set.
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
        //add tau 28.03.2006 
        setDirty(true);		
	}

	public String getName(){
		return resource.getFullPath().toString();
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMappingStorage#getResource()
	 */
	public IResource getResource() {
		return resource;
	}
	
	public void setResource(IResource res) {
		resource=res;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	/**
	 * Get the query language imports
	 *
	 * @return a mapping from "import" names to fully qualified class names
	 */
	public Map getImports() {
		return imports;
	}
	
	public Map getFilterDefinitions() {
		return filterDefinitions;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMappingStorage#getPersistentClassMappings()
	 */
	public IPersistentClassMapping[] getPersistentClassMappings(){
		return (IPersistentClassMapping[])classes.values().toArray(MAPPINGS);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitMappingStorage(this,argument);
	}
	
	/**
	 * by troyas
	 * @throws CoreException
	 * @throws SAXException 
	 * @throws IOException 
	 */
	public void reload() throws CoreException, IOException {
		XMLFileReader reader = new XMLFileReader(this, resource, hc);
		if(reader.canReadFile())
		{
			hc.removeMappingStorage(this);
			classes.clear();
			imports.clear();
			filterDefinitions.clear();
			queries.clear();
			typeDefs.clear();
			try{
	/* rem by yk 28.09.2005			XMLFileReader reader = new XMLFileReader(this, resource, hc); */
				reader.reload();
			} catch (DocumentException ex){
				//throw new NestableRuntimeException(ex);
				OrmCore.getPluginLog().logError(ex); // add tau 19.06.2005 - for Alex
			} finally {
	            //added 19.05.2005 by Nick
	            timeStamp = refreshTimeStamp();
	            
	            //add tau 28.03.2006 
	            setDirty(false);
	        }
			hc.addMappingStorage(this);
		}
		else
			hc.addErrorMarker(getResource().getName());
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
            	OrmCore.getPluginLog().logError("Exception refreshing resource timestamp..., "+ e.toString());            
            }
        }
        return result;
   }
    //by Nick
    
/*
	private InputStream getMappingContent(IFile file) throws CoreException{
		InputStream input = file.getContents();
		return input;
	}
*/

	public void addImport(String className, String rename){
		//XXX toAlex: May be better use some exception instead logger.info ?
		Object old = imports.put(rename, className);
		if (old!=null ) {
			OrmCore.getPluginLog().logInfo("duplicate import: " + rename);
		}
	}

	public IDatabaseTable getTable(String schema, String catalog, String name){
		//by alex on 06/28/05
		//if(catalog==null || catalog.length()==0)catalog = hc.getProperty("hibernate.default_catalog");
		//if(schema==null || schema.length() == 0 )schema = hc.getProperty("hibernate.default_schema");
		String key = Table.toFullyQualifiedName(catalog, schema, name) ;
		IDatabaseTable table =  hc.getHibernateMapping().getOrCreateTable(key);
		return table;
	}

	public IDatabaseTable getTempTable(String className){
		Table colTable=new Table();
		colTable.setName(HibernateAutoMapping.classToTableName(className));
		Schema scm=new Schema(null);
		scm.setName("");
		colTable.setSchema(scm);
		return colTable;
	}
	
	public void save() throws IOException ,CoreException{
		
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
			OrmCore.getPluginLog().logInfo("XMLFileStorage.save() for" + getName() + ", dirty= " + dirty );
		}
		
		if (!isDirty()) return;
		
		if(resource.getType()==IResource.FILE){
			IFile res = (IFile) resource;
			
			// del tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?
			/*
// added by yk 20.09.2005
			ResourceAttributes resattrib = resource.getResourceAttributes();
			if(resattrib != null && resattrib.isReadOnly())
			{		
				resattrib.setReadOnly(false);
				resource.setResourceAttributes(resattrib);
			}
// added by yk 20.09.2005.

			*/

			
			InputStream input=null;
			setSaveInProgress(true);
			try{
				XMLFileWriter writer=new XMLFileWriter(this,res, hc);
				input=writer.write();
				if(input!=null) {
					//XXX remove after testing:
					//IPath path=res.getProjectRelativePath();
					//path=path.addFileExtension("test");
					//res=hc.getProject().getFile(path);
					//-------------- end test code -----------
	    			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?					
						//if(!res.exists()) res.create(null, IResource.FORCE, null);
					if(!res.exists()) res.create(null, IResource.NONE, null);					
						
					//edit tau 19.01.2006 
					//res.setContents(input, IResource.FILE, null);
	    			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?					
				    //res.setContents(input, IResource.FORCE, null);						
				    res.setContents(input, IResource.NONE, null);				    
				}
			}
			finally{
				if(input!=null) {
                    try{ input.close(); } catch(Exception ex){}
                }
                //added 19.05.2005 by Nick
                timeStamp = refreshTimeStamp();
                //by Nick
				setSaveInProgress(false);
				
				//add tau 28.03.2006
				setDirty(false);
			}
		}
}

	public Map getTypeDefs() {
		return typeDefs;
	}
	
	public void setTypeDefs(Map<String,TypeDef> typeDefs) {
		this.typeDefs = typeDefs;
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
		TypeDef def = new TypeDef(typeClass, paramMap);
		typeDefs.put(typeName, def);
        //add tau 28.03.2006 
        setDirty(true);		
	}

	public TypeDef getTypeDef(String typeName) {
		return (TypeDef) typeDefs.get(typeName);
	}
	
	// added by yk 19.10.2005
	public void addFilterDef(final String fdname, Properties prop)
	{
		FilterDef filterdef = new FilterDef(fdname, prop);
		filterDefinitions.put(fdname, filterdef);
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	public int getFilterDefSize()
	{
		return filterDefinitions.size();
	}
	public FilterDef getFilterDef(final String key)
	{
		if(filterDefinitions.containsKey(key))
			return (FilterDef)filterDefinitions.get(key);
		else
			return null;
	}
	// added by yk 19.10.2005.

	
	public Map<String,NamedQueryDefinition> getQueries() {
		return queries;
	}
	
	public void addQuery(String name, NamedQueryDefinition query) {
		queries.put(name, query);
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	public void removeQuery(String name) {
		queries.remove(name);
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	public void removeQueries() {
		queries.clear();
        //add tau 28.03.2006 
        setDirty(true);		
	}
	
	public NamedQueryDefinition getQuery(String name){
		return (NamedQueryDefinition) queries.get(name);
	}
	
	public String getFullyQualifiedClassName(String unqualifiedName){
		if ( unqualifiedName == null ) return null;
		if(unqualifiedName.indexOf('.') < 0 && defaultPackage!=null && defaultPackage.length() >0 ){
			return defaultPackage+"."+unqualifiedName;
		}
		return unqualifiedName;
	}

    //added 19.05.2005 by Nick
    public boolean resourceChanged() {
		if(isSaveInProgress())return false;
        return (timeStamp != refreshTimeStamp());
    }
    
    // add tau 02.11.2005
    public synchronized boolean isResourceChanged() {
        boolean isChanged = this.resourceChanged();    	
    	if (isChanged) {
    		try {
    	        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
    	        	OrmCore.getPluginLog().logInfo("XMLFileStorage.isResourceChanged()->storage.reload():"+this.getName());
    	        }
    	        
    	        /*
    	        if (this.getProjectMapping() instanceof AbstractMapping) {
        	        ((AbstractMapping)this.getProjectMapping()).r
        	        	classes.clear();
        	        	tables.clear();
        	        	schemas.clear();
        	        	packages.clear(); 
    	        }
    	        */
				//this.reload();
    	        
    	        // remove PersistentClasses
    	        /*
    	        IPersistentClassMapping[] persistentClassMappings = this.getPersistentClassMappings();
    	        IPersistentClass pc = null;
				for (int i = 0; i < persistentClassMappings.length; i++) {
					IPersistentClassMapping mapping = persistentClassMappings[i];
					pc = mapping.getPersistentClass();
					if (pc != null){
		    			this.getProjectMapping().removePersistentClass(pc.getName(),false);
						//pc.getProjectMapping().save();
					}
				}
				*/
    	        
    	        // remove MappingStorage				
				this.getProjectMapping().removeMappingStorage(this);
				
				//  add MappingStorage
				XMLFileStorageDublicate.set(null);
				
				//add tau 13.06.2006 for ESORM-623
				// NPE after mapping editing for ormHibernate3-jsf project
				IFile fileStorage = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(this.getResource().getLocation());
				if (fileStorage == null) {
					IFile[] fileStorages = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(this.getResource().getLocation());
					if (fileStorages != null && fileStorages.length > 0){
						fileStorage = fileStorages[0];
					}
				}
				if (fileStorage != null) {				
					this.getProjectMapping().addMappingStorage(fileStorage);
				}
				// tau
				
				if (XMLFileStorageDublicate.get()!= null){
					/*
					MessageDialog.openError(ViewPlugin.getActiveWorkbenchShell(),
 							BUNDLE.getString("Explorer.XMLFileStorageDublicateTitle"),
 							MessageFormat.format(BUNDLE.getString("Explorer.XMLFileStorageDublicateMessage"),
 									new Object[]{fileForLocation.getName(),XMLFileStorageDublicate.get()}));
					isError = true;
					*/
				} else {
					//isSave = true;
					
					// TODO (tau!!!->tau) 28.03.2006 DoMappingsUpdate() - will TEST
					this.getProjectMapping().refresh(false, true); // edit tau 17.11.2005
					
					// delete tau 30.11.2005
					//this.getProjectMapping().save();	
				}
				
			} catch (CoreException e) {
				OrmCore.getPluginLog().logError("Exception refreshing resources...",e);
			} catch (IOException e) {
				OrmCore.getPluginLog().logError("Exception refreshing resources...",e);
			}
    	}
        return isChanged;
    }    
    
    // 20050726 <yan>
	public INamedQueryMapping[] getNamedQueryMappings() {
		return (NamedQueryDefinition[])queries.values().toArray(QUERIES);
	}

	public void addNamedQueryMapping(INamedQueryMapping mapping) {
		if (mapping instanceof NamedQueryDefinition) addQuery(mapping.getName(),(NamedQueryDefinition)mapping);
	}
	
	public void removeNamedQueryMapping(INamedQueryMapping mapping) {
		if (mapping instanceof NamedQueryDefinition) removeQuery(mapping.getName());
	}
	// </yan>
	
	public String getQualifiedName(TreeItem item) {
		//return (String) accept(QualifiedNameVisitor.visitor,item);		
		// add 02.12.2005
		return StringUtils.parentItemName(item, this.getName());		

	}

	public synchronized boolean isDirty() {
		return dirty;
	}

	public synchronized void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	// add tau 28.03.2006
	public void save(boolean force) throws IOException, CoreException {
		if (force) {
			setDirty(true);
			save();
		}
		
	}
	
}
