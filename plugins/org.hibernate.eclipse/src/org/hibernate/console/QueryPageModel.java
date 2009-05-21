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
package org.hibernate.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;


/**
 * @author max
 *
 */
public class QueryPageModel extends AbstractListModel {

	List<QueryPage> pages = new ArrayList<QueryPage>();

	public int getSize() {
		return pages.size();
	}

	public QueryPage getElementAt(int index) {
		return pages.get(index);
	}

	PropertyChangeListener pcl = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			update(pages.indexOf(evt.getSource() ) );
		}
	};
	/**
	 * @param qp
	 */
	public void add(QueryPage qp) {	
		
		for (int i = pages.size() - 1; i >= 0; i--) {
			QueryPage element = pages.get(i);
			if (!element.isSticky() ) {
                pages.remove(i);
			}
		}
		//TODO: should we monitor each querypage ? qp.addPropertyChangeListener(pcl);
        pages.add(qp);
		fireIntervalAdded(this, 0, pages.size() );
	}

	/**
	 * @param i
	 */
	public void remove(int i) {
		
		QueryPage qp = pages.remove(i);
		if(qp!=null) {
			qp.removePropertyChangeListener(pcl);
		}
		fireIntervalRemoved(pages, i, i);

	}

	/**
	 * @param i
	 * @return
	 */
	public QueryPage get(int i) {
		return getElementAt(i);
	}

	/**
	 * 
	 */
	protected void update(int index) {
		fireContentsChanged(pages, index, index);
	}

	public Iterator<QueryPage> getPages() {
		return pages.iterator();
	}

	public List<QueryPage> getPagesAsList() {
		return new ArrayList<QueryPage>(pages);
	}

	public boolean remove(QueryPage page) {
		boolean b = pages.remove(page);
		if(b) {
			fireContentsChanged(pages, 0, getSize() );
			page.release();
		}
		return b;
	}

}
