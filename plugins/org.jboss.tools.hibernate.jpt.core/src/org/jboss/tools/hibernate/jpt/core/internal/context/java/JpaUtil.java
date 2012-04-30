/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Dmitry Geraskov
 *
 */
public class JpaUtil {
	
	/**
	 * 
	 * @param javaProject
	 * @param type
	 * @param interfaceName
	 * @return <code>true</code> if the type implements interface interfaceName. Returns null if class hierarchy is inconsistent.
	 * @throws JavaModelException
	 */
	public static Boolean isTypeImplementsInterface(IJavaProject javaProject, IType type, String interfaceName) throws JavaModelException{
		if (type == null) return false;
		boolean hasInconsistency = false;
		String[] interfaces = type.getSuperInterfaceNames();
		List<String> resolvedInterfaceNames = new LinkedList<String>();
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaceName.equals(interfaces[i]))
				return true;
			String[][] resolvedInterfaces = type.resolveType(interfaces[i]);
			if (resolvedInterfaces != null){
				for (String[] parts : resolvedInterfaces) {
					String fullName = parts[0].length() > 0 ? parts[0] + '.' + parts[1] : parts[1];
					resolvedInterfaceNames.add(fullName);
					if (interfaceName.equals(fullName))
						return true;
				}
			} else {
				hasInconsistency = true;
			}
		}
		if (type.getSuperclassName() != null){
			String[][] resolvedSuperClass = type.resolveType(type.getSuperclassName());
			if (resolvedSuperClass != null){
				String fullName = resolvedSuperClass[0][0].length() > 0 ? resolvedSuperClass[0][0] + '.' + resolvedSuperClass[0][1] : resolvedSuperClass[0][1];
				if (interfaceName.equals(fullName))
					return true;
				IType parentType = javaProject.findType(fullName);
				if (parentType != null){
					if (isTypeImplementsInterface(javaProject, parentType, interfaceName)){
						return true;
					}
				}
			} else {
				hasInconsistency = true;
			}
		}
		for (String interface_ : resolvedInterfaceNames) {
			IType parentInterface = javaProject.findType(interface_);
			if (isTypeImplementsInterface(javaProject, parentInterface, interfaceName)){
				return true;
			}
		}
		return hasInconsistency ? null : false;
	}
	
	/**
	 * 
	 * @param javaProject
	 * @param type
	 * @param interfacesName
	 * @return if the type implements one of the interfaces. Returns null if class hierarchy is inconsistent.
	 * @throws JavaModelException
	 */
	public static Boolean isTypeImplementsOneOfInterfaces(IJavaProject javaProject, IType type, String... interfacesName) throws JavaModelException{
		boolean hasInconsistency = false;
		for (String interfaceName : interfacesName) {
			Boolean implementsThis = isTypeImplementsInterface(javaProject, type, interfaceName);
			if (implementsThis == null ){
				hasInconsistency = true;
			} else if (implementsThis == true){
				return true;
			}
		}
		return hasInconsistency ? null : false;
	}

}
