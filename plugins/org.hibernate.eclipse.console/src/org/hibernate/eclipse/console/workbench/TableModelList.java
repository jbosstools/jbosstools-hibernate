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
package org.hibernate.eclipse.console.workbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/** simple list to be used in e.g. tables */
public abstract class TableModelList extends Observable {

	final protected List<Object> filters;

	public TableModelList() {
		this(new ArrayList<Object>());
	}
	
	public TableModelList(List<Object> queryParameterList) {
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

	public List<Object> getList() {
		return filters;
	}
}
