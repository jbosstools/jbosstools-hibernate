package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;
import java.util.Iterator;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.spi.IValueVisitor;

public abstract class AbstractValueFacade 
extends AbstractFacade 
implements IValue {

	protected IValue collectionElement = null;
	protected ITable table = null;
	protected IType type = null;
	protected ITable collectionTable = null;
	protected HashSet<IColumn> columns = null;
	protected IValue key = null;

	public AbstractValueFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public boolean isSimpleValue() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isSimpleValue", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isCollection() {
		return getCollectionClass().isAssignableFrom(getTarget().getClass());
	}
	
	@Override
	public IValue getCollectionElement() {
		if (isCollection() && collectionElement == null) {
			Object targetElement = Util.invokeMethod(
					getTarget(), 
					"getElement", 
					new Class[] {}, 
					new Object[] {});
			if (targetElement != null) {
				collectionElement = getFacadeFactory().createValue(targetElement);
			}
		}
		return collectionElement;
	}

	@Override
	public boolean isOneToMany() {
		return getOneToManyClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isManyToOne() {
		return getManyToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isOneToOne() {
		return getOneToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isMap() {
		return getMapClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isComponent() {
		return getComponentClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isToOne() {
		return getToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public Boolean isEmbedded() {
		Boolean result = null;
		if (isComponent() || isToOne()) {
			result = (Boolean)Util.invokeMethod(
					getTarget(), 
					"isEmbedded", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public Object accept(IValueVisitor valueVisitor) {
		return valueVisitor.accept(this);
	}

	@Override
	public ITable getTable() {
		if (table == null) {
			Object targetTable = Util.invokeMethod(
					getTarget(), 
					"getTable", 
					new Class[] {}, 
					new Object[] {});
			if (targetTable != null) {
				table = getFacadeFactory().createTable(targetTable);
			}
		}
		return table;
	}

	@Override
	public IType getType() {
		if (type == null) {
			Object targetType = Util.invokeMethod(
					getTarget(), 
					"getType", 
					new Class[] {}, 
					new Object[] {});	
			if (targetType != null) {
				type = getFacadeFactory().createType(targetType);
			}
		}
		return type;
	}

	@Override
	public void setElement(IValue element) {
		if (isCollection()) {
			Object elementTarget = Util.invokeMethod(
					element, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setElement", 
					new Class[] { getValueClass() }, 
					new Object[] { elementTarget });
		}
	}

	@Override
	public void setCollectionTable(ITable table) {
		if (isCollection()) {
			collectionTable = table;
			Object tableTarget = Util.invokeMethod(
					table, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setCollectionTable", 
					new Class[] { getTableClass() }, 
					new Object[] { tableTarget });
		}
	}

	@Override
	public void setTable(ITable table) {
		if (isSimpleValue()) {
			Object tableTarget = Util.invokeMethod(
					table, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setTable", 
					new Class[] { getTableClass() }, 
					new Object[] { tableTarget });
		}
	}

	@Override
	public boolean isList() {
		return getListClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public void setIndex(IValue value) {
		Object valueTarget = Util.invokeMethod(
				value, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setIndex", 
				new Class[] { getValueClass() }, 
				new Object[] { valueTarget });
	}

	@Override
	public void setTypeName(String name) {
		if (isSimpleValue()) {
			Util.invokeMethod(
					getTarget(), 
					"setTypeName", 
					new Class[] { String.class }, 
					new Object[] { name });
		}
	}

	@Override
	public String getComponentClassName() {
		String result = null;
		if (isComponent()) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"getComponentClassName", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public Iterator<IColumn> getColumnIterator() {
		if (columns == null) {
			initializeColumns();
		}
		return columns.iterator();
	}
	
	@Override
	public Boolean isTypeSpecified() {
		Boolean result = null;
		if (isSimpleValue()) {
			result = (Boolean)Util.invokeMethod(
					getTarget(), 
					"isTypeSpecified", 
					new Class[] {}, 
					new Object[] {});
		}
		return result; 
	}
	
	@Override
	public String toString() {
		return getTarget().toString();
	}

	@Override
	public ITable getCollectionTable() {
		if (isCollection() && collectionTable == null) {
			Object ct = Util.invokeMethod(
					getTarget(), 
					"getCollectionTable", 
					new Class[] {}, 
					new Object[] {});
			if (ct != null) {
				collectionTable = getFacadeFactory().createTable(ct);
			}
		}
		return collectionTable;
	}

	@Override
	public IValue getKey() {
		if (key == null && isCollection()) {
			Object key = Util.invokeMethod(
					getTarget(), 
					"getKey", 
					new Class[] {}, 
					new Object[] {});
			if (key != null) {
				key = getFacadeFactory().createValue(key);
			}
		}
		return key;
	}

	protected Class<?> getCollectionClass() {
		return Util.getClass(collectionClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToManyClass() {
		return Util.getClass(oneToManyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getManyToOneClass() {
		return Util.getClass(manyToOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToOneClass() {
		return Util.getClass(oneToOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getMapClass() {
		return Util.getClass(mapClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getComponentClass() {
		return Util.getClass(componentClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getToOneClass() {
		return Util.getClass(toOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getValueClass() {
		return Util.getClass(valueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getTableClass() {
		return Util.getClass(tableClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getListClass() {
		return Util.getClass(listClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getColumnClass() {
		return Util.getClass(columnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String collectionClassName() {
		return "org.hibernate.mapping.Collection";
	}
	
	protected String oneToManyClassName() {
		return "org.hibernate.mapping.OneToMany";
	}

	protected String manyToOneClassName() {
		return "org.hibernate.mapping.ManyToOne";
	}

	protected String oneToOneClassName() {
		return "org.hibernate.mapping.OneToOne";
	}

	protected String mapClassName() {
		return "org.hibernate.mapping.Map";
	}

	protected String componentClassName() {
		return "org.hibernate.mapping.Component";
	}

	protected String toOneClassName() {
		return "org.hibernate.mapping.ToOne";
	}

	protected String valueClassName() {
		return "org.hibernate.mapping.Value";
	}

	protected String tableClassName() {
		return "org.hibernate.mapping.Table";
	}

	protected String listClassName() {
		return "org.hibernate.mapping.List";
	}

	protected String columnClassName() {
		return "org.hibernate.mapping.Column";
	}

	protected void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<?> iterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getColumnIterator", 
				new Class[] {}, 
				new Object[] {});
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (getColumnClass().isAssignableFrom(object.getClass())) {
				columns.add(getFacadeFactory().createColumn(object));
			}
		}
	}

}
