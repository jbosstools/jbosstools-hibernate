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

	@Override
	public void addColumn(IColumn column) {
		assert column instanceof IFacade;
		Object columnTarget = Util.invokeMethod(
				column, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"addColumn", 
				new Class[] { getColumnClass() }, 
				new Object[] { columnTarget });
		columns = null;
	}
	
	protected Class<?> getColumnClass() {
		return Util.getClass(getColumnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getColumnClassName() {
		return "org.hibernate.mapping.Column";
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
