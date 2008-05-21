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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.internal.core.hibernate.descriptors.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String HibernatePropertyDescriptorsHolder_HibernateInterceptorD;

	public static String HibernatePropertyDescriptorsHolder_HibernateInterceptorN;

	public static String HibernatePropertyDescriptorsHolder_SpringCategory;

	public static String HibernatePropertyDescriptorsHolder_SpringDataSourceD;

	public static String HibernatePropertyDescriptorsHolder_SpringDataSourceN;

	public static String HibernatePropertyDescriptorsHolder_SpringSessionFactoryD;

	public static String HibernatePropertyDescriptorsHolder_SpringSessionFactoryN;

	public static String HibernatePropertyDescriptorsHolder_TransactionAttributesD;

	public static String HibernatePropertyDescriptorsHolder_TransactionAttributesN;

	public static String HibernatePropertyDescriptorsHolder_TransactionInterceptorD;

	public static String HibernatePropertyDescriptorsHolder_TransactionInterceptorN;

	public static String HibernatePropertyDescriptorsHolder_TransactionManagerD;

	public static String HibernatePropertyDescriptorsHolder_TransactionManagerN;

	public static String VersionPropertyDescriptorsHolder_VersionCategory;

	public static String VersionPropertyDescriptorsHolder_VersionValueTypeNameN;

	public static String VersionPropertyDescriptorsHolder_VersionValueTypeNameD;

	public static String VersionPropertyDescriptorsHolder_VersionValueNullValueN;

	public static String VersionPropertyDescriptorsHolder_VersionValueNullValueD;

	public static String VersionPropertyDescriptorsHolder_VersionPropertyAccessorNameN;

	public static String VersionPropertyDescriptorsHolder_VersionPropertyAccessorNameD;

	public static String VersionPropertyDescriptorsHolder_VersionColumnNameN;

	public static String VersionPropertyDescriptorsHolder_VersionColumnNameD;

	public static String SimpleValueMappingDescriptorsHolder_GeneralCategory;

	public static String SimpleValueMappingDescriptorsHolder_AdvancedlCategory;

	public static String SimpleValueMappingDescriptorsHolder_TableNameN;

	public static String SimpleValueMappingDescriptorsHolder_TableNameD;

	public static String SimpleValueMappingDescriptorsHolder_MappingColumnN;

	public static String SimpleValueMappingDescriptorsHolder_MappingColumnD;

	public static String SimpleValueMappingDescriptorsHolder_FormulaN;

	public static String SimpleValueMappingDescriptorsHolder_FormulaD;

	public static String SimpleValueMappingDescriptorsHolder_TypeByStringN;

	public static String SimpleValueMappingDescriptorsHolder_TypeByStringD;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorCategory;

	public static String DiscriminatorPropertyDescriptorsHolder_ForceDiscriminatorN;

	public static String DiscriminatorPropertyDescriptorsHolder_ForceDiscriminatorD;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorInsertableN;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorInsertableD;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorFormulaN;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorFormulaD;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorColumnNameN;

	public static String DiscriminatorPropertyDescriptorsHolder_DiscriminatorColumnNameD;

	public static String IdBagMappingDescriptorsHolderWithTable_GeneralCategory;

	public static String IdBagMappingDescriptorsHolderWithTable_CollectionTableN;

	public static String IdBagMappingDescriptorsHolderWithTable_CollectionTableD;

	public static String PrimitiveArrayMappingDescriptorsHolder_IndexCategory;

	public static String PrimitiveArrayMappingDescriptorsHolder_BaseIndexN;

	public static String PrimitiveArrayMappingDescriptorsHolder_BaseIndexD;

	public static String PrimitiveArrayMappingDescriptorsHolder_IndexMappingColumnN;

	public static String PrimitiveArrayMappingDescriptorsHolder_IndexMappingColumnD;

	public static String CollectionMappingFKDescriptorsHolder_GeneralCategory;

	public static String CollectionMappingFKDescriptorsHolder_AdvancedCategory;

	public static String CollectionMappingFKDescriptorsHolder_KeyColumnN;

	public static String CollectionMappingFKDescriptorsHolder_KeyColumnD;

	public static String CollectionMappingFKDescriptorsHolder_KeyTableNameN;

	public static String CollectionMappingFKDescriptorsHolder_KeyTableNameD;

	public static String CollectionMappingFKDescriptorsHolder_KeyForeignKeyNameN;

	public static String CollectionMappingFKDescriptorsHolder_KeyForeignKeyNameD;

	public static String CollectionMappingFKDescriptorsHolder_KeyCascadeDeleteEnabledN;

	public static String CollectionMappingFKDescriptorsHolder_KeyCascadeDeleteEnabledD;

	public static String CollectionMappingFKDescriptorsHolder_KeyUpdateableN;

	public static String CollectionMappingFKDescriptorsHolder_KeyUpdateableD;

	public static String CollectionMappingFKDescriptorsHolder_KeyNullableN;

	public static String CollectionMappingFKDescriptorsHolder_KeyNullableD;

	public static String AnyMappingDescriptorsHolder_GeneralCategory;

	public static String AnyMappingDescriptorsHolder_MetaTypeN;

	public static String AnyMappingDescriptorsHolder_MetaTypeD;

	public static String AnyMappingDescriptorsHolder_IdentifierTypeN;

	public static String AnyMappingDescriptorsHolder_IdentifierTypeD;

	public static String AnyMappingDescriptorsHolder_MappingColumnN;

	public static String AnyMappingDescriptorsHolder_MappingColumnD;

	public static String AnyMappingDescriptorsHolder_MetaTypeColumnN;

	public static String AnyMappingDescriptorsHolder_MetaTypeColumnD;

	public static String ManyToManyMappingDescriptorsHolder_GeneralCategory;

	public static String ManyToManyMappingDescriptorsHolder_AdvancedCategory;

	public static String ManyToManyMappingDescriptorsHolder_ReferencedEntityNameN;

	public static String ManyToManyMappingDescriptorsHolder_ReferencedEntityNameD;

	public static String ManyToManyMappingDescriptorsHolder_MappingColumnN;

	public static String ManyToManyMappingDescriptorsHolder_MappingColumnD;

	public static String ManyToManyMappingDescriptorsHolder_FormulaN;

	public static String ManyToManyMappingDescriptorsHolder_FormulaD;

	public static String ManyToManyMappingDescriptorsHolder_IgnoreNotFoundN;

	public static String ManyToManyMappingDescriptorsHolder_IgnoreNotFoundD;

	public static String ManyToManyMappingDescriptorsHolder_FetchModeN;

	public static String ManyToManyMappingDescriptorsHolder_FetchModeD;

	public static String ManyToManyMappingDescriptorsHolder_ForeignKeyNameN;

	public static String ManyToManyMappingDescriptorsHolder_ForeignKeyNameD;

	public static String SubclassMappingPropertyDescriptorsHolder_DatabaseTableNameN;

	public static String SubclassMappingPropertyDescriptorsHolder_DatabaseTableNameD;

	public static String SubclassMappingPropertyDescriptorsHolder_SuperclassNameN;

	public static String SubclassMappingPropertyDescriptorsHolder_SuperclassNameD;

	public static String SubclassMappingPropertyDescriptorsHolder_DiscriminatorValueN;

	public static String SubclassMappingPropertyDescriptorsHolder_DiscriminatorValueD;

	public static String OneToManyMappingDescriptorsHolder_GeneralCategory;

	public static String OneToManyMappingDescriptorsHolder_AdvancedCategory;

	public static String OneToManyMappingDescriptorsHolder_ReferencedEntityNameN;

	public static String OneToManyMappingDescriptorsHolder_ReferencedEntityNameD;

	public static String OneToManyMappingDescriptorsHolder_IgnoreNotFoundN;

	public static String OneToManyMappingDescriptorsHolder_IgnoreNotFoundD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_NameN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_NameD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_UpdateableN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_UpdateableD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_InsertableN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_InsertableD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_OptimisticLockedN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_OptimisticLockedD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_LazyN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_LazyD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_NotNullN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_NotNullD;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_UniqueN;

	public static String PropertyMappingForSimpleMapingDescriptorsHolder_UniqueD;

	public static String ClassMappingPropertyDescriptorsHolder_GeneralCategory;

	public static String ClassMappingPropertyDescriptorsHolder_AdvancedlCategory;

	public static String ClassMappingPropertyDescriptorsHolder_NameN;

	public static String ClassMappingPropertyDescriptorsHolder_NameD;

	public static String ClassMappingPropertyDescriptorsHolder_EntityNameN;

	public static String ClassMappingPropertyDescriptorsHolder_EntityNameD;

	public static String ClassMappingPropertyDescriptorsHolder_ProxyInterfaceNameN;

	public static String ClassMappingPropertyDescriptorsHolder_ProxyInterfaceNameD;

	public static String ClassMappingPropertyDescriptorsHolder_DynamicInsertN;

	public static String ClassMappingPropertyDescriptorsHolder_DynamicInsertD;

	public static String ClassMappingPropertyDescriptorsHolder_DynamicUpdateN;

	public static String ClassMappingPropertyDescriptorsHolder_DynamicUpdateD;

	public static String ClassMappingPropertyDescriptorsHolder_BatchSizeN;

	public static String ClassMappingPropertyDescriptorsHolder_BatchSizeD;

	public static String ClassMappingPropertyDescriptorsHolder_SelectBeforeUpdateN;

	public static String ClassMappingPropertyDescriptorsHolder_SelectBeforeUpdateD;

	public static String ClassMappingPropertyDescriptorsHolder_LoaderNameN;

	public static String ClassMappingPropertyDescriptorsHolder_LoaderNameD;

	public static String ClassMappingPropertyDescriptorsHolder_PersisterClassNameN;

	public static String ClassMappingPropertyDescriptorsHolder_PersisterClassNameD;

	public static String ClassMappingPropertyDescriptorsHolder_IsAbstractN;

	public static String ClassMappingPropertyDescriptorsHolder_IsAbstractD;

	public static String ClassMappingPropertyDescriptorsHolder_LazyN;

	public static String ClassMappingPropertyDescriptorsHolder_LazyD;

	public static String ClassMappingPropertyDescriptorsHolder_SynchronizedTablesN;

	public static String ClassMappingPropertyDescriptorsHolder_SynchronizedTablesD;

	public static String PropertyMappingForPrimitiveArrayDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForPrimitiveArrayDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForPrimitiveArrayDescriptorsHolder_NameN;

	public static String PropertyMappingForPrimitiveArrayDescriptorsHolder_NameD;

	public static String PropertyMappingForPrimitiveArrayDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForPrimitiveArrayDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_NameN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_NameD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_UpdateableN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_UpdateableD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_InsertableN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_InsertableD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_OptimisticLockedN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_OptimisticLockedD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_ToOneLazyN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_ToOneLazyD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_CascadeN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_CascadeD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_NotNullN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_NotNullD;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_UniqueN;

	public static String PropertyMappingForManytoOneMapingDescriptorsHolder_UniqueD;

	public static String ArrayMappingDescriptorsHolderWithTable_GeneralCategory;

	public static String ArrayMappingDescriptorsHolderWithTable_CollectionTableN;

	public static String ArrayMappingDescriptorsHolderWithTable_CollectionTableD;

	public static String ListMappingDescriptorsHolderWithTable_GeneralCategory;

	public static String ListMappingDescriptorsHolderWithTable_CollectionTableN;

	public static String ListMappingDescriptorsHolderWithTable_CollectionTableD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_NameN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_NameD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_UpdateableN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_UpdateableD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_InsertableN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_InsertableD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_LazyN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_LazyD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_OptimisticLockedN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_OptimisticLockedD;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_UniqueN;

	public static String PropertyMappingForComponentMapingDescriptorsHolder_UniqueD;

	public static String CollectionBaseMappingDescriptorsHolder_AdvancedCategory;

	public static String CollectionBaseMappingDescriptorsHolder_SubselectN;

	public static String CollectionBaseMappingDescriptorsHolder_SubselectD;

	public static String CollectionBaseMappingDescriptorsHolder_WhereN;

	public static String CollectionBaseMappingDescriptorsHolder_WhereD;

	public static String CollectionBaseMappingDescriptorsHolder_PersisterN;

	public static String CollectionBaseMappingDescriptorsHolder_PersisterD;

	public static String CollectionBaseMappingDescriptorsHolder_CheckN;

	public static String CollectionBaseMappingDescriptorsHolder_CheckD;

	public static String CollectionBaseMappingDescriptorsHolder_SynchronizedTablesN;

	public static String CollectionBaseMappingDescriptorsHolder_SynchronizedTablesD;

	public static String CollectionBaseMappingDescriptorsHolder_LoaderNameN;

	public static String CollectionBaseMappingDescriptorsHolder_LoaderNameD;

	public static String CollectionBaseMappingDescriptorsHolder_BatchSizeN;

	public static String CollectionBaseMappingDescriptorsHolder_BatchSizeD;

	public static String CollectionBaseMappingDescriptorsHolder_FetchModeN;

	public static String CollectionBaseMappingDescriptorsHolder_FetchModeD;

	public static String CollectionBaseMappingDescriptorsHolder_OptimisticLockedN;

	public static String CollectionBaseMappingDescriptorsHolder_OptimisticLockedD;

	public static String CollectionMappingFKWithTextRefDescriptorsHolder_AdvancedCategory;

	public static String CollectionMappingFKWithTextRefDescriptorsHolder_ReferencedPropertyNameN;

	public static String CollectionMappingFKWithTextRefDescriptorsHolder_ReferencedPropertyNameD;

	public static String IdBagMappingDescriptorsHolder_AdvancedCategory;

	public static String IdBagMappingDescriptorsHolder_OrderByN;

	public static String IdBagMappingDescriptorsHolder_OrderByD;

	public static String IdBagMappingDescriptorsHolder_LazyN;

	public static String IdBagMappingDescriptorsHolder_LazyD;

	public static String CollectionMappingDescriptorsHolder_AdvancedCategory;

	public static String CollectionMappingDescriptorsHolder_GeneralCategory;

	public static String CollectionMappingDescriptorsHolder_OrderByN;

	public static String CollectionMappingDescriptorsHolder_OrderByD;

	public static String CollectionMappingDescriptorsHolder_SortN;

	public static String CollectionMappingDescriptorsHolder_SortD;

	public static String CollectionMappingDescriptorsHolder_InverseN;

	public static String CollectionMappingDescriptorsHolder_InverseD;

	public static String CollectionMappingDescriptorsHolder_LazyN;

	public static String CollectionMappingDescriptorsHolder_LazyD;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_NameN;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_NameD;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_ToOneLazyN;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_ToOneLazyD;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_CascadeN;

	public static String PropertyMappingForOnetoOneMapingDescriptorsHolder_CascadeD;

	public static String PrimitiveArrayMappingDescriptorsHolderWithTable_GeneralCategory;

	public static String PrimitiveArrayMappingDescriptorsHolderWithTable_CollectionTableN;

	public static String PrimitiveArrayMappingDescriptorsHolderWithTable_CollectionTableD;

	public static String VersionMappingDescriptorsHolder_GeneralCategory;

	public static String VersionMappingDescriptorsHolder_AdvancedCategory;

	public static String VersionMappingDescriptorsHolder_MappingColumnN;

	public static String VersionMappingDescriptorsHolder_MappingColumnD;

	public static String VersionMappingDescriptorsHolder_TypeByStringN;

	public static String VersionMappingDescriptorsHolder_TypeByStringD;

	public static String VersionMappingDescriptorsHolder_NullValueN;

	public static String VersionMappingDescriptorsHolder_NullValueD;

	public static String HibernatePropertyDescriptorsHolder_GeneralCategory;

	public static String HibernatePropertyDescriptorsHolder_DatasourceCategory;

	public static String HibernatePropertyDescriptorsHolder_JDBCParamsCategory;

	public static String HibernatePropertyDescriptorsHolder_TransactionsCategory;

	public static String HibernatePropertyDescriptorsHolder_JDBCConnectionCategory;

	public static String HibernatePropertyDescriptorsHolder_C3P0Category;

	public static String HibernatePropertyDescriptorsHolder_ConnectionProviderCategory;

	public static String HibernatePropertyDescriptorsHolder_CacheCategory;

	public static String HibernatePropertyDescriptorsHolder_QueryCategory;

	public static String HibernatePropertyDescriptorsHolder_MiscCategory;

	public static String HibernatePropertyDescriptorsHolder_HibernateDialectN;

	public static String HibernatePropertyDescriptorsHolder_HibernateDialectD;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultSchemaN;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultSchemaD;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultCatalogN;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultCatalogD;

	public static String HibernatePropertyDescriptorsHolder_HibernateShowSqlN;

	public static String HibernatePropertyDescriptorsHolder_HibernateShowSqlD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcFetchSizeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcFetchSizeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcBatchSizeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcBatchSizeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcBatchVersionedDataN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcBatchVersionedDataD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcFactoryClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcFactoryClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcUseScrollableResultsetN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcUseScrollableResultsetD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcUseStreamsForBinaryN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcUseStreamsForBinaryD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcUseGetGeneratedKeysN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJdbcUseGetGeneratedKeysD;

	public static String HibernatePropertyDescriptorsHolder_HibernateSessionFactoryNameN;

	public static String HibernatePropertyDescriptorsHolder_HibernateSessionFactoryNameD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJndiUrlN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJndiUrlD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJndiClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJndiClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionDatasourceN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionDatasourceD;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionFactoryClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionFactoryClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateJtaUserTransactionN;

	public static String HibernatePropertyDescriptorsHolder_HibernateJtaUserTransactionD;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionManagerLookupClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionManagerLookupClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionFlushBeforeCompletionN;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionFlushBeforeCompletionD;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionAutoCloseSessionN;

	public static String HibernatePropertyDescriptorsHolder_HibernateTransactionAutoCloseSessionD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionDriverClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionDriverClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionUrlN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionUrlD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionUsernameN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionUsernameD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionPasswordN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionPasswordD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionIsolationN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionIsolationD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionAutocommitN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionAutocommitD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionReleaseModeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionReleaseModeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionPoolSizeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionPoolSizeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0MinSizeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0MinSizeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0MaxSizeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0MaxSizeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0TimeoutN;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0TimeoutD;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0MaxStatementsN;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0MaxStatementsD;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0IdleTestPeriodN;

	public static String HibernatePropertyDescriptorsHolder_HibernateC3p0IdleTestPeriodD;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionProviderClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateConnectionProviderClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseMinimalPutsN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseMinimalPutsD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseQueryCacheN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseQueryCacheD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheQueryCacheFactoryN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheQueryCacheFactoryD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheRegionPrefixN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheRegionPrefixD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheProviderClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheProviderClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseSecondLevelCacheN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseSecondLevelCacheD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseStructuredEntriesN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCacheUseStructuredEntriesD;

	public static String HibernatePropertyDescriptorsHolder_HibernateQuerySubstitutionsN;

	public static String HibernatePropertyDescriptorsHolder_HibernateQuerySubstitutionsD;

	public static String HibernatePropertyDescriptorsHolder_HibernateQueryFactoryClassN;

	public static String HibernatePropertyDescriptorsHolder_HibernateQueryFactoryClassD;

	public static String HibernatePropertyDescriptorsHolder_HibernateHbm2ddlAutoN;

	public static String HibernatePropertyDescriptorsHolder_HibernateHbm2ddlAutoD;

	public static String HibernatePropertyDescriptorsHolder_HibernateGenerateStatisticsN;

	public static String HibernatePropertyDescriptorsHolder_HibernateGenerateStatisticsD;

	public static String HibernatePropertyDescriptorsHolder_HibernateUseIdentiferRollbackN;

	public static String HibernatePropertyDescriptorsHolder_HibernateUseIdentiferRollbackD;

	public static String HibernatePropertyDescriptorsHolder_HibernateUseSqlCommentsN;

	public static String HibernatePropertyDescriptorsHolder_HibernateUseSqlCommentsD;

	public static String HibernatePropertyDescriptorsHolder_HibernateMaxFetchDepthN;

	public static String HibernatePropertyDescriptorsHolder_HibernateMaxFetchDepthD;

	public static String HibernatePropertyDescriptorsHolder_HibernateCglibUseReflectionOptimizerN;

	public static String HibernatePropertyDescriptorsHolder_HibernateCglibUseReflectionOptimizerD;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultBatchFetchSizeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultBatchFetchSizeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultEntityModeN;

	public static String HibernatePropertyDescriptorsHolder_HibernateDefaultEntityModeD;

	public static String HibernatePropertyDescriptorsHolder_HibernateOrderUpdatesN;

	public static String HibernatePropertyDescriptorsHolder_HibernateOrderUpdatesD;

	public static String IdBagIdentifireMappingDescriptorsHolder_GeneralCategory;

	public static String IdBagIdentifireMappingDescriptorsHolder_IdentifierColumnN;

	public static String IdBagIdentifireMappingDescriptorsHolder_IdentifierColumnD;

	public static String IdBagIdentifireMappingDescriptorsHolder_TypeByStringN;

	public static String IdBagIdentifireMappingDescriptorsHolder_TypeByStringD;

	public static String IdBagIdentifireMappingDescriptorsHolder_GeneratorStrategyN;

	public static String IdBagIdentifireMappingDescriptorsHolder_GeneratorStrategyD;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_DatabaseTableN;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_DatabaseTableD;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_CheckN;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_CheckD;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_SubselectN;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_SubselectD;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_SuperclassNameN;

	public static String UnionSubclassMappingPropertyDescriptorsHolder_SuperclassNameD;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_NameN;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_NameD;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_CascadeN;

	public static String PropertyMappingForCollectionMapingDescriptorsHolder_CascadeD;

	public static String ManyToOneMappingDescriptorsHolder_GeneralCategory;

	public static String ManyToOneMappingDescriptorsHolder_AdvancedCategory;

	public static String ManyToOneMappingDescriptorsHolder_ReferencedEntityNameN;

	public static String ManyToOneMappingDescriptorsHolder_ReferencedEntityNameD;

	public static String ManyToOneMappingDescriptorsHolder_MappingColumnN;

	public static String ManyToOneMappingDescriptorsHolder_MappingColumnD;

	public static String ManyToOneMappingDescriptorsHolder_FetchModeN;

	public static String ManyToOneMappingDescriptorsHolder_FetchModeD;

	public static String ManyToOneMappingDescriptorsHolder_FormulaN;

	public static String ManyToOneMappingDescriptorsHolder_FormulaD;

	public static String ManyToOneMappingDescriptorsHolder_ReferencedPropertyNameN;

	public static String ManyToOneMappingDescriptorsHolder_ReferencedPropertyNameD;

	public static String ManyToOneMappingDescriptorsHolder_IgnoreNotFoundN;

	public static String ManyToOneMappingDescriptorsHolder_IgnoreNotFoundD;

	public static String ManyToOneMappingDescriptorsHolder_ForeignKeyNameN;

	public static String ManyToOneMappingDescriptorsHolder_ForeignKeyNameD;

	public static String KeyPropertyDescriptorsHolder_AdvancedCategory;

	public static String KeyPropertyDescriptorsHolder_ForeignKeyNameN;

	public static String KeyPropertyDescriptorsHolder_ForeignKeyNameD;

	public static String KeyPropertyDescriptorsHolder_CascadeDeleteEnabledN;

	public static String KeyPropertyDescriptorsHolder_CascadeDeleteEnabledD;

	public static String KeyPropertyDescriptorsHolder_UpdateableN;

	public static String KeyPropertyDescriptorsHolder_UpdateableD;

	public static String KeyPropertyDescriptorsHolder_NullableN;

	public static String KeyPropertyDescriptorsHolder_NullableD;

	public static String CollectionMappingFKWithRefDescriptorsHolder_AdvancedCategory;

	public static String CollectionMappingFKWithRefDescriptorsHolder_ReferencedPropertyNameN;

	public static String CollectionMappingFKWithRefDescriptorsHolder_ReferencedPropertyNameD;

	public static String ComponentMappingDescriptorsHolder_GeneralCategory;

	public static String ComponentMappingDescriptorsHolder_AdvancedCategory;

	public static String ComponentMappingDescriptorsHolder_ComponentClassNameN;

	public static String ComponentMappingDescriptorsHolder_ComponentClassNameD;

	public static String ComponentMappingDescriptorsHolder_DynamicN;

	public static String ComponentMappingDescriptorsHolder_DynamicD;

	public static String ComponentMappingDescriptorsHolder_ParentPropertyN;

	public static String ComponentMappingDescriptorsHolder_ParentPropertyD;

	public static String BagMappingDescriptorsHolder_AdvancedCategory;

	public static String BagMappingDescriptorsHolder_GeneralCategory;

	public static String BagMappingDescriptorsHolder_OrderByN;

	public static String BagMappingDescriptorsHolder_OrderByD;

	public static String BagMappingDescriptorsHolder_InverseN;

	public static String BagMappingDescriptorsHolder_InverseD;

	public static String BagMappingDescriptorsHolder_LazyN;

	public static String BagMappingDescriptorsHolder_LazyD;

	public static String OneToOneMappingDescriptorsHolder_GeneralCategory;

	public static String OneToOneMappingDescriptorsHolder_AdvancedCategory;

	public static String OneToOneMappingDescriptorsHolder_ReferencedPropertyNameN;

	public static String OneToOneMappingDescriptorsHolder_ReferencedPropertyNameD;

	public static String OneToOneMappingDescriptorsHolder_ReferencedEntityNameN;

	public static String OneToOneMappingDescriptorsHolder_ReferencedEntityNameD;

	public static String OneToOneMappingDescriptorsHolder_FetchModeN;

	public static String OneToOneMappingDescriptorsHolder_FetchModeD;

	public static String OneToOneMappingDescriptorsHolder_FormulaN;

	public static String OneToOneMappingDescriptorsHolder_FormulaD;

	public static String OneToOneMappingDescriptorsHolder_ConstrainedN;

	public static String OneToOneMappingDescriptorsHolder_ConstrainedD;

	public static String OneToOneMappingDescriptorsHolder_ForeignKeyNameN;

	public static String OneToOneMappingDescriptorsHolder_ForeignKeyNameD;

	public static String ToOneMappingDescriptorsHolder_ToOneCategory;

	public static String ToOneMappingDescriptorsHolder_FetchModeN;

	public static String ToOneMappingDescriptorsHolder_FetchModeD;

	public static String ToOneMappingDescriptorsHolder_ReferencedPropertyNameN;

	public static String ToOneMappingDescriptorsHolder_ReferencedPropertyNameD;

	public static String ToOneMappingDescriptorsHolder_ReferencedEntityNameN;

	public static String ToOneMappingDescriptorsHolder_ReferencedEntityNameD;

	public static String CollectionMappingDescriptorsHolderWithTable_GeneralCategory;

	public static String CollectionMappingDescriptorsHolderWithTable_CollectionTableN;

	public static String CollectionMappingDescriptorsHolderWithTable_CollectionTableD;

	public static String ArrayMappingDescriptorsHolder_AdvancedCategory;

	public static String ArrayMappingDescriptorsHolder_IndexCategory;

	public static String ArrayMappingDescriptorsHolder_GeneralCategory;

	public static String ArrayMappingDescriptorsHolder_ElementClassNameN;

	public static String ArrayMappingDescriptorsHolder_ElementClassNameD;

	public static String ArrayMappingDescriptorsHolder_BaseIndexN;

	public static String ArrayMappingDescriptorsHolder_BaseIndexD;

	public static String ArrayMappingDescriptorsHolder_IndexMappingColumnN;

	public static String ArrayMappingDescriptorsHolder_IndexMappingColumnD;

	public static String ArrayMappingDescriptorsHolder_InverseN;

	public static String ArrayMappingDescriptorsHolder_InverseD;

	public static String PropertyMappingForVersionDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForVersionDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForVersionDescriptorsHolder_NameN;

	public static String PropertyMappingForVersionDescriptorsHolder_NameD;

	public static String PropertyMappingForVersionDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForVersionDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_GeneralCategory;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_AdvancedCategory;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_NameN;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_NameD;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_PropertyAccessorNameN;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_PropertyAccessorNameD;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_UpdateableN;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_UpdateableD;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_InsertableN;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_InsertableD;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_CascadeN;

	public static String PropertyMappingForAnyMapingDescriptorsHolder_CascadeD;

	public static String BagMappingDescriptorsHolderWithTable_GeneralCategory;

	public static String BagMappingDescriptorsHolderWithTable_CollectionTableN;

	public static String BagMappingDescriptorsHolderWithTable_CollectionTableD;

	public static String JoinMapingDescriptorsHolder_GeneralCategory;

	public static String JoinMapingDescriptorsHolder_NameN;

	public static String JoinMapingDescriptorsHolder_NameD;

	public static String JoinMapingDescriptorsHolder_InverseN;

	public static String JoinMapingDescriptorsHolder_InverseD;

	public static String JoinMapingDescriptorsHolder_OptionalN;

	public static String JoinMapingDescriptorsHolder_OptionalD;

	public static String JoinMapingDescriptorsHolder_TableNameN;

	public static String JoinMapingDescriptorsHolder_TableNameD;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_DatabaseTableN;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_DatabaseTableD;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_SuperclassNameN;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_SuperclassNameD;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_KeyColumnN;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_KeyColumnD;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_CheckN;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_CheckD;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_SubselectN;

	public static String JoinedSubclassMappingPropertyDescriptorsHolder_SubselectD;

	public static String ListMappingDescriptorsHolder_IndexCategory;

	public static String ListMappingDescriptorsHolder_GeneralCategory;

	public static String ListMappingDescriptorsHolder_AdvancedCategory;

	public static String ListMappingDescriptorsHolder_BaseIndexN;

	public static String ListMappingDescriptorsHolder_BaseIndexD;

	public static String ListMappingDescriptorsHolder_IndexMappingColumnN;

	public static String ListMappingDescriptorsHolder_IndexMappingColumnD;

	public static String ListMappingDescriptorsHolder_InverseN;

	public static String ListMappingDescriptorsHolder_InverseD;

	public static String ListMappingDescriptorsHolder_LazyN;

	public static String ListMappingDescriptorsHolder_LazyD;

	public static String RootClassMappingPropertyDescriptorsHolder_DatabaseTableByNameN;

	public static String RootClassMappingPropertyDescriptorsHolder_DatabaseTableByNameD;

	public static String RootClassMappingPropertyDescriptorsHolder_MutableN;

	public static String RootClassMappingPropertyDescriptorsHolder_MutableD;

	public static String RootClassMappingPropertyDescriptorsHolder_ExplicitPolymorphismN;

	public static String RootClassMappingPropertyDescriptorsHolder_ExplicitPolymorphismD;

	public static String RootClassMappingPropertyDescriptorsHolder_WhereN;

	public static String RootClassMappingPropertyDescriptorsHolder_WhereD;

	public static String RootClassMappingPropertyDescriptorsHolder_DiscriminatorValueN;

	public static String RootClassMappingPropertyDescriptorsHolder_DiscriminatorValueD;

	public static String RootClassMappingPropertyDescriptorsHolder_OptimisticLockModeN;

	public static String RootClassMappingPropertyDescriptorsHolder_OptimisticLockModeD;

	public static String RootClassMappingPropertyDescriptorsHolder_CheckN;

	public static String RootClassMappingPropertyDescriptorsHolder_CheckD;

	public static String RootClassMappingPropertyDescriptorsHolder_RowIdN;

	public static String RootClassMappingPropertyDescriptorsHolder_RowIdD;

	public static String RootClassMappingPropertyDescriptorsHolder_SubselectN;

	public static String RootClassMappingPropertyDescriptorsHolder_SubselectD;
}
