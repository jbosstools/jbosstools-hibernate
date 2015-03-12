package org.jboss.tools.hibernate.runtime.common;

import java.io.File;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;

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

	@Override
	public void addTableFilter(ITableFilter tf) {
		assert tf instanceof IFacade;
		Util.invokeMethod(
				getTarget(), 
				"addTableFilter", 
				new Class[] { getTableFilterClass() }, 
				new Object[] { tf });
	}
	
	protected Class<?> getTableFilterClass() {
		return Util.getClass(getTableFilterClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getTableFilterClassName() {
		return "org.hibernate.cfg.reveng.TableFilter";
	}

}
