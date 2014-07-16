package org.hibernate.console.proxy;

import org.hibernate.cfg.Environment;

public class HibernateEnvironment {
	
	public static String TRANSACTION_MANAGER_STRATEGY = Environment.TRANSACTION_MANAGER_STRATEGY;
	public static String DRIVER = Environment.DRIVER;
	public static String HBM2DDL_AUTO = Environment.HBM2DDL_AUTO;
	public static String DIALECT = Environment.DIALECT;
	public static String DATASOURCE = Environment.DATASOURCE;
	public static String CONNECTION_PROVIDER = Environment.CONNECTION_PROVIDER;

}
