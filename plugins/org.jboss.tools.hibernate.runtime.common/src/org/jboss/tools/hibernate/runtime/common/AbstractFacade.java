package org.jboss.tools.hibernate.runtime.common;

import org.jboss.tools.hibernate.runtime.common.internal.Util;

public abstract class AbstractFacade {

	private Object target = null;
	
	protected abstract String getTargetClassName();
	
	public Object getTarget() {
		if (target == null) {
			target = Util.getInstance(getTargetClassName(), this);
		}
		return target;
	}

}
