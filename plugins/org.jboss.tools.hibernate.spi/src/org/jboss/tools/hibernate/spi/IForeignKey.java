package org.jboss.tools.hibernate.spi;

import java.util.Iterator;
import java.util.List;

public interface IForeignKey {

	ITable getReferencedTable();
	Iterator<IColumn> columnIterator();
	boolean isReferenceToPrimaryKey();
	List<IColumn> getReferencedColumns();
	boolean containsColumn(IColumn column);

}
