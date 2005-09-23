package org.hibernate.eclipse.console.workbench;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.BaseNode;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;

public class ConfigurationAdapterFactory implements IAdapterFactory {

	private Class[] classes;
	private IDeferredWorkbenchAdapter[] adapters;
	
	
	public ConfigurationAdapterFactory() {
		Map map = new HashMap();
		
		map.put(ConsoleConfiguration.class, new ConsoleConfigurationWorkbenchAdapter());
		map.put(Configuration.class, new ConfigurationWorkbenchAdapter());
		map.put(KnownConfigurations.class, new KnownConfigurationsWorkbenchAdapter());
		map.put(PersistentClass.class, new PersistentClassWorkbenchAdapter());
		map.put(Property.class, new PropertyWorkbenchAdapter());
		map.put(Value.class, new ValueWorkbenchAdapter());
		map.put(BaseNode.class, new BaseNodeWorkbenchAdapter());
		map.put(LazyDatabaseSchema.class, new LazyDatabaseSchemaWorkbenchAdapter());
		map.put(TableContainer.class, new TableContainerWorkbenchAdapter());
		map.put(Table.class, new TableWorkbenchAdapter());
		map.put(PrimaryKey.class, new PrimaryKeyWorkbenchAdapter());
		map.put(Column.class, new ColumnWorkbenchAdapter());				
		
		classes = new Class[map.size()];
		adapters = new IDeferredWorkbenchAdapter[map.size()];
		
		Iterator iter = map.entrySet().iterator();
		int cnt = 0;
		while ( iter.hasNext() ) {
			Map.Entry entry = (Map.Entry) iter.next();
			classes[cnt] = (Class) entry.getKey();
			adapters[cnt] = (IDeferredWorkbenchAdapter) entry.getValue();
			cnt++;			
		}
	}
	
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if((adapterType==IDeferredWorkbenchAdapter.class || adapterType==IWorkbenchAdapter.class)) {
			return getAdapter( adaptableObject );
		}		
		if(adapterType==IPropertySource2.class || adapterType==IPropertySource.class) {
			return getPropertySource(adaptableObject);
		}
		return null;
	}

	
	private Object getPropertySource(Object adaptableObject) {	
		return null;//new GenericPropertySource(adaptableObject);		
	}

	private Object getAdapter(Object adaptableObject) {
		for (int i = 0; i < classes.length; i++) {
			Class clazz = classes[i];
			if (clazz.isInstance(adaptableObject)) {
				return adapters[i];
			}
		}		
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IDeferredWorkbenchAdapter.class, IWorkbenchAdapter.class, IPropertySource.class, IPropertySource2.class };
	}

	public void registerAdapters(IAdapterManager adapterManager) {
		for (int i = 0; i < classes.length; i++) {
			Class clazz = classes[i];
			adapterManager.registerAdapters(this, clazz);
		}		
	}

}
