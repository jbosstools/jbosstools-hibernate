/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.viewers.xpl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * TreeViewer has the next lack design to work with the objects as TreeItem id:
 * internalFindItems always return array of size <= 1
 * cause doFindItems & internalFindItem returns FIRST proper item which it find
 * but in common case this is not the TRUTH - so TreeViewer add method doesn't work
 * as we expected.
 * 
 * So MTreeViewer fix the problem.
 * It redefine add method taking into account what for one parentElementOrTreePath as id
 * could exist several ( >= 0 ) widgets.
 * doFindItems & internalFindItem returns ALL proper items which it find.
 * 
 * We expect what TreeViewer developers fix the bug in future versions,
 * but we can't wait it.
 * 
 * more info is here http://jira.jboss.com/jira/browse/JBIDE-1482
 * 
 * @author Vitali
 */
public class MTreeViewer extends TreeViewer {

	public MTreeViewer(Composite parent, int style) {
		super(parent, style);
	}

	public void clearChildren(Object node) {
		Widget[] items = getChildren(node == null ? getTree() : (Widget)node);
		for (int j = 0; j < items.length; j++) {
			clearChildren(items[j]);
			if (items[j] instanceof TreeItem) {
				((TreeItem)items[j]).setExpanded(false);
				((TreeItem)items[j]).clearAll(true);
			}
		}
	}
	
	/**
	 * Adds the given child elements to this viewer as children of the given
	 * parent element. If this viewer does not have a sorter, the elements are
	 * added at the end of the parent's list of children in the order given;
	 * otherwise, the elements are inserted at the appropriate positions.
	 * <p>
	 * This method should be called (by the content provider) when elements have
	 * been added to the model, in order to cause the viewer to accurately
	 * reflect the model. This method only affects the viewer, not the model.
	 * </p>
	 * 
	 * @param parentElementOrTreePath
	 *            the parent element
	 * @param childElements
	 *            the child elements to add
	 */
	public void add(Object parentElementOrTreePath, Object[] childElements) {
		Assert.isNotNull(parentElementOrTreePath);
		assertElementsNotNull(childElements);
		if (isBusy())
			return;
		Widget[] widgets = internalFindItems2(parentElementOrTreePath);
		// If parent hasn't been realized yet, just ignore the add.
		if (widgets.length == 0) {
			return;
		}

		for (int i = 0; i < widgets.length; i++) {
			internalAdd(widgets[i], parentElementOrTreePath, childElements);
		}
	}

	/**
	 * Find the items for the given element of tree path
	 * 
	 * @param parentElementOrTreePath
	 *            the element or tree path
	 * @return the items for that element
	 */
	protected Widget[] internalFindItems2(Object parentElementOrTreePath) {
		Widget[] widgets;
		if (parentElementOrTreePath instanceof TreePath) {
			widgets = internalFindItems(parentElementOrTreePath);
		} else {
			widgets = findItems2(parentElementOrTreePath);
		}
		return widgets;
	}
	
	/**
	 * Finds the widgets which represent the given element. The returned array
	 * must not be changed by clients; it might change upon calling other
	 * methods on this viewer.
	 * <p>
	 * This method was introduced to support multiple equal elements in a viewer
	 * (@see {@link AbstractTreeViewer}). Multiple equal elements are only
	 * supported if the element map is enabled by calling
	 * {@link #setUseHashlookup(boolean)} and passing <code>true</code>.
	 * </p>
	 * <p>
	 * The default implementation of this method tries first to find the widget
	 * for the given element assuming that it is the viewer's input; this is
	 * done by calling <code>doFindInputItem</code>. If it is not found
	 * there, the widgets are looked up in the internal element map provided
	 * that this feature has been enabled. If the element map is disabled, the
	 * widget is found via <code>doFindInputItem</code>.
	 * </p>
	 * 
	 * @param element
	 *            the element
	 * @return the corresponding widgets
	 */
	protected Widget[] findItems2(Object element) {
		Widget result = doFindInputItem(element);
		if (result != null) {
			return new Widget[] { result };
		}
		// if we have an element map use it, otherwise search for the item.
		if (usingElementMap()) {
			return findItems2(element);
		}
		return doFindItems(element);
	}

	/**
	 * Recursively tries to find the given element.
	 * 
	 * @param parent
	 *            the parent item
	 * @param element
	 *            the element
	 * @return Widget
	 */
	protected List<Item> internalFindItem(Item parent, Object element) {

		List<Item> ret = new ArrayList<Item>();
		// compare with node
		Object data = parent.getData();
		if (data != null) {
			if (equals(data, element)) {
				ret.add(parent);
				return ret;
			}
		}
		// recurse over children
		Item[] items = getChildren(parent);
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			List<Item> o = internalFindItem(item, element);
			if (null != o) {
				ret.addAll(o);
			}
		}
		return ret;
	}

	protected Widget[] doFindItems(Object element) {
		Widget[] ret = new Widget[0];
		// compare with root
		Object root = getRoot();
		if (null == root) {
			return ret;
		}
		List<Widget> res = new ArrayList<Widget>();
		Item[] items = getChildren(getControl());
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				List<Item> o = internalFindItem(items[i], element);
				if (null != o) {
					res.addAll(o);
				}
			}
		}
		ret = res.toArray(new Widget[0]);
		return ret;
	}

}
