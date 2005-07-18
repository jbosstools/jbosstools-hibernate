package org.hibernate.eclipse.console.workbench;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;

public class DeferredContentProvider extends BaseWorkbenchContentProvider {

	private DeferredTreeContentManager manager;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (viewer instanceof AbstractTreeViewer) {
			manager = new DeferredTreeContentManager(this, (AbstractTreeViewer) viewer) {
				protected IDeferredWorkbenchAdapter getAdapter(Object element) {
					if (element instanceof IDeferredWorkbenchAdapter)
			            return (IDeferredWorkbenchAdapter) element;
			        Object adapter = Platform.getAdapterManager().getAdapter(element,IDeferredWorkbenchAdapter.class);
			        return (IDeferredWorkbenchAdapter) adapter;				
			   }
			};
		}
		super.inputChanged(viewer, oldInput, newInput);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		// the + box will always appear, but then disappear
		// if not needed after you first click on it.
		if (manager != null) {
			if (manager.isDeferredAdapter(element))
				return manager.mayHaveChildren(element);
		}

		return super.hasChildren(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.WorkbenchContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object element) {
		if (manager != null) {
			Object[] children = manager.getChildren(element);
			if(children != null) {
				return children;
			}
		}
		return super.getChildren(element);
	}
	
	protected IWorkbenchAdapter getAdapter(Object o) {
	    if (o instanceof IAdaptable) {
        	return (IWorkbenchAdapter) ((IAdaptable) o).getAdapter(IWorkbenchAdapter.class);
        } else {
        	return (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(o, IWorkbenchAdapter.class);
        }
	}
}
