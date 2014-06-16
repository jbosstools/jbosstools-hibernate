package org.jboss.tools.hibernate.spi;

import java.io.File;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;

public interface IOverrideRepository {

	void addFile(File file);
	ReverseEngineeringStrategy getReverseEngineeringStrategy(ReverseEngineeringStrategy res);
	void addTableFilter(ITableFilter tf);

}
