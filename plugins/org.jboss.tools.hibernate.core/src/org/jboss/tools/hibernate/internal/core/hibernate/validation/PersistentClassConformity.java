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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.hibernate.core.CodeRendererService;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.TypeAnalyzer;


/**
 * @author alex
 *
 * Check persistent classes on conformity with Hibernate.
 * Possible quick fixes: 
 * -Remove persistent class (not source code!), 
 * -Remove all non-conformant persistent classes
 * -Add required methods/constructors
 * -Add required methods/constructors to all non-conformant classes 
 */
public class PersistentClassConformity extends HibernateValidationProblem {
	
	// add tau 30.03.2005
	private final int NO_ERROR = 0;	
	private final int ENUM_ERROR = 2;
	private final int ANNOTATION_ERROR = 3;
	private final int DEFAULT_CONSTRUCTOR_ERROR = 4;
	// #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78
	private final int NOT_IMPLEMENT_SERIALIZABLE_ERROR = 5;
	private final int NOT_REIMPLEMENT_EQUALS_WARNING = 6;
	private final int NOT_REIMPLEMENT_HASHCODE_WARNING = 7;
	// #added#
	//private ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassConformity.class.getPackage().getName() + ".validation");	

	protected PersistentClassConformity(){
		super();
	}
    
    // added by Nick 22.09.2005
	protected void validatePersistentClassMapping(IMapping model, IPersistentClassMapping mapping, String validationPath)
    {
        if (validationPath != null)
        {
            IType type = mapping.getPersistentClass().getType();
            if (type != null && type.getResource() != null)
            {
                HibernateValidationProblem.deleteMarkers(type.getResource(),validationPath,this.getClass());
            }
        }
        
        try{
            // tau 30.03.2005
            int errorID = checkConformity(mapping.getPersistentClass()); 
            if ( errorID != 0) {
//              if (mappings[i].getPersistentClass().getSourceCode() != null){
//                  this.createMarker(mappings[i].getPersistentClass().getSourceCode().getResource(),
//                          getErrorMessage(errorID),
//                          IMarker.SEVERITY_ERROR,
//                          String.valueOf(errorID),
//                          mappings[i].getName());
//              }
                
                IType type = mapping.getPersistentClass().getType();
                if (type != null)
                {
                    this.createMarker(type,getErrorMessage(errorID),
                          IMarker.SEVERITY_ERROR,
                          String.valueOf(errorID),
                          mapping.getName(),
                          null,
                          mapping.getStorage());
                }
                
            }
            // #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78
            if (mapping.getIdentifier() instanceof ComponentMapping) {
            	IPersistentClass persistentClass = ((ComponentMapping)mapping.getIdentifier()).getComponentClass();
            	if(persistentClass != null) {
            		IType type = persistentClass.getType();
            		if(type!= null){
            			if (!ClassUtils.isImplementing(type,"java.io.Serializable"))
            				this.createMarker(type,getErrorMessage(NOT_IMPLEMENT_SERIALIZABLE_ERROR),
            						IMarker.SEVERITY_ERROR,
            						String.valueOf(NOT_IMPLEMENT_SERIALIZABLE_ERROR),
            						mapping.getName(),
            						null,
            						mapping.getStorage());
            			TypeAnalyzer typeAnalyzer = new TypeAnalyzer(type);
            			if (!typeAnalyzer.hasEqualsMethod())
            				this.createMarker(type,getErrorMessage(NOT_REIMPLEMENT_EQUALS_WARNING),
            						IMarker.SEVERITY_WARNING,
            						String.valueOf(NOT_REIMPLEMENT_EQUALS_WARNING),
            						mapping.getName(),
            						null,
            						mapping.getStorage());
            			if (!typeAnalyzer.hasHashCodeMethod())
            				this.createMarker(type,getErrorMessage(NOT_REIMPLEMENT_HASHCODE_WARNING),
            						IMarker.SEVERITY_WARNING,
            						String.valueOf(NOT_REIMPLEMENT_HASHCODE_WARNING),
            						mapping.getName(),
            						null,
            						mapping.getStorage());
            		}
            	}
            }
            // #added#
        } catch(Exception e){
        	ExceptionHandler.logThrowableWarning(e,"validateMapping for "+mapping.getName()); // changed by Nick 29.09.2005
        }
    }
	// by Nick
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationProblem#validateMapping(org.jboss.tools.hibernate.core.IMapping)
	 */
	public void validateMapping(IMapping mapping) {
		IPersistentClassMapping[] mappings=mapping.getPersistentClassMappings();
		for(int i=0;i<mappings.length;++i){
		    // changed by Nick 22.09.2005 - code moved to validatePersistentClassMapping()
            validatePersistentClassMapping(mapping, mappings[i], null);
        }
	}
	/**
	 * @param errorID
	 * @return
	 */
	private String getErrorMessage(int errorID) {
		switch (errorID) {
		case NO_ERROR:
			return "";
		case ENUM_ERROR:
			return BUNDLE.getString("PERSISTENT_CLASS_ENUM_ERROR");
		case ANNOTATION_ERROR:
			return BUNDLE.getString("PERSISTENT_CLASS_ANNOTATION_ERROR");
		case DEFAULT_CONSTRUCTOR_ERROR:
			return BUNDLE.getString("PERSISTENT_CLASS_DEFAULT_CONSTRUCTOR_ERROR");
		// #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78	
		case NOT_IMPLEMENT_SERIALIZABLE_ERROR:
			return BUNDLE.getString("NOT_IMPLEMENT_SERIALIZABLE_ERROR");
		case NOT_REIMPLEMENT_EQUALS_WARNING:
			return BUNDLE.getString("NOT_REIMPLEMENT_EQUALS_WARNING");
		case NOT_REIMPLEMENT_HASHCODE_WARNING:
			return BUNDLE.getString("NOT_REIMPLEMENT_HASHCODE_WARNING");
		// #added#
		default:
			return "";
		}

	}
	/**
	 * Return true if given class conforms with Hibernate specification, false otherwise. 
	 * */
	// modify by tau 30.03.2005
	public int checkConformity(IPersistentClass clazz) throws CoreException
	{
        //types array contains all classes from our unit
        //main class is always the first
        //nested classes follow

	    // added by Nick 21.09.2005
        if (clazz == null)
            return NO_ERROR;
        // by Nick    
            
        IType classRoot = clazz.getType();
        if (classRoot == null)
            return NO_ERROR; //no source code
        

        if (classRoot == null)
            return NO_ERROR;
        
	    if (classRoot.isEnum())
	        return ENUM_ERROR;
	    if (classRoot.isAnnotation())
	        return ANNOTATION_ERROR;
	    
	    //are there any default constructors?    
	    IMethod[] ctors = classRoot.findMethods(classRoot.getMethod(classRoot.getElementName(),new String[]{}));

        TypeAnalyzer ta = new TypeAnalyzer(classRoot);
	    if ( ta.hasRedefinedConstructor() && (ctors == null || ctors.length == 0) )
	        return DEFAULT_CONSTRUCTOR_ERROR;
	    return NO_ERROR;
	}
	
	//by Nick
	class PersistentClassConformityResolution implements IMarkerResolution2
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
		        switch (errorId)
		        {
		        	case (DEFAULT_CONSTRUCTOR_ERROR):
		        	{
				        ICompilationUnit unit = ScanProject.findCUByResource(resource);
				        String className = (String)marker.getAttribute("entity");
                        if (className != null)
                        {
                            IType type = ScanProject.findClassInCU(unit,
                                    Signature.getSimpleName(className));
                            new CodeRendererServiceWrapper(new CodeRendererService()).createConstructor(type);
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
	    PersistentClassConformityResolution resolutions = new PersistentClassConformityResolution();
        switch (errorId)
        {
        	case (DEFAULT_CONSTRUCTOR_ERROR):
        	{
        	    String dotsStr = ClassUtils.formatLine("...",ClassUtils.BODY_HTML);
        	    ICompilationUnit unit = ScanProject.findCUByResource(marker.getResource());
                ICompilationUnit wc = null;
                try {
                
                    if (!unit.isWorkingCopy())
                        wc = unit.getWorkingCopy(null);
                    
                    IType type = unit.findPrimaryType();
                    resolutions.setDescription(dotsStr + ClassUtils.generateDefaultCtorBody(type,null,null,ClassUtils.GDC_PUBLIC,ClassUtils.BODY_HTML) + dotsStr);
                    resolutions.setLabel(BUNDLE.getString("PERSISTENT_CLASS_DEFAULT_CONSTRUCTOR_RESOLUTION"));

                } catch (JavaModelException e) {
                    ExceptionHandler.logThrowableError(e,e.getMessage());
                } finally {
                    if (wc != null)
                        wc.discardWorkingCopy();
                }
                
        	}
        	break;
        		
        	case (ENUM_ERROR):
        	{
        		resolutions.setDescription(BUNDLE.getString("PERSISTENT_ENUM_DELETE"));
        		resolutions.setLabel(BUNDLE.getString("PERSISTENT_ENUM_DELETE"));
        	}
        	break;

        	case (ANNOTATION_ERROR):
        	{
        		resolutions.setDescription(BUNDLE.getString("PERSISTENT_ANNOTATION_DELETE"));
        		resolutions.setLabel(BUNDLE.getString("PERSISTENT_ANNOTATION_DELETE"));
        	}
        	break;
        	// #added# by Konstantin Mishin on 30.09.2005 fixed for ESORM-78
        	default:
        	{
        		return new IMarkerResolution[] {};       		
        	}
        	// #added#
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
                        if (unit != null && scope.equals(unit.getUnderlyingResource()))
                        {
                            //redefine to obtain functionality needed
                            validatePersistentClassMapping(model, mapping, scope.getFullPath().toString());
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
