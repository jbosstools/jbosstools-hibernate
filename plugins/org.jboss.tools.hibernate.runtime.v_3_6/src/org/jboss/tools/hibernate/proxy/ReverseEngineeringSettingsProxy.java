package org.jboss.tools.hibernate.proxy;

import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.common.AbstractReverseEngineeringSettingsFacade;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;

public class ReverseEngineeringSettingsProxy 
extends AbstractReverseEngineeringSettingsFacade {
	
	private ReverseEngineeringSettings target = null;
	
	public ReverseEngineeringSettingsProxy(
			IFacadeFactory facadeFactory,
			ReverseEngineeringSettings settings) {
		super(facadeFactory, settings);
		target = settings;
	}

	@Override
	public IReverseEngineeringSettings setDefaultPackageName(String str) {
		getTarget().setDefaultPackageName(str);
		return this;
	}

	@Override
	public IReverseEngineeringSettings setDetectManyToMany(boolean b) {
		getTarget().setDetectManyToMany(b);
		return this;
	}

	@Override
	public IReverseEngineeringSettings setDetectOneToOne(boolean b) {
		getTarget().setDetectOneToOne(b);
		return this;
	}

	@Override
	public IReverseEngineeringSettings setDetectOptimisticLock(boolean b) {
		getTarget().setDetectOptimisticLock(b);
		return this;
	}

	public ReverseEngineeringSettings getTarget() {
		return target;
	}

}
