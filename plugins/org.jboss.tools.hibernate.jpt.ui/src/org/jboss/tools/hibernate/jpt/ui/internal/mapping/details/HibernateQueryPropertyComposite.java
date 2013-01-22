/*******************************************************************************
 * Copyright (c) 2009-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import java.util.Collection;

import org.eclipse.jpt.common.ui.internal.widgets.EnumFormComboViewer;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.ui.internal.JpaHelpContextIds;
import org.eclipse.jpt.jpa.ui.internal.details.JptUiDetailsMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.jboss.tools.hibernate.jpt.core.internal.context.CacheModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.FlushModeType;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateQuery;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateQueryPropertyComposite<T extends HibernateQuery> extends Pane<T> {

	public HibernateQueryPropertyComposite(Pane<?> parentPane,
            PropertyValueModel<? extends T> subjectHolder,
            Composite parent) {

		super(parentPane, subjectHolder, parent);
	}

	@Override
	protected void initializeLayout(Composite container) {

		this.addLabel(container, JptUiDetailsMessages.NamedQueryComposite_nameTextLabel);
		this.addText(container, buildNameTextHolder());
//		addLabeledText(
//			container,
//			JptUiDetailsMessages.NamedQueryComposite_nameTextLabel,
//			buildNameTextHolder());

		// Query text area
		this.addLabel(container, JptUiDetailsMessages.NamedQueryPropertyComposite_query);
		this.addMultiLineText(container, buildQueryHolder(), 4);
//		addLabeledMultiLineText(
//			container,
//			JptUiDetailsMessages.NamedQueryPropertyComposite_query,
//			buildQueryHolder(),
//			4,
//			null
//		);

		// ReadOnly tri-state check box
		addTriStateCheckBoxWithDefault(
			container,
			HibernateUIMappingMessages.NamedQueryPropertyComposite_readOnly,
			buildReadOnlyHolder(),
			buildReadOnlyStringHolder(),
			null//TODO help
		);

		//Flush Mode combobox
		this.addLabel(container, HibernateUIMappingMessages.NamedQueryPropertyComposite_flushMode);
		this.addFlushModeTypeCombo(container);
//		addLabeledComposite(
//			container,
//			HibernateUIMappingMessages.NamedQueryPropertyComposite_flushMode,
//			addFlushModeTypeCombo(container),
//			null//TODO help
//		);

		// Cacheable tri-state check box
		addTriStateCheckBoxWithDefault(
			container,
			HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheable,
			buildCacheableHolder(),
			buildCacheableStringHolder(),
			null//TODO help
		);

		//Cache Mode combobox
		this.addLabel(container, HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheMode);
		this.addCacheModeTypeCombo(container);
//		addLabeledComposite(
//			container,
//			HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheMode,
//			addCacheModeTypeCombo(container),
//			null//TODO help
//		);

		this.addLabel(container, HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheRegion);
		this.addText(container, buildCacheRegionTextHolder());
//		addLabeledText(
//			container,
//			HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheRegion,
//			buildCacheRegionTextHolder());

		// Fetch size widgets
		this.addLabel(container, HibernateUIMappingMessages.NamedQueryPropertyComposite_fetchSize);
		Spinner fetchSizeSpinner = 
				this.addSpinner(
						container, 
						buildFetchSizeHolder(), 
						-1, 
						-1, 
						Integer.MAX_VALUE, 
						JpaHelpContextIds.MAPPING_COLUMN_LENGTH);
//		Spinner fetchSizeSpinner = addLabeledSpinner(
//			container,
//			HibernateUIMappingMessages.NamedQueryPropertyComposite_fetchSize,
//			buildFetchSizeHolder(),
//			-1,
//			-1,
//			Integer.MAX_VALUE,
//			addDefaultFetchSizeLabel(container),
//			JpaHelpContextIds.MAPPING_COLUMN_LENGTH
//		);

		updateGridData(container, fetchSizeSpinner);

		this.addLabel(container, HibernateUIMappingMessages.NamedQueryPropertyComposite_timeout);
		Spinner timeoutSpinner = this.addSpinner(
				container, 
				buildTimeoutHolder(), 
				-1, 
				-1, 
				Integer.MAX_VALUE, JpaHelpContextIds.MAPPING_COLUMN_LENGTH);
		// Timeout size widgets
//		Spinner timeoutSpinner = addLabeledSpinner(
//			container,
//			HibernateUIMappingMessages.NamedQueryPropertyComposite_timeout,
//			buildTimeoutHolder(),
//			-1,
//			-1,
//			Integer.MAX_VALUE,
//			addDefaultFetchSizeLabel(container),
//			JpaHelpContextIds.MAPPING_COLUMN_LENGTH
//		);

		updateGridData(container, timeoutSpinner);

	}

//	private Control addDefaultFetchSizeLabel(Composite container) {
//
//		Label label = addLabel(
//			container,
//			JptCommonUiMessages.DefaultEmpty
//		);
//
//		new LabeledControlUpdater(
//			new LabeledLabel(label),
//			buildDefaultFetchSizeLabelHolder()
//		);
//
//		return label;
//	}

//	private PropertyValueModel<String> buildDefaultFetchSizeLabelHolder() {
//
//		return new TransformationPropertyValueModel<Integer, String>(buildDefaultFetchSizeHolder()) {
//
//			@Override
//			protected String transform(Integer value) {
//
//				int defaultValue = (getSubject() != null) ? getSubject().getDefaultFetchSize() :
//					HibernateNamedQuery.DEFAULT_FETCH_SIZE;
//
//				return NLS.bind(
//					JptCommonUiMessages.DefaultWithOneParam,
//					Integer.valueOf(defaultValue)
//				);
//			}
//		};
//	}

//	private ModifiablePropertyValueModel<Integer> buildDefaultFetchSizeHolder() {
//		return new PropertyAspectAdapter<HibernateQuery, Integer>(getSubjectHolder(), HibernateQuery.DEFAULT_FETCH_SIZE_PROPERTY) {
//			@Override
//			protected Integer buildValue_() {
//				return Integer.valueOf(this.subject.getDefaultFetchSize());
//			}
//		};
//	}

	protected ModifiablePropertyValueModel<String> buildNameTextHolder() {
		return new PropertyAspectAdapter<HibernateQuery, String>(
				getSubjectHolder(), HibernateQuery.NAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getName();
			}

			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				this.subject.setName(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildQueryHolder() {
		return new PropertyAspectAdapter<HibernateQuery, String>(getSubjectHolder(), HibernateQuery.QUERY_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getQuery();
			}

			@Override
			protected void setValue_(String value) {
				this.subject.setQuery(value);
			}
		};
	}

	private ModifiablePropertyValueModel<Boolean> buildCacheableHolder() {
		return new PropertyAspectAdapter<HibernateQuery, Boolean>(
			getSubjectHolder(),
			HibernateQuery.DEFAULT_CACHEABLE_PROPERTY,
			HibernateQuery.SPECIFIED_CACHEABLE_PROPERTY)
		{
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedCacheable();
			}

			@Override
			protected void setValue_(Boolean value) {
				this.subject.setSpecifiedCacheable(value);
			}
		};
	}

	private PropertyValueModel<String> buildCacheableStringHolder() {

		return new TransformationPropertyValueModel<Boolean, String>(buildCacheableHolder()) {

			@Override
			protected String transform(Boolean value) {

				if ((getSubject() != null) && (value == null)) {
					boolean defaultValue = getSubject().isDefaultCacheable();

					String defaultStringValue = defaultValue ? JptUiDetailsMessages.OptionalComposite_true :
					                                           JptUiDetailsMessages.OptionalComposite_false;

					return NLS.bind(
						HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheableWithDefault,
						defaultStringValue
					);
				}

				return HibernateUIMappingMessages.NamedQueryPropertyComposite_cacheable;
			}
		};
	}

	private ModifiablePropertyValueModel<Boolean> buildReadOnlyHolder() {
		return new PropertyAspectAdapter<HibernateQuery, Boolean>(
			getSubjectHolder(),
			HibernateQuery.DEFAULT_READ_ONLY_PROPERTY,
			HibernateQuery.SPECIFIED_READ_ONLY_PROPERTY)
		{
			@Override
			protected Boolean buildValue_() {
				return this.subject.getSpecifiedReadOnly();
			}

			@Override
			protected void setValue_(Boolean value) {
				this.subject.setSpecifiedReadOnly(value);
			}

		};
	}

	private PropertyValueModel<String> buildReadOnlyStringHolder() {

		return new TransformationPropertyValueModel<Boolean, String>(buildReadOnlyHolder()) {

			@Override
			protected String transform(Boolean value) {

				if ((getSubject() != null) && (value == null)) {
					boolean defaultValue = getSubject().isDefaultReadOnly();

					String defaultStringValue = defaultValue ? JptUiDetailsMessages.OptionalComposite_true :
					                                           JptUiDetailsMessages.OptionalComposite_false;

					return NLS.bind(
						HibernateUIMappingMessages.NamedQueryPropertyComposite_readOnlyWithDefault,
						defaultStringValue
					);
				}

				return HibernateUIMappingMessages.NamedQueryPropertyComposite_readOnly;
			}
		};
	}

	private EnumFormComboViewer<HibernateQuery, FlushModeType> addFlushModeTypeCombo(Composite container) {

		return new EnumFormComboViewer<HibernateQuery, FlushModeType>(this, container) {

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(HibernateQuery.DEFAULT_FLUSH_MODE_PROPERTY);
				propertyNames.add(HibernateQuery.SPECIFIED_FLUSH_MODE_PROPERTY);
			}

			@Override
			protected FlushModeType[] getChoices() {
				return FlushModeType.values();
			}

			@Override
			protected FlushModeType getDefaultValue() {
				return getSubject().getDefaultFlushMode();
			}

			@Override
			protected String displayString(FlushModeType value) {
				return value.toString();

			}

			@Override
			protected FlushModeType getValue() {
				return getSubject().getSpecifiedFlushMode();
			}

			@Override
			protected void setValue(FlushModeType value) {
				getSubject().setSpecifiedFlushMode(value);
			}
		};
	}

	private EnumFormComboViewer<HibernateQuery, CacheModeType> addCacheModeTypeCombo(Composite container) {

		return new EnumFormComboViewer<HibernateQuery, CacheModeType>(this, container) {

			@Override
			protected void addPropertyNames(Collection<String> propertyNames) {
				super.addPropertyNames(propertyNames);
				propertyNames.add(HibernateQuery.DEFAULT_CACHE_MODE_PROPERTY);
				propertyNames.add(HibernateQuery.SPECIFIED_CACHE_MODE_PROPERTY);
			}

			@Override
			protected CacheModeType[] getChoices() {
				return CacheModeType.values();
			}

			@Override
			protected CacheModeType getDefaultValue() {
				return getSubject().getDefaultCacheMode();
			}

			@Override
			protected String displayString(CacheModeType value) {
				return value.toString();

			}

			@Override
			protected CacheModeType getValue() {
				return getSubject().getSpecifiedCacheMode();
			}

			@Override
			protected void setValue(CacheModeType value) {
				getSubject().setSpecifiedCacheMode(value);
			}
		};
	}

	private ModifiablePropertyValueModel<String> buildCacheRegionTextHolder() {
		return new PropertyAspectAdapter<HibernateQuery, String>(
				getSubjectHolder(),
				HibernateQuery.DEFAULT_CACHE_REGION_PROPERTY,
				HibernateQuery.SPECIFIED_CACHE_REGION_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getSpecifiedCacheRegion();
			}

			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				this.subject.setSpecifiedCacheRegion(value);
			}
		};
	}

	private ModifiablePropertyValueModel<Integer> buildFetchSizeHolder() {
		return new PropertyAspectAdapter<HibernateQuery, Integer>(getSubjectHolder(), HibernateQuery.SPECIFIED_FETCH_SIZE_PROPERTY) {
			@Override
			protected Integer buildValue_() {
				return this.subject.getSpecifiedFetchSize();
			}

			@Override
			protected void setValue_(Integer value) {
				if (value.intValue() == -1) {
					value = null;
				}
				this.subject.setSpecifiedFetchSize(value);
			}
		};
	}



	private ModifiablePropertyValueModel<Integer> buildTimeoutHolder() {
		return new PropertyAspectAdapter<HibernateQuery, Integer>(getSubjectHolder(), HibernateQuery.SPECIFIED_TIMEOUT_PROPERTY) {
			@Override
			protected Integer buildValue_() {
				return this.subject.getSpecifiedTimeout();
			}

			@Override
			protected void setValue_(Integer value) {
				if (value.intValue() == -1) {
					value = null;
				}
				this.subject.setSpecifiedTimeout(value);
			}
		};
	}

	protected void updateGridData(Composite container, Spinner spinner) {

		// It is possible the spinner's parent is not the container of the
		// label, spinner and right control (a pane is sometimes required for
		// painting the spinner's border)
		Composite paneContainer = spinner.getParent();

		while (container != paneContainer.getParent()) {
			paneContainer = paneContainer.getParent();
		}

		Control[] controls = paneContainer.getChildren();

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment       = GridData.BEGINNING;
		controls[1].setLayoutData(gridData);

		controls[2].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		removeAlignRight(controls[2]);
	}

}
