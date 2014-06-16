package org.jboss.tools.hibernate.proxy;

import java.io.File;

import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.jboss.tools.hibernate.spi.IOverrideRepository;
import org.jboss.tools.hibernate.spi.ITableFilter;

public class OverrideRepositoryProxy implements IOverrideRepository {
	
	private OverrideRepository target = null;

	public OverrideRepositoryProxy(OverrideRepository overrideRepository) {
		target = overrideRepository;
	}

	@Override
	public void addFile(File file) {
		target.addFile(file);
	}

	@Override
	public ReverseEngineeringStrategy getReverseEngineeringStrategy(
			ReverseEngineeringStrategy res) {
		return target.getReverseEngineeringStrategy(res);
	}

	@Override
	public void addTableFilter(ITableFilter tf) {
		assert tf instanceof TableFilterProxy;
		target.addTableFilter(((TableFilterProxy)tf).getTarget());
	}

}
