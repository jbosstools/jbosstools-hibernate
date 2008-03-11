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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

/**
 * The interface for code renderers. 
 * This interface can be implemented in the whole or existing default implementation 
 * <code>org.jboss.tools.hibernate.core.CodeRendererService</code> can be overrided to customize code generation
 * 
 * @author Nick - mailto:n.belaevski@exadel.com
 */
public interface ICodeRendererService {

    /**
     * Finds all <b>source</b> package fragments in the project and creates some if none found
     * @param project project to create or find package in
     * @param packageName name of the package to create or find
     * @return array of found package fragments or array containing one element - just created package
     * @throws CoreException
     */
    public IPackageFragment[] getOrCreatePackage(IProject project, String packageName) throws CoreException;
    /**
     * Creates compilation unit (.java file) in the specified package. Created unit shouldn't contain type declarations, 
     * just package and imports
     * @param compilationUnitName name of the compilation unit to create
     * @param fragment package to create compilation unit in
     * @return created compilation unit
     * @throws CoreException
     */
    public ICompilationUnit createCompilationUnit(String compilationUnitName, IPackageFragment fragment) throws CoreException;
    /**
     * Creates public type declaration in the compilation unit specified. Typename is short name of the type to create. 
     * Note that implementation is required to create inner types also
     * @param unit compilation unit to create type in
     * @param typeName short name of the type to create. Type requested to create can be primary type or inner type (name has "$" separators)
     * @param baseTypeName fully-qualified name of the base type
     * @param implementingInterfaceNames array of fully-qualified names of implemented interfaces
     * @param isStatic indicates that class should be have static modifier
     * @return created type
     * @throws CoreException
     */
    public IType createType(ICompilationUnit unit, String typeName, String baseTypeName, String[] implementingInterfaceNames, boolean isStatic) throws CoreException;

    /**
     * Creates private field declaration
     * @param enclosingType type to create field in
     * @param fieldInfo structure descrbing field to create
     * @return created field
     * @throws CoreException
     */
    public IField createField(IType enclosingType, PropertyInfoStructure fieldInfo) throws CoreException;
    /**
     * Creates public getter method. The method created should comply with Java Beans conventions
     * @param enclosingType type to create getter in
     * @param getterInfo structure descrbing getter to create
     * @return created getter
     * @throws CoreException
     */
    public IMethod createGetter(IType enclosingType, PropertyInfoStructure getterInfo) throws CoreException;
    /**
     * Creates public setter method. The method created should comply with Java Beans conventions
     * @param enclosingType type to create setter in
     * @param setterInfo structure descrbing setter to create
     * @return created setter
     * @throws CoreException
     */
    public IMethod createSetter(IType enclosingType, PropertyInfoStructure setterInfo) throws CoreException;
    /**
     * Creates default constructor
     * @param enclosingType type to create constructor in
     * @return created constructor
     * @throws CoreException
     */
    public IMethod createConstructor(IType enclosingType) throws CoreException;
    /**
     * Creates parametrized constructor
     * @param enclosingType type to create constructor in
     * @param parameters constructor parameters 
     * @return created constructor
     * @throws CoreException
     */
    public IMethod createConstructor(IType enclosingType, PropertyInfoStructure[] parameters) throws CoreException;

    /**
     * Creates equals(Object) method implementing 
     * bussiness key equality checks
     * @param enclosingType type to create method in
     * @param fieldNames array of bussiness key fields names
     * @return created method
     * @throws CoreException
     * @see Object#equals(java.lang.Object)
     */
    public IMethod createEquals(IType enclosingType, String[] fieldNames) throws CoreException;
    /**
     * Creates hashCode() method complying with
     * bussiness key semantics
     * @param enclosingType type to create method in
     * @param fieldNames array of bussiness key fields names
     * @return created method
     * @throws CoreException
     * @see Object#hashCode()
     */
    public IMethod createHashCode(IType enclosingType, String[] fieldNames) throws CoreException;
    /**
     * Creates toString() method complying with
     * bussiness key semantics
     * @param enclosingType type to create method in
     * @param fieldNames array of bussiness key fields names
     * @return created method
     * @throws CoreException
     * @see Object#toString()
     */
    public IMethod createToString(IType enclosingType, String[] fieldNames) throws CoreException;
    
    /**
     * Formats specified member using default platform preferences
     * @param member member to format
     * @throws CoreException
     */
    public void formatMember(IMember member) throws CoreException;
    
    /**
     * Creates default method stubs for inherited abstract/interface methods
     * @param type type to create stubs in
     * @throws CoreException
     */
    public void createMethodStubs(IType type) throws CoreException;
}
