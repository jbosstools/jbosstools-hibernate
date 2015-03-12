package org.jboss.tools.hibernate.runtime.common;

import java.io.File;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;

public abstract class AbstractOverrideRepositoryFacade 
extends AbstractFacade 
implements IOverrideRepository {

	public AbstractOverrideRepositoryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void addFile(File file) {
		Util.invokeMethod(
				getTarget(), 
				"addFile", 
				new Class[] { File.class }, 
				new Object[] { file });
	}

}
