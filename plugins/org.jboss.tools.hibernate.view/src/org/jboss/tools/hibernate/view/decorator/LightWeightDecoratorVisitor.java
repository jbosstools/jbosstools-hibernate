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
package org.jboss.tools.hibernate.view.decorator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
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
import org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationProblem;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author Tau
 *
 */
public class LightWeightDecoratorVisitor implements IOrmModelVisitor {

	public Object visitOrmProject(IOrmProject project, Object argument) {
		
		String retSeverity = null;
		IProject proj = project.getProject();
		
		// add tau 14.11.2005 - resolve ESORM-374
		if (proj.isOpen()) {
			try {
				IMarker[] markers = proj.findMarkers(
						HibernateValidationProblem.MARKER_TYPE, true,
						IResource.DEPTH_INFINITE);
				
				//edit tau 22.03.2006
				retSeverity = getSeverite(markers);
				
				//TODO (tau->tau) 22.03.2006 add JavaMarkers!!!
				
//				for (int i = 0; i < markers.length; i++) {
//					IMarker marker = markers[i];
//					int severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
//					if (severity == IMarker.SEVERITY_ERROR){
//						return LightWeightDecorator.SEVERITY_ERROR;
//					}
//					if (severity == IMarker.SEVERITY_WARNING){
//						retSeverity = LightWeightDecorator.SEVERITY_WARNING;
//					} 
//				}			
			} catch (CoreException e) {
				ViewPlugin.getPluginLog().logError(e);			
			}
		}

		return retSeverity;
	}

	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {

		return null;
	}

	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {

		return null;
	}

	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		String retSeverity = null;
		if (column.getPersistentValueMapping() == null) {
			retSeverity = LightWeightDecorator.SEVERITY_UNKNOWN;
		}
		return retSeverity;
	}

	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {

		return null;
	}

	// edit tau 22.03.2006
	public Object visitPackage(IPackage pakage, Object argument) {
		String retSeverity = null;		
		IPersistentClass[] persistentClasses = pakage.getPersistentClasses();
		for (int i = 0; i < persistentClasses.length; i++) {
			ICompilationUnit sourceCode = persistentClasses[i].getSourceCode();
			if (sourceCode != null){
				IType type = sourceCode.findPrimaryType();
				if (type != null){
					IPackageFragment pf = type.getPackageFragment();
					if (pf != null){
						IResource res = pf.getResource();
						if (res != null){
							try {
								IMarker[] pakageMarkers = res.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_ONE);								
								String retSeverityProblemJava = getSeverite(pakageMarkers);
								if (retSeverityProblemJava == LightWeightDecorator.SEVERITY_ERROR) {
									retSeverity = retSeverityProblemJava;
									break;									
								}
								else {
									IMarker[] hibernateProblemMarkers = res.findMarkers(HibernateValidationProblem.MARKER_TYPE, false, IResource.DEPTH_ONE);								
									String retSeverityHibernateJava = getSeverite(hibernateProblemMarkers);
									if (retSeverityHibernateJava == LightWeightDecorator.SEVERITY_ERROR ) {
										retSeverity = retSeverityHibernateJava;
										break;									
									} else if (retSeverityHibernateJava == LightWeightDecorator.SEVERITY_WARNING ) {
										retSeverity = retSeverityHibernateJava;
										break;										
									} else {
										retSeverity = retSeverityProblemJava;
										break;										
									}
								}
							} catch (CoreException e) {
								ViewPlugin.getPluginLog().logError(e);			
							}							
						}
					}
				}
			}
			
//			Object errorFlag = persistentClasses[i].accept(this, argument);
//			if (errorFlag != null && errorFlag instanceof String) {
//				if (errorFlag.equals(LightWeightDecorator.SEVERITY_ERROR)){
//					return LightWeightDecorator.SEVERITY_ERROR; 
//				} else if (errorFlag.equals(LightWeightDecorator.SEVERITY_WARNING)) {
//					retSeverity = LightWeightDecorator.SEVERITY_WARNING;
//				}
//			}
		}		
		return retSeverity;
	}

	public Object visitMapping(IMapping mapping, Object argument) {
		String retSeverity = null;
		IMarker[] markersMapping =  new IMarker[0];		
		
		// edit 30.09.2005
		// find markers for cfg
		IResource resourceMappingConfiguration = mapping.getConfiguration().getResource();
		if (resourceMappingConfiguration != null && resourceMappingConfiguration.exists()) {
			try {
				markersMapping = resourceMappingConfiguration.findMarkers(
						HibernateValidationProblem.MARKER_TYPE, true,
						IResource.DEPTH_ZERO);
			} catch (CoreException e) {
				ViewPlugin.getPluginLog().logError(e);			
			}
		}
		
		retSeverity = getSeverite(markersMapping);		
		
//		for (int i = 0; i < markersMapping.length; i++) {
//			IMarker marker = markersMapping[i];
//			int severity = marker.getAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
//			if (severity == IMarker.SEVERITY_ERROR) {
//				return LightWeightDecorator.SEVERITY_ERROR;
//			}
//			if (severity == IMarker.SEVERITY_WARNING) {
//				retSeverity = LightWeightDecorator.SEVERITY_WARNING;
//			}
//		}
		
		// del tau 23.02.2006
		//TODO (tau->tau) in 3.5.2 addd for error in IJavaPackage IMarker for resourceMappingConfiguration   
		
		/*
		// find markers for packages		
		IPackage[] packages = mapping.getPackages();
		for (int i = 0; i < packages.length; i++) {
			Object errorFlag = packages[i].accept(this, argument);
			if (errorFlag != null && errorFlag instanceof String) {
					if (errorFlag.equals(LightWeightDecorator.SEVERITY_ERROR)){
						return LightWeightDecorator.SEVERITY_ERROR; 
					} else if (errorFlag.equals(LightWeightDecorator.SEVERITY_WARNING)) {
						retSeverity = LightWeightDecorator.SEVERITY_WARNING;
					}
			}			
		}
		*/		
		return retSeverity;
	}

	// edit tau 22.03.2006
	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		// edit tau 22.07.2005
		String retSeverity = null;
		IMarker[] markersStorage =  new IMarker[0];
		
		// find markers for storage
		IResource resourceStorage = storage.getResource();
		if (resourceStorage != null && resourceStorage.exists()) {
			try {
				markersStorage = resourceStorage.findMarkers(
						HibernateValidationProblem.MARKER_TYPE, true,
						IResource.DEPTH_ZERO);
			} catch (CoreException e) {
				ViewPlugin.getPluginLog().logError(e);			
			}
		}
		
		retSeverity = getSeverite(markersStorage);
		
		//TODO (tau->tau) 22.03.2006 add markers for markersStorage !!!
		
//		for (int i = 0; i < markersStorage.length; i++) {
//			IMarker marker = markersStorage[i];
//			int severity = marker.getAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
//			if (severity == IMarker.SEVERITY_ERROR) {
//				return LightWeightDecorator.SEVERITY_ERROR;
//			}
//			if (severity == IMarker.SEVERITY_WARNING) {
//				retSeverity = LightWeightDecorator.SEVERITY_WARNING;
//			}
//		}
		
		// find markers for PersistentClass in this storage
//		IPersistentClassMapping[] persistentClassMappings = storage.getPersistentClassMappings();
//		for (int i = 0; i < persistentClassMappings.length; i++) {
//			Object errorFlag = persistentClassMappings[i].accept(this, argument);
//			if (errorFlag != null && errorFlag instanceof String) {
//				if (errorFlag.equals(LightWeightDecorator.SEVERITY_ERROR)){
//					return LightWeightDecorator.SEVERITY_ERROR; 
//				} else if (errorFlag.equals(LightWeightDecorator.SEVERITY_WARNING)) {
//					retSeverity = LightWeightDecorator.SEVERITY_WARNING;
//				}
//			}
//		}
		
		return retSeverity;
	}

	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		// edit 05.07.2005 tau
		String retSeverity = null;
		IMappingStorage storage = null;		
		ICompilationUnit sourceCode = clazz.getSourceCode();
		if (sourceCode != null ){
			IResource resource = sourceCode.getResource();
			if (resource != null && resource.exists()){ 
				try {
					IMarker[] markersStorage = new IMarker[0];
					IMarker[] markersJavaModel = new IMarker[0];					
					
					IMarker[] markersPersistentClass = resource.findMarkers(
							HibernateValidationProblem.MARKER_TYPE, true,
							IResource.DEPTH_ZERO);
						
//					// add 13.07.2005 tau - for check markers in mapping files
//					IPersistentClassMapping persistentClassMapping = clazz.getPersistentClassMapping();
//					if (persistentClassMapping != null){
//						storage = persistentClassMapping.getStorage();
//						if (storage != null){
//							IResource resourceStorage = storage.getResource();
//							if (resourceStorage != null && resourceStorage.exists()){
//								        markersStorage = resourceStorage.findMarkers(
//										HibernateValidationProblem.MARKER_TYPE, true,
//										IResource.DEPTH_ZERO);
//							}
//						}
//					}
					// edit tau 24.04.2006
						storage = clazz.getPersistentClassMappingStorage();
						if (storage != null){
							IResource resourceStorage = storage.getResource();
							if (resourceStorage != null && resourceStorage.exists()){
								        markersStorage = resourceStorage.findMarkers(
										HibernateValidationProblem.MARKER_TYPE, true,
										IResource.DEPTH_ZERO);
							}
						}
					//
					
					
					markersJavaModel = resource.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
						
					IMarker[] markers = new IMarker[markersPersistentClass.length + markersStorage.length + markersJavaModel.length];
					System.arraycopy(markersPersistentClass, 0, markers, 0,	markersPersistentClass.length);
					System.arraycopy(markersStorage, 0, markers, markersPersistentClass.length, markersStorage.length);
					System.arraycopy(markersJavaModel, 0, markers, markersPersistentClass.length+markersStorage.length, markersJavaModel.length);					
					
					for (int i = 0; i < markers.length; i++) {
						IMarker marker = markers[i];
						if (marker.getType().equals(HibernateValidationProblem.MARKER_TYPE)){
							String entityFromMarker = (String) marker.getAttribute("entity");
							String storageFromMarker = (String) marker.getAttribute("storage");
							// edit tau 29.09.2005 ESORM-7
							if (entityFromMarker != null && !entityFromMarker.equalsIgnoreCase(clazz.getName())){
								continue;
							}
						
							// edit tau 20.12.2005
//							if ( storageFromMarker != null && !storageFromMarker.equalsIgnoreCase(storage.getName())){
							if ( storageFromMarker != null && storage != null && !storageFromMarker.equalsIgnoreCase(storage.getName())){							
								continue;
							}
						}
					
						int severity = marker.getAttribute(IMarker.SEVERITY,
								IMarker.SEVERITY_INFO);
						if (severity == IMarker.SEVERITY_ERROR) {
							return LightWeightDecorator.SEVERITY_ERROR;
						}
						if (severity == IMarker.SEVERITY_WARNING) {
							retSeverity = LightWeightDecorator.SEVERITY_WARNING;
						}
					}
				} catch (CoreException e) {
					ViewPlugin.getPluginLog().logError(e);			
				}
			}
		}
		return retSeverity;
	}

	public Object visitPersistentField(IPersistentField field, Object argument) {
		// akuzmin 12.09.2005
		String retSeverity = null;
//		IType classWorkingCopy = null;
//		if ((field.getOwnerClass() != null) && (field.getOwnerClass().getType() != null)) {
//			try {
//				field.getOwnerClass().getSourceCode().makeConsistent(null); // add tau 19.06.2005				
//				classWorkingCopy = ScanProject.findClassInCU(field
//						.getOwnerClass().getSourceCode(), field.getOwnerClass()
//						.getType().getElementName());
//			} catch (JavaModelException e) {
//				ExceptionHandler.log(e, e.getMessage());
//			}
//			String[] params = {};
//			if ((classWorkingCopy != null)
//					&& (!classWorkingCopy.getField(field.getName()).exists())
//					&& (!classWorkingCopy.getMethod("get"
//											+ StringUtils.beanCapitalize(field.getName()), params)
//							.exists())) {
//				retSeverity = LightWeightDecorator.SEVERITY_UNKNOWN;
//			}
//		}
		if (field.getType()==null)
        {
			retSeverity = LightWeightDecorator.SEVERITY_UNKNOWN;
        }
		
		return retSeverity;

	}

	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
		// add by tau 19.07.2005
		if (mapping!=null) return visitPersistentClass(mapping.getPersistentClass(),argument);		
		return null;
	}

	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
		return null;
	}

	// add tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		return null;
	}
	
	// add tau 23.03.2006
	private String getSeverite(IMarker[] markers) {
		String retSeverity = null;
		for (int i = 0; i < markers.length; i++) {
			int severity = markers[i].getAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
			if (severity == IMarker.SEVERITY_ERROR) {
				retSeverity = LightWeightDecorator.SEVERITY_ERROR;
				break;
			}
			if (severity == IMarker.SEVERITY_WARNING) {
				retSeverity = LightWeightDecorator.SEVERITY_WARNING;
			}
		}
		return retSeverity;
	}

}
