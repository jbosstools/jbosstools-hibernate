package org.hibernate.console.model;

/**
 * Common protocol for elements that contain other elements.
 */
public interface IParentEl {
	/**
	 * Returns the immediate children of this element.
	 * Unless otherwise specified by the implementing element,
	 * the children are in no particular order.
	 *
	 * @return the immediate children of this element
	 */
	Object[] getChildren();
	/**
	 * Returns whether this element has one or more immediate children.
	 * This is a convenience method, and may be more efficient than
	 * testing whether <code>getChildren</code> is an empty array.
	 *
	 * @return true if the immediate children of this element, false otherwise
	 */
	boolean hasChildren();
	//
	void setChildren(Object[] children);
	//
	void addChild(Object child);
	//
	void removeChild(Object child);
}
