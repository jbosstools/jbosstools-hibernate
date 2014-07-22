package org.jboss.tools.hibernate.util;

import org.hibernate.console.ext.HibernateExtensionDefinition;
import org.hibernate.console.ext.HibernateExtensionManager;
import org.jboss.tools.hibernate.spi.IService;

public class ServiceLookup {
	
	public static final ServiceLookup INSTANCE = new ServiceLookup();
	
	public IService getService(String hibernateVersion) {
		HibernateExtensionDefinition hed = HibernateExtensionManager.findHibernateExtensionDefinition(hibernateVersion);
		return hed.createHibernateExtensionInstance().getHibernateService();
	}

}
