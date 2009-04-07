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
			children[kids] = Long.valueOf(kids);
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
