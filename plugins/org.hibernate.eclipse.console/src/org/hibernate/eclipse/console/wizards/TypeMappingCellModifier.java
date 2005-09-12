/**
 * 
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.eclipse.console.model.ITypeMapping;

final public class TypeMappingCellModifier implements ICellModifier {
	private final TableViewer tv;

	public TypeMappingCellModifier(TableViewer tv) {
		this.tv = tv;
	}

	public void modify(Object element, String property, Object value) {
		ITypeMapping tf = (ITypeMapping) ((TableItem)element).getData();
		if("jdbctype".equals(property)) {
			if(!safeEquals( value, tf.getJDBCType() )) {
				tf.setJDBCType((String) value);
			}
		}
		if("hibernatetype".equals(property)) {
			if(!safeEquals( value, tf.getHibernateType())) {
				tf.setHibernateType((String) value);
			}
		}
		if("length".equals(property)) {
			if(!safeEquals(value, tf.getLength())) {
				tf.setLength((Integer) value);
			}
		}
		if("precision".equals(property)) {
			if(!safeEquals(value, tf)) {
				tf.setPrecision((Integer) value);
			}
		}
		if("scale".equals(property)) {
			if(!safeEquals(value,tf.getScale())) {
				tf.setScale((Integer) value);
			}
		}
		tv.update(new Object[] { tf }, new String[] { property });
	}

	private boolean safeEquals(Object value, Object tf) {
		if(value==tf) return true;
		if(value==null) return false;
		return value.equals(tf);
	}

	public Object getValue(Object element, String property) {
		ITypeMapping tf = (ITypeMapping) element;
		if("precision".equals(property)) {
			return tf.getPrecision();
		}
		if("jdbctype".equals(property)) {
			return tf.getJDBCType();
		}
		if("hibernatetype".equals(property)) {
			return tf.getHibernateType();
		}
		if("scale".equals(property)) {
			return tf.getScale();
		}		

		if("length".equals(property)) {
			return tf.getLength();
		}		
		return null;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}
}