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
package org.jboss.tools.hibernate.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 18, 2005
 */
public interface ICodeGenerationService extends ICodeRendererService{
	
	public IType createImportType(ICompilationUnit unit, String typeName) throws CoreException;
	public void createFinders(IType type, String classTypeName, String idTypeName,boolean isInterface) throws CoreException;
	public void createWorkMethods(IType type, String classTypeName, String idTypeName,boolean isInterface,boolean isLog) throws CoreException;
	public void createSearchCriteriaMethods(IType type) throws CoreException;
	
    // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-526
	//public void createSessionMethods(IType type,boolean isLog) throws CoreException;
	public void createLogField(IType type) throws CoreException;
    // #changed#
	
    public IType createTypeWithOutMethodStubs(ICompilationUnit unit, String typeName, String baseTypeName, String[] implementingInterfaceNames, boolean isStatic) throws CoreException;
	public void createHBCriteria(IType enclosingType, String classTypeName, PropertyInfoStructure[] fieldNames) throws CoreException;
	public IField createField(IType enclosingType, PropertyInfoStructure fieldNames,String scopeModifier) throws CoreException;    
	public void createDAOTestCaseMethods(IType enclosingType, String classTypeName, PropertyInfoStructure idInfo,PropertyInfoStructure[] fieldNames) throws CoreException;    
	
}
