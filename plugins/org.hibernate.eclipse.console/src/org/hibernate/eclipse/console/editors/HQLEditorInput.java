/*
 * Created on 2004-10-27 by max
 * 
 */
package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author max
 *
 */
public class HQLEditorInput implements IEditorInput {

	private final String queryString;

	public HQLEditorInput(String queryString) {
		this.queryString = queryString;
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return this.toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "HQL";
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	

}
