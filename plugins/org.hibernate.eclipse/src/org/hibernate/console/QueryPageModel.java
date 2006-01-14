/*
 * Created on 20-04-2003
 *
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

	List pages = new ArrayList();

	public int getSize() {
		return pages.size();
	}

	public Object getElementAt(int index) {
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
			QueryPage element = (QueryPage) pages.get(i);
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
		
		QueryPage qp = (QueryPage) pages.remove(i);
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
		return (QueryPage) getElementAt(i);
	}

	/**
	 * 
	 */
	protected void update(int index) {
		fireContentsChanged(pages, index, index);
	}

	public Iterator getPages() {
		return pages.iterator();
	}

	public List getPagesAsList() {
		return new ArrayList(pages);
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
