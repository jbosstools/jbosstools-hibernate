package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngTable;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class TablePropertiesContentProvider implements
		ITreeContentProvider, PropertyChangeListener {

	Viewer viewer;
	
	public Object[] getElements(Object inputElement) {
		IReverseEngineeringDefinition ied = (IReverseEngineeringDefinition)inputElement;
		return ied.getTables();
	}

	public void dispose() {
		

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		IReverseEngineeringDefinition old = (IReverseEngineeringDefinition) oldInput;
		IReverseEngineeringDefinition neu = (IReverseEngineeringDefinition) newInput;
		
		this.viewer=viewer;
		
		if(old!=null) {
			old.removePropertyChangeListener(IReverseEngineeringDefinition.TABLES_STRUCTURE, this);
		}
		
		if(neu!=null) {
			neu.addPropertyChangeListener(IReverseEngineeringDefinition.TABLES_STRUCTURE, this);
		}
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IRevEngTable ) {
			return ((IRevEngTable)parentElement).getColumns();
		} 
		else if (parentElement instanceof IRevEngColumn) {
			return new Object[0];
		} else {
			return new Object[] { parentElement };
		}
	}

	public Object getParent(Object element) {		
		return null;
	}

	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return true;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh();
	}
	
	

}
