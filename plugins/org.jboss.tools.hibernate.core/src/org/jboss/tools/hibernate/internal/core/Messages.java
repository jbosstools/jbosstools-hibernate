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
package org.jboss.tools.hibernate.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.internal.core.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String OrmPropertyDescriptorsHolder_TypesCategory;

	public static String OrmPropertyDescriptorsHolder_NumericTypesCategory;

	public static String OrmPropertyDescriptorsHolder_DiscriminatorCategory;

	public static String OrmPropertyDescriptorsHolder_IdCategory;

	public static String OrmPropertyDescriptorsHolder_LockingCategory;

	public static String OrmPropertyDescriptorsHolder_AssociationsCategory;

	public static String OrmPropertyDescriptorsHolder_AutomappingCategory;

	public static String OrmPropertyDescriptorsHolder_Names;

	public static String OrmPropertyDescriptorsHolder_ClassMappingCategory;

	public static String OrmPropertyDescriptorsHolder_SpringFramework;

	public static String OrmPropertyDescriptorsHolder_HibernateStorageN;

	public static String OrmPropertyDescriptorsHolder_HibernateStorageD;

	public static String OrmPropertyDescriptorsHolder_POJORendererN;

	public static String OrmPropertyDescriptorsHolder_POJORendererD;

	public static String OrmPropertyDescriptorsHolder_HibernateInheritanceN;

	public static String OrmPropertyDescriptorsHolder_HibernateInheritanceD;

	public static String OrmPropertyDescriptorsHolder_HibernateOptimisticN;

	public static String OrmPropertyDescriptorsHolder_HibernateOptimisticD;

	public static String OrmPropertyDescriptorsHolder_ReversingNativeSQLTypesN;

	public static String OrmPropertyDescriptorsHolder_ReversingNativeSQLTypesD;

	public static String OrmPropertyDescriptorsHolder_HibernateVersionDatatypeN;

	public static String OrmPropertyDescriptorsHolder_HibernateVersionDatatypeD;

	public static String OrmPropertyDescriptorsHolder_HibernateVersionUnsavedValueN;

	public static String OrmPropertyDescriptorsHolder_HibernateVersionUnsavedValueD;

	public static String OrmPropertyDescriptorsHolder_HibernateCascadeN;

	public static String OrmPropertyDescriptorsHolder_HibernateCascadeD;

	public static String OrmPropertyDescriptorsHolder_HibernateLazyN;

	public static String OrmPropertyDescriptorsHolder_HibernateLazyD;

	public static String OrmPropertyDescriptorsHolder_HibernateAssociationsLazyN;

	public static String OrmPropertyDescriptorsHolder_HibernateAssociationsLazyD;

	public static String OrmPropertyDescriptorsHolder_HibernateCollectionsLazyN;

	public static String OrmPropertyDescriptorsHolder_HibernateCollectionsLazyD;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorDatatypeN;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorDatatypeD;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorForceN;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorForceD;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorInsertN;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorInsertD;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorFormulaN;

	public static String OrmPropertyDescriptorsHolder_HibernateDiscriminatorFormulaD;

	public static String OrmPropertyDescriptorsHolder_HibernateIdDatatypeN;

	public static String OrmPropertyDescriptorsHolder_HibernateIdDatatypeD;

	public static String OrmPropertyDescriptorsHolder_HibernateIdUnsavedValueN;

	public static String OrmPropertyDescriptorsHolder_HibernateIdUnsavedValueD;

	public static String OrmPropertyDescriptorsHolder_HibernateIdGeneratorN;

	public static String OrmPropertyDescriptorsHolder_HibernateIdGeneratorD;

	public static String OrmPropertyDescriptorsHolder_HibernateIdGeneratorQualityN;

	public static String OrmPropertyDescriptorsHolder_HibernateIdGeneratorQualityD;

	public static String OrmPropertyDescriptorsHolder_HibernateAssociationsCascadeN;

	public static String OrmPropertyDescriptorsHolder_HibernateAssociationsCascadeD;

	public static String OrmPropertyDescriptorsHolder_HibernateKeyOnDeleteN;

	public static String OrmPropertyDescriptorsHolder_HibernateKeyOnDeleteD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeIntN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeIntD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeLongN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeLongD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeShortN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeShortD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeFloatN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeFloatD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeDoubleN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeDoubleD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeCharacterN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeCharacterD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeByteN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeByteD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeBooleanN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeBooleanD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeTimeN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeTimeD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeDateN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeDateD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeTimestampN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeTimestampD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeCalendarN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeCalendarD;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeCalendarDateN;

	public static String OrmPropertyDescriptorsHolder_HibernateTypeCalendarDateD;

	public static String OrmPropertyDescriptorsHolder_ClassSelectBeforeUpdateN;

	public static String OrmPropertyDescriptorsHolder_ClassSelectBeforeUpdateD;

	public static String OrmPropertyDescriptorsHolder_ClassBatchSizeN;

	public static String OrmPropertyDescriptorsHolder_ClassBatchSizeD;

	public static String OrmPropertyDescriptorsHolder_HamSafeCollectionN;

	public static String OrmPropertyDescriptorsHolder_HamSafeCollectionD;

	public static String OrmPropertyDescriptorsHolder_HamCollectionElementTypeN;

	public static String OrmPropertyDescriptorsHolder_HamCollectionElementTypeD;

	public static String OrmPropertyDescriptorsHolder_HamReachabilityN;

	public static String OrmPropertyDescriptorsHolder_HamReachabilityD;

	public static String OrmPropertyDescriptorsHolder_HamAccessorN;

	public static String OrmPropertyDescriptorsHolder_HamAccessorD;

	public static String OrmPropertyDescriptorsHolder_HamQueryFuzzyOnN;

	public static String OrmPropertyDescriptorsHolder_HamQueryFuzzyOnD;

	public static String OrmPropertyDescriptorsHolder_HamQueryFuzzinessN;

	public static String OrmPropertyDescriptorsHolder_HamQueryFuzzinessD;

	public static String OrmPropertyDescriptorsHolder_HamLinkTablesN;

	public static String OrmPropertyDescriptorsHolder_HamLinkTablesD;

	public static String OrmPropertyDescriptorsHolder_RevtypeNumeric10N;

	public static String OrmPropertyDescriptorsHolder_RevtypeNumeric10D;

	public static String OrmPropertyDescriptorsHolder_RevtypeNumericConvertN;

	public static String OrmPropertyDescriptorsHolder_RevtypeNumericConvertD;

	public static String OrmPropertyDescriptorsHolder_RevtypeNumericXYN;

	public static String OrmPropertyDescriptorsHolder_RevtypeNumericXYD;

	public static String OrmPropertyDescriptorsHolder_IdentifierColumnNameN;

	public static String OrmPropertyDescriptorsHolder_IdentifierColumnNameD;

	public static String OrmPropertyDescriptorsHolder_VersionColumnNameN;

	public static String OrmPropertyDescriptorsHolder_VersionColumnNameD;

	public static String OrmPropertyDescriptorsHolder_DiscriminatorColumnNameN;

	public static String OrmPropertyDescriptorsHolder_DiscriminatorColumnNameD;

	public static String OrmPropertyDescriptorsHolder_IdentifierQueryN;

	public static String OrmPropertyDescriptorsHolder_IdentifierQueryD;

	public static String OrmPropertyDescriptorsHolder_VersionQueryN;

	public static String OrmPropertyDescriptorsHolder_VersionQueryD;

	public static String OrmPropertyDescriptorsHolder_DiscriminatorQueryN;

	public static String OrmPropertyDescriptorsHolder_DiscriminatorQueryD;

	public static String OrmPropertyDescriptorsHolder_HibernateDefaultSchemaN;

	public static String OrmPropertyDescriptorsHolder_HibernateDefaultSchemaD;

	public static String OrmPropertyDescriptorsHolder_HibernateDefaultPackageN;

	public static String OrmPropertyDescriptorsHolder_HibernateDefaultPackageD;

	public static String OrmPropertyDescriptorsHolder_HamIgnoreListN;

	public static String OrmPropertyDescriptorsHolder_HamIgnoreListD;

	public static String OrmPropertyDescriptorsHolder_TablePrefixQueryN;

	public static String OrmPropertyDescriptorsHolder_TablePrefixQueryD;

	public static String OrmPropertyDescriptorsHolder_ReversingBaseClassN;

	public static String OrmPropertyDescriptorsHolder_ReversingBaseClassD;

	public static String OrmPropertyDescriptorsHolder_SpringSessionfactoryN;

	public static String OrmPropertyDescriptorsHolder_SpringSessionfactoryD;

	public static String OrmPropertyDescriptorsHolder_SpringDataSourceN;

	public static String OrmPropertyDescriptorsHolder_SpringDataSourceD;

	public static String OrmPropertyDescriptorsHolder_SpringTransactionManagerN;

	public static String OrmPropertyDescriptorsHolder_SpringTransactionManagerD;

	public static String OrmPropertyDescriptorsHolder_SpringTransactionInterceptorN;

	public static String OrmPropertyDescriptorsHolder_SpringTransactionInterceptorD;

	public static String OrmPropertyDescriptorsHolder_SpringHibernateInterceptorN;

	public static String OrmPropertyDescriptorsHolder_SpringHibernateInterceptorD;

	public static String OrmPropertyDescriptorsHolder_SpringTransactionAttributesN;

	public static String OrmPropertyDescriptorsHolder_SpringTransactionAttributesD;

	public static String JavaLoggingPropertiesHolder_GeneralCategory;

	public static String JavaLoggingPropertiesHolder_HibernateStorageN;

	public static String JavaLoggingPropertiesHolder_HibernateStorageD;
}
