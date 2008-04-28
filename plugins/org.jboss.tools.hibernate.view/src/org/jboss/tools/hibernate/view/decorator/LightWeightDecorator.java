/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.view.decorator;

/**
 * @author Tau
 *
 */
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.view.ColourProvider;
import org.jboss.tools.hibernate.view.ViewPlugin;


public class LightWeightDecorator implements ILightweightLabelDecorator 
{
	
	static private LightWeightDecoratorVisitor decoratorVisitor = new LightWeightDecoratorVisitor();
	static private ColourProvider fColourProvider = new ColourProvider();
	
	// add tau 31.05.2005
	public static final String SEVERITY_ERROR = "2";	
	public static final String SEVERITY_WARNING = "1";	
	public static final String SEVERITY_INFO = "0";
	
	// add tau 26.07.2005
	public static final String SEVERITY_UNKNOWN = "3";	
	

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object object, IDecoration decoration) {
		
		if (!(object instanceof IOrmElement)) {
			return;
		}
		
		IOrmElement ormElement = getOrmElement(object);

		if (ormElement == null) {
			return;
		}
		
		Object errorFlag = ormElement.accept(decoratorVisitor, null);
		
		if (errorFlag != null && errorFlag instanceof String) {
				if (errorFlag.equals(SEVERITY_ERROR)){
					decoration.addOverlay(ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("Decorator.Error")));
					//decoration.setForegroundColor(fColourProvider.getColor(ColourProvider.ERROR));					
				} else if (errorFlag.equals(SEVERITY_WARNING)) {
					decoration.addOverlay(ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("Decorator.Warning")));
				} else if (errorFlag.equals(SEVERITY_UNKNOWN)) {
					decoration.addOverlay(ViewPlugin.getImageDescriptor(ViewPlugin.BUNDLE_IMAGE.getString("Decorator.Unknown")));
				}
		}
	}

	private IOrmElement getOrmElement(Object object) {
		if (object instanceof IOrmElement) {
			return (IOrmElement) object;
		}
		if (object instanceof IAdaptable) {
			return (IOrmElement) ((IAdaptable) object)
					.getAdapter(IOrmElement.class);
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {

	}

	public void dispose() {


	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {

	}

}
