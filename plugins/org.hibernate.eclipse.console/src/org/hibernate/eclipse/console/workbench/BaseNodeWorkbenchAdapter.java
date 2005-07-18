package org.hibernate.eclipse.console.workbench;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.hibernate.console.node.BaseNode;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class BaseNodeWorkbenchAdapter extends BasicWorkbenchAdapter implements
		IDeferredWorkbenchAdapter {

	public Object[] getChildren(Object o) {
		BaseNode bn = (BaseNode) o;
		return toArray(bn.children(),BaseNode.class);
	}
	
	public ImageDescriptor getImageDescriptor(Object object) {
		BaseNode bn = (BaseNode) object;
		return EclipseImages.getImageDescriptor(bn.getIconName());
	}

	public String getLabel(Object o) {
		BaseNode bn = (BaseNode) o;
		return bn.renderLabel(true);
	}

	public Object getParent(Object o) {
		BaseNode bn = (BaseNode) o;
		return bn.getParent();
	}

}
