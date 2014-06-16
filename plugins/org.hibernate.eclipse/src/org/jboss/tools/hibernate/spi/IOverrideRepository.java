package org.jboss.tools.hibernate.spi;

import java.io.File;

import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;

public interface IOverrideRepository {

	void addFile(File file);
	ReverseEngineeringStrategy getReverseEngineeringStrategy(ReverseEngineeringStrategy res);
	void addTableFilter(TableFilter tf);

}
