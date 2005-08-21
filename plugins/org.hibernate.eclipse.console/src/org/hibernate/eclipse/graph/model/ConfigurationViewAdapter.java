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
	private Map persistentClasses; // key: name, value: PersistentClassViewAdapter
	//private final Map sourceAssociations; // key: name, value: List of AssociationViewAdapter
	//private final Map targetAssociations; // key: name, value: List of AssociationViewAdapter
	private boolean manualLayout = false;

	public ConfigurationViewAdapter(Configuration cfg) {
		this.cfg = cfg;		
		
		//sourceAssociations = new HashMap();
		//targetAssociations = new HashMap();
	}

	public List getPersistentClasses() {
		if(persistentClasses==null) {
			Iterator classMappings = cfg.getClassMappings();
			persistentClasses = new HashMap();
			while ( classMappings.hasNext() ) {
				PersistentClass clazz = (PersistentClass) classMappings.next();
				persistentClasses.put( clazz.getEntityName(), new PersistentClassViewAdapter(this, clazz) );
			}
			
			Iterator iterator = persistentClasses.values().iterator();
			while ( iterator.hasNext() ) {
				PersistentClassViewAdapter element = (PersistentClassViewAdapter) iterator.next();
				element.getSourceAssociations();				
			}
		}
		
		return new ArrayList(persistentClasses.values());
	}

	/*public List getSourceAssociations(String key) {
		List list = (List) sourceAssociations.get(key);
		return list;		
	}*/

	/*public List getTargetAssociations(String key) {
		List list = (List) targetAssociations.get(key);
		return list;		
	}*/

	public PersistentClassViewAdapter getPersistentClassViewAdapter(String associatedEntityName) {
		return (PersistentClassViewAdapter) persistentClasses.get(associatedEntityName);		
	}

	/*public void addAssociation(AssociationViewAdapter adapter) {
		List l = getSourceAssociations(adapter.getSourceName());
		if(l==null) {
			l = new ArrayList();
			sourceAssociations.put(adapter.getSourceName(), l);
		}
		l.add(adapter);
		
		l = getTargetAssociations(adapter.getTargetName());
		if(l==null) {
			l = new ArrayList();
			targetAssociations.put(adapter.getTargetName(), l);
		}
		l.add(adapter);		
	}*/

	public boolean isManualLayoutDesired() {
		return manualLayout;
	}
	
	public void setManualLayoutDesired(boolean b) {
		manualLayout = b;
		setChanged();
		notifyObservers("manualLayout");
	}

}
