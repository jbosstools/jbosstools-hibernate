package org.hibernate.eclipse.console.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class NavigatorProvider implements ITreeContentProvider {

	
	private Viewer viewer;

	KnownConfigurationsProvider kcp = null;
	public NavigatorProvider() {
		kcp = new KnownConfigurationsProvider();
	}
	
	public Object[] getChildren(Object parentElement) {
		return kcp.getChildren( parentElement );
		/*if(parentElement instanceof Number) {
			PendingNode placeHolder = new PendingNode(parentElement.toString());				
			PendingJob loadJob = new PendingJob((AbstractTreeViewer) viewer, placeHolder, parentElement, new ITreeContentProvider() {
			
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					
			
				}
			
				public void dispose() {
					
			
				}
			
				public Object[] getElements(Object inputElement) {
					return NavigatorProvider.this.getElements( inputElement );
				}
			
				public boolean hasChildren(Object element) {
					return true;
				}
			
				public Object getParent(Object element) {
					return null;
				}
			
				public Object[] getChildren(Object parentElement) {
					return getRealChildren( parentElement );
				}
			
			});
			loadJob.schedule();
			return new Object[] { placeHolder };			
		} else {
			return getRealChildren( parentElement );
		}	*/	
	}

	private Object[] getRealChildren(Object parentElement) {
		Number parent = (Number) parentElement;
		Object[] children = new Object[parent.intValue()*2];
		for(int kids=0;kids<children.length;kids++) {
			try {
				Thread.sleep( children.length );
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			children[kids] = new Long(kids);
		}
		return children;
	}

	public Object getParent(Object element) {
		return kcp.getParent( element );
	}

	public boolean hasChildren(Object element) {
		return kcp.hasChildren( element );
	}

	public Object[] getElements(Object inputElement) {
		return kcp.getElements( inputElement );
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		kcp.inputChanged( viewer, oldInput, newInput );
	}

}
