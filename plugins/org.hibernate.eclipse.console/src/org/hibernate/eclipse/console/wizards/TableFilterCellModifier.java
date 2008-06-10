/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.eclipse.console.model.ITableFilter;

final class TableFilterCellModifier implements ICellModifier {
	private final TableViewer tv;

	public TableFilterCellModifier(TableViewer tv) {
		this.tv = tv;
	}

	public void modify(Object element, String property, Object value) {
		ITableFilter tf = (ITableFilter) ((TableItem)element).getData();
		if("inclusion".equals(property)) { //$NON-NLS-1$
			if(!value.equals(tf.getExclude())) {
				tf.setExclude((Boolean) value);
			}
		}
		if("catalog".equals(property)) { //$NON-NLS-1$
			if(!value.equals(tf.getMatchCatalog())) {
				tf.setMatchCatalog((String) value);
			}
		}
		if("schema".equals(property)) { //$NON-NLS-1$
			if(!value.equals(tf.getMatchSchema())) {
				tf.setMatchSchema((String) value);
			}
		}
		if("name".equals(property)) { //$NON-NLS-1$
			if(!value.equals(tf.getMatchName())) {
				tf.setMatchName((String) value);
			}
		}			
		tv.update(new Object[] { tf }, new String[] { property });
	}

	public Object getValue(Object element, String property) {
		ITableFilter tf = (ITableFilter) element;
		if("inclusion".equals(property)) { //$NON-NLS-1$
			return tf.getExclude();
		}
		if("catalog".equals(property)) { //$NON-NLS-1$
			return tf.getMatchCatalog();
		}
		if("schema".equals(property)) { //$NON-NLS-1$
			return tf.getMatchSchema();
		}
		if("name".equals(property)) { //$NON-NLS-1$
			return tf.getMatchName();
		}		
		return null;
	}

	public boolean canModify(Object element, String property) {
		return true;
	}
}