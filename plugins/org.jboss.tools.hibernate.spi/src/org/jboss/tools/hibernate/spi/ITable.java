package org.jboss.tools.hibernate.spi;

import java.util.Iterator;

public interface ITable {

	String getName();
	void addColumn(IColumn column);
	void setPrimaryKey(IPrimaryKey pk);
	String getCatalog();
	String getSchema();
	IPrimaryKey getPrimaryKey();
	Iterator<IColumn> getColumnIterator();
	Iterator<IForeignKey> getForeignKeyIterator();
	String getComment();
	String getRowId();
	String getSubselect();
	boolean hasDenormalizedTables();
	boolean isAbstract();
	boolean isAbstractUnionTable();
	boolean isPhysicalTable();
	IValue getIdentifierValue();

}
