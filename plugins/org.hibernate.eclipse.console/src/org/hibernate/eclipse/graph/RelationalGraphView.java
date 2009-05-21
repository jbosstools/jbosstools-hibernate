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
		Iterator<Object> iterator = selection.iterator();
		
				
		List<TableViewAdapter> tables=new ArrayList<TableViewAdapter>();
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
