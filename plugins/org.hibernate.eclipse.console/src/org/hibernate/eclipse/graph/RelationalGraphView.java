package org.hibernate.eclipse.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.hibernate.eclipse.graph.model.ConfigurationViewAdapter;
import org.hibernate.eclipse.graph.model.TableViewAdapter;
import org.hibernate.eclipse.graph.parts.ConfigurationEditPart;
import org.hibernate.eclipse.graph.parts.PersistentClassEditPart;
import org.hibernate.mapping.Table;

public class RelationalGraphView extends AbstractGraphViewPart {

	public RelationalGraphView() {
		super();
	}

	protected void setupListener() {
		getSite().getPage().addSelectionListener(EntityGraphView.ID, listener);
	}

	protected void disposeListeners() {
		getSite().getPage().removeSelectionListener(EntityGraphView.ID, listener);
	}

	protected void selectionChanged(IStructuredSelection selection) {
		Iterator iterator = selection.iterator();
		
				
		List tables=new ArrayList();
		ConfigurationViewAdapter cv = null;
		while(iterator.hasNext()) {
			Object o = iterator.next();
			if(o instanceof PersistentClassEditPart) {
				PersistentClassEditPart pe = (PersistentClassEditPart) o;
				if(cv==null) {
					cv = new ConfigurationViewAdapter(pe.getPersistentClassViewAdapter().getConfiguration().getConfiguration());
				} 
				
				Table table = pe.getPersistentClassViewAdapter().getPersistentClass().getTable();
				tables.add(new TableViewAdapter(cv,table));
				
			}			
		}		
		
		if(cv!=null) {
			cv.setSelectedTables(tables);
			
			viewer.setContents(cv);
			((ConfigurationEditPart)viewer.getContents()).setManualLayoutActive(true);
			
		}
	}
	
	
	protected HibernateConfigurationPartFactory createEditPartFactory() {
		return new RelationalConfigurationEditPartFactory();
	}
	

}
