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
package org.jboss.tools.hibernate.internal.core.codegenerator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.hibernate.core.ICodeGenerationService;
import org.jboss.tools.hibernate.core.PropertyInfoStructure;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Oct 14, 2005
 */
public class CodeGenerationServiceWraper extends CodeRendererServiceWrapper implements ICodeGenerationService{
	
    private ICodeGenerationService extrenderer = null;
    
    public CodeGenerationServiceWraper(ICodeGenerationService extrenderer)
    {
    	super(extrenderer);
        this.extrenderer = extrenderer;
    }

	public IType createImportType(ICompilationUnit unit, String typeName) throws CoreException {
	        if (unit == null || typeName == null)
	            return null;
	        
	        ICompilationUnit workingCopy = unit.getWorkingCopy(null);

	        IType type = extrenderer.createImportType(workingCopy, typeName);
	    
	        commitChanges(workingCopy);
	        saveChanges(workingCopy);
	    
	        return type;
	    }


	public void createFinders(IType enclosingType, String classTypeName,String idTypeName,boolean isInterface) throws CoreException {
	       if (enclosingType == null)
	            return ;
	        IType type = getWorkingCopy(enclosingType);
	        
	        extrenderer.createFinders(type,classTypeName,idTypeName,isInterface);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return;
	}

	public void createWorkMethods(IType enclosingType, String classTypeName, String idTypeName,boolean isInterface,boolean isLog) throws CoreException {
	       if (enclosingType == null)
	            return ;
	        IType type = getWorkingCopy(enclosingType);
	        
	        extrenderer.createWorkMethods(type,classTypeName,idTypeName,isInterface,isLog);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return;
		
	}

	public void createSearchCriteriaMethods(IType enclosingType) throws CoreException {
	       if (enclosingType == null)
	            return ;
	        IType type = getWorkingCopy(enclosingType);
	        
	        extrenderer.createSearchCriteriaMethods(type);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return;
		
	}
	
    // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-526
//	public void createSessionMethods(IType enclosingType,boolean isLog) throws CoreException {
//	       if (enclosingType == null)
//	            return ;
//	        IType type = getWorkingCopy(enclosingType);
//	        
//	        extrenderer.createSessionMethods(type,isLog);
//
//	        commitChanges(type.getCompilationUnit());
//	        saveChanges(type.getCompilationUnit());
//	        return;
//		
//	}

	public void createLogField(IType enclosingType) throws CoreException {
	       if (enclosingType == null)
	            return ;
	        IType type = getWorkingCopy(enclosingType);
	        
	        extrenderer.createLogField(type);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return;
		
	}
    // #changed#
	
    public IType createTypeWithOutMethodStubs(ICompilationUnit unit, String typeName, String baseTypeName, String[] implementingInterfaceNames, boolean isStatic) throws CoreException {
        if (unit == null || typeName == null)
            return null;
        
        ICompilationUnit workingCopy = unit.getWorkingCopy(null);

        IType type = extrenderer.createTypeWithOutMethodStubs(workingCopy, typeName, baseTypeName, implementingInterfaceNames, isStatic);
    
        commitChanges(workingCopy);
        saveChanges(workingCopy);
    
        return type;
    }

	public void createHBCriteria(IType enclosingType, String classTypeName, PropertyInfoStructure[] fieldNames) throws CoreException {
	       if (enclosingType == null)
	            return ;
	        IType type = getWorkingCopy(enclosingType);
	        
	        extrenderer.createHBCriteria(type,classTypeName,fieldNames);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return;
		
	}

	public IField createField(IType enclosingType, PropertyInfoStructure fieldNames,String scopeModifier) throws CoreException {
	       if ((enclosingType == null)||(fieldNames==null))
	            return null;
	        IType type = getWorkingCopy(enclosingType);
	        
	        IField field=extrenderer.createField(type,fieldNames,scopeModifier);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return field;
	}

	public void createDAOTestCaseMethods(IType enclosingType, String classTypeName, PropertyInfoStructure idInfo, PropertyInfoStructure[] fieldNames) throws CoreException {
	       if ((enclosingType == null)||(fieldNames==null)||(classTypeName==null)||(idInfo==null))
	            return;
	        IType type = getWorkingCopy(enclosingType);
	        
	        extrenderer.createDAOTestCaseMethods(type,classTypeName,idInfo,fieldNames);

	        commitChanges(type.getCompilationUnit());
	        saveChanges(type.getCompilationUnit());
	        return;
	}
   
}
