package org.jboss.tools.hibernate.orm.runtime.exp.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.orm.jbt.wrp.ColumnWrapper;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.GenericFacadeUtil;
import org.jboss.tools.hibernate.orm.runtime.exp.internal.util.NewFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.AbstractForeignKeyFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.runtime.spi.IColumn;

public class ForeignKeyFacadeImpl extends AbstractForeignKeyFacade {

	public ForeignKeyFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	@Override
	public Iterator<IColumn> columnIterator() {
		ArrayList<IColumn> facades = new ArrayList<IColumn>();
		Iterator<Column> iterator = ((ForeignKey)getTarget()).getColumnIterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			IColumn columnFacade = NewFacadeFactory.INSTANCE.createColumn(null);
			GenericFacadeUtil.setTarget((IFacade)columnFacade, column);
			facades.add(columnFacade);
		}
		return facades.iterator();
	}

	@Override
	public boolean containsColumn(IColumn column) {
		try {
			Field wrappedColumnField = ColumnWrapper.class.getDeclaredField("wrappedColumn");
			wrappedColumnField.setAccessible(true);
			Column c = (Column)wrappedColumnField.get(((IFacade)column).getTarget());
			return ((ForeignKey)getTarget()).containsColumn(c);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
