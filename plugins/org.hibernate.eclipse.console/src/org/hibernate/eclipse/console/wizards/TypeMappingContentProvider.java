/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class TypeMappingContentProvider implements IStructuredContentProvider, PropertyChangeListener {

	private final Viewer tv;

	public TypeMappingContentProvider(Viewer tv) {
		this.tv = tv;			
	}
	
	public Object[] getElements(Object inputElement) {
		return (getReverseEngineeringDef( inputElement )).getTypeMappings();
	}

	private IReverseEngineeringDefinition getReverseEngineeringDef(Object inputElement) {
		return (IReverseEngineeringDefinition)inputElement;
	}

	public void dispose() {
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null) {
			(getReverseEngineeringDef(oldInput)).removePropertyChangeListener(this);
		}
		if (newInput != null) {
			(getReverseEngineeringDef(newInput)).addPropertyChangeListener(this);
		}		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName()==null || evt.getPropertyName().equals(IReverseEngineeringDefinition.TYPEMAPPING_STRUCTURE)) {
			tv.refresh();	
		}							
	}
}