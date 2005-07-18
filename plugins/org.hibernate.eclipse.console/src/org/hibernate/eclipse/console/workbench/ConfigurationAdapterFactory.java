package org.hibernate.eclipse.console.workbench;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.BaseNode;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Value;

public class ConfigurationAdapterFactory implements IAdapterFactory {

	IDeferredWorkbenchAdapter configuration = new ConfigurationWorkbenchAdapter();
	IDeferredWorkbenchAdapter knownConfigurations = new KnownConfigurationsWorkbenchAdapter();
	IDeferredWorkbenchAdapter consoleConfiguration = new ConsoleConfigurationWorkbenchAdapter();
	IDeferredWorkbenchAdapter persistentClass = new PersistentClassWorkbenchAdapter();
	IDeferredWorkbenchAdapter property = new PropertyWorkbenchAdapter();
	IDeferredWorkbenchAdapter value = new ValueWorkbenchAdapter();
	IDeferredWorkbenchAdapter baseNode = new BaseNodeWorkbenchAdapter();
	
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if((adapterType==IDeferredWorkbenchAdapter.class || adapterType==IWorkbenchAdapter.class)) {
			if(adaptableObject instanceof Configuration) {
				return configuration;
			}
			if(adaptableObject instanceof KnownConfigurations) {
				return knownConfigurations;
			}
			if(adaptableObject instanceof ConsoleConfiguration) {
				return consoleConfiguration;
			}
			if(adaptableObject instanceof PersistentClass) {
				return persistentClass;
			}
			if(adaptableObject instanceof Property) {
				return property;
			}
			if(adaptableObject instanceof Value) {
				return value;
			}
			if(adaptableObject instanceof BaseNode) {
				return baseNode;
			}
		}
		
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IDeferredWorkbenchAdapter.class, IWorkbenchAdapter.class };
	}

	public void registerAdapters(IAdapterManager adapterManager) {
		adapterManager.registerAdapters(this, Configuration.class);
		adapterManager.registerAdapters(this, KnownConfigurations.class);
		adapterManager.registerAdapters(this, ConsoleConfiguration.class);
		adapterManager.registerAdapters(this, RootClass.class);
		adapterManager.registerAdapters(this, Subclass.class);
		adapterManager.registerAdapters(this, Property.class);
		adapterManager.registerAdapters(this, Value.class);
		
		adapterManager.registerAdapters(this, BaseNode.class);
	}

}
