package org.jboss.tools.hibernate.spi;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;

public interface IReverseEngineeringSettings {

	IReverseEngineeringSettings setDefaultPackageName(String str);
	IReverseEngineeringSettings setDetectManyToMany(boolean b);
	IReverseEngineeringSettings setDetectOneToOne(boolean b);
	IReverseEngineeringSettings setDetectOptimisticLock(boolean b);
	ReverseEngineeringSettings getTarget();

}
