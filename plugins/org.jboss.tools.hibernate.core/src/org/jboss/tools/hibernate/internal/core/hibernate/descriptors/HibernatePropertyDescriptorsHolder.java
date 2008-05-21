/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.hibernate.descriptors;



import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.ChangeblePropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptorWithType;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ParentListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.TextPropertyDescriptorWithType;


/**
 * @author alex
 *
 * Class for maintaining hibernate property descriptors 
 */
public class HibernatePropertyDescriptorsHolder extends
		PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder managedProperties=new PropertyDescriptorsHolder();
	private static PropertyDescriptorsHolder generalProperties=new PropertyDescriptorsHolder();
	private static PropertyDescriptorsHolder poolProperties=new PropertyDescriptorsHolder();
	// #added# by Konstantin Mishin on 26.05.2006 fixed for ESORM-528
	private static PropertyDescriptorsHolder springProperties=new PropertyDescriptorsHolder();
	// #added#
	//akuzmin 31.05.2005
	private static PropertyDescriptorsHolder instance=new HibernatePropertyDescriptorsHolder(true);
	
	private static final String GENERAL_CATEGORY=Messages.HibernatePropertyDescriptorsHolder_GeneralCategory;
	private static final String DATASOURCE_CATEGORY=Messages.HibernatePropertyDescriptorsHolder_DatasourceCategory;
	private static final String JDBC_PARAMS_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_JDBCParamsCategory;
	private static final String TRANSACTIONS_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_TransactionsCategory;
	private static final String JDBC_CONNECTION_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_JDBCConnectionCategory;
	private static final String C3PO_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_C3P0Category;
	// #added# by Konstantin Mishin on 26.05.2006 fixed for ESORM-528
	private static final String SPRING_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_SpringCategory;
	// #added#
	private static final String CONNECTION_PROVIDER_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_ConnectionProviderCategory;
	private static final String CACHE_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_CacheCategory;
	private static final String QUERY_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_QueryCategory;
	private static final String MISC_CATEGORY = Messages.HibernatePropertyDescriptorsHolder_MiscCategory;
	
	public static PropertyDescriptorsHolder getManagedPropertyDescriptors(){
		return managedProperties;
	}
	public static PropertyDescriptorsHolder getPoolPropertyDescriptors(){
		return poolProperties;
	}
	
	// #added# by Konstantin Mishin on 26.05.2006 fixed for ESORM-528
	public static PropertyDescriptorsHolder getSpringPropertyDescriptors(){
		return springProperties;
	}
	// #added#

	public static PropertyDescriptorsHolder getGeneralPropertyDescriptors(boolean isDialectChange){
		//akuzmin 31.05.2005
		instance=new HibernatePropertyDescriptorsHolder(isDialectChange);
		return generalProperties;
	}
	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	//akuzmin 31.05.2005
	private HibernatePropertyDescriptorsHolder(boolean isDialectChange){
		//XXX Nick(4) Set default values for all properties (mostly ""), set categories for all properties 
		
		String[] BOOLEAN_VALUES = {"false","true"}; //$NON-NLS-1$ //$NON-NLS-2$
		String[] TERNARY_VALUES = {"", "false", "true"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String[] DEPENDENCE_DESCRIPTORS = {"hibernate.connection.driver_class","hibernate.connection.url"}; //$NON-NLS-1$ //$NON-NLS-2$
		PropertyDescriptor pd;
		if (isDialectChange)
			//akuzmin 21.08.2005
			pd=new ParentListPropertyDescriptor("hibernate.dialect",Messages.HibernatePropertyDescriptorsHolder_HibernateDialectN,OrmConfiguration.dialects,DEPENDENCE_DESCRIPTORS); //$NON-NLS-1$
		else
			pd=new PropertyDescriptor("hibernate.dialect",Messages.HibernatePropertyDescriptorsHolder_HibernateDialectN); //$NON-NLS-1$
			
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateDialectD);
		addPropertyDescriptor(pd);
		generalProperties.addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"");//dialects[5] //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("hibernate.default_schema",Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultSchemaN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultSchemaD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		generalProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_SCHEMA);
		
		pd=new TextPropertyDescriptor("hibernate.default_catalog",Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultCatalogN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultCatalogD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		generalProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_CATALOG);
		
		

		pd=new ListPropertyDescriptor("hibernate.show_sql",Messages.HibernatePropertyDescriptorsHolder_HibernateShowSqlN,BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateShowSqlD);
		setDefaultPropertyValue(pd.getId(),BOOLEAN_VALUES[0]);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		if (generalProperties!=null)		
		generalProperties.addPropertyDescriptor(pd);
		
		
		//--------------- JDBC parameters category -------------------
		pd=new EditableListPropertyDescriptorWithType("hibernate.jdbc.fetch_size",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcFetchSizeN,new String[]{"4","8","16"},true,false,false,null,null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcFetchSizeD);
		addPropertyDescriptor(pd);
		//XXX.toAlex (Nick) there are three recommended values. can I use them all?
		// Good point. I created EditableListPropertyDescriptor to provide list of recommended values.
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_PARAMS_CATEGORY);

		pd=new EditableListPropertyDescriptorWithType("hibernate.jdbc.batch_size",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcBatchSizeN,new String[]{String.valueOf(OrmConfiguration.DEFAULT_JDBC_BATCH_SIZE)},true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcBatchSizeD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"0"); //$NON-NLS-1$
		pd.setCategory(JDBC_PARAMS_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.jdbc.batch_versioned_data",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcBatchVersionedDataN,BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcBatchVersionedDataD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),BOOLEAN_VALUES[0]);
		pd.setCategory(JDBC_PARAMS_CATEGORY);

		pd=new TextPropertyDescriptor("hibernate.jdbc.factory_class",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcFactoryClassN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcFactoryClassD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_PARAMS_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.jdbc.use_scrollable_resultset",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcUseScrollableResultsetN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcUseScrollableResultsetD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_PARAMS_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.jdbc.use_streams_for_binary",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcUseStreamsForBinaryN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcUseStreamsForBinaryD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_PARAMS_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.jdbc.use_get_generated_keys",Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcUseGetGeneratedKeysN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJdbcUseGetGeneratedKeysD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_PARAMS_CATEGORY);
		

		//---------------------- Datasource properties ------------------------
		pd=new TextPropertyDescriptor("hibernate.session_factory_name",Messages.HibernatePropertyDescriptorsHolder_HibernateSessionFactoryNameN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateSessionFactoryNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(DATASOURCE_CATEGORY);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		managedProperties.addPropertyDescriptor(pd);
		
		pd=new TextPropertyDescriptor("hibernate.jndi.url",Messages.HibernatePropertyDescriptorsHolder_HibernateJndiUrlN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJndiUrlD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(DATASOURCE_CATEGORY);
		
		pd=new TextPropertyDescriptor("hibernate.jndi.class",Messages.HibernatePropertyDescriptorsHolder_HibernateJndiClassN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJndiClassD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(DATASOURCE_CATEGORY);
		
		pd=new TextPropertyDescriptor("hibernate.connection.datasource",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionDatasourceN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionDatasourceD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(DATASOURCE_CATEGORY);
		
		//---------------------- Transaction properties ------------------------
		//XXX Nick(4) Provide list of possible values, include "" that is default
		String[] factory_classes = {"org.hibernate.transaction.JDBCTransactionFactory", //$NON-NLS-1$
		        "org.hibernate.transaction.JTATransactionFactory"}; //$NON-NLS-1$
		pd=new EditableListPropertyDescriptor("hibernate.transaction.factory_class",Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionFactoryClassN,factory_classes); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionFactoryClassD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(TRANSACTIONS_CATEGORY);
		
		pd=new TextPropertyDescriptor("hibernate.jta.UserTransaction",Messages.HibernatePropertyDescriptorsHolder_HibernateJtaUserTransactionN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateJtaUserTransactionD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(TRANSACTIONS_CATEGORY);
		
		//XXX Nick(4) Provide list of possible values, include "" that is default
		String[] transaction_managers = {
		        "org.hibernate.transaction.JBossTransactionManagerLookup",  //$NON-NLS-1$
		        "org.hibernate.transaction.WeblogicTransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.WebSphereTransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.OrionTransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.ResinTransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.JOTMTransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.JOnASTransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.JRun4TransactionManagerLookup", //$NON-NLS-1$
		        "org.hibernate.transaction.BESTransactionManagerLookup"}; //$NON-NLS-1$
		pd=new EditableListPropertyDescriptor("hibernate.transaction.manager_lookup_class",Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionManagerLookupClassN,transaction_managers); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionManagerLookupClassD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(TRANSACTIONS_CATEGORY);
//akuzmin 12.05.2005
		pd=new ListPropertyDescriptor("hibernate.transaction.flush_before_completion",Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionFlushBeforeCompletionN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionFlushBeforeCompletionD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(TRANSACTIONS_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.transaction.auto_close_session",Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionAutoCloseSessionN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateTransactionAutoCloseSessionD);
		addPropertyDescriptor(pd);
		managedProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(TRANSACTIONS_CATEGORY);
		 		
		//------------------ JDBC connection properties -------------------------
		//akuzmin 21.08.2005
		pd=new ChangeblePropertyDescriptor("hibernate.connection.driver_class",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionDriverClassN,null,OrmConfiguration.dialects,null,OrmConfiguration.drivers); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionDriverClassD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		//akuzmin 21.08.2005
		pd=new ChangeblePropertyDescriptor("hibernate.connection.url",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionUrlN,null,OrmConfiguration.dialects,null,OrmConfiguration.urls); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionUrlD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		
		pd=new TextPropertyDescriptor("hibernate.connection.username",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionUsernameN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionUsernameD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);

		pd=new TextPropertyDescriptor("hibernate.connection.password",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionPasswordN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionPasswordD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		
		//XXX Nick(4) Provide list of possible values using ListPropertyDescriptor("hibernate.connection.isolation","Transaction isolation level",..._NAMES,..._VALUES);
		//""-Use default "1"-Read uncommitted isolation  "2"-Read committed isolation "4"-Repeatable read isolation "8"-Serializable isolation
		String[] ISOLATION_LEVELS = {"", "1","2","4","8"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		String[] ISOLATION_LEVELS_DESCRIPTION = {"", "Read uncommitted","Read committed","Repeatable read","Serializable"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		pd=new ListPropertyDescriptor("hibernate.connection.isolation",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionIsolationN,ISOLATION_LEVELS_DESCRIPTION,ISOLATION_LEVELS); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionIsolationD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		
//		akuzmin 12.05.2005		
		pd=new ListPropertyDescriptor("hibernate.connection.autocommit",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionAutocommitN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionAutocommitD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		
		String[] connection_release_mode={
				"on_close", //$NON-NLS-1$
				"after_transaction", //$NON-NLS-1$
				"after_statement", //$NON-NLS-1$
				"auto"				 //$NON-NLS-1$
		};
		pd=new ListPropertyDescriptor("hibernate.connection.release_mode",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionReleaseModeN,connection_release_mode); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionReleaseModeD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),connection_release_mode[0]);
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		
		
		pd=new TextPropertyDescriptorWithType("hibernate.connection.pool_size",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionPoolSizeN,true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionPoolSizeD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(JDBC_CONNECTION_CATEGORY);
		
		//----------------------- end JDBC connection properties -------------------------
		
		//--------------- C3PO pool properties ---------------------------
		pd=new TextPropertyDescriptorWithType("hibernate.c3p0.min_size",Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0MinSizeN,true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0MinSizeD);
		addPropertyDescriptor(pd);
		poolProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), ""); //$NON-NLS-1$
		pd.setCategory(C3PO_CATEGORY);
		
		pd=new TextPropertyDescriptorWithType("hibernate.c3p0.max_size",Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0MaxSizeN,true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0MaxSizeD);
		addPropertyDescriptor(pd);
		poolProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), ""); //$NON-NLS-1$
		pd.setCategory(C3PO_CATEGORY);
		
		pd=new TextPropertyDescriptorWithType("hibernate.c3p0.timeout",Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0TimeoutN,true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0TimeoutD);
		addPropertyDescriptor(pd);
		poolProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), ""); //$NON-NLS-1$
		pd.setCategory(C3PO_CATEGORY);
		
		pd=new TextPropertyDescriptorWithType("hibernate.c3p0.max_statements",Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0MaxStatementsN,true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0MaxStatementsD);
		addPropertyDescriptor(pd);
		poolProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), ""); //$NON-NLS-1$
		pd.setCategory(C3PO_CATEGORY);
		
		pd=new TextPropertyDescriptorWithType("hibernate.c3p0.idle_test_period",Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0IdleTestPeriodN,true,false,false,null,null); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateC3p0IdleTestPeriodD);
		addPropertyDescriptor(pd);
		poolProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), ""); //$NON-NLS-1$
		pd.setCategory(C3PO_CATEGORY);
		//-------------------------------------- end C3PO pool

		// #added# by Konstantin Mishin on 26.05.2006 fixed for ESORM-528
		//--------------- Spring properties ---------------------------
		pd=new TextPropertyDescriptor("spring.sessionFactory",Messages.HibernatePropertyDescriptorsHolder_SpringSessionFactoryN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_SpringSessionFactoryD);
		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), "sessionFactory"); //$NON-NLS-1$
		pd.setCategory(SPRING_CATEGORY);
		
		pd=new TextPropertyDescriptor("spring.dataSource",Messages.HibernatePropertyDescriptorsHolder_SpringDataSourceN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_SpringDataSourceD);
		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), "dataSource"); //$NON-NLS-1$
		pd.setCategory(SPRING_CATEGORY);
		
		pd=new TextPropertyDescriptor("spring.transactionManager",Messages.HibernatePropertyDescriptorsHolder_TransactionManagerN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_TransactionManagerD);
		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), "transactionManager"); //$NON-NLS-1$
		pd.setCategory(SPRING_CATEGORY);
		
		pd=new TextPropertyDescriptor("spring.daoTransactionInterceptor",Messages.HibernatePropertyDescriptorsHolder_TransactionInterceptorN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_TransactionInterceptorD);
		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), "daoTransactionInterceptor"); //$NON-NLS-1$
		pd.setCategory(SPRING_CATEGORY);
		
		pd=new TextPropertyDescriptor("spring.hibernateInterceptor",Messages.HibernatePropertyDescriptorsHolder_HibernateInterceptorN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateInterceptorD);
		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), "hibernateInterceptor"); //$NON-NLS-1$
		pd.setCategory(SPRING_CATEGORY);
		
		pd=new TextPropertyDescriptor("spring.daoTxAttributes",Messages.HibernatePropertyDescriptorsHolder_TransactionAttributesN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_TransactionAttributesD);
		addPropertyDescriptor(pd);
		springProperties.addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(), "daoTxAttributes"); //$NON-NLS-1$
		pd.setCategory(SPRING_CATEGORY);
		//-------------------------------------- end Spring
		// #added#

		// ------------ Connection provider -----------
		pd=new TextPropertyDescriptor("hibernate.connection.provider_class",Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionProviderClassN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateConnectionProviderClassD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(CONNECTION_PROVIDER_CATEGORY);
		
		//-------------- Cache category
		
		pd=new ListPropertyDescriptor("hibernate.cache.use_minimal_puts",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseMinimalPutsN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseMinimalPutsD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(CACHE_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.cache.use_query_cache",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseQueryCacheN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseQueryCacheD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),BOOLEAN_VALUES[0]);
		pd.setCategory(CACHE_CATEGORY);
		
		pd=new TextPropertyDescriptor("hibernate.cache.query_cache_factory",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheQueryCacheFactoryN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheQueryCacheFactoryD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"org.hibernate.cache.StandardQueryCacheFactory"); //$NON-NLS-1$
		pd.setCategory(CACHE_CATEGORY);
		
		pd=new TextPropertyDescriptor("hibernate.cache.region_prefix",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheRegionPrefixN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheRegionPrefixD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(CACHE_CATEGORY);
//		akuzmin 12.05.2005
		String[] cacheProviders= {
				"org.hibernate.cache.HashtableCacheProvider",  //$NON-NLS-1$
				"org.hibernate.cache.EhCacheProvider",  //$NON-NLS-1$
				"org.hibernate.cache.OSCacheProvider" , //$NON-NLS-1$
				"org.hibernate.cache.SwarmCacheProvider",  //$NON-NLS-1$
				"org.hibernate.cache.TreeCacheProvider" //$NON-NLS-1$
				};
		pd=new EditableListPropertyDescriptor("hibernate.cache.provider_class",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheProviderClassN, cacheProviders); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheProviderClassD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"org.hibernate.cache.EhCacheProvider"); //$NON-NLS-1$
		pd.setCategory(CACHE_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.cache.use_second_level_cache",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseSecondLevelCacheN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseSecondLevelCacheD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),BOOLEAN_VALUES[1]);
		pd.setCategory(CACHE_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.cache.use_structured_entries",Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseStructuredEntriesN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCacheUseStructuredEntriesD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),BOOLEAN_VALUES[0]);
		pd.setCategory(CACHE_CATEGORY);
		
				
		//------------------ Query category
		pd=new TextPropertyDescriptor("hibernate.query.substitutions",Messages.HibernatePropertyDescriptorsHolder_HibernateQuerySubstitutionsN); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateQuerySubstitutionsD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(QUERY_CATEGORY);
		
//akuzmin 12.05.2005
		String[] HQLparsers ={
		 "", //$NON-NLS-1$
		 "org.hibernate.hql.ast.ASTQueryTranslatorFactory", //$NON-NLS-1$
		 "org.hibernate.hql.classic.ClassicQueryTranslatorFactory" //$NON-NLS-1$
		};
		
		pd=new EditableListPropertyDescriptor("hibernate.query.factory_class",Messages.HibernatePropertyDescriptorsHolder_HibernateQueryFactoryClassN,HQLparsers); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateQueryFactoryClassD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),HQLparsers[1]);
		pd.setCategory(QUERY_CATEGORY);
		
		//------------------- Misc category
		String[] hbm2ddl_auto = {"", "update","create","create-drop"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		pd=new ListPropertyDescriptor("hibernate.hbm2ddl.auto",Messages.HibernatePropertyDescriptorsHolder_HibernateHbm2ddlAutoN,hbm2ddl_auto); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateHbm2ddlAutoD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.generate_statistics",Messages.HibernatePropertyDescriptorsHolder_HibernateGenerateStatisticsN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateGenerateStatisticsD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.use_identifer_rollback",Messages.HibernatePropertyDescriptorsHolder_HibernateUseIdentiferRollbackN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateUseIdentiferRollbackD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);
		
		pd=new ListPropertyDescriptor("hibernate.use_sql_comments",Messages.HibernatePropertyDescriptorsHolder_HibernateUseSqlCommentsN,BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateUseSqlCommentsD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),BOOLEAN_VALUES[0]);
		pd.setCategory(MISC_CATEGORY);

		pd=new EditableListPropertyDescriptorWithType("hibernate.max_fetch_depth",Messages.HibernatePropertyDescriptorsHolder_HibernateMaxFetchDepthN,new String[]{"2"},true,false,false,null,null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateMaxFetchDepthD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.cglib.use_reflection_optimizer",Messages.HibernatePropertyDescriptorsHolder_HibernateCglibUseReflectionOptimizerN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateCglibUseReflectionOptimizerD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);
//akuzmin 12.05.2005		
		pd=new EditableListPropertyDescriptorWithType("hibernate.default_batch_fetch_size",Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultBatchFetchSizeN,new String[]{"4","8","16"},true,false,false,null,null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultBatchFetchSizeD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"1"); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.default_entity_mode",Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultEntityModeN,new String[]{"pojo", "dynamic-map","dom4j"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateDefaultEntityModeD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"pojo"); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);

		pd=new ListPropertyDescriptor("hibernate.order_updates",Messages.HibernatePropertyDescriptorsHolder_HibernateOrderUpdatesN,TERNARY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.HibernatePropertyDescriptorsHolder_HibernateOrderUpdatesD);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		pd.setCategory(MISC_CATEGORY);
		
		
		/*
		 * XXX Nick(3) add all hibernate property descriptors. 
		 * note: some properties require special property editors like:
		 *  boolean, int, list of values, select java class, properties editor
		 * */
		//XXX.toAlex what classes should I use for int, boolean properties?
		//Use TextPropertyDescriptor for int
		//Use ListPropertyDescriptor for boolean
		//if you are not sure what class to choose then use TextPropertyDescriptor
	}
	
	/*
	
+hibernate.dialect
+hibernate.default_schema
+hibernate.default_catalog
+hibernate.session_factory_name
+hibernate.max_fetch_depth
+hibernate.jdbc.fetch_size
+hibernate.jdbc.batch_size
+hibernate.jdbc.batch_versioned_data
+hibernate.jdbc.factory_class
+hibernate.jdbc.use_scrollable_resultset
+hibernate.jdbc.use_streams_for_binary
+hibernate.jdbc.use_get_generated_keys
+hibernate.cglib.use_reflection_optimizer
+hibernate.jndi.<propertyName>
+hibernate.connection.isolation
+hibernate.connection.<propertyName>
+hibernate.connection.provider_class
+hibernate.cache.use_minimal_puts
+hibernate.cache.use_query_cache
+hibernate.cache.query_cache_factory
+hibernate.cache.region_prefix
+hibernate.transaction.factory_class
+jta.UserTransaction
+hibernate.transaction.manager_lookup_class
+hibernate.query.substitutions
+hibernate.show_sql
+hibernate.hbm2ddl.auto
+hibernate.generate_statistics
+hibernate.use_identifer_rollback
+hibernate.use_sql_comments	

new:
hibernate.default_batch_fetch_size
hibernate.default_entity_mode
hibernate.order_updates
hibernate.connection.autocommit
hibernate.connection.release_mode

hibernate.cache.provider_class
hibernate.cache.use_second_level_cache
hibernate.cache.use_structured_entries
hibernate.transaction.flush_before_completion
hibernate.transaction.auto_close_session

hibernate.query.factory_class

*/
	/*
	private boolean showSql;
	private int maximumFetchDepth;
	private String querySubstitutions;
	private String dialect;
	private int jdbcBatchSize;
	private int defaultBatchFetchSize;
	private boolean scrollableResultSetsEnabled;
	private boolean getGeneratedKeysEnabled;
	private String defaultSchemaName;
	private String defaultCatalogName;
	private int jdbcFetchSize;
	private String sessionFactoryName;
	private boolean autoCreateSchema;
	private boolean autoDropSchema;
	private boolean autoUpdateSchema;
	private boolean queryCacheEnabled;
	private boolean structuredCacheEntriesEnabled;
	private boolean secondLevelCacheEnabled;
	private String cacheRegionPrefix;
	private boolean minimalPutsEnabled;
	private boolean commentsEnabled;
	private boolean statisticsEnabled;
	private boolean jdbcBatchVersionedData;
	private boolean identifierRollbackEnabled;
	private boolean flushBeforeCompletionEnabled;
	private boolean autoCloseSessionEnabled;
	private CacheProvider cacheProvider;
	private QueryCacheFactory queryCacheFactory;
	private ConnectionProvider connectionProvider;
	private TransactionFactory transactionFactory;
	private TransactionManagerLookup transactionManagerLookup;
	private BatcherFactory batcherFactory;
	private QueryTranslatorFactory queryTranslatorFactory;
	private SQLExceptionConverter sqlExceptionConverter;
	private boolean wrapResultSetsEnabled;
	private boolean orderUpdatesEnabled;
	*/

}
