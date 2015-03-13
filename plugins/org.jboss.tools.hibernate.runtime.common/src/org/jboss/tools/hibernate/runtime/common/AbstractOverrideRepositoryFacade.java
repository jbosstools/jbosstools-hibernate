package org.jboss.tools.hibernate.runtime.common;

import java.io.File;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
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
	
	@Override
	public IReverseEngineeringStrategy getReverseEngineeringStrategy(
			IReverseEngineeringStrategy res) {
		assert res instanceof IFacade;
		Object targetRes = getReverseEngineeringStrategy(((IFacade)res).getTarget());
		return getFacadeFactory().createReverseEngineeringStrategy(targetRes);
	}
	
	private Object getReverseEngineeringStrategy(Object object) {
		return Util.invokeMethod(
				getTarget(), 
				"getReverseEngineeringStrategy", 
				new Class[] { getReverseEngineeringStrategyClass() }, 
				new Object[] { object });
	}

	protected Class<?> getTableFilterClass() {
		return Util.getClass(
				getTableFilterClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getReverseEngineeringStrategyClass() {
		return Util.getClass(
				getReverseEngineeringStrategyClassName(), 
				getFacadeFactoryClassLoader());
	}
	
	protected String getTableFilterClassName() {
		return "org.hibernate.cfg.reveng.TableFilter";
	}
	
	protected String getReverseEngineeringStrategyClassName() {
		return "org.hibernate.cfg.reveng.ReverseEngineeringStrategy";
	}

}
