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
package org.jboss.tools.hibernate.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.hibernate.core.ICodeRendererService;
import org.jboss.tools.hibernate.core.PropertyInfoStructure;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 27.09.2005
 * 
 */
public class CodeRendererServiceWrapper implements ICodeRendererService {

    private ICodeRendererService renderer = null;
    
    public CodeRendererServiceWrapper(ICodeRendererService renderer)
    {
        this.renderer = renderer;
    }
    
    public static IType getWorkingCopy(IType type) throws JavaModelException
    {
        if (type == null)
            return null;
        
        ICompilationUnit unit = type.getCompilationUnit();
        
        if (unit == null || !unit.exists())
            return null;
        
        ICompilationUnit workingCopy = unit.getWorkingCopy(null);
        //TODO exp-11d ????
        return ScanProject.findClassInCU(workingCopy,type.getTypeQualifiedName());
    }
    
    public static void commitChanges(ICompilationUnit unit) throws CoreException
    {
        synchronized(unit) 
        {
            unit.reconcile(ICompilationUnit.NO_AST, true, null, null);
        }
        unit.commitWorkingCopy(true,null);
    }
    
    public static void saveChanges(ICompilationUnit unit) throws CoreException
    {
        if (unit != null)
            unit.getPrimary().save(null,true);
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createCompilationUnit(java.lang.String, org.eclipse.jdt.core.IPackageFragment)
     */
    public ICompilationUnit createCompilationUnit(String compilationUnitName, IPackageFragment fragment) throws CoreException {
        if (compilationUnitName == null || fragment == null)
            return null;
        
        return renderer.createCompilationUnit(compilationUnitName, fragment);
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createType(java.lang.String, java.lang.String, java.lang.String[], org.eclipse.jdt.core.dom.IMethodBinding[], boolean)
     */
    public IType createType(ICompilationUnit unit, String typeName, String baseTypeName, String[] implementingInterfaceNames, boolean isStatic) throws CoreException {
        if (unit == null || typeName == null)
            return null;
        
        ICompilationUnit workingCopy = unit.getWorkingCopy(null);

        IType type = renderer.createType(workingCopy, typeName, baseTypeName, implementingInterfaceNames, isStatic);
    
        commitChanges(workingCopy);
        saveChanges(workingCopy);
    
        return type;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createField(org.eclipse.jdt.core.IType, org.jboss.tools.hibernate.core.ICodeRendererService.PropertyInfoStructure)
     */
    public IField createField(IType enclosingType, PropertyInfoStructure fieldInfo) throws CoreException {
        if (enclosingType == null || fieldInfo == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);
        
        IField result = renderer.createField(type, fieldInfo);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createGetter(org.eclipse.jdt.core.IType, org.jboss.tools.hibernate.core.ICodeRendererService.PropertyInfoStructure)
     */
    public IMethod createGetter(IType enclosingType, PropertyInfoStructure getterInfo) throws CoreException {
        if (enclosingType == null || getterInfo == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);
        
        IMethod result = renderer.createGetter(type, getterInfo);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createSetter(org.eclipse.jdt.core.IType, org.jboss.tools.hibernate.core.ICodeRendererService.PropertyInfoStructure)
     */
    public IMethod createSetter(IType enclosingType, PropertyInfoStructure setterInfo) throws CoreException {
        if (enclosingType == null || setterInfo == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);
        
        IMethod result = renderer.createSetter(type, setterInfo);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createConstructor(org.eclipse.jdt.core.IType)
     */
    public IMethod createConstructor(IType enclosingType) throws CoreException {
        if (enclosingType == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);
        
        IMethod result = renderer.createConstructor(type);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createConstructor(org.eclipse.jdt.core.IType, org.jboss.tools.hibernate.core.ICodeRendererService.PropertyInfoStructure[])
     */
    public IMethod createConstructor(IType enclosingType, PropertyInfoStructure[] parameters) throws CoreException {
        if (enclosingType == null || parameters == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);
        
        IMethod result = renderer.createConstructor(type, parameters);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createEquals(org.eclipse.jdt.core.IType, java.lang.String[])
     */
    public IMethod createEquals(IType enclosingType, String[] fieldNames) throws CoreException {
        if (enclosingType == null || fieldNames == null)
            return null;
            
        IType type = getWorkingCopy(enclosingType);
        
        IMethod result = renderer.createEquals(type, fieldNames);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createHashCode(org.eclipse.jdt.core.IType, java.lang.String[])
     */
    public IMethod createHashCode(IType enclosingType, String[] fieldNames) throws CoreException {
        if (enclosingType == null || fieldNames == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);

        IMethod result = renderer.createHashCode(type, fieldNames);
    
        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createToString(org.eclipse.jdt.core.IType, java.lang.String[])
     */
    public IMethod createToString(IType enclosingType, String[] propertyNames) throws CoreException {
        if (enclosingType == null || propertyNames == null)
            return null;
        
        IType type = getWorkingCopy(enclosingType);

        IMethod result = renderer.createToString(type, propertyNames);

        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
        return result;
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#formatMember(org.eclipse.jdt.core.IMember)
     */
    public void formatMember(IMember member) throws CoreException {
        renderer.formatMember(member);
    }
    
    private void internalCreateProperty(String name, String typeName, IType inType) throws CoreException
    {
        if (name == null || typeName == null || inType == null || inType.getCompilationUnit() == null)
            return ;
        
        PropertyInfoStructure info = new PropertyInfoStructure(name, typeName);
        
        IType type = !inType.getCompilationUnit().isWorkingCopy() ? getWorkingCopy(inType) : inType;
        
        renderer.createField(type, info);
        renderer.createGetter(type, info);
        renderer.createSetter(type, info);
        
        commitChanges(type.getCompilationUnit());
    }
    
    public void createProperty(String name, String typeName, IType inType) throws CoreException
    {
        internalCreateProperty(name, typeName, getWorkingCopy(inType));
        saveChanges(inType.getCompilationUnit());
    }

    public void batchGenerateProperties(String[] names, String[] types, IType enclosingClass) throws CoreException
    {
        if (enclosingClass == null || names == null || types == null)
            return ;
        
        IType inType = getWorkingCopy(enclosingClass);
        
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String type = types[i];
            internalCreateProperty(name,type,inType);
        }
        
        saveChanges(enclosingClass.getCompilationUnit());
    }

    public IPackageFragment[] getOrCreatePackage(IProject project, String packageName) throws CoreException {
        return renderer.getOrCreatePackage(project, packageName);
    }

    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.ICodeRendererService#createMethodStubs(org.eclipse.jdt.core.IType)
     */
    public void createMethodStubs(IType type) throws CoreException {
        if (type == null)
            return ;
        
        IType inType = getWorkingCopy(type);
        
        renderer.createMethodStubs(inType);
        
        commitChanges(type.getCompilationUnit());
        saveChanges(type.getCompilationUnit());
    }
}
