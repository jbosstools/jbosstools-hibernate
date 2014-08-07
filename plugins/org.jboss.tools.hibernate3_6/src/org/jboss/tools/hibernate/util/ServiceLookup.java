package org.jboss.tools.hibernate.util;

import org.jboss.tools.hibernate.spi.IService;

public class ServiceLookup {
	
	public static IService service() {
		return org.jboss.tools.hibernate.spi.ServiceLookup.findService("3.6");
	}

}
