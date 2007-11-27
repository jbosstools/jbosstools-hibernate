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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.hibernate.core.CodeRendererService;
import org.jboss.tools.hibernate.core.IAutoMappingService;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;


public class SourceCodeValidation extends HibernateValidationProblem {

	private final int NO_ERROR = 0;	
	private final int NO_SOURCE = 1;	
	private final int NO_MAPPING = 2;	
	private final int NO_FIELD = 3;	
	
	protected SourceCodeValidation(){
		super();
	}
    
    // added by Nick 22.09.2005
    protected void validatePersistentClassMapping(IMapping iMapping, IPersistentClassMapping mapping, String validationPath)
    {
        try{
            
            if (validationPath != null)
                HibernateValidationProblem.deleteMarkers(mapping.getStorage().getResource(),validationPath,this.getClass());

            if (mapping.getPersistentClass().getSourceCode() == null // added by Nick 22.09.2005
                        || !mapping.getPersistentClass().getSourceCode().exists()){
                    this.createMarker(mapping.getStorage().getResource(),
                            getErrorMessage(NO_SOURCE,mapping.getPersistentClass().getName()),
                            IMarker.SEVERITY_ERROR,
                            String.valueOf(NO_SOURCE),
                            mapping.getName(),
                            iMapping,
                            mapping.getStorage());
            }else{
            	
            	// edit tau 10.03.2006
            	IType persistentClassType = mapping.getPersistentClass().getType();
            
                if (validationPath != null && persistentClassType != null && persistentClassType.getResource() != null)
                    HibernateValidationProblem.deleteMarkers(persistentClassType.getResource(),
                            validationPath,
                            this.getClass());
                
                String entity=checkFields(mapping.getPersistentClass());
                if(entity!=null){
                    this.createMarker(/*mappings[i].getPersistentClass().getSourceCode().getResource(),*/
                            /* by Nick 28.06.2005 */persistentClassType,
                            getErrorMessage(NO_MAPPING,entity),
                            IMarker.SEVERITY_WARNING,
                            String.valueOf(NO_MAPPING),
                            mapping.getName(),
                            iMapping,
                            mapping.getStorage());
                    
                }
                entity=checkMappings(mapping.getPersistentClass());
                if(entity!=null){
                    this.createMarker(/*mappings[i].getPersistentClass().getSourceCode().getResource(),*/
                            /* by Nick 28.06.2005 */persistentClassType,
                            getErrorMessage(NO_FIELD,mapping.getName() + "." + entity),
                            IMarker.SEVERITY_ERROR,
                            String.valueOf(NO_FIELD),
                            entity,
                            iMapping,
                            mapping.getStorage());
                    
                }
            }

        } catch(Exception e){
            ExceptionHandler.logThrowableWarning(e,"SourceCodeValidation for "+mapping.getName()); // changed by Nick 29.09.2005
        }
    }
    // by Nick
    
	public void validateMapping(IMapping mapping) {
		IPersistentClassMapping[] mappings=mapping.getPersistentClassMappings();
		for(int i=0;i<mappings.length;++i){
		    // changed by Nick 22.09.2005 - code moved to validatePersistentClassMapping()
		    validatePersistentClassMapping(mapping, mappings[i], null);
        }
	}
	private String getErrorMessage(int errorID, String entityName) {
		switch (errorID) {
		case NO_SOURCE:
			return MessageFormat.format(BUNDLE.getString("PERSISTENT_CLASS_NO_SOURCE_ERROR"),new Object[]{entityName});
		case NO_MAPPING:
			return MessageFormat.format(BUNDLE.getString("PERSISTENT_CLASS_NO_MAPPING_ERROR"),new Object[]{entityName});
		case NO_FIELD:
			return MessageFormat.format(BUNDLE.getString("PERSISTENT_CLASS_NO_FIELD_ERROR"),new Object[]{entityName});
		default:
			return "";
		}

	}
	private String checkFields(IPersistentClass clazz) throws CoreException
	{
		IPersistentField[] fields=clazz.getFields();
		String entity=null;
		for(int i=0;i<fields.length;++i){
			if(fields[i].getMapping()==null || fields[i].getMapping().getPersistentValueMapping()==null){
				if(fields[i].getType()!=null && 
                        /* added by Nick 11.07.2005 */ (fields[i].getAccessorMask() & PersistentField.ACCESSOR_PROPERTY) != 0 /*ORMIISTUD-415*/){ //no mapping and no source code
					if(entity==null)entity= clazz.getName() + "." + fields[i].getName();
				}
			}
		}
	    return entity;
	}
	private String checkMappings(IPersistentClass clazz) throws CoreException
	{
        Iterator it=clazz.getPersistentClassMapping().getFieldMappingIterator();
		while(it.hasNext()){
			IPersistentFieldMapping pfm=(IPersistentFieldMapping)it.next();
			if(pfm.getPersistentField()==null || pfm.getPersistentField().getType()==null)
				return pfm.getName();
		}
	    return null;
	}
	class SourceCodeResolution implements IMarkerResolution2
	{
	    private IMarker marker;
	    private String description;
	    private String label;
	    
        public void setLabel(String label) {
            this.label = label;
        }

        public void setMarker(IMarker marker)
	    {
	        this.marker = marker;
	    }
	    
		public String getLabel() {
			return label;
		}

		public void run(IMarker marker){
		    IResource resource = marker.getResource();
		    try {
		        int errorId = Integer.decode((String) marker.getAttribute("id")).intValue();
				String entity=marker.getAttribute("entity", null);
				String mapId = marker.getAttribute("mapping", null);
		        switch (errorId)
		        {
	        		case (NO_SOURCE):
		        	{
						//XXX remove the persistent class from mapping
						if(entity!=null && mapId!=null){
							IOrmProject project=OrmCore.getDefault().create(resource.getProject());
							IMapping mapping=project.getMapping(mapId);
							mapping.removePersistentClass(entity);
							try{
								mapping.save();
							} catch(Exception ex){
								ExceptionHandler.logThrowableError(ex,"save for "+entity);
							}
					        // TODO (tau->tau) del? 27.01.2006							
							//project.fireProjectChanged(this, false);
						}
		        	}
		        	break;
		        	case (NO_MAPPING):
		        	{
						//Run auto mapping
						if(entity!=null && mapId!=null){
							IOrmProject project=OrmCore.getDefault().create(resource.getProject());
							IMapping mapping=project.getMapping(mapId);
							if(mapping==null) return;
							IPersistentClass pc=mapping.findClass(entity);
							if(pc==null) return;
							IAutoMappingService.Settings settings=new IAutoMappingService.Settings();
							settings.canChangeTables=true;
							mapping.getAutoMappingService().generateMapping(new IPersistentClass[]{pc},settings);
							try{
								mapping.save();
							} catch(Exception ex){
								ExceptionHandler.logThrowableError(ex,"save for "+entity);
							}
					        // TODO (tau->tau) del? 27.01.2006							
							//project.fireProjectChanged(this, false);
						}
		        	}
		        	break;
		        	case (NO_FIELD):
		        	{
						if(entity!=null && mapId!=null){
							IOrmProject project=OrmCore.getDefault().create(resource.getProject());
							IMapping mapping=project.getMapping(mapId);
							if(mapping==null) return;
							
					        ICompilationUnit unit = ScanProject.findCUByResource(resource);
					        ICompilationUnit wc = null;
                            
                            try {
                                if (!unit.isWorkingCopy())
                                    wc = unit.getWorkingCopy(null);
                                
                                IType type = unit.findPrimaryType();
                                
                                IPersistentClass pc=mapping.findClass(type.getFullyQualifiedName());
                                if(pc==null) return;
                                IPersistentField pf=pc.getField(entity);
                                if(pf!=null){
                                    String jType=mapping.getAutoMappingService().getJavaType(pf);
                                    new CodeRendererServiceWrapper(new CodeRendererService()).createProperty(entity, jType ,type);
                                }

                            } catch (JavaModelException e) {
                                ExceptionHandler.logThrowableError(e,e.getMessage());
                            } finally {
                                if (wc != null)
                                    wc.discardWorkingCopy();
                            }
						}
						
		        	}
		        	break;
		        	
		        }
                // changed by Nick 30.06.2005 - if resolution changes file, marker becomes invalid
                //marker.setAttribute(IMarker.DONE,true);
                // by Nick
            } catch (CoreException e) {
				ExceptionHandler.logThrowableError(e,null);
            }
		}

		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description)
		{
		    this.description = description;
		}

		public Image getImage() {
			String iconPath = "icons/";
			try {
				URL installURL = OrmCore.getDefault().getDescriptor().getInstallURL();
				URL url = new URL(installURL, iconPath + "error_tsk.gif");
				return ImageDescriptor.createFromURL(url).createImage();
			} catch (MalformedURLException e) {
				// should not happen
				return ImageDescriptor.getMissingImageDescriptor().createImage();
			}  
		}
	}
    //by Nick
	
	public IMarkerResolution[] getResolutions(IMarker marker) throws CoreException{
	    int errorId = Integer.decode((String) marker.getAttribute("id")).intValue();
		SourceCodeResolution resolutions = new SourceCodeResolution();
        switch (errorId)
        {
        	
	    	case (NO_SOURCE):
	    	{
	    		resolutions.setDescription(BUNDLE.getString("PERSISTENT_CLASS_DELETE"));
	    		resolutions.setLabel(BUNDLE.getString("PERSISTENT_CLASS_DELETE"));
	    	}
	    	break;
			
        	case (NO_MAPPING):
        	{
        		resolutions.setDescription(BUNDLE.getString("PERSISTENT_CLASS_AUTOMAPPING"));
        		resolutions.setLabel(BUNDLE.getString("PERSISTENT_CLASS_AUTOMAPPING"));
        	}
        	break;

        	case (NO_FIELD):
        	{
        		resolutions.setDescription(BUNDLE.getString("PERSISTENT_CLASS_CREATE_FIELDS"));
        		resolutions.setLabel(BUNDLE.getString("PERSISTENT_CLASS_CREATE_FIELDS"));
        	}
        	break;
        	
        }
		return new IMarkerResolution[] {resolutions};
	}
    
    // added by Nick 22.09.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationProblem#validateMapping(org.jboss.tools.hibernate.core.IMapping, org.eclipse.core.resources.IResource)
     */
    public void validateMapping(IMapping model, IResource scope) {
        if (scope == null)
            return;
        
        try
        {
            IPersistentClassMapping[] mappings = model.getPersistentClassMappings();
            for (int i = 0; i < mappings.length; i++) {
                IPersistentClassMapping mapping = mappings[i];
                
                if (mapping.getPersistentClass() != null)
                {
                    ICompilationUnit unit = mapping.getPersistentClass().getSourceCode();
                    
                    try {
                        if ( (unit != null && scope.equals(unit.getUnderlyingResource())) ||
                                scope.equals(mapping.getStorage().getResource()) )
                        {
                            //redefine to obtain functionality needed
                            validatePersistentClassMapping(model, mapping, scope.getFullPath().toString());
                        }
                        
                        if (unit == null || !unit.exists())
                        {
                            
                        }
                        
                    } catch (JavaModelException e) {
                        ExceptionHandler.logThrowableWarning(e,e.getMessage());
                    }
                }
            }
        }
        finally
        {
            HibernateValidationProblem.clearSingleResourceClearSet();
        }
    } 	
    // by Nick
}
