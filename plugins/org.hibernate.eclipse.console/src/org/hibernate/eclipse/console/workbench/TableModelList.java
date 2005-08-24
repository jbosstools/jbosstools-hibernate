package org.hibernate.eclipse.console.workbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/** simple list to be used in e.g. tables */
public abstract class TableModelList extends Observable {

	final protected List filters;

	public TableModelList() {
		this(new ArrayList());
	}
	
	public TableModelList(List queryParameterList) {
		filters = queryParameterList;
	}

	public void moveUp(Object tf) {
		move( tf, -1 );
	}

	private void move(Object tf, int shift) {
		int i = filters.indexOf(tf);
		
		if(i>=0) {
			if(i+shift<filters.size() && i+shift>=0) { 
				filters.remove(i);
				filters.add(i+shift, tf);
			}
		}
		setChanged();
		notifyObservers();
	}

	public void moveDown(Object tf) {
		move( tf, 1 );
	}

	protected void add(Object tf) {
		filters.add(tf);
		setChanged();
		notifyObservers();
	}

	protected void remove(Object tf) {
		filters.remove(tf);
		setChanged();
		notifyObservers();
	}

	public List getList() {
		return filters;
	}
}
