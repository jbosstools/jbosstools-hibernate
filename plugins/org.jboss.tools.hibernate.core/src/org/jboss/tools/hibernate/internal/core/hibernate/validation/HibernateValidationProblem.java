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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.internal.core.util.RuleUtils;


/**
 * @author alex
 *
 * Base class for Hibernate validation problems. Derived classes provide
 * problem detection and quick fixes
 * 
 * XXX put into plugin.xml:
 * do Tau 25.03.2005
 * 
   <extension point="org.eclipse.ui.ide.markerResolution">
      <markerResolutionGenerator
         markerType="org.jboss.tools.hibernate.core.HibernateMappingProblem"
         class="org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateResolutionGenerator"/>
   </extension>
   
   <extension
      point="org.eclipse.core.resources.markers"   
      id="HibernateMappingProblem"
      name="Hibernate mapping problem">
      <super type="org.eclipse.core.resources.problemmarker"/>
      <attribute name="class" />
      <attribute name="id" />
      <persistent value="true" />
   </extension>
   
 */
public abstract class HibernateValidationProblem  {
	private static final IMarkerResolution[] RESOLUTIONS={};
	private static final Map problems=new HashMap();
	public static final String MARKER_TYPE="org.jboss.tools.hibernate.core.hibernateMappingProblem"; // add 25.03.2005
	
	public static final PersistentClassConformity classConformity=new PersistentClassConformity();
	public static final SourceCodeValidation sourceCodeValidation=new SourceCodeValidation();
	public static final MappingValidation mappingValidation=new MappingValidation();
	
	// added by yk 30.09.2005
	public static final ConfigurationValidation cfgValidation = new ConfigurationValidation();
	// added by yk 30.09.2005.

	public static ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateValidationProblem.class.getPackage().getName() + ".validation");	
	
	protected HibernateValidationProblem(){
		problems.put(this.getClass().getName(), this);
	}
	
	public static Iterator getProblemIterator(){
		return problems.values().iterator();
	}
	
	public static HibernateValidationProblem getProblem(String problemClass){
		return (HibernateValidationProblem)problems.get(problemClass);
	}
	
	public static IMarkerResolution[] getMarkerResolutions(IMarker marker){
		try{
			String problemClass=(String)marker.getAttribute("class");
			if(problemClass!=null){
				HibernateValidationProblem problem=(HibernateValidationProblem)problems.get(problemClass);
				if(problem!=null){
					return problem.getResolutions(marker);
				}
			}
		} catch(CoreException cex){
			ExceptionHandler.logThrowableWarning(cex,"Exception occured in getMarkerResolutions"); // changed by Nick 29.09.2005
		}
		return RESOLUTIONS;
	}
	
	/**
	 * Derived classes should override the method to return own marker resolutions.
	 * */
	public IMarkerResolution[] getResolutions(IMarker marker) throws CoreException{
        return RESOLUTIONS;
	}
	
	public IMarker createMarker(IResource resource, String message, int severity, String id, String entity) throws CoreException {
		return createMarker(resource, message, severity,  id,  entity, null, null);
	}
	
    // added by Nick 28.06.2005
	private Document getDocument(IResource resource, int lineNumber, boolean isOffset) throws CoreException
    {
		// added by yk 14.09.2005
		Document document = null;
		// added by yk 14.09.2005.

	    if (resource != null && resource instanceof IFile && resource.isAccessible()) {
	        IFile file = (IFile) resource;
	        InputStream isf = file.getContents();
	        StringWriter sw = new StringWriter();

            int length = 0;
            int delims_count = 0;
            boolean allRead = false;
            
            try {
	            
	            int next_char = 0;
	            
	            while ((next_char = isf.read()) != -1 && !allRead) {
	                sw.write(next_char);
	                length++;
                    if (next_char == '\r' || next_char == '\n')
                    {
                        delims_count++;
                    }
                    
                    if (isOffset)
                    {
                        allRead = length >= lineNumber + 1;
                    }
                    else
                    {
                        if (lineNumber <= delims_count)
                        {
                            allRead = (lineNumber + 1 <= new Document(sw.toString()).getNumberOfLines());
                        }
                    }
                }
	            /* rem by yk 14.09.2005 isf.close();*/
	            
	        } catch (IOException e) {
	        	return document;
	            /* rem by yk 14.09.2005 throw new NestableRuntimeException(e); */
	        }
// added by yk 14.09.2005
	        finally
	        {		try{ if(isf != null){isf.close();} } 		catch(IOException exc){};		}
// added by yk 14.09.2005.
	        
	        document = new Document(sw.toString());
            return document;
	    }
	    else
	        return document;
    }
    
    public IMarker createMarker(IMember member, final String message, final int severity, final String id, final String entity, final IMapping mapping, final IMappingStorage storage) throws CoreException
    {
	    if (member == null)
            return null;
        
        IMarker marker = null;
        
        ISourceRange range = member.getNameRange();
        if (member.getResource() != null)
        {
            IResource resource = member.getResource();

            if (range != null)
            {
                Document document = getDocument(resource,range.getOffset(),true);
                
                if (document != null)
                {
                    try {
                        int lineNumber = document.getLineOfOffset(range.getOffset());
                        
                        marker = internalCreateMarker(resource,
                                this.getClass().getName(),
                                message,
                                severity,
                                id,
                                entity,
                                mapping,
                                storage,
                                lineNumber,
                                range.getOffset(),
                                range.getLength());
                    } catch (CoreException e) {
                        throw new NestableRuntimeException(e);
                    } catch (BadLocationException e) {
                        throw new NestableRuntimeException(e);
                    }
                }
            }
            else
            {
                marker = createMarker(resource, message, severity, id, entity, mapping, storage);
            }
        }
        return marker;
    }
    
    private IMarker internalCreateMarker(final IResource resource, final String className, final String message, final int severity, final String id, final String entity, final IMapping mapping, final IMappingStorage storage, final int lineNumber, final int offset, final int length) throws CoreException
    {
        return internalCreateMarker(resource,className,message,severity,id,entity,mapping,storage,lineNumber,offset,length,null);
    }
    
    private IMarker internalCreateMarker(final IResource resource, final String className, final String message, final int severity, final String id, final String entity, final IMapping mapping, final IMappingStorage storage, final int lineNumber, final int offset, final int length, final IResource validatingResource) throws CoreException
    {
        IMarker marker = null;

        // IWorkspaceRunnable add tau 31.05.2005
        class MarkerRunnable implements IWorkspaceRunnable{
            private IMarker marker;
            public void run(IProgressMonitor monitor) throws CoreException {
                marker=resource.createMarker(MARKER_TYPE);
                marker.setAttribute(IMarker.MESSAGE,message);
                marker.setAttribute("class", className);
                marker.setAttribute(IMarker.SEVERITY, severity);
                
                if (offset != -1)
                {
                    marker.setAttribute(IMarker.CHAR_START,offset);
                    marker.setAttribute(IMarker.CHAR_END,offset + length);
                }
                
                if(id!=null)marker.setAttribute("id", id);
                if(entity!=null) marker.setAttribute("entity", entity);
                if(mapping!=null) marker.setAttribute("mapping", mapping.getName());
                if(storage!=null) marker.setAttribute("storage",storage.getName());
                // add tau 29.03.2005
                marker.setAttribute(IMarker.LINE_NUMBER,lineNumber);
            
                // added by Nick 23.09.2005
                if (validatingResource != null && validatingResource.exists())
                {
                    marker.setAttribute("marker.source.path",validatingResource.getFullPath().toString());
                }
                else if (storage != null && storage.getResource() != null && storage.getResource().exists())
                {
                    marker.setAttribute("marker.source.path",storage.getResource().getFullPath().toString());
                }
                    
                // by Nick
            }
            IMarker getMarker()
            {
                return marker;
            }
        }
        
        MarkerRunnable runnable = new MarkerRunnable();
        
        // edit tau 16.03.2006 -> add ISchedulingRule (MARKER_RULE) for resource
        //resource.getWorkspace().run(runnable, new NullProgressMonitor());
        resource.getWorkspace().run(runnable, RuleUtils.modifyRule(new IResource[]{resource}, RuleUtils.MARKER_RULE ), IResource.NONE, new NullProgressMonitor());        
        
        return runnable.getMarker();
    }
    
    // by Nick
    
    public IMarker createMarker(final IResource resource, final String message, final int severity, final String id, final String entity, final IMapping mapping, final IMappingStorage storage, IResource validatingResource) throws CoreException 
    {
        final String className = this.getClass().getName();
        
        // changed by Nick 28.06.2005
        int lineNumber = 2;
        int lineLength = 0;
        int lineOffset = -1;
        
        Document document = getDocument(resource,lineNumber,false);
        if (document != null)
        {
            try {
                lineLength = document.getLineLength(lineNumber);
                lineOffset = document.getLineOffset(lineNumber);
            } catch (BadLocationException e) {
                throw new NestableRuntimeException(e);
            }
        }
        
        IMarker marker = internalCreateMarker(resource,className,message,severity,id,entity,mapping,storage,lineNumber,lineOffset,lineLength,validatingResource);      
        // by Nick      
        
        return marker;
    }
        
    public IMarker createMarker(final IResource resource, final String message, final int severity, final String id, final String entity, final IMapping mapping, final IMappingStorage storage) throws CoreException {
        return createMarker(resource,message,severity,id,entity,mapping,storage,null);
	}
	
	public static void deleteMarkers(IResource resource){
		   int depth = IResource.DEPTH_INFINITE;
		   try {
		      resource.deleteMarkers(MARKER_TYPE, true, depth);
			  if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("deleteMarkers for " + resource.getName());			  
		   } catch (CoreException e) {
			   ExceptionHandler.logThrowableWarning(e,"Exception occured in deleteMarkers"); // changed by Nick 29.09.2005
		   }

	}
	
    // added by Nick 26.09.2005
	private static Set resourcesCleaned = new HashSet();
    
    public static void clearSingleResourceClearSet()
    {
        resourcesCleaned.clear();
    }
    
    // by Nick
    
    // added by Nick 23.09.2005
    public static void deleteMarkers(IResource resource, String validatingPath, Class validatorClass){
           int depth = IResource.DEPTH_INFINITE;
           try {
              
               if (resourcesCleaned.contains(resource))
                   return ;
               
               resourcesCleaned.add(resource);
               
              IMarker[] markers = resource.findMarkers(MARKER_TYPE, true, depth);
              for (int i = 0; i < markers.length; i++) {
                IMarker marker = markers[i];
                
                boolean delete = false;
                
                Object path = marker.getAttribute("marker.source.path");
                if (path == null || validatingPath == null)
                {
                    delete = true;
                }
                else
                {
                    if (path instanceof String) {
                        if ( (validatorClass == null || validatorClass.getName().equals(marker.getAttribute("class"))) && (resource.equals(marker.getResource()) || validatingPath.equals(path)) )
                        {
                            delete = true;
                        }
                        else
                        {
                            IResource rsrc = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path( (String) path));
                            
                            if (rsrc == null || !rsrc.exists())
                            {
                                delete = true;
                            }
                        }
                    }
                }
           
                if (delete)
                    marker.delete();
              }
              
              if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("deleteMarkers for " + resource.getName()+ (validatingPath != null ? (":validation path = "+validatingPath) : "") +  (validatorClass != null ? (":validation domain = "+validatorClass): ""));              
           } catch (CoreException e) {
               ExceptionHandler.logThrowableWarning(e,"Exception occured in deleteMarkers"); // changed by Nick 29.09.2005
           }

    }
    // by Nick
    
	public static IMarker[] getMarkers(IResource resource) throws CoreException {
		  int depth = IResource.DEPTH_INFINITE;
	      return resource.findMarkers(MARKER_TYPE, true, depth);
	}
	
	public abstract void validateMapping(IMapping model);
    
    // added by Nick 22.09.2005
    public abstract void validateMapping(IMapping model, IResource scope);
    // by Nick
	
}
