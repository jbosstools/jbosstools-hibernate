package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;

public interface ITable {

	String getName();
	void addColumn(IColumn column);
	void setPrimaryKey(PrimaryKey pk);
	String getCatalog();
	String getSchema();
	PrimaryKey getPrimaryKey();
	Iterator<IColumn> getColumnIterator();
	Iterator<ForeignKey> getForeignKeyIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables();
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();

}
