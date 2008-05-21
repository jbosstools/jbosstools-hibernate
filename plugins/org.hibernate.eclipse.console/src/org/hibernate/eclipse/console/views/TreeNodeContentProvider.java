/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.views;

import javax.swing.tree.TreeNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author max
 *
 */
abstract public class TreeNodeContentProvider implements ITreeContentProvider {

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	public void dispose() {
	}

	/**
	 * return roots elements! 
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public boolean hasChildren(final Object element) {
		if(element instanceof TreeNode) {
			return !( (TreeNode)element).isLeaf();
		}
		return false;
	}

	public Object getParent(Object element) {
		if (element instanceof TreeNode) {
			return ( (TreeNode)element).getParent();
		} 
		return null;
	}

	/**
	 * return children for the element 
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof TreeNode) {
			final TreeNode t = (TreeNode) parentElement;
			TreeNode[] children = new TreeNode[0];
			if(!t.isLeaf() ) {
				children = new TreeNode[t.getChildCount()];
				for(int i = 0; i < t.getChildCount(); i++) {
					children[i] = t.getChildAt(i);
				}
			}
			return children;			
		}
		return EMPTY_CHILDREN;
	}

	private static final Object[] EMPTY_CHILDREN = new TreeNode[0];


}
