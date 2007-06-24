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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.hibernate.core.IAutoMappingService;
import org.jboss.tools.hibernate.core.IDAOGenerator;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingProperties;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IValidationService;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.AbstractMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmProject;
import org.jboss.tools.hibernate.internal.core.OrmPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.codegenerator.DAOGenerator;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationService;

/**
 * @author alex
 *
 * A collection of mappings from classes and collections to
 * relational database tables.
 */
public class HibernateMapping extends AbstractMapping {
	private static final long serialVersionUID = 1L;
	private HibernateConfiguration config;
	private IMappingProperties properties;
	private HibernateAutoMapping autoMapping;
	private HibernateValidationService validationService;
	private DAOGenerator codeGenerator;
	
	public HibernateMapping(OrmProject ormProject, IFile configResource) {
		super(ormProject);
		//TODO EXP 3d
		this.config=new HibernateConfiguration(this, configResource);
		this.properties=new HibernateProperties(ormProject.getProject());		
		
		/* tau ->  move in metod get...();
		this.properties=new HibernateProperties(model.getProject());
		//changed by Nick 4.04.2005 to provide HibernateAutoMapping with valid HibernateConfiguration
		//
		//was:
		//this.autoMapping=new HibernateAutoMapping(this);
		//this.config=new HibernateConfiguration(this, configResource);
		this.config=new HibernateConfiguration(this, configResource);
		this.autoMapping=new HibernateAutoMapping(this);
		//by Nick
		this.codeGenerator=new DAOGenerator(this);
		this.validationService=new HibernateValidationService(this);
		*/
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#moveTo(java.lang.String)
	 */
	
//	public void moveTo(IPath newPath) throws CoreException {
//		
//        
//		IFile resource=(IFile)config.getResource();
//		if(resource.getFullPath().equals(newPath)) return;
//		if(!resource.exists()) resource.create(null, IResource.NONE, null);
//		resource.move(newPath, true,null);
//	}
	public void moveTo(IPath newPath) throws CoreException {
		IFile rsrc;
		 InputStream input=null;
		HibernateConfigurationWriter writer;
        
		IFile resource=(IFile)config.getResource();
		if(resource.getFullPath().equals(newPath)) return;
		else{ 
			rsrc = config.getProject().getFile(newPath.removeFirstSegments(1));
			try{
			 writer=new HibernateConfigurationWriter(config,rsrc);
			try{
			 input=writer.write(false); // edit tau 14.02.2006 - or writer.write(true)? 
			}catch (IOException e1) {
				ExceptionHandler.logThrowableError(e1,"Hibernate Connection  Wizard:Finish ->"+e1.toString());
				
		 	}
			
			if(!rsrc.exists()) rsrc.create(null, IResource.NONE, null); //add  gavrs 4.03.05
			rsrc.setContents(input, IResource.NONE, null);			 
			 
			 	// edit tau 18.01.2006
    			// edit tau 10.02.2006 for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?				 
			 	// rsrc.setContents(input, IResource.FILE, null);
			 	//rsrc.create(null, IResource.FORCE, null);			 
			 	//rsrc.setContents(input, IResource.FORCE, null);
				 
			 	
			 	//resource.delete(false,null);
			 	config.getResource().delete(false,null);
			 
			 	this.config=new HibernateConfiguration(this, rsrc);
 
			}
			 finally{
		           
		            if(input!=null)
						try {
							input.close();
						} catch (IOException e) {
							ExceptionHandler.logThrowableError(e,"Hibernate Connection  Wizard:Finish ->"+e.toString());
						}
		        }
		//}
		}
		
		//if(!resource.exists()) resource.create(null, IResource.NONE, null);
		//resource.move(newPath, true,null);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#getName()
	 */
	public String getName() {
		return config.getPath().toString();
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getPersistentClassMappings()
	 */
	public IPersistentClassMapping[] getPersistentClassMappings() {
		return config.getPersistentClassMappings();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getConfiguration()
	 */
	public IMappingConfiguration getConfiguration() {
		/* del tau 27.02.2006
		//TODO EXP 5d
		// 27.02.2006
		if (!config.isLoad()) {
			try {
				config.reload();
			} catch (Exception e) {
	            ExceptionHandler.logThrowableError(e,null);
			}
		}
		*/
		
		return config;
	}

		/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getProperties()
	 */
	public IMappingProperties getProperties() {
		//akuzmin 21.06.2005
		if (properties.getResource()!=null)
		{
		IPath filePath = properties.getResource().getLocation();
		if (!filePath.toFile().exists())
		{
			this.properties=new HibernateProperties(config.getOrmProject().getProject());
		}
		}
		((OrmConfiguration)config.getOrmProject().getOrmConfiguration()).setPropertyDescriptorsHolder(OrmPropertyDescriptorsHolder.getInstance(config.getOrmProject()));

		return properties;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getMappingStorages()
	 */
	public IMappingStorage[] getMappingStorages() {
		return config.getMappingStorages();
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitMapping(this,argument);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getAutoMappingService()
	 */
	public IAutoMappingService getAutoMappingService() {
		//TODO EXP 3d
		// 23.02.2006
		if (autoMapping == null) {
			autoMapping = new HibernateAutoMapping(this);			
		}
		return autoMapping;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getValidationService()
	 */
	public IValidationService getValidationService() {
		//TODO EXP 3d
		// 23.02.2006
		if (validationService == null) {
			validationService = new HibernateValidationService(this);
		}
		return validationService;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getMappingForPersistentClass(org.jboss.tools.hibernate.core.IPersistentClass)
	 */
	public IPersistentClassMapping getMappingForPersistentClass(IPersistentClass clazz, IMappingStorage storage) throws CoreException {
		if(clazz.getPersistentClassMapping()!=null) return clazz.getPersistentClassMapping();
		return config.getOrCreateClassMapping (clazz, null, storage);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#replaceMappingForPersistentClass(org.jboss.tools.hibernate.core.IPersistentClass, java.lang.String)
	 */
	public IPersistentClassMapping replaceMappingForPersistentClass(IPersistentClass clazz, String newMappingType) throws CoreException {
		return config.replaceMappingForPersistentClass(clazz, newMappingType);
	}

	public void removeMapping(IPersistentClass pc)throws CoreException{
		removeMapping(pc,true);
	}
	
	public void removeMapping(IPersistentClass pc,boolean clearStorage)throws CoreException{
		if(config!=null){
			config.removeClassMapping(pc,clearStorage);
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#addMappingStorage(org.eclipse.core.runtime.IPath)
	 */
	// #changed# by Konstantin Mishin on 10.09.2005 fixed for ORMIISTUD-660
	//public IMappingStorage addMappingStorage(IPath path) throws IOException, CoreException {
	public IMappingStorage addMappingStorage(IFile path) throws IOException, CoreException {
	// #changed#
		return config.addMappingStorage(path);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#getMappingStorage(java.lang.String)
	 */
	public IMappingStorage getMappingStorage(String id) {
		return config.getMappingStorage(id);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IMapping#removeMappingStorage(java.lang.String)
	 */
	public void removeMappingStorage(IMappingStorage storage) throws IOException, CoreException {
		IPersistentClassMapping pcm[]=storage.getPersistentClassMappings();
		for(int i=0;i<pcm.length;++i) this.removePersistentClass(pcm[i].getName());
		config.removeMappingStorage(storage);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.AbstractMapping#loadMappings()
	 */
	protected void loadMappings() {
		try{ 
			properties.reload();
		} catch (Exception ex){
			ExceptionHandler.logThrowableError(ex, "loading mapping properties");
		}
		try{ 
			config.reload();
		} catch (Exception ex){
			// edit tau 30.09.2005
			ExceptionHandler.logThrowableWarning(ex, "Error in other to loading mapping configuration");
		}
	}

    //added By Nick 19.05.2005
    public void resourcesChanged()
    {
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged() START");
        
        if (properties != null && properties.getResource() != null && properties.getResource().isLocal(IResource.DEPTH_ZERO) &&  properties.resourceChanged())
            try {
                if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged(),properties.reload():"+properties);            	
                properties.reload();
                if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged(),properties.reload() END");                
            } catch (Exception e) {
                ExceptionHandler.logThrowableError(e,"Exception refreshing resources...");
            }
            
            if (config != null)
            {
                if (config.getResource().isLocal(IResource.DEPTH_ZERO) && config.resourceChanged()){
                    try {
                        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged(),config.reload(): " + config);
                        config.reload();
                        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged(),config.reload() END" + config);                        
                        refresh(true, true); // do doMappingsUpdate - add tau 17.11.2005
                        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged(),refresh(true) END" + config);                        
                    } catch (Exception e1) {
                        ExceptionHandler.logThrowableError(e1,"Exception refreshing resources...");
                    }
                }
                if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged(),config.resourcesChanged(): " + config);                
				config.resourcesChanged();
            }
            if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("HibernateMapping.resourcesChanged() END");            
    }
    //by Nick

	/**
	 * @return Returns the codeGenerator.
	 */
	public IDAOGenerator getDAOGenerator() {
		//TODO EXP 3d
		// 23.02.2006
		if (codeGenerator == null) {
			codeGenerator = new DAOGenerator(this);				
		}
		return codeGenerator;
	}

}
