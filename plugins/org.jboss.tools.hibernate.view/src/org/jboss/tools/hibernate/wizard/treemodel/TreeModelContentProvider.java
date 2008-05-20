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
package org.jboss.tools.hibernate.wizard.treemodel;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author kaa akuzmin@exadel.com Jul 18, 2005
 */
public class TreeModelContentProvider implements ITreeContentProvider {

	private boolean sort = true;

	public TreeModelContentProvider() {
		sort = true;
	}

	public TreeModelContentProvider(boolean sort) {
		this.sort = sort;
	}

	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeModel) {
			Object[] children = ((TreeModel) parentElement).getChildren();
			if (sort) {
				Arrays.sort(children, comparator);
			}
			return children;
		} else {
			return new Object[0];
		}
	}

	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof TreeModel) {
			if (((TreeModel) element).getChildren().length != 0)
				return true;
		}

		return false;
	}

	/*
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/*
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	static protected Comparator comparator = new Comparator() {
		private Collator fCollator = Collator.getInstance();

		public int compare(Object o1, Object o2) {

			String name1 = null;
			String name2 = null;
			if (o1 instanceof TreeModel)
				name1 = ((TreeModel) o1).getName();

			if (o2 instanceof TreeModel)
				name2 = ((TreeModel) o2).getName();

			return fCollator.compare(name1, name2);
		}
	};
}
