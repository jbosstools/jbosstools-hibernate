/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.eclipse.console.workbench.TableModelList;

public class TableFilterList extends TableModelList {
	public TableFilterList() { }
	
	public void addTableFilter(TableFilter tf) {
		add( tf );
	}

	public void removeTableFilter(TableFilter tf) {
		remove( tf );
	}
}