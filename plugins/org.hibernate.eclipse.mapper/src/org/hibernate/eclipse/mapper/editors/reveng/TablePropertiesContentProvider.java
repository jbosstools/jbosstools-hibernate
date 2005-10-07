package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.hibernate.eclipse.console.model.IRevEngColumn;
import org.hibernate.eclipse.console.model.IRevEngGenerator;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;
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
			IRevEngTable revEngTable = ((IRevEngTable)parentElement);
			IRevEngPrimaryKey primaryKey = revEngTable.getPrimaryKey();
			IRevEngColumn[] columns = revEngTable.getColumns();
			
			if(primaryKey!=null) {
				Object[] des = new Object[columns.length+1];
				des[0] = primaryKey;
				System.arraycopy(columns, 0, des, 1, columns.length);
				return des;
			} else {
				return columns;
			}			
		} 
		else if (parentElement instanceof IRevEngColumn) {
			return new Object[0];
		} else if (parentElement instanceof IRevEngPrimaryKey) {
			IRevEngPrimaryKey pk = (IRevEngPrimaryKey) parentElement;
			IRevEngGenerator generator = pk.getGenerator();
			IRevEngColumn[] columns = pk.getColumns();
			if(generator!=null) {
				Object[] des = new Object[columns.length+1];
				des[0] = generator;
				System.arraycopy(columns, 0, des, 1, columns.length);
				return des;
			} else {
				return columns;
			}			
		} else if (parentElement instanceof IRevEngGenerator) { 
			IRevEngGenerator generator = (IRevEngGenerator) parentElement;
			return generator.getParameters();
		}
		else {
			return new Object[0];
		}
	}

	public Object getParent(Object element) {		
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof IRevEngColumn || element instanceof IRevEngParameter) {
			return false;
		} else {
			return true;
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh();
	}
	
	

}
