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
package org.jboss.tools.hibernate.ui.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;

/**
 *
 * author: ?
 * author: Vitali Yemialyanchyk
 */
public class ObjectEditorInput implements IEditorInput{
	
	protected ArrayList<RootClass> roots;
	protected ConsoleConfiguration configuration;

	public ObjectEditorInput(ConsoleConfiguration configuration, RootClass rc) {
		roots = new ArrayList<RootClass>();
		roots.add(rc);
		this.configuration = configuration;
	}

	public ObjectEditorInput(ConsoleConfiguration configuration, RootClass[] rcs) {
		roots = new ArrayList<RootClass>();
    	for (int i = 0; i < rcs.length; i++) {
    		roots.add(rcs[i]);
    	}
		Collections.sort(roots, new RootClassComparator());
		this.configuration = configuration;
	}
	
	public class RootClassComparator implements Comparator<RootClass> {
		public int compare(RootClass o1, RootClass o2) {
			return getItemName(o1).compareTo(getItemName(o2));
		}
	}

	public boolean exists() {
		return false;
	}

	public ArrayList<RootClass> getRootClasses() {
		return roots;
	}

	public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getName() {
		return getDiagramName();
	}

	public String getDiagramName() {
		String name = ""; //$NON-NLS-1$
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < roots.size(); i++) {
			names.add(getItemName(roots.get(i)));
		}
		// sort to get same name for same combinations of entities
		Collections.sort(names);
		name = names.size() > 0 ? names.get(0) : ""; //$NON-NLS-1$
		for (int i = 1; i < names.size(); i++) {
			name += " & " + names.get(i); //$NON-NLS-1$
		}
		return name;
	}
	
	protected String getItemName(RootClass rootClass) {
		String res = rootClass.getEntityName();
		if (res == null) {
			res = rootClass.getClassName();
		}
		if (res == null) {
			res = rootClass.getNodeName();
		}
		res = res.substring(res.lastIndexOf(".") + 1); //$NON-NLS-1$
		return res;
	}

	public IPersistableElement getPersistable() {
		return null;
	}


	public String getToolTipText() {
		return ""; //$NON-NLS-1$
	}


	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public ConsoleConfiguration getConfiguration() {
		return configuration;
	}

	public boolean equals(Object obj) {
		boolean res = false;
		if (!(obj instanceof ObjectEditorInput)) {
			return res;
		}
		final ObjectEditorInput oei = (ObjectEditorInput)obj;
		if (!configuration.equals(oei.getConfiguration())) {
			return res;
		}
		final ArrayList<RootClass> rootsOei = oei.getRootClasses();
		if (roots.size() != rootsOei.size()) {
			return res;
		}
		res = true;
		for (int i = 0; i < roots.size(); i++) {
			if (!roots.get(i).equals(rootsOei.get(i))) {
				res = false;
				break;
			}
		}
		return res;
	}

	public int hashCode() {
		return roots.hashCode() + configuration.hashCode();
	}
}
