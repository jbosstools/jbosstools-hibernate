package org.validation;

import org.hibernate.cfg.DefaultNamingStrategy;

public class MyNamingStrategy extends DefaultNamingStrategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String columnName(String columnName) {	
		return super.columnName(columnName);
	}
}