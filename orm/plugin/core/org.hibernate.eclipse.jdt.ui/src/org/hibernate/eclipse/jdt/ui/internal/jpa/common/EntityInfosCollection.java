/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;

/**
 * To handle EntityInfo bundle complex properties:
 * for example several EntityInfos in one file ->
 * so here is necessary to handle it's imports
 * 
 * @author Vitali
 */
public class EntityInfosCollection {

	/**
	 * path to the innermost resource enclosing these EntityInfos
	 */
	protected IPath path;
	/**
	 * 
	 */
	protected ICompilationUnit iCompilationUnit;
	/**
	 * 
	 */
	protected org.eclipse.jdt.core.dom.CompilationUnit compilationUnit;
	/**
	 * collection of EntityInfo's
	 */
	protected HashMap<String, EntityInfo> entityInfos = new HashMap<String, EntityInfo>();
	/**
	 * existing imports set
	 */
	protected Set<String> setExistingImports = new TreeSet<String>();
	/**
	 * required imports set
	 */
	protected Set<String> setRequiredImports = new TreeSet<String>();

	public void addEntityInfo(EntityInfo ei) {
		entityInfos.put(ei.getFullyQualifiedName(), ei);
	}

	public EntityInfo getEntityInfo(String fullyQualifiedName) {
		return entityInfos.get(fullyQualifiedName);
	}

	public void updateExistingImportSet() {
		setExistingImports.clear();
		Iterator<EntityInfo> it = entityInfos.values().iterator();
		while (it.hasNext()) {
			it.next().collectExistingImport(setExistingImports);
		}
	}

	public void updateRequiredImportSet() {
		setRequiredImports.clear();
		Iterator<EntityInfo> it = entityInfos.values().iterator();
		while (it.hasNext()) {
			it.next().collectRequiredImport(setRequiredImports);
		}
	}

	public boolean needImport(String checkImport) {
		return (!setExistingImports.contains(checkImport) && setRequiredImports.contains(checkImport));
	}

	public IPath getPath() {
		return path;
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public ICompilationUnit getICompilationUnit() {
		return iCompilationUnit;
	}

	public void setICompilationUnit(ICompilationUnit iCompilationUnit) {
		this.iCompilationUnit = iCompilationUnit;
	}

	public org.eclipse.jdt.core.dom.CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public void setCompilationUnit(org.eclipse.jdt.core.dom.CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}
	
	public String toString() {
		StringBuffer res = new StringBuffer();
		Iterator<EntityInfo> it = entityInfos.values().iterator();
		while (it.hasNext()) {
			res.append(it.next());
			res.append(";"); //$NON-NLS-1$
		}
		return res.toString();
	}

	public int hashCode() {
		return toString().hashCode();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof EntityInfosCollection)) {
			return false;
		}
		EntityInfosCollection eic = (EntityInfosCollection)obj;
		if (toString().equals(eic.toString())) {
			return true;
		}
		return false;
	}
}
