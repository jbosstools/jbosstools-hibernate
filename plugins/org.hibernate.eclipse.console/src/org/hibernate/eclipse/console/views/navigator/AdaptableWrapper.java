package org.hibernate.eclipse.console.views.navigator;

import org.eclipse.core.runtime.IAdaptable;

public class AdaptableWrapper implements IAdaptable {

	private Object element;
	
	public AdaptableWrapper(Object element) {
		this.element = element;
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter.isInstance(element)) {
			return element;
		}
		if(element instanceof IAdaptable) {
			return ((IAdaptable)element).getAdapter(adapter);
		}
		return null;
	}
}
