package org.hibernate.eclipse.console.views;


import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.console.node.BaseNode;

public class ConfigurationsLabelProvider implements ILabelProvider {
    
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
       if (element != null && element instanceof BaseNode) {
            return EclipseImages.getImage( ( (BaseNode) element).getIconName() );
        } else {
            return null;
        }
		}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element != null && element instanceof BaseNode) {
            String labelName = ( (BaseNode) element).getName();
            return labelName;
		} else {
            return element == null ? "<<empty>>" : element.toString();
        }
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
    }
    
}