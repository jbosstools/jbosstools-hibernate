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
package org.jboss.tools.hibernate3_5.console;

import java.util.Collection;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.console.ext.HibernateExtension;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.common.CollectionPropertySource;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.spi.IClassMetadata;
import org.jboss.tools.hibernate.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.spi.ISession;



public class EntityPropertySource implements IPropertySource2
{
	private Object reflectedObject;
	private IPropertyDescriptor[] propertyDescriptors;

	private final HibernateExtension extension;
	private final ISession currentSession;
	private IClassMetadata classMetadata;

	public EntityPropertySource(final Object object, final ISession currentSession, HibernateExtension extension)
	{
		this.currentSession = currentSession;
		this.extension = extension;
		reflectedObject = object;
		if(currentSession.isOpen()) {
			classMetadata = currentSession.getSessionFactory().getClassMetadata( currentSession.getEntityName(reflectedObject) );
		} else {
			classMetadata = currentSession.getSessionFactory().getClassMetadata( HibernateProxyHelper.getClassWithoutInitializingProxy(reflectedObject));
		}

	}


	public Object getEditableValue() {
		return ""; //$NON-NLS-1$
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (propertyDescriptors == null) {
			if (extension != null) {
				if (!extension.hasExecutionContext()) {
					extension.build();
				}
				extension.execute(new Command() {
					public Object execute() {
						propertyDescriptors = initializePropertyDescriptors(classMetadata);
						return null;
					}
				});
			}
		}
		return propertyDescriptors;
	}

	static protected IPropertyDescriptor[] initializePropertyDescriptors(IClassMetadata classMetadata) {

		String[] propertyNames = classMetadata.getPropertyNames();
		int length = propertyNames.length;

		PropertyDescriptor identifier = null;

		if(classMetadata.hasIdentifierProperty() ) {
			identifier = new PropertyDescriptor(classMetadata.getIdentifierPropertyName(), classMetadata.getIdentifierPropertyName());
			identifier.setCategory(HibernateConsoleMessages.EntityPropertySource_identifier);
			length++;
		}

		PropertyDescriptor[] properties = new PropertyDescriptor[length];

		int idx = 0;
		if(identifier!=null) {
			properties[idx++] = identifier;
		}

		for (int i = 0; i < propertyNames.length; i++) {
			 PropertyDescriptor prop = new PropertyDescriptor(propertyNames[i],propertyNames[i]);
			 prop.setCategory(HibernateConsoleMessages.EntityPropertySource_properties);
			 properties[i+idx] = prop;
		}

		return properties;
	}


	public Object getPropertyValue(Object id) {
		Object propertyValue;

		if(id.equals(classMetadata.getIdentifierPropertyName())) {
			propertyValue = classMetadata.getIdentifier(reflectedObject);
		} else {
			try {
				propertyValue = classMetadata.getPropertyValue(reflectedObject, (String)id);
			} catch (HibernateException he) {
				propertyValue = HibernateConsoleMessages.EntityPropertySource_unable_to_resolve_property;
				if (classMetadata instanceof AbstractEntityPersister) {
					AbstractEntityPersister aep = (AbstractEntityPersister)classMetadata;
					EntityMetamodel emm = aep.getEntityMetamodel();
					if (emm != null) {
						Integer idx = emm.getPropertyIndexOrNull((String)id);
						if (idx != null) {
							propertyValue = emm.getTuplizer(EntityMode.POJO).getPropertyValue(reflectedObject, idx);
						}
					}
				}
			}
		}

		if (propertyValue instanceof Collection<?>) {
			ICollectionMetadata collectionMetadata = currentSession.getSessionFactory().getCollectionMetadata(classMetadata.getEntityName() + "." + id); //$NON-NLS-1$
			if(collectionMetadata!=null) {
				propertyValue = new CollectionPropertySource((Collection<?>) propertyValue);
			}
		}
		return propertyValue;
	}

	public boolean isPropertySet(Object id) {
		return false; // we can not decide this at the given point.
	}

	public void resetPropertyValue(Object id) {

	}

	public void setPropertyValue(Object id, Object value) {
		// lets not support editing in the raw properties view - to flakey ui.
		//classMetadata.setPropertyValue(reflectedObject, (String) id, value, EntityMode.POJO);
	}

	public boolean isPropertyResettable(Object id) {
		return false;
	}

}