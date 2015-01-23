package org.jboss.tools.hibernate.runtime.spi;

import java.util.Iterator;
import java.util.List;

public interface IPrimaryKey {

	void addColumn(IColumn column);
	int getColumnSpan();
	List<IColumn> getColumns();
	IColumn getColumn(int i);
	ITable getTable();
	boolean containsColumn(IColumn column);
	Iterator<IColumn> columnIterator();
	String getName();

}
