/*
 * Created on 2004-10-30 by max
 * 
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
