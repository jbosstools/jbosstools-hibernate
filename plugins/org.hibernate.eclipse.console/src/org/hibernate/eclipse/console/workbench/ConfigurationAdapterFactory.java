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
package org.hibernate.eclipse.console.workbench;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.BaseNode;
import org.hibernate.mapping.PrimaryKey;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IValue;

public class ConfigurationAdapterFactory implements IAdapterFactory {

	private Class<?>[] classes;
	private IWorkbenchAdapter[] adapters;
	
	private Class<?>[] deferredClasses;
	private IDeferredWorkbenchAdapter[] deferredAdapters;
	
	
	public ConfigurationAdapterFactory() {
		Map<Class<?>, IDeferredWorkbenchAdapter> deferredMap = new HashMap<Class<?>, IDeferredWorkbenchAdapter>();
		
		deferredMap.put(ConsoleConfiguration.class, new ConsoleConfigurationWorkbenchAdapter());
		deferredMap.put(IConfiguration.class, new ConfigurationWorkbenchAdapter());
		deferredMap.put(KnownConfigurations.class, new KnownConfigurationsWorkbenchAdapter());
		deferredMap.put(LazyDatabaseSchema.class, new LazyDatabaseSchemaWorkbenchAdapter());
		deferredMap.put( LazySessionFactory.class, new LazySessionFactoryAdapter() );
					
		
		deferredClasses = new Class[deferredMap.size()];
		deferredAdapters = new IDeferredWorkbenchAdapter[deferredMap.size()];
		
		int cnt = 0;
		for (Map.Entry<Class<?>, IDeferredWorkbenchAdapter> entry : deferredMap.entrySet()) {
			deferredClasses[cnt] = entry.getKey();
			deferredAdapters[cnt] = entry.getValue();
			cnt++;
		}
		
		Map<Class<?>, IWorkbenchAdapter> map = new HashMap<Class<?>, IWorkbenchAdapter>();
		map.put(TableContainer.class, new TableContainerWorkbenchAdapter());
		map.put(IPersistentClass.class, new PersistentClassWorkbenchAdapter());
		map.put(IProperty.class, new PropertyWorkbenchAdapter());
		map.put(IValue.class, new ValueWorkbenchAdapter());
		map.put(ITable.class, new TableWorkbenchAdapter());
		map.put(PrimaryKey.class, new PrimaryKeyWorkbenchAdapter());
		map.put(IColumn.class, new ColumnWorkbenchAdapter());
		map.put(BaseNode.class, new BaseNodeWorkbenchAdapter());
		
		
		
		classes = new Class[map.size()];
		adapters = new IWorkbenchAdapter[map.size()];
		cnt = 0;
		for (Map.Entry<Class<?>, IWorkbenchAdapter> entry : map.entrySet()) {
			classes[cnt] = entry.getKey();
			adapters[cnt] = entry.getValue();
			cnt++;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType==IDeferredWorkbenchAdapter.class){
			return getDeferredAdapter( adaptableObject );
		} else if (adapterType==IWorkbenchAdapter.class){
			Object adapter = getAdapter( adaptableObject );
			return adapter != null ? adapter : getDeferredAdapter( adaptableObject );
		}
		if(adapterType==IPropertySource2.class || adapterType==IPropertySource.class) {
			return getPropertySource(adaptableObject);
		}
		return null;
	}

	
	private Object getPropertySource(Object adaptableObject) {	
		return new GenericPropertySource(adaptableObject);		
	}
	
	private Object getDeferredAdapter(Object adaptableObject) {
		for (int i = 0; i < deferredClasses.length; i++) {
			Class<?> clazz = deferredClasses[i];
			if (clazz.isInstance(adaptableObject)) {
				return deferredAdapters[i];
			}
		}		
		return null;
	}

	private Object getAdapter(Object adaptableObject) {
		for (int i = 0; i < classes.length; i++) {
			Class<?> clazz = classes[i];
			if (clazz.isInstance(adaptableObject)) {
				return adapters[i];
			}
		}		
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IDeferredWorkbenchAdapter.class, IWorkbenchAdapter.class, IPropertySource.class, IPropertySource2.class };
	}

	public void registerAdapters(IAdapterManager adapterManager) {
		for (int i = 0; i < classes.length; i++) {
			Class<?> clazz = classes[i];
			adapterManager.registerAdapters(this, clazz);
		}
		for (int i = 0; i < deferredClasses.length; i++) {
			Class<?> clazz = deferredClasses[i];
			adapterManager.registerAdapters(this, clazz);
		}
	}

}
