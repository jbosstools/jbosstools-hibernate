package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PrimaryKey;

public interface ITable {

	String getName();
	void addColumn(Column column);
	void setPrimaryKey(PrimaryKey pk);
	String getCatalog();
	String getSchema();
	PrimaryKey getPrimaryKey();
	Iterator<Column> getColumnIterator();
	Iterator<ForeignKey> getForeignKeyIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables();
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();

}
