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
package org.jboss.tools.hibernate.internal.core.hibernate.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;


public class ConfigurationValidation extends HibernateValidationProblem {
	
	private IMarker marker;
	public static final String STRUCTURE_ERROR = "Loading file error. Probably the file has damaged structure.";
    private IFile resource;
    private HibernateConfiguration configuration;
    
	protected ConfigurationValidation()
	{	super();	}
	
	
	public void validateMapping(IMapping model) {
		IMapping[] mappings = model.getProject().getMappings();
		for(int i = 0; i < mappings.length; i++)
		{
			initAndValidate(mappings[i].getConfiguration(), null);
		}
	}

	public void validateMapping(IMapping model, IResource scope) 
	{
		initAndValidate(model.getConfiguration(), scope);
	}
	private void initAndValidate(IMappingConfiguration config, IResource scope)
	{
		if(init(config, scope))
			validate();
	}
	private boolean init(IMappingConfiguration config, IResource scope)
	{
		boolean successful = false;
		if(config != null && config instanceof HibernateConfiguration)
		{
	    	configuration = (HibernateConfiguration)config;
	    	resource = (IFile)configuration.getResource();
	    	
	    	// add tau 28.10.2005
	    	if (scope != null){
				if (scope.equals(configuration.getResource())){
			    	successful = true;
				} else {	    		
		    		IMappingStorage[] storages = configuration.getMappingStorages();
		    		for (int i = 0; i < storages.length && !successful; i++) {
						IMappingStorage storage = storages[i];
						IResource resourceStorage = storage.getResource();
						if (scope.equals(resourceStorage)){
					    	successful = true;
						}
					}
				}
	    	}
		}
		return successful;
	}
	private void validate()
	{		setMarkers();		}
    // added by yk 29.09.2005
	
    private void setMarkers()
    {
		removeMarkers(resource);
		if(configuration == null) return;
    	if(configuration.getErrorMarkers().size() < 1)
    		return;
    	try{
    		int rowcounter = 0;
    		File file = FileBuffers.getSystemFileAtLocation(resource.getFullPath());
    		BufferedReader reader = new BufferedReader(new FileReader(file));
    		while(reader.ready())
    		{
    			rowcounter++;
    			String line = reader.readLine();
    			Iterator itmarkers = configuration.getErrorMarkers().iterator();
    			while(itmarkers.hasNext())
    			{
    				if(line.indexOf((String)itmarkers.next()) != -1)
    					createMarker(resource, rowcounter);
    			}
    		}
    	}
    	catch(Exception exc)
    	{
    		ExceptionHandler.logInfo(exc.getMessage());
    	}
    }
	public void createMarker(IFile resource, final int line)
	{
		try
		{
			marker = resource.createMarker(HibernateValidationProblem.MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE,ConfigurationValidation.STRUCTURE_ERROR);
			marker.setAttribute("class",this.getClass().getName());
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.LINE_NUMBER,line);
		}
		catch(CoreException exc)
		{
			ExceptionHandler.logInfo(this.getClass().getName() + " createMarker error.");
		}
	}
	
	public void removeMarkers(IFile file)
	{
		try{
			if(file == null) return;
			file.deleteMarkers(HibernateValidationProblem.MARKER_TYPE, false, IResource.DEPTH_ZERO);
		}
		catch(CoreException exc)
		{
			ExceptionHandler.logInfo(this.getClass().getName() + " delete markers error.");
		}
	}

}
