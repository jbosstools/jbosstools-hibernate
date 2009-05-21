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
package org.hibernate.eclipse.graph.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;

public class ConfigurationViewAdapter extends Observable {

	private final Configuration cfg;
	private Map<String, PersistentClassViewAdapter> persistentClasses; // key: name, value: PersistentClassViewAdapter
	private List<TableViewAdapter> selectedTables;
	//private final Map sourceAssociations; // key: name, value: List of AssociationViewAdapter
	//private final Map targetAssociations; // key: name, value: List of AssociationViewAdapter
	
	public ConfigurationViewAdapter(Configuration cfg) {
		this.cfg = cfg;		
		
		//sourceAssociations = new HashMap();
		//targetAssociations = new HashMap();
	}

	public List<PersistentClassViewAdapter> getPersistentClasses() {
		if(persistentClasses==null) {
			Iterator<PersistentClass> classMappings = cfg.getClassMappings();
			persistentClasses = new HashMap<String, PersistentClassViewAdapter>();
			while ( classMappings.hasNext() ) {
				PersistentClass clazz = classMappings.next();
				persistentClasses.put( clazz.getEntityName(), new PersistentClassViewAdapter(this, clazz) );
			}
			
			Iterator<PersistentClassViewAdapter> iterator = persistentClasses.values().iterator();
			while ( iterator.hasNext() ) {
				PersistentClassViewAdapter element = iterator.next();
				element.getSourceAssociations();				
			}
		}
		
		return new ArrayList<PersistentClassViewAdapter>(persistentClasses.values());
	}

	public PersistentClassViewAdapter getPersistentClassViewAdapter(String associatedEntityName) {
		return persistentClasses.get(associatedEntityName);		
	}

	public List<TableViewAdapter> getSelectedTables() {		
		return selectedTables;
	}

	public Configuration getConfiguration() {
		return cfg;
	}

	public void setSelectedTables(List<TableViewAdapter> tables) {
		selectedTables = tables;		
	}
	
	
}
