package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.search.runtime.spi.IField;

public abstract class AbstractField extends AbstractFacade implements IField {

	public AbstractField(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String name() {
		return Util.invokeMethod(getTarget(), "name", new Class[] {}, new Object[] {}).toString();
	}
	
	@Override
	public String stringValue() {
		return Util.invokeMethod(getTarget(), "stringValue", new Class[] {}, new Object[] {}).toString();
	}

}
