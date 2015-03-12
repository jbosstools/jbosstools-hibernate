package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;

public class ReverseEngineeringSettingsProxy implements IReverseEngineeringSettings {
	
	private ReverseEngineeringSettings target = null;
	
	public ReverseEngineeringSettingsProxy(
			IFacadeFactory facadeFactory,
			ReverseEngineeringSettings settings) {
		target = settings;
	}

	@Override
	public IReverseEngineeringSettings setDefaultPackageName(String str) {
		target.setDefaultPackageName(str);
		return this;
	}

	@Override
	public IReverseEngineeringSettings setDetectManyToMany(boolean b) {
		target.setDetectManyToMany(b);
		return this;
	}

	@Override
	public IReverseEngineeringSettings setDetectOneToOne(boolean b) {
		target.setDetectOneToOne(b);
		return this;
	}

	@Override
	public IReverseEngineeringSettings setDetectOptimisticLock(boolean b) {
		target.setDetectOptimisticLock(b);
		return this;
	}

	public ReverseEngineeringSettings getTarget() {
		return target;
	}

}
