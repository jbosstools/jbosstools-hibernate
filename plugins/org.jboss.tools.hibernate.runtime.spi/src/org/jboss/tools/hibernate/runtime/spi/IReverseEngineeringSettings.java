package org.jboss.tools.hibernate.runtime.spi;


public interface IReverseEngineeringSettings {

	IReverseEngineeringSettings setDefaultPackageName(String str);
	IReverseEngineeringSettings setDetectManyToMany(boolean b);
	IReverseEngineeringSettings setDetectOneToOne(boolean b);
	IReverseEngineeringSettings setDetectOptimisticLock(boolean b);

}
