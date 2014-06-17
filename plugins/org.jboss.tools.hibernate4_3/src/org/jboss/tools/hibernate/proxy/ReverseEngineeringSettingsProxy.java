package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.jboss.tools.hibernate.spi.IReverseEngineeringSettings;

public class ReverseEngineeringSettingsProxy implements IReverseEngineeringSettings {
	
	private ReverseEngineeringSettings target = null;
	
	public ReverseEngineeringSettingsProxy(ReverseEngineeringSettings settings) {
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

	@Override
	public ReverseEngineeringSettings getTarget() {
		return target;
	}

}
