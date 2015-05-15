package org.jboss.tools.hibernate.runtime.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPrimaryKey;

public abstract class AbstractPrimaryKeyFacade 
extends AbstractFacade 
implements IPrimaryKey {

	protected List<IColumn> columns = null;

	public AbstractPrimaryKeyFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	protected void initializeColumns() {
		columns = new ArrayList<IColumn>();
		List<?> targetColumns = (List<?>)Util.invokeMethod(
				getTarget(), 
				"getColumns", 
				new Class[] {}, 
				new Object[] {});
		Iterator<?> origin = targetColumns.iterator();
		while (origin.hasNext()) {
			columns.add(getFacadeFactory().createColumn(origin.next()));
		}
	}

}
