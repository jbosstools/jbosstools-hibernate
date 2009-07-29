/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.osgi.util.NLS;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateUIMappingMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.jpt.ui.internal.mapping.details.messages"; //$NON-NLS-1$


	public static String GenericGeneratorComposite_name;
	public static String GenericGeneratorComposite_strategy;
	public static String HibernateGeneratorsComposite_CheckBoxLabel;
	public static String HibernateGeneratorsComposite_SectionLabel;
	public static String NamedQueryPropertyComposite_cacheable;
	public static String NamedQueryPropertyComposite_cacheableWithDefault;
	public static String NamedQueryPropertyComposite_flushMode;
	public static String NamedQueryPropertyComposite_cacheMode;
	public static String NamedQueryPropertyComposite_readOnly;
	public static String NamedQueryPropertyComposite_readOnlyWithDefault;
	public static String NamedQueryPropertyComposite_cacheRegion;
	public static String NamedQueryPropertyComposite_fetchSize;
	public static String NamedQueryPropertyComposite_timeout;
	public static String HibernateAddQueryDialog_hibernateNamedQuery;
	public static String HibernateAddQueryDialog_hibernateNamedNativeQuery;
	public static String GenericGeneratorsComposite_generatorNullName;
	public static String GenericGeneratorsComposite_addGeneratorNameDescription;
	public static String EnterNameDialog_title;
	public static String EnterNameDialog_labelText;
	public static String NameStateObject_nameMustBeSpecified;
	public static String NameStateObject_nameAlreadyExists;
	public static String HibernateIdMappingComposite_genericGeneratorSection;
	public static String HibernateIdMappingComposite_genericGeneratorCheckBox;
	public static String HibernateDiscriminatorColumnComposite_formula;

	private HibernateUIMappingMessages() {}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, HibernateUIMappingMessages.class);
	}
}
