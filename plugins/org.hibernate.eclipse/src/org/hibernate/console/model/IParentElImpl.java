package org.hibernate.console.model;

import java.util.ArrayList;
import java.util.List;

public class IParentElImpl implements IParentEl {

	private List children = null;

	public Object[] getChildren() {
		if (null == children) {
			return null;
		}
		return children.toArray();
	}

	public boolean hasChildren() {
		if (null == children) {
			return true;
		}
		return( children.size() > 0 );
	}	

	public void setChildren(Object[] children) {
		if (null == children || 0 == children.length) {
			return;
		}
		this.children = new ArrayList(children.length);
		for (int i = 0; i < children.length; i++) {
			this.children.add(children[i]);
		}
	}

	public void addChild(Object child) {
		this.children.add(child);
	}

	public void removeChild(Object child) {
		this.children.remove(child);
	}

}
